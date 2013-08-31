package org.smoothbuild.parse.err;

import org.smoothbuild.problem.CodeError;
import org.smoothbuild.problem.SourceLocation;

public class CycleInCallGraphError extends CodeError {
  public CycleInCallGraphError(SourceLocation sourceLocation, String cycle) {
    super(sourceLocation, "Function call graph contains cycle:\n" + cycle);
  }
}
