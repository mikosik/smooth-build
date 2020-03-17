package org.smoothbuild.cli;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.lang.object.base.Messages.ERROR;
import static org.smoothbuild.lang.object.base.Messages.INFO;
import static org.smoothbuild.lang.object.base.Messages.WARNING;
import static org.smoothbuild.lang.object.base.Messages.severity;
import static org.smoothbuild.lang.object.base.Messages.text;
import static org.smoothbuild.util.Strings.unlines;

import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.Struct;

/**
 * This class is thread-save.
 */
@Singleton
public class Console {
  public static final int MESSAGE_GROUP_NAME_HEADER_LENGTH = 73;

  private static final String GROUP_PREFIX = " + ";
  private static final String MESSAGE_FIRST_LINE_PREFIX = "  " + GROUP_PREFIX;
  private static final String MESSAGE_OTHER_LINES_PREFIX = "     ";

  private final PrintStream printStream;
  private final AtomicInteger failureCount;
  private final AtomicInteger errorCount;
  private final AtomicInteger warningCount;
  private final AtomicInteger infoCount;

  @Inject
  public Console() {
    this(System.out);
  }

  public Console(PrintStream printStream) {
    this.printStream = printStream;
    this.failureCount = new AtomicInteger();
    this.errorCount = new AtomicInteger();
    this.warningCount = new AtomicInteger();
    this.infoCount = new AtomicInteger();
  }

  public void errors(java.util.List<?> errors) {
    errors.forEach(this::error);
  }

  public void error(Object error) {
    println(error.toString());
    errorCount.incrementAndGet();
  }

  public void error(String message) {
    println("error: " + message);
    errorCount.incrementAndGet();
  }

  public void print(String header, Array messages) {
    print(toTextAndIncreaseCounts(header, messages));
  }

  private String toTextAndIncreaseCounts(String header, Array messages) {
    StringBuilder text = new StringBuilder(GROUP_PREFIX + header + "\n");
    for (Struct message : messages.asIterable(Struct.class)) {
      String severity = severity(message);
      text.append(prefixMultiline(severity + ": " + text(message)));
      increaseCount(severity);
    }
    return text.toString();
  }

  public void print(String header, Throwable failure) {
    print(GROUP_PREFIX + header + "\n" + getStackTraceAsString(failure));
    failureCount.incrementAndGet();
  }

  private void increaseCount(String severity) {
    switch (severity) {
      case ERROR:
        errorCount.incrementAndGet();
        break;
      case WARNING:
        warningCount.incrementAndGet();
        break;
      case INFO:
        infoCount.incrementAndGet();
        break;
      default:
        throw new RuntimeException("Unknown message severity: " + severity);
    }
  }

  public boolean isProblemReported() {
    return failureCount.get() != 0 || errorCount.get() != 0;
  }

  public void printFinalSummary() {
    print(
        statText(failureCount, "failure(s)") +
        statText(errorCount, "error(s)") +
        statText(warningCount, "warning(s)") +
        statText(infoCount, "info(s)"));
  }

  private static String statText(AtomicInteger count, String messageType) {
    int value = count.get();
    if (value != 0) {
      return MESSAGE_FIRST_LINE_PREFIX + value + " " + messageType + "\n";
    } else {
      return "";
    }
  }

  private static String prefixMultiline(String text) {
    String[] lines = text.lines().toArray(String[]::new);
    lines[0] = MESSAGE_FIRST_LINE_PREFIX + lines[0];
    for (int i = 1; i < lines.length; i++) {
      lines[i] = MESSAGE_OTHER_LINES_PREFIX + lines[i];
    }
    return unlines(lines) + "\n";
  }

  public void println(String line) {
    printStream.println(line);
  }

  public void print(String line) {
    printStream.print(line);
  }
}
