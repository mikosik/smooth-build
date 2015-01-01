package org.smoothbuild.lang.function.def.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.lang.function.def.Argument;
import org.smoothbuild.message.base.CodeMessage;

public class DuplicateArgNameError extends CodeMessage {
  public DuplicateArgNameError(Argument argument) {
    super(ERROR, argument.codeLocation(), "Duplicated argument name = " + argument.name());
  }
}
