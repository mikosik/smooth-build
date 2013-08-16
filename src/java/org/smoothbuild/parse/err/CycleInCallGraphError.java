package org.smoothbuild.parse.err;

import org.smoothbuild.problem.Error;
import org.smoothbuild.problem.SourceLocation;

public class CycleInCallGraphError extends Error {
  public CycleInCallGraphError(SourceLocation sourceLocation, String cycle) {
    super(sourceLocation, "Function call graph contains cycle:\n" + cycle);
  }
}
