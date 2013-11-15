package org.smoothbuild.parse.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;

public class UnknownFunctionCallError extends CodeMessage {
  public UnknownFunctionCallError(CodeLocation codeLocation, Name name) {
    super(ERROR, codeLocation, "Call to unknown function " + name);
  }
}
