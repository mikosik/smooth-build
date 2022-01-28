package org.smoothbuild.util.reflect;

import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.util.io.Okios.readAndClose;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import com.google.common.io.ByteStreams;

import okio.BufferedSource;
import okio.ByteString;

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
    return clazz.getName().replace('.', '/') + CLASS_FILE_EXTENSION;
  }

  public static String binaryPathToBinaryName(String binaryPath) {
    if (!binaryPath.endsWith(CLASS_FILE_EXTENSION)) {
      throw new IllegalArgumentException("Path '" + binaryPath
          + "' is not class file. It should end with " + CLASS_FILE_EXTENSION);
    }
    int newLength = binaryPath.length() - CLASS_FILE_EXTENSION.length();
    return binaryPath.substring(0, newLength).replace('/', '.');
  }

  public static void saveBytecodeInJar(Path jarPath, Class<?>... classes) throws IOException {
    try (var outputStream = new FileOutputStream(jarPath.toFile())) {
      saveByteCodeInJar(outputStream, classes);
    }
  }

  public static void saveByteCodeInJar(OutputStream outputStream, Class<?>... classes)
      throws IOException {
    try (JarOutputStream jarOutputStream = new JarOutputStream(outputStream)) {
      for (Class<?> clazz : classes) {
        jarOutputStream.putNextEntry(new ZipEntry(binaryPath(clazz)));
        try (InputStream byteCodeInputStream = byteCodeAsInputStream(clazz)) {
          ByteStreams.copy(byteCodeInputStream, jarOutputStream);
        }
      }
    }
  }

  public static InputStream byteCodeAsInputStream(Class<?> clazz) {
    return clazz.getClassLoader().getResourceAsStream(binaryPath(clazz));
  }

  public static ByteString bytecode(Class<?> clazz) throws IOException {
    return readAndClose(buffer(source(byteCodeAsInputStream(clazz))),
        BufferedSource::readByteString);
  }
}
