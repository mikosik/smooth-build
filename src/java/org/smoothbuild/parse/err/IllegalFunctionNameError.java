package org.smoothbuild.parse.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;

public class IllegalFunctionNameError extends CodeMessage {
  public IllegalFunctionNameError(CodeLocation codeLocation, String name) {
    super(ERROR, codeLocation, "Illegal function name '" + name + "'");
  }
}
