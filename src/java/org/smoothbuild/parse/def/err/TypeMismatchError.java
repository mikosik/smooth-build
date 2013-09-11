package org.smoothbuild.parse.def.err;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.parse.def.Argument;
import org.smoothbuild.problem.CodeError;

public class TypeMismatchError extends CodeError {
  public TypeMismatchError(Argument argument, Type type) {
    super(argument.codeLocation(), "Type mismatch, cannot convert from "
        + argument.definitionNode().type().name() + " to " + type.name());
  }
}
