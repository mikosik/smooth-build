package org.smoothbuild.util;

import static com.google.common.io.ByteStreams.toByteArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import com.google.common.io.ByteStreams;

public class Classes {
  public static final String CLASS_FILE_EXTENSION = ".class";

  public static String binaryPath(Class<?> clazz) {
    return clazz.getName().replace('.', '/') + CLASS_FILE_EXTENSION;
  }

  public static String binaryPathToBinaryName(String binaryPath) {
    if (!binaryPath.endsWith(CLASS_FILE_EXTENSION)) {
      throw new IllegalArgumentException("Path '' is not class file. It should end with "
          + CLASS_FILE_EXTENSION);
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

  public static byte[] bytecode(Class<?> klass) throws IOException {
    try (InputStream inputStream = byteCodeAsInputStream(klass)) {
      return toByteArray(inputStream);
    }
  }
}
