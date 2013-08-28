package org.smoothbuild.run.err;

import org.smoothbuild.problem.Error;

public class ScriptFileNotFoundError extends Error {
  public ScriptFileNotFoundError(String scriptFile) {
    super(null, "Cannot find build script file '" + scriptFile + "'");
  }
}
