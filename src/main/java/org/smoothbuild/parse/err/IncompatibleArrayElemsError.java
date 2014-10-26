package org.smoothbuild.parse.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.lang.base.Type;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;

@SuppressWarnings("serial")
public class IncompatibleArrayElemsError extends CodeMessage {
  public IncompatibleArrayElemsError(CodeLocation location, Type<?> firstElemType, int index,
      Type<?> indexElemType) {
    super(ERROR, location, "Array cannot contain elements of incompatible types.\n"
        + "First element has type " + firstElemType + " while element at index " + index
        + " has type " + indexElemType + ".");
  }
}
