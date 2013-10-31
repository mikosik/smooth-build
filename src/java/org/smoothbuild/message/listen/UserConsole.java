package org.smoothbuild.message.listen;

import java.io.PrintStream;

import javax.inject.Singleton;

import org.smoothbuild.message.message.Message;

import com.google.common.base.Splitter;

@Singleton
public class UserConsole {
  private static final String MESSAGE_GROUP_PREFIX = " + ";
  private static final String MESSAGE_PREFIX = "  " + MESSAGE_GROUP_PREFIX;

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
    println(MESSAGE_GROUP_PREFIX + messageGroup.name());
    printGroup(messageGroup);

    isErrorReported = isErrorReported || messageGroup.containsErrors();
  }

  private void printGroup(MessageGroup messageGroup) {
    for (Message message : messageGroup) {
      report(message);
    }
  }

  public boolean isErrorReported() {
    return isErrorReported;
  }

  public void printFinalSummary() {
    if (isErrorReported) {
      println(MESSAGE_GROUP_PREFIX + "FAILED :(");
    } else {
      println(MESSAGE_GROUP_PREFIX + "SUCCESS :)");
    }
  }

  protected void report(Message message) {
    for (String line : Splitter.on("\n").split(message.toString())) {
      print(MESSAGE_PREFIX);
      println(line);
    }
  }

  private void println(String line) {
    printStream.println(line);
  }

  private void print(String line) {
    printStream.print(line);
  }
}
