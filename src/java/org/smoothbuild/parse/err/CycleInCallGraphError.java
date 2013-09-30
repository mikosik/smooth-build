package org.smoothbuild.parse.err;

import static org.smoothbuild.message.listen.MessageType.ERROR;

import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.CodeMessage;

public class CycleInCallGraphError extends CodeMessage {
  public CycleInCallGraphError(CodeLocation codeLocation, String cycle) {
    super(ERROR, codeLocation, "Function call graph contains cycle:\n" + cycle);
  }
}
