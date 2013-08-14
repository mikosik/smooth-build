package org.smoothbuild.parse.err;

import static org.smoothbuild.problem.ProblemType.ERROR;

import org.smoothbuild.problem.Problem;
import org.smoothbuild.problem.SourceLocation;

public class IllegalFunctionNameError extends Problem {
  public IllegalFunctionNameError(SourceLocation sourceLocation, String name) {
    super(ERROR, sourceLocation, "Illegal function name '" + name + "'");
  }
}
