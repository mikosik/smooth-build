package org.smoothbuild.parse.err;

import org.smoothbuild.message.message.CodeError;
import org.smoothbuild.message.message.CodeLocation;

@SuppressWarnings("serial")
public class IllegalFunctionNameError extends CodeError {
  public IllegalFunctionNameError(CodeLocation codeLocation, String name) {
    super(codeLocation, "Illegal function name '" + name + "'");
  }
}
