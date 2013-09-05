package org.smoothbuild.parse.err;

import java.io.IOException;

import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.problem.Error;

public class CannotReadScriptError extends Error {
  public CannotReadScriptError(Path scriptFile, IOException exception) {
    super("Cannot read build script " + scriptFile + "\n" + exception.getMessage());
  }
}
