package org.smoothbuild.message.base;

import static org.smoothbuild.message.base.MessageType.ERROR;

public class Messages {
  public static boolean containsErrors(Iterable<Message> messages) {
    for (Message message : messages) {
      if (message.type() == ERROR) {
        return true;
      }
    }
    return false;
  }
}
