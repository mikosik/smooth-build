package org.smoothbuild.parse.err;

import org.smoothbuild.problem.CodeError;
import org.smoothbuild.problem.SourceLocation;

public class UndefinedFunctionError extends CodeError {
  public UndefinedFunctionError(SourceLocation sourceLocation, String name) {
    super(sourceLocation, "Undefined function  '" + name + "'");
  }
}
