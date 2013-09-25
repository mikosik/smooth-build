package org.smoothbuild.message;

import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("serial")
public class CodeMessage extends Message {
  private final CodeLocation codeLocation;

  public CodeMessage(MessageType type, CodeLocation codeLocation, String message) {
    super(type, message);
    this.codeLocation = checkNotNull(codeLocation);
  }

  @Override
  public String toString() {
    return type().toString() + codeLocation.toString() + ": " + message();
  }
}
