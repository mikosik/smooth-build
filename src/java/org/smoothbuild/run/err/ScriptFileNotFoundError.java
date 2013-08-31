package org.smoothbuild.run.err;

import org.smoothbuild.plugin.Path;
import org.smoothbuild.problem.Error;

public class ScriptFileNotFoundError extends Error {
  public ScriptFileNotFoundError(Path scriptFile) {
    super("Cannot find build script file " + scriptFile);
  }
}
