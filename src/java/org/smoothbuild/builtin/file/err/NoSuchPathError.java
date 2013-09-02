package org.smoothbuild.builtin.file.err;

import org.smoothbuild.plugin.Path;
import org.smoothbuild.problem.Error;

public class NoSuchPathError extends Error {
  public NoSuchPathError(String paramName, Path dirPath) {
    super("Param '" + paramName + "' has illegal value. Path " + dirPath + " does not exist");
  }
}
