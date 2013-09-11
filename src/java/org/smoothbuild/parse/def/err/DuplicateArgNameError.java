package org.smoothbuild.parse.def.err;

import org.smoothbuild.parse.def.Argument;
import org.smoothbuild.problem.CodeError;

public class DuplicateArgNameError extends CodeError {
  public DuplicateArgNameError(Argument argument) {
    super(argument.codeLocation(), "Duplicated argument name = " + argument.name());
  }
}
