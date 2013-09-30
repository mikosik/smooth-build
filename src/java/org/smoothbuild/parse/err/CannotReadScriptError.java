package org.smoothbuild.parse.err;

import java.io.IOException;

import org.smoothbuild.message.message.ErrorMessage;
import org.smoothbuild.plugin.api.Path;

public class CannotReadScriptError extends ErrorMessage {
  public CannotReadScriptError(Path scriptFile, IOException exception) {
    super("Cannot read build script " + scriptFile + "\n" + exception.getMessage());
  }
}
