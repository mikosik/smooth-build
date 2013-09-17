package org.smoothbuild.parse.err;

import org.smoothbuild.message.CodeError;
import org.smoothbuild.message.CodeLocation;

public class CycleInCallGraphError extends CodeError {
  public CycleInCallGraphError(CodeLocation codeLocation, String cycle) {
    super(codeLocation, "Function call graph contains cycle:\n" + cycle);
  }
}
