package org.smoothbuild.parse.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.CodeMessage;

public class UndefinedFunctionError extends CodeMessage {
  public UndefinedFunctionError(CodeLocation codeLocation, String name) {
    super(ERROR, codeLocation, "Undefined function  '" + name + "'");
  }
}
