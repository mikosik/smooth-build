package org.smoothbuild.message.listen;

import java.io.PrintStream;

import javax.inject.Singleton;

import org.smoothbuild.message.message.Message;

@Singleton
public class UserConsole {
  private final PrintStream printStream;
  private boolean isErrorReported;

  public UserConsole() {
    this(System.out);
  }

  public UserConsole(PrintStream printStream) {
    this.printStream = printStream;
    this.isErrorReported = false;
  }

  public void report(MessageGroup messageGroup) {
    String status = messageGroup.containsErrors() ? " FAILED" : "";
    print("[" + messageGroup.name() + "]" + status);

    isErrorReported = isErrorReported || messageGroup.containsErrors();

    for (Message message : messageGroup) {
      printStream.println(message.toString());
    }
  }

  public boolean isErrorReported() {
    return isErrorReported;
  }

  public void printFinalSummary() {
    if (isErrorReported) {
      print("*** FAILED ***");
    } else {
      print("*** SUCCESS ***");
    }
  }

  private void print(String line) {
    printStream.println(line);
  }
}
