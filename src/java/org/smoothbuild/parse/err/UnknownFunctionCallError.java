package org.smoothbuild.parse.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.CodeMessage;

public class UnknownFunctionCallError extends CodeMessage {
  public UnknownFunctionCallError(CodeLocation codeLocation, Name name) {
    super(ERROR, codeLocation, "Call to unknown function " + name);
  }
}
