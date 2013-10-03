package org.smoothbuild.run.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.Message;
import org.smoothbuild.type.api.Path;

public class ScriptFileNotFoundError extends Message {
  public ScriptFileNotFoundError(Path scriptFile) {
    super(ERROR, "Cannot find build script file " + scriptFile);
  }
}
