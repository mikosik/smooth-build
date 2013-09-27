package org.smoothbuild.message.message;

import static org.smoothbuild.message.listen.MessageType.WARNING;


@SuppressWarnings("serial")
public class Warning extends Message {
  public Warning(String message) {
    super(WARNING, message);
  }
}
