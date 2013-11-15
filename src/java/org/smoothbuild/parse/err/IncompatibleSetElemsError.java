package org.smoothbuild.parse.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;

public class IncompatibleSetElemsError extends CodeMessage {
  public IncompatibleSetElemsError(CodeLocation location, Type firstElemType, int index,
      Type indexElemType) {
    super(ERROR, location, "Set cannot contain elements of different types.\n"
        + "First element has type " + firstElemType + " while element at index " + index
        + " has type " + indexElemType + ".");
  }
}
