package org.smoothbuild.run.err;

import org.smoothbuild.message.message.ErrorMessage;
import org.smoothbuild.plugin.api.Path;

public class ScriptFileNotFoundError extends ErrorMessage {
  public ScriptFileNotFoundError(Path scriptFile) {
    super("Cannot find build script file " + scriptFile);
  }
}
