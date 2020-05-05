package org.smoothbuild.util;

import java.nio.file.Path;

public class Paths {
  public static Path changeExtension(Path path, String extension) {
    String string = changeExtension(path.toString(), extension);
    return java.nio.file.Paths.get(string);
  }

  public static String changeExtension(String stringPath, String extension) {
    return removeExtension(stringPath) + "." + extension;
  }

  public static String removeExtension(String string) {
    int dotIndex = string.lastIndexOf('.');
    if (dotIndex == -1) {
      return string;
    } else {
      return string.substring(0, dotIndex);
    }
  }
}
