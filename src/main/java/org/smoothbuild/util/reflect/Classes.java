package org.smoothbuild.util.reflect;

import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.util.Okios.readAndClose;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import com.google.common.io.ByteStreams;

import okio.ByteString;

public class Classes {
  public static final String CLASS_FILE_EXTENSION = ".class";

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

  public static void saveBytecodeInJar(File jarFile, Class<?>... classes) throws IOException {
    try (JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(jarFile))) {
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

  public static ByteString bytecode(Class<?> klass) throws IOException {
    return readAndClose(buffer(source(byteCodeAsInputStream(klass))), s -> s.readByteString());
  }
}
