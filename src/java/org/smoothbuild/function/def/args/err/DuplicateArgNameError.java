package org.smoothbuild.function.def.args.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.function.def.args.Argument;
import org.smoothbuild.message.base.CodeMessage;

public class DuplicateArgNameError extends CodeMessage {
  public DuplicateArgNameError(Argument argument) {
    super(ERROR, argument.codeLocation(), "Duplicated argument name = " + argument.name());
  }
}
