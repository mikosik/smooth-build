package org.smoothbuild.parse.err;

import org.smoothbuild.parse.Argument;
import org.smoothbuild.problem.CodeError;

public class ManyAmbigiousParamsAssignableFromImplicitArgProblem extends CodeError {
  public ManyAmbigiousParamsAssignableFromImplicitArgProblem(Argument argument) {
    super(argument.sourceLocation(),
        "Cannot find unambigiuous param for implicit argument. More than one param has type = "
            + argument.definitionNode().type().name());
  }
}
