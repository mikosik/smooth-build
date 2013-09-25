package org.smoothbuild.parse.err;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.CodeError;
import org.smoothbuild.message.CodeLocation;

@SuppressWarnings("serial")
public class ForbiddenSetElemTypeError extends CodeError {
  public ForbiddenSetElemTypeError(CodeLocation codeLocation, Type type) {
    super(codeLocation, "Set cannot contain element of type " + type
        + ". Only following types are allowed: " + Type.allowedForSetElem());
  }
}
