package org.smoothbuild.problem;

import static org.smoothbuild.problem.MessageType.WARNING;

public class CodeWarning extends CodeMessage {
  public CodeWarning(CodeLocation codeLocation, String message) {
    super(WARNING, codeLocation, message);
  }
}
