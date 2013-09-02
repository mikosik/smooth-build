package org.smoothbuild.builtin.file.err;

import org.smoothbuild.plugin.Path;
import org.smoothbuild.problem.Error;

public class PathIsNotADirError extends Error {
  public PathIsNotADirError(String paramName, Path path) {
    super("Param '" + paramName + "' has illegal value. Path " + path
        + " exists but is not a directory but a file.");
  }
}
