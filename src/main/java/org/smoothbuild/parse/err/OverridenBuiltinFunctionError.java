package org.smoothbuild.parse.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;

@SuppressWarnings("serial")
public class OverridenBuiltinFunctionError extends CodeMessage {
  public OverridenBuiltinFunctionError(CodeLocation codeLocation, Name name) {
    super(ERROR, codeLocation, "Function " + name
        + " cannot override builtin function with the same name.");
  }
}
