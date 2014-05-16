package org.smoothbuild.util;

import java.io.InputStream;

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

  public static InputStream byteCodeAsInputStream(Class<?> clazz) {
    return clazz.getClassLoader().getResourceAsStream(binaryPath(clazz));
  }
}
