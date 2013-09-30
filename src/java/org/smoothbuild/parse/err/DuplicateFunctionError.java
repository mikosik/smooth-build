package org.smoothbuild.parse.err;

import static org.smoothbuild.message.listen.MessageType.ERROR;

import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.CodeMessage;

public class DuplicateFunctionError extends CodeMessage {
  public DuplicateFunctionError(CodeLocation codeLocation, String name) {
    super(ERROR, codeLocation, "Duplicate function '" + name + "'");
  }
}
