package org.smoothbuild.parse.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;

public class ForbiddenSetElemTypeError extends CodeMessage {
  public ForbiddenSetElemTypeError(CodeLocation codeLocation, Type type) {
    super(ERROR, codeLocation, "Set cannot contain element of type " + type
        + ". Only following types are allowed: " + Type.allowedForSetElem());
  }
}
