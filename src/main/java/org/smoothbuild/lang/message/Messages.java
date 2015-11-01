package org.smoothbuild.lang.message;

import static org.smoothbuild.lang.message.MessageType.ERROR;

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
