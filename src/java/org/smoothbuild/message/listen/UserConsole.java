package org.smoothbuild.message.listen;

import java.io.PrintStream;

import javax.inject.Singleton;

import org.smoothbuild.message.message.Message;

@Singleton
public class UserConsole {
  private final PrintStream printStream;

  public UserConsole() {
    this(System.out);
  }

  public UserConsole(PrintStream printStream) {
    this.printStream = printStream;
  }

  public void report(MessageGroup messageGroup) {
    String status = messageGroup.containsErrors() ? " FAILED" : "";
    printStream.println("[" + messageGroup.name() + "]" + status);

    for (Message message : messageGroup) {
      printStream.println(message.toString());
    }
  }
}
