package org.smoothbuild.builtin.file.err;

import org.smoothbuild.problem.Error;

public class MissingRequiredArgError extends Error {
  public MissingRequiredArgError(String paramName) {
    super("Param '" + paramName + "' is required but is not assigned.");
  }
}
