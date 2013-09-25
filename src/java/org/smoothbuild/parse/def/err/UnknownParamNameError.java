package org.smoothbuild.parse.def.err;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.CodeError;
import org.smoothbuild.parse.def.Argument;

@SuppressWarnings("serial")
public class UnknownParamNameError extends CodeError {
  public UnknownParamNameError(Name name, Argument argument) {
    super(argument.codeLocation(), "Function " + name + " has no parameter named '"
        + argument.name() + "'.");
  }
}
