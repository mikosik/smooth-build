package org.smoothbuild.parse.err;

import org.smoothbuild.parse.Argument;
import org.smoothbuild.problem.CodeError;

public class NoParamAssignableFromImplicitArgProblem extends CodeError {
  public NoParamAssignableFromImplicitArgProblem(Argument argument) {
    super(argument.codeLocation(),
        "No parameter is assignable from implicit argument with type = "
            + argument.definitionNode().type().name());
  }
}
