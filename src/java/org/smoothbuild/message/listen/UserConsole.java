package org.smoothbuild.message.listen;

import java.io.PrintStream;
import java.util.Iterator;

import javax.inject.Singleton;

import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.base.MessageStats;
import org.smoothbuild.message.base.MessageType;

import com.google.common.base.Splitter;

@Singleton
public class UserConsole {
  public static final int MESSAGE_GROUP_NAME_HEADER_LENGTH = 73;

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

  public void print(String header, LoggedMessages loggedMessages) {
    println(GROUP_PREFIX + header);
    print(loggedMessages);
    messageStats.add(loggedMessages.stats());
  }

  private void print(LoggedMessages loggedMessages) {
    for (Message message : loggedMessages) {
      print(message);
    }
  }

  public boolean isProblemReported() {
    return messageStats.containsProblems();
  }

  public void printFinalSummary() {
    String message = isProblemReported() ? "FAILED :(" : "SUCCESS :)";
    println(GROUP_PREFIX + message);
    printMessageStats();
  }

  private void printMessageStats() {
    for (MessageType messageType : MessageType.values()) {
      int count = messageStats.getCount(messageType);
      if (0 < count) {
        println(MESSAGE_FIRST_LINE_PREFIX + count + " " + messageType.namePlural());
      }
    }
  }

  protected void print(Message message) {
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
