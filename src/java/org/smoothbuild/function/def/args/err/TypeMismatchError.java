package org.smoothbuild.function.def.args.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.def.args.Argument;
import org.smoothbuild.message.message.CodeMessage;

public class TypeMismatchError extends CodeMessage {
  public TypeMismatchError(Argument argument, Type type) {
    super(ERROR, argument.codeLocation(), "Type mismatch, cannot convert from "
        + argument.type().name() + " to " + type.name());
  }
}
