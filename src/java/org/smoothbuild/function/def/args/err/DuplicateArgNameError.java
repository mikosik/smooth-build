package org.smoothbuild.function.def.args.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.function.def.args.Argument;
import org.smoothbuild.message.message.CodeMessage;

public class DuplicateArgNameError extends CodeMessage {
  public DuplicateArgNameError(Argument argument) {
    super(ERROR, argument.codeLocation(), "Duplicated argument name = " + argument.name());
  }
}
