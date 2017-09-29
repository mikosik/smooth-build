package org.smoothbuild.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;

public class Paths {
  public static BufferedInputStream openBufferedInputStream(Path path)
      throws FileNotFoundException {
    return new BufferedInputStream(new FileInputStream(path.toFile()));
  }

  public static Path changeExtension(Path path, String extension) {
    String string = removeExtension(path.toString()) + "." + extension;
    return java.nio.file.Paths.get(string);
  }

  private static String removeExtension(String string) {
    return string.substring(0, string.lastIndexOf('.'));
  }
}
