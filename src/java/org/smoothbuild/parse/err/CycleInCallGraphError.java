package org.smoothbuild.parse.err;

import org.smoothbuild.message.message.CodeError;
import org.smoothbuild.message.message.CodeLocation;

@SuppressWarnings("serial")
public class CycleInCallGraphError extends CodeError {
  public CycleInCallGraphError(CodeLocation codeLocation, String cycle) {
    super(codeLocation, "Function call graph contains cycle:\n" + cycle);
  }
}
