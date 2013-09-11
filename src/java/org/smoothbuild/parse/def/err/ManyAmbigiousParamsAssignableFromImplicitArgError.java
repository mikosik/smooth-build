package org.smoothbuild.parse.def.err;

import org.smoothbuild.parse.def.Argument;
import org.smoothbuild.problem.CodeError;

public class ManyAmbigiousParamsAssignableFromImplicitArgError extends CodeError {
  public ManyAmbigiousParamsAssignableFromImplicitArgError(Argument argument) {
    super(argument.codeLocation(),
        "Cannot find unambigiuous param for implicit argument. More than one param has type = "
            + argument.definitionNode().type().name());
  }
}
