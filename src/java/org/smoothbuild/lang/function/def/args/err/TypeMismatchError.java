package org.smoothbuild.lang.function.def.args.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.lang.function.def.args.Arg;
import org.smoothbuild.lang.type.SType;
import org.smoothbuild.message.base.CodeMessage;

public class TypeMismatchError extends CodeMessage {
  public TypeMismatchError(Arg arg, SType<?> type) {
    super(ERROR, arg.codeLocation(), "Type mismatch, cannot convert from "
        + arg.type().name() + " to " + type.name());
  }
}
