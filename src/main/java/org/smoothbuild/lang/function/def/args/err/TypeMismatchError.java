package org.smoothbuild.lang.function.def.args.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.function.def.args.Arg;
import org.smoothbuild.message.base.CodeMessage;

@SuppressWarnings("serial")
public class TypeMismatchError extends CodeMessage {
  public TypeMismatchError(Arg arg, Type<?> type) {
    super(ERROR, arg.codeLocation(), "Type mismatch, cannot convert from " + arg.type().name()
        + " to " + type.name());
  }
}
