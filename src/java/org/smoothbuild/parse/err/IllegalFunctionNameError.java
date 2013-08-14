package org.smoothbuild.parse.err;

import org.smoothbuild.problem.Error;
import org.smoothbuild.problem.SourceLocation;

public class IllegalFunctionNameError extends Error {
  public IllegalFunctionNameError(SourceLocation sourceLocation, String name) {
    super(sourceLocation, "Illegal function name '" + name + "'");
  }
}
