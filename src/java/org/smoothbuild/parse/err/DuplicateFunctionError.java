package org.smoothbuild.parse.err;

import static org.smoothbuild.problem.ProblemType.ERROR;

import org.smoothbuild.problem.Problem;
import org.smoothbuild.problem.SourceLocation;

public class DuplicateFunctionError extends Problem {
  public DuplicateFunctionError(SourceLocation sourceLocation, String name) {
    super(ERROR, sourceLocation, "Duplicate function '" + name + "'");
  }
}
