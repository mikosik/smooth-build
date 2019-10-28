package org.smoothbuild.cli;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.lang.object.base.Messages.ERROR;
import static org.smoothbuild.lang.object.base.Messages.INFO;
import static org.smoothbuild.lang.object.base.Messages.WARNING;
import static org.smoothbuild.lang.object.base.Messages.severity;
import static org.smoothbuild.lang.object.base.Messages.text;

import java.io.PrintStream;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.Struct;

import com.google.common.base.Splitter;

@Singleton
public class Console {
  public static final int MESSAGE_GROUP_NAME_HEADER_LENGTH = 73;

  private static final String GROUP_PREFIX = " + ";
  private static final String MESSAGE_FIRST_LINE_PREFIX = "  " + GROUP_PREFIX;
  private static final String MESSAGE_OTHER_LINES_PREFIX = "     ";

  private final PrintStream printStream;
  private int failureCount;
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

  public void errors(java.util.List<?> errors) {
    errors.forEach(this::error);
  }

  public void error(Object error) {
    println(error.toString());
    errorCount++;
  }

  public void error(String message) {
    println("error: " + message);
    errorCount++;
  }

  public void print(String header, Array messages) {
    println(GROUP_PREFIX + header);
    print(messages);
  }

  public void print(String header, Exception failure) {
    println(GROUP_PREFIX + header);
    print(getStackTraceAsString(failure));
    failureCount++;
  }

  private void print(Array messages) {
    for (Struct message : messages.asIterable(Struct.class)) {
      String severity = severity(message);
      printMultiline(severity + ": " + text(message));
      switch (severity) {
        case ERROR:
          errorCount++;
          break;
        case WARNING:
          warningCount++;
          break;
        case INFO:
          infoCount++;
          break;
        default:
          throw new RuntimeException("Unknown message severity: " + severity);
      }
    }
  }

  public boolean isProblemReported() {
    return failureCount != 0 || errorCount != 0;
  }

  public void printFinalSummary() {
    printStat(failureCount, "failure(s)");
    printStat(errorCount, "error(s)");
    printStat(warningCount, "warning(s)");
    printStat(infoCount, "info(s)");
  }

  private void printStat(int count, String messageType) {
    if (count != 0) {
      println(MESSAGE_FIRST_LINE_PREFIX + count + " " + messageType);
    }
  }

  protected void printMultiline(String text) {
    Iterator<String> it = Splitter.on("\n").split(text).iterator();

    print(MESSAGE_FIRST_LINE_PREFIX);
    println(it.next());

    while (it.hasNext()) {
      print(MESSAGE_OTHER_LINES_PREFIX);
      println(it.next());
    }
  }

  public void println(String line) {
    printStream.println(line);
  }

  public void print(String line) {
    printStream.print(line);
  }
}
