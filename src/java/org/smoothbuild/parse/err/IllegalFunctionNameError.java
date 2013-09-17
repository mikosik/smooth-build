package org.smoothbuild.parse.err;

import org.smoothbuild.message.CodeError;
import org.smoothbuild.message.CodeLocation;

public class IllegalFunctionNameError extends CodeError {
  public IllegalFunctionNameError(CodeLocation codeLocation, String name) {
    super(codeLocation, "Illegal function name '" + name + "'");
  }
}
