package org.smoothbuild.builtin.file.err;

import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.problem.Error;

public class PathIsNotAFileError extends Error {
  public PathIsNotAFileError(String paramName, Path path) {
    super("Param '" + paramName + "' has illegal value. Path " + path
        + " exists but is not a file but a directory.");
  }
}
