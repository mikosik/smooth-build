package org.smoothbuild.parse.err;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.ErrorCodeMessage;
import org.smoothbuild.message.message.CodeLocation;

public class ForbiddenSetElemTypeError extends ErrorCodeMessage {
  public ForbiddenSetElemTypeError(CodeLocation codeLocation, Type type) {
    super(codeLocation, "Set cannot contain element of type " + type
        + ". Only following types are allowed: " + Type.allowedForSetElem());
  }
}
