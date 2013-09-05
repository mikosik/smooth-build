package org.smoothbuild.parse.err;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.problem.CodeError;
import org.smoothbuild.problem.CodeLocation;

public class ForbiddenSetElemTypeError extends CodeError {
  public ForbiddenSetElemTypeError(CodeLocation codeLocation, Type type) {
    super(codeLocation, "Set cannot contain element of type " + type
        + ". Only following types are allowed: " + Type.allowedForSetElem());
  }
}
