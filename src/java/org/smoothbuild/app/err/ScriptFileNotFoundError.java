package org.smoothbuild.app.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.Message;

public class ScriptFileNotFoundError extends Message {
  public ScriptFileNotFoundError(Path scriptFile) {
    super(ERROR, "Cannot find build script file " + scriptFile);
  }
}
