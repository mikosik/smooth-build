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
}
