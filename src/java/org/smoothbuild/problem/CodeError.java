package org.smoothbuild.problem;

public class CodeError extends CodeMessage {
  public CodeError(CodeLocation codeLocation, String message) {
    super(MessageType.ERROR, codeLocation, message);
  }
}
