package org.smoothbuild.builtin.java.javac;

import org.smoothbuild.plugin.api.Path;

@SuppressWarnings("serial")
public class DuplicatedClassFileException extends Exception {
  private final Path path;

  public DuplicatedClassFileException(Path path, String jarFileName1, String jarFileName2) {
    this.path = path;
  }

  public Path path() {
    return path;
  }
}
