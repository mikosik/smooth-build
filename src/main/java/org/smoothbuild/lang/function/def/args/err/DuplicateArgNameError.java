package org.smoothbuild.lang.function.def.args.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.lang.function.def.args.Argument;
import org.smoothbuild.message.base.CodeMessage;

@SuppressWarnings("serial")
public class DuplicateArgNameError extends CodeMessage {
  public DuplicateArgNameError(Argument argument) {
    super(ERROR, argument.codeLocation(), "Duplicated argument name = " + argument.name());
  }
}
