package org.smoothbuild.parse.err;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.problem.CodeError;
import org.smoothbuild.problem.CodeLocation;

public class IncompatibleSetElemsError extends CodeError {
  public IncompatibleSetElemsError(CodeLocation location, Type firstElemType, int index,
      Type indexElemType) {
    super(location, "Set cannot contain elements of different types.\n" + "First element has type "
        + firstElemType + " while element at index " + index + " has type " + indexElemType + ".");
  }
}
