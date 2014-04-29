package org.smoothbuild.lang.function.def.args.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.def.args.Arg;
import org.smoothbuild.message.base.CodeMessage;

@SuppressWarnings("serial")
public class UnknownParamNameError extends CodeMessage {
  public UnknownParamNameError(Name name, Arg arg) {
    super(ERROR, arg.codeLocation(), "Function " + name + " has no parameter named '" + arg.name()
        + "'.");
  }
}
