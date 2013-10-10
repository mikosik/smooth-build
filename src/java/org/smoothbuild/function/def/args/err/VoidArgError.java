package org.smoothbuild.function.def.args.err;

import static org.smoothbuild.function.base.Type.VOID;
import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.function.def.args.Argument;
import org.smoothbuild.message.message.CodeMessage;

public class VoidArgError extends CodeMessage {
  public VoidArgError(Argument argument) {
    super(ERROR, argument.codeLocation(), "Arguments with " + VOID.name()
        + " type are not allowed.");
  }
}
