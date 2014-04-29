package org.smoothbuild.lang.function.def.args.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.lang.function.def.args.Arg;
import org.smoothbuild.message.base.CodeMessage;

@SuppressWarnings("serial")
public class DuplicateArgNameError extends CodeMessage {
  public DuplicateArgNameError(Arg arg) {
    super(ERROR, arg.codeLocation(), "Duplicated argument name = " + arg.name());
  }
}
