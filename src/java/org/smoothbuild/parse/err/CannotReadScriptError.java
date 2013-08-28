package org.smoothbuild.parse.err;

import java.io.IOException;

import org.smoothbuild.problem.Error;

public class CannotReadScriptError extends Error {
  public CannotReadScriptError(String scriptFile, IOException exception) {
    super(null, "Cannot read build script '" + scriptFile + "'\n" + exception.getMessage());
  }
}
