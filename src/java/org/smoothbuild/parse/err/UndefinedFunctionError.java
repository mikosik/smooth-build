package org.smoothbuild.parse.err;

import org.smoothbuild.problem.CodeError;
import org.smoothbuild.problem.CodeLocation;

public class UndefinedFunctionError extends CodeError {
  public UndefinedFunctionError(CodeLocation codeLocation, String name) {
    super(codeLocation, "Undefined function  '" + name + "'");
  }
}
