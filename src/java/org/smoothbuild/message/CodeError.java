package org.smoothbuild.message;

public class CodeError extends CodeMessage {
  public CodeError(CodeLocation codeLocation, String message) {
    super(MessageType.ERROR, codeLocation, message);
  }
}
