package org.smoothbuild.util;

public class Text {
  public static String unlines(String... lines) {
    return String.join("\n", lines);
  }
}
