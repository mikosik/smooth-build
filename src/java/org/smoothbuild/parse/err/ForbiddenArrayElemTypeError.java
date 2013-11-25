package org.smoothbuild.parse.err;

import static org.smoothbuild.lang.type.STypes.allowedForArrayElem;
import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.lang.type.SType;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;

public class ForbiddenArrayElemTypeError extends CodeMessage {
  public ForbiddenArrayElemTypeError(CodeLocation codeLocation, SType<?> type) {
    super(ERROR, codeLocation, "Array cannot contain element of type " + type
        + ". Only following types are allowed: " + allowedForArrayElem());
  }
}
