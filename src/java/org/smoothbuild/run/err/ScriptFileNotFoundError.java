package org.smoothbuild.run.err;

import org.smoothbuild.plugin.Path;
import org.smoothbuild.problem.Error;

public class ScriptFileNotFoundError extends Error {
  public ScriptFileNotFoundError(Path scriptFile) {
    super(null, "Cannot find build script file " + scriptFile);
  }
}
