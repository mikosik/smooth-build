package org.smoothbuild.parse.err;

import org.smoothbuild.message.message.ErrorCodeMessage;
import org.smoothbuild.message.message.CodeLocation;

public class IllegalFunctionNameError extends ErrorCodeMessage {
  public IllegalFunctionNameError(CodeLocation codeLocation, String name) {
    super(codeLocation, "Illegal function name '" + name + "'");
  }
}
