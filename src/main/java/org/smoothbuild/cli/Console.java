package org.smoothbuild.cli;

import static org.smoothbuild.lang.message.MessageType.ERROR;

import java.io.PrintStream;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.message.MessageStats;
import org.smoothbuild.lang.message.MessageType;

import com.google.common.base.Splitter;

@Singleton
public class Console {
  public static final int MESSAGE_GROUP_NAME_HEADER_LENGTH = 73;

  private static final String GROUP_PREFIX = " + ";
  private static final String MESSAGE_FIRST_LINE_PREFIX = "  " + GROUP_PREFIX;
  private static final String MESSAGE_OTHER_LINES_PREFIX = "     ";

  private final PrintStream printStream;
  private final MessageStats messageStats;

  @Inject
  public Console() {
    this(System.out);
  }

  public Console(PrintStream printStream) {
    this.printStream = printStream;
    this.messageStats = new MessageStats();
  }

  public void error(CodeLocation location, String message) {
    println("build.smooth:" + location.line() + ": error: " + message);
    messageStats.incCount(ERROR);
  }

  public void error(String message) {
    println("error: " + message);
    messageStats.incCount(ERROR);
  }

  public void print(String header, Iterable<? extends Message> messages) {
    println(GROUP_PREFIX + header);
    print(messages);
  }

  private void print(Iterable<? extends Message> messages) {
    for (Message message : messages) {
      print(message);
      messageStats.incCount(message.type());
    }
  }

  public boolean isErrorReported() {
    return messageStats.containsErrors();
  }

  public void printFinalSummary() {
    String message = isErrorReported() ? "FAILED :(" : "SUCCESS :)";
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
