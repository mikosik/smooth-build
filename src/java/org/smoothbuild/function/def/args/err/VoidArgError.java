package org.smoothbuild.function.def.args.err;

import static org.smoothbuild.function.base.Type.VOID;
import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.function.def.args.Argument;
import org.smoothbuild.message.base.CodeMessage;

public class VoidArgError extends CodeMessage {
  public VoidArgError(Argument argument) {
    super(ERROR, argument.codeLocation(), "Arguments with " + VOID.name()
        + " type are not allowed.");
  }
}
