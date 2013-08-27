package org.smoothbuild.parse.err;

import org.smoothbuild.parse.Argument;
import org.smoothbuild.problem.Error;

public class NoParamAssignableFromImplicitArgProblem extends Error {
  public NoParamAssignableFromImplicitArgProblem(Argument argument) {
    super(argument.sourceLocation(),
        "No parameter is assignable from implicit argument with type = "
            + argument.definitionNode().type().name());
  }
}
