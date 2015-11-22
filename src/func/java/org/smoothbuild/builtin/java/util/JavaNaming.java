package org.smoothbuild.builtin.java.util;

import java.util.function.Predicate;

import javax.tools.JavaFileObject.Kind;

import org.smoothbuild.io.fs.base.Path;

public class JavaNaming {
  private static final String CLASS_FILE_EXTENSION = Kind.CLASS.extension;
  private static final Predicate<String> IS_CLASS_FILE = (string) -> string.endsWith(
      CLASS_FILE_EXTENSION);

  public static String toBinaryName(Path path) {
    String pathString = path.value();
    int endIndex = pathString.length() - CLASS_FILE_EXTENSION.length();
    String withoutExtension = pathString.substring(0, endIndex);
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
    return IS_CLASS_FILE;
  }
}
