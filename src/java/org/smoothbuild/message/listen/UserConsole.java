package org.smoothbuild.message.listen;

import static org.smoothbuild.message.message.MessageType.ERROR;

import java.io.PrintStream;

import javax.inject.Singleton;

import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.MessageStats;
import org.smoothbuild.message.message.MessageType;

import com.google.common.base.Splitter;

@Singleton
public class UserConsole {
  private static final String MESSAGE_GROUP_PREFIX = " + ";
  private static final String MESSAGE_PREFIX = "  " + MESSAGE_GROUP_PREFIX;

  private final PrintStream printStream;
  private final MessageStats messageStats;

  public UserConsole() {
    this(System.out);
  }

  public UserConsole(PrintStream printStream) {
    this.printStream = printStream;
    this.messageStats = new MessageStats();
  }

  public void report(MessageGroup messageGroup) {
    println(MESSAGE_GROUP_PREFIX + messageGroup.name());
    printGroup(messageGroup);

    messageStats.add(messageGroup.messageStats());
  }

  private void printGroup(MessageGroup messageGroup) {
    for (Message message : messageGroup) {
      report(message);
    }
  }

  public boolean isErrorReported() {
    return 0 < messageStats.getCount(ERROR);
  }

  public void printFinalSummary() {
    if (isErrorReported()) {
      println(MESSAGE_GROUP_PREFIX + "FAILED :(");
      printMessageStats();
    } else {
      println(MESSAGE_GROUP_PREFIX + "SUCCESS :)");
      printMessageStats();
    }
  }

  private void printMessageStats() {
    for (MessageType messageType : MessageType.values()) {
      int count = messageStats.getCount(messageType);
      if (0 < count) {
        println(MESSAGE_PREFIX + count + " " + messageType.namePlural());
      }
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
