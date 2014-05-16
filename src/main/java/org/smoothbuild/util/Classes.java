package org.smoothbuild.util;

import java.io.InputStream;

public class Classes {

  public static String binaryPath(Class<?> clazz) {
    return clazz.getName().replace('.', '/') + ".class";
  }

  public static InputStream byteCodeAsInputStream(Class<?> clazz) {
    return clazz.getClassLoader().getResourceAsStream(binaryPath(clazz));
  }
}
