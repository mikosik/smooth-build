package org.smoothbuild.parse.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import java.io.IOException;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.message.base.Message;

@SuppressWarnings("serial")
public class CannotReadScriptError extends Message {
  public CannotReadScriptError(Path scriptFile, IOException exception) {
    super(ERROR, "Cannot read build script " + scriptFile + "\n" + exception.getMessage());
  }
}
