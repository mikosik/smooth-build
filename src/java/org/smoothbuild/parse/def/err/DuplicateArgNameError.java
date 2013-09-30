package org.smoothbuild.parse.def.err;

import static org.smoothbuild.message.listen.MessageType.ERROR;

import org.smoothbuild.message.message.CodeMessage;
import org.smoothbuild.parse.def.Argument;

public class DuplicateArgNameError extends CodeMessage {
  public DuplicateArgNameError(Argument argument) {
    super(ERROR, argument.codeLocation(), "Duplicated argument name = " + argument.name());
  }
}
