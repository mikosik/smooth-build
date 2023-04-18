package org.smoothbuild.stdlib.java.util;

import java.util.function.Predicate;

import javax.tools.JavaFileObject.Kind;

public class JavaNaming {
  private static final String CLASS_FILE_EXTENSION = Kind.CLASS.extension;

  public static String toBinaryName(String path) {
    int endIndex = path.length() - CLASS_FILE_EXTENSION.length();
    String withoutExtension = path.substring(0, endIndex);
    return withoutExtension.replace('/', '.');
  }

  public static String binaryNameToPackage(String binaryName) {
    int lastIndex = binaryName.lastIndexOf('.');
    if (lastIndex == -1) {
      return "";
    } else {
      return binaryName.substring(0, lastIndex);
    }
  }

  public static Predicate<String> isClassFilePredicate() {
    return (string) -> string.endsWith(CLASS_FILE_EXTENSION);
  }
}
