package org.smoothbuild.message.base;

public class Messages {
  public static boolean containsProblems(Iterable<Message> messages) {
    for (Message message : messages) {
      if (message.type().isProblem()) {
        return true;
      }
    }
    return false;
  }
}
