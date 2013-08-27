package org.smoothbuild.parse.err;

import org.smoothbuild.function.base.QualifiedName;
import org.smoothbuild.parse.Argument;
import org.smoothbuild.problem.Error;

public class UnknownParamNameProblem extends Error {
  public UnknownParamNameProblem(QualifiedName name, Argument argument) {
    super(argument.sourceLocation(), "Function " + name + " has no parameter with name = "
        + argument.name());
  }
}
