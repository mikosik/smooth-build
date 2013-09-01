package org.smoothbuild.parse.err;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.parse.Argument;
import org.smoothbuild.problem.CodeError;

public class UnknownParamNameProblem extends CodeError {
  public UnknownParamNameProblem(Name name, Argument argument) {
    super(argument.codeLocation(), "Function " + name + " has no parameter with name = "
        + argument.name());
  }
}
