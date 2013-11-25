package org.smoothbuild.lang.function.def.args.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.lang.function.def.args.Argument;
import org.smoothbuild.lang.type.SType;
import org.smoothbuild.message.base.CodeMessage;

public class TypeMismatchError extends CodeMessage {
  public TypeMismatchError(Argument argument, SType<?> type) {
    super(ERROR, argument.codeLocation(), "Type mismatch, cannot convert from "
        + argument.type().name() + " to " + type.name());
  }
}
