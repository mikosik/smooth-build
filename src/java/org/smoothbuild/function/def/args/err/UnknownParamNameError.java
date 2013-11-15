package org.smoothbuild.function.def.args.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.function.def.args.Argument;
import org.smoothbuild.message.base.CodeMessage;

public class UnknownParamNameError extends CodeMessage {
  public UnknownParamNameError(Name name, Argument argument) {
    super(ERROR, argument.codeLocation(), "Function " + name + " has no parameter named '"
        + argument.name() + "'.");
  }
}
