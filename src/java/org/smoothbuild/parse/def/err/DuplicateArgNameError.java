package org.smoothbuild.parse.def.err;

import org.smoothbuild.message.message.ErrorCodeMessage;
import org.smoothbuild.parse.def.Argument;

public class DuplicateArgNameError extends ErrorCodeMessage {
  public DuplicateArgNameError(Argument argument) {
    super(argument.codeLocation(), "Duplicated argument name = " + argument.name());
  }
}
