package org.smoothbuild.parse.err;

import org.smoothbuild.problem.CodeError;
import org.smoothbuild.problem.CodeLocation;

public class IllegalFunctionNameError extends CodeError {
  public IllegalFunctionNameError(CodeLocation codeLocation, String name) {
    super(codeLocation, "Illegal function name '" + name + "'");
  }
}
