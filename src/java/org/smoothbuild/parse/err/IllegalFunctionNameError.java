package org.smoothbuild.parse.err;

import org.smoothbuild.problem.CodeError;
import org.smoothbuild.problem.SourceLocation;

public class IllegalFunctionNameError extends CodeError {
  public IllegalFunctionNameError(SourceLocation sourceLocation, String name) {
    super(sourceLocation, "Illegal function name '" + name + "'");
  }
}
