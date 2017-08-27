package org.smoothbuild.cli;

import java.io.PrintStream;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.message.InfoMessage;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.message.WarningMessage;

import com.google.common.base.Splitter;

@Singleton
public class Console {
  public static final int MESSAGE_GROUP_NAME_HEADER_LENGTH = 73;

  private static final String GROUP_PREFIX = " + ";
  private static final String MESSAGE_FIRST_LINE_PREFIX = "  " + GROUP_PREFIX;
  private static final String MESSAGE_OTHER_LINES_PREFIX = "     ";

  private final PrintStream printStream;
  private int errorCount;
  private int warningCount;
  private int infoCount;

  @Inject
  public Console() {
    this(System.out);
  }

  public Console(PrintStream printStream) {
    this.printStream = printStream;
  }

  public void error(Location location, String message) {
    println(errorLine(location, message));
    errorCount++;
  }

  public void rawError(Object error) {
    println(error.toString());
    errorCount++;
  }

  public static String errorLine(Location location, String message) {
    return "build.smooth:" + location.line() + ": error: " + message;
  }

  public void error(String message) {
    println("error: " + message);
    errorCount++;
  }

  public void print(String header, Iterable<? extends Message> messages) {
    println(GROUP_PREFIX + header);
    print(messages);
  }

  private void print(Iterable<? extends Message> messages) {
    for (Message message : messages) {
      print(message);
      incrementCount(message);
    }
  }

  private void incrementCount(Message message) {
    if (message instanceof ErrorMessage) {
      errorCount++;
    } else if (message instanceof WarningMessage) {
      warningCount++;
    } else if (message instanceof InfoMessage) {
      infoCount++;
    } else {
      throw new RuntimeException("Unknown message type: " + message.getClass().getCanonicalName());
    }
  }

  public boolean isErrorReported() {
    return errorCount != 0;
  }

  public void printFinalSummary() {
    String message = isErrorReported() ? "FAILED :(" : "SUCCESS :)";
    println(GROUP_PREFIX + message);
    printMessageStats();
  }

  private void printMessageStats() {
    printStat(errorCount, "error(s)");
    printStat(warningCount, "warning(s)");
    printStat(infoCount, "info(s)");
  }

  private void printStat(int count, String messageType) {
    if (count != 0) {
      println(MESSAGE_FIRST_LINE_PREFIX + count + " " + messageType);
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
