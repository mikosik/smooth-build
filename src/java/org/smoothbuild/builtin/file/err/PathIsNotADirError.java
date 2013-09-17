package org.smoothbuild.builtin.file.err;

import org.smoothbuild.message.Error;
import org.smoothbuild.plugin.api.Path;

public class PathIsNotADirError extends Error {
  public PathIsNotADirError(String paramName, Path path) {
    super("Param '" + paramName + "' has illegal value. Path " + path
        + " exists but is not a directory but a file.");
  }
}
