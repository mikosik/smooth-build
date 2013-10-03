package org.smoothbuild.parse.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import java.io.IOException;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.Message;

public class CannotReadScriptError extends Message {
  public CannotReadScriptError(Path scriptFile, IOException exception) {
    super(ERROR, "Cannot read build script " + scriptFile + "\n" + exception.getMessage());
  }
}
