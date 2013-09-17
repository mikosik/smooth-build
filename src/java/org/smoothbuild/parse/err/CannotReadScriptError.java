package org.smoothbuild.parse.err;

import java.io.IOException;

import org.smoothbuild.message.Error;
import org.smoothbuild.plugin.api.Path;

public class CannotReadScriptError extends Error {
  public CannotReadScriptError(Path scriptFile, IOException exception) {
    super("Cannot read build script " + scriptFile + "\n" + exception.getMessage());
  }
}
