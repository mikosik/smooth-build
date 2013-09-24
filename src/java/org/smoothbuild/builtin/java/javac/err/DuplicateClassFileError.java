package org.smoothbuild.builtin.java.javac.err;

import org.smoothbuild.message.Error;
import org.smoothbuild.plugin.api.Path;

public class DuplicateClassFileError extends Error {
  public DuplicateClassFileError(Path path, Path jar1, Path jar2) {
    super("File " + path + " is contained by both library jar files: " + jar1 + " and " + jar2
        + ".");
  }
}
