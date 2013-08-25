package org.smoothbuild.parse.err;

import org.smoothbuild.parse.Argument;
import org.smoothbuild.problem.Error;

public class DuplicateArgNameProblem extends Error {
  public DuplicateArgNameProblem(Argument argument) {
    super(argument.sourceLocation(), "Duplicated argument name = " + argument.name());
  }
}
