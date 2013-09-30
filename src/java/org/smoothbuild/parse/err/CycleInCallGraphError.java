package org.smoothbuild.parse.err;

import org.smoothbuild.message.message.ErrorCodeMessage;
import org.smoothbuild.message.message.CodeLocation;

public class CycleInCallGraphError extends ErrorCodeMessage {
  public CycleInCallGraphError(CodeLocation codeLocation, String cycle) {
    super(codeLocation, "Function call graph contains cycle:\n" + cycle);
  }
}
