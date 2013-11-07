package org.smoothbuild.parse.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.CodeMessage;

public class UndefinedFunctionError extends CodeMessage {
  public UndefinedFunctionError(CodeLocation codeLocation, Name name) {
    super(ERROR, codeLocation, "Undefined function " + name);
  }
}
