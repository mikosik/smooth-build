package org.smoothbuild.parse.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.CodeMessage;

public class ForbiddenSetElemTypeError extends CodeMessage {
  public ForbiddenSetElemTypeError(CodeLocation codeLocation, Type type) {
    super(ERROR, codeLocation, "Set cannot contain element of type " + type
        + ". Only following types are allowed: " + Type.allowedForSetElem());
  }
}
