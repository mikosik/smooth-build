package org.smoothbuild.parse.err;

import org.smoothbuild.problem.Error;
import org.smoothbuild.problem.SourceLocation;

public class UndefinedFunctionError extends Error {
  public UndefinedFunctionError(SourceLocation sourceLocation, String name) {
    super(sourceLocation, "Undefined function  '" + name + "'");
  }
}
