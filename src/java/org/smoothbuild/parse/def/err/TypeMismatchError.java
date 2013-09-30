package org.smoothbuild.parse.def.err;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.ErrorCodeMessage;
import org.smoothbuild.parse.def.Argument;

public class TypeMismatchError extends ErrorCodeMessage {
  public TypeMismatchError(Argument argument, Type type) {
    super(argument.codeLocation(), "Type mismatch, cannot convert from " + argument.type().name()
        + " to " + type.name());
  }
}
