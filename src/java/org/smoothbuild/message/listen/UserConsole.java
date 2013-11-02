package org.smoothbuild.message.listen;

import static org.smoothbuild.message.message.MessageType.ERROR;

import java.io.PrintStream;
import java.util.Iterator;

import javax.inject.Singleton;

import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.MessageStats;
import org.smoothbuild.message.message.MessageType;

import com.google.common.base.Splitter;

@Singleton
public class UserConsole {
  private static final String GROUP_PREFIX = " + ";
  private static final String MESSAGE_FIRST_LINE_PREFIX = "  " + GROUP_PREFIX;
  private static final String MESSAGE_OTHER_LINES_PREFIX = "     ";

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
    println(GROUP_PREFIX + messageGroup.name());
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
      println(GROUP_PREFIX + "FAILED :(");
      printMessageStats();
    } else {
      println(GROUP_PREFIX + "SUCCESS :)");
      printMessageStats();
    }
  }

  private void printMessageStats() {
    for (MessageType messageType : MessageType.values()) {
      int count = messageStats.getCount(messageType);
      if (0 < count) {
        println(MESSAGE_FIRST_LINE_PREFIX + count + " " + messageType.namePlural());
      }
    }
  }

  protected void report(Message message) {
    Iterator<String> it = Splitter.on("\n").split(message.toString()).iterator();

    print(MESSAGE_FIRST_LINE_PREFIX);
    println(it.next());

    while (it.hasNext()) {
      print(MESSAGE_OTHER_LINES_PREFIX);
      println(it.next());
    }
  }

  private void println(String line) {
    printStream.println(line);
  }

  private void print(String line) {
    printStream.print(line);
  }
}
