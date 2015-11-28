package org.smoothbuild.lang.message;

public class Messages {
  public static boolean containsErrors(Iterable<Message> messages) {
    for (Message message : messages) {
      if (message instanceof ErrorMessage) {
        return true;
      }
    }
    return false;
  }
}
