package org.smoothbuild.run.err;

import org.smoothbuild.message.message.Error;
import org.smoothbuild.plugin.api.Path;

public class ScriptFileNotFoundError extends Error {
  public ScriptFileNotFoundError(Path scriptFile) {
    super("Cannot find build script file " + scriptFile);
  }
}
