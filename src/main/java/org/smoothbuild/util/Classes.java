package org.smoothbuild.util;

public class Classes {

  public static String binaryPath(Class<?> clazz) {
    return clazz.getName().replace('.', '/') + ".class";
  }

}
