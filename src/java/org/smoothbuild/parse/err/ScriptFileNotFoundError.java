package org.smoothbuild.parse.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.message.base.Message;

public class ScriptFileNotFoundError extends Message {
  public ScriptFileNotFoundError(Path scriptFile) {
    super(ERROR, "Cannot find build script file " + scriptFile);
  }
}
