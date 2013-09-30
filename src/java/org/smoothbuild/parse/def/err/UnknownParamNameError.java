package org.smoothbuild.parse.def.err;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.message.ErrorCodeMessage;
import org.smoothbuild.parse.def.Argument;

public class UnknownParamNameError extends ErrorCodeMessage {
  public UnknownParamNameError(Name name, Argument argument) {
    super(argument.codeLocation(), "Function " + name + " has no parameter named '"
        + argument.name() + "'.");
  }
}
