package org.smoothbuild.util;

import java.nio.file.Path;

public class Paths {
  public static Path changeExtension(Path path, String extension) {
    String string = removeExtension(path.toString()) + "." + extension;
    return java.nio.file.Paths.get(string);
  }

  private static String removeExtension(String string) {
    return string.substring(0, string.lastIndexOf('.'));
  }
}
