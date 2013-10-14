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
    println("[" + messageGroup.name() + "]" + status);

    isErrorReported = isErrorReported || messageGroup.containsErrors();

    printGroup(messageGroup);
  }

  private void printGroup(MessageGroup messageGroup) {
    boolean printedAnything = false;
    for (Message message : messageGroup) {
      report(message);
      printedAnything = true;
    }
    if (printedAnything) {
      println("");
    }
  }

  public boolean isErrorReported() {
    return isErrorReported;
  }

  public void printFinalSummary() {
    if (isErrorReported) {
      println("*** FAILED ***");
    } else {
      println("*** SUCCESS ***");
    }
  }

  protected void report(Message message) {
    println(message.toString());
  }

  private void println(String line) {
    printStream.println(line);
  }
}
