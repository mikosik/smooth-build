package org.smoothbuild.parse.err;

import org.smoothbuild.problem.CodeError;
import org.smoothbuild.problem.SourceLocation;

public class DuplicateFunctionError extends CodeError {
  public DuplicateFunctionError(SourceLocation sourceLocation, String name) {
    super(sourceLocation, "Duplicate function '" + name + "'");
  }
}
