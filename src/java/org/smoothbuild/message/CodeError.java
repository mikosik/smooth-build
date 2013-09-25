package org.smoothbuild.message;

@SuppressWarnings("serial")
public class CodeError extends CodeMessage {
  public CodeError(CodeLocation codeLocation, String message) {
    super(MessageType.ERROR, codeLocation, message);
  }
}
