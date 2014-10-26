package org.smoothbuild.lang.function.def.args.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.def.args.Argument;
import org.smoothbuild.message.base.CodeMessage;

@SuppressWarnings("serial")
public class UnknownParamNameError extends CodeMessage {
  public UnknownParamNameError(Name name, Argument argument) {
    super(ERROR, argument.codeLocation(), "Function " + name + " has no parameter named '" + argument.name()
        + "'.");
  }
}
