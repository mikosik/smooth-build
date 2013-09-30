package org.smoothbuild.run.err;

import static org.smoothbuild.message.listen.MessageType.ERROR;

import org.smoothbuild.message.message.Message;
import org.smoothbuild.plugin.api.Path;

public class ScriptFileNotFoundError extends Message {
  public ScriptFileNotFoundError(Path scriptFile) {
    super(ERROR, "Cannot find build script file " + scriptFile);
  }
}
