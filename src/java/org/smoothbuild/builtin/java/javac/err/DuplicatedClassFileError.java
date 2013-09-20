package org.smoothbuild.builtin.java.javac.err;

import org.smoothbuild.message.Error;
import org.smoothbuild.plugin.api.Path;

public class DuplicatedClassFileError extends Error {
  public DuplicatedClassFileError(Path path, String jarName1, String jarName2) {
    super("File " + path + " is contained by both library jar files: '" + jarName1 + "' and '"
        + jarName2 + "'.");
  }
}
