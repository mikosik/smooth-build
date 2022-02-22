package org.smoothbuild.util.reflect;

import static okio.Okio.buffer;
import static okio.Okio.sink;
import static okio.Okio.source;
import static org.smoothbuild.util.io.Okios.readAndClose;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import com.google.common.io.ByteStreams;

import okio.BufferedSource;
import okio.ByteString;
import okio.Sink;

public class Classes {
  public static final String CLASS_FILE_EXTENSION = ".class";

  public static Class<?> loadClass(Path jarPath, String binaryName)
      throws ClassNotFoundException, FileNotFoundException {
    return jarClassLoader(jarPath).loadClass(binaryName);
  }

  private static ClassLoader jarClassLoader(Path jar) throws FileNotFoundException {
    ClassLoader parentClassLoader = Classes.class.getClassLoader();
    return ClassLoaders.jarClassLoader(parentClassLoader, jar);
  }

  public static String binaryPath(Class<?> clazz) {
    return binaryPath(clazz.getName());
  }

  public static String binaryPath(String binaryName) {
    return binaryName.replace('.', '/') + CLASS_FILE_EXTENSION;
  }

  public static String binaryPathToBinaryName(String binaryPath) {
    if (!binaryPath.endsWith(CLASS_FILE_EXTENSION)) {
      throw new IllegalArgumentException("Path '" + binaryPath
          + "' is not class file. It should end with " + CLASS_FILE_EXTENSION);
    }
    int newLength = binaryPath.length() - CLASS_FILE_EXTENSION.length();
    return binaryPath.substring(0, newLength).replace('/', '.');
  }

  public static void saveBytecodeInJar(Path jarPath, List<Class<?>> classes) throws IOException {
    try (var outputStream = sink(jarPath.toFile())) {
      saveBytecodeInJar(outputStream, classes);
    }
  }

  public static void saveBytecodeInJar(Sink sink, List<Class<?>> classes)
      throws IOException {
    try (var jarOutputStream = new JarOutputStream(buffer(sink).outputStream())) {
      for (Class<?> clazz : classes) {
        jarOutputStream.putNextEntry(new ZipEntry(binaryPath(clazz)));
        try (InputStream bytecodeInputStream = bytecodeAsInputStream(clazz)) {
          ByteStreams.copy(bytecodeInputStream, jarOutputStream);
        }
      }
    }
  }

  public static InputStream bytecodeAsInputStream(Class<?> clazz) {
    return clazz.getClassLoader().getResourceAsStream(binaryPath(clazz));
  }

  public static ByteString bytecode(Class<?> clazz) throws IOException {
    return readAndClose(buffer(source(bytecodeAsInputStream(clazz))),
        BufferedSource::readByteString);
  }
}
