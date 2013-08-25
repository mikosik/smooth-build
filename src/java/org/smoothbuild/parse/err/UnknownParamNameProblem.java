package org.smoothbuild.parse.err;

import org.smoothbuild.parse.Argument;
import org.smoothbuild.problem.Error;

public class UnknownParamNameProblem extends Error {
  public UnknownParamNameProblem(Argument argument) {
    // TODO fix XXX
    super(argument.sourceLocation(), "Function XXX has no parameter with name = " + argument.name());
  }
}
