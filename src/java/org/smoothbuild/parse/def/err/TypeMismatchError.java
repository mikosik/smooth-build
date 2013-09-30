package org.smoothbuild.parse.def.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeMessage;
import org.smoothbuild.parse.def.Argument;

public class TypeMismatchError extends CodeMessage {
  public TypeMismatchError(Argument argument, Type type) {
    super(ERROR, argument.codeLocation(), "Type mismatch, cannot convert from "
        + argument.type().name() + " to " + type.name());
  }
}
