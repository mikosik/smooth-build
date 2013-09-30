package org.smoothbuild.parse.def.err;

import static org.smoothbuild.function.base.Type.VOID;
import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.CodeMessage;
import org.smoothbuild.parse.def.Argument;

public class VoidArgError extends CodeMessage {
  public VoidArgError(Argument argument) {
    super(ERROR, argument.codeLocation(), "Arguments with " + VOID.name()
        + " type are not allowed.");
  }
}
