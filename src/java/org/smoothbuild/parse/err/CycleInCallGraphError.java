package org.smoothbuild.parse.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;

public class CycleInCallGraphError extends CodeMessage {
  public CycleInCallGraphError(CodeLocation codeLocation, String cycle) {
    super(ERROR, codeLocation, "Function call graph contains cycle:\n" + cycle);
  }
}
