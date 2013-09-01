package org.smoothbuild.parse.err;

import org.smoothbuild.parse.Argument;
import org.smoothbuild.problem.CodeError;

public class DuplicateArgNameProblem extends CodeError {
  public DuplicateArgNameProblem(Argument argument) {
    super(argument.codeLocation(), "Duplicated argument name = " + argument.name());
  }
}
