package org.smoothbuild.parse.def.err;

import org.smoothbuild.parse.def.Argument;
import org.smoothbuild.problem.CodeError;

public class NoParamAssignableFromImplicitArgError extends CodeError {
  public NoParamAssignableFromImplicitArgError(Argument argument) {
    super(argument.codeLocation(),
        "No parameter is assignable from implicit argument with type = "
            + argument.definitionNode().type().name());
  }
}
