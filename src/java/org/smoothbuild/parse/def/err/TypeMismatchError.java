package org.smoothbuild.parse.def.err;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeError;
import org.smoothbuild.parse.def.Argument;

public class TypeMismatchError extends CodeError {
  public TypeMismatchError(Argument argument, Type type) {
    super(argument.codeLocation(), "Type mismatch, cannot convert from " + argument.type().name()
        + " to " + type.name());
  }
}
