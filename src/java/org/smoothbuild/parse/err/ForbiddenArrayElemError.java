package org.smoothbuild.parse.err;

import static org.smoothbuild.lang.type.STypes.basicTypes;
import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.lang.type.SType;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;

@SuppressWarnings("serial")
public class ForbiddenArrayElemError extends CodeMessage {
  public ForbiddenArrayElemError(CodeLocation codeLocation, SType<?> type) {
    super(ERROR, codeLocation, "Array cannot contain element with type " + type
        + ". Only following types are allowed: " + basicTypes());
  }
}
