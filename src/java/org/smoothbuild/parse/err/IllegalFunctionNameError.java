package org.smoothbuild.parse.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.CodeMessage;

public class IllegalFunctionNameError extends CodeMessage {
  public IllegalFunctionNameError(CodeLocation codeLocation, String name) {
    super(ERROR, codeLocation, "Illegal function name '" + name + "'");
  }
}
