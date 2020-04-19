package org.smoothbuild.cli.console;

import static org.smoothbuild.util.Strings.unlines;

import java.io.PrintStream;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This class is thread-save.
 */
@Singleton
public class Console {
  public static final int MESSAGE_GROUP_NAME_HEADER_LENGTH = 73;

  private static final String TASK_HEADER_PREFIX = "  ";
  private static final String MESSAGE_FIRST_LINE_PREFIX = "   + ";
  private static final String MESSAGE_OTHER_LINES_PREFIX = "     ";

  private final PrintStream printStream;

  @Inject
  public Console() {
    this(System.out);
  }

  // visible for testing
  Console(PrintStream printStream) {
    this.printStream = printStream;
  }
  public void errors(java.util.List<?> errors) {
    errors.forEach(this::error);
  }

  public void error(Object error) {
    println("error: " + error.toString());
  }

  public void print(String header, List<Log> logs) {
    print(toText(header, logs));
  }

  private String toText(String header, List<Log> logs) {
    StringBuilder text = new StringBuilder(TASK_HEADER_PREFIX + header + "\n");
    for (Log log : logs) {
      text.append(prefixMultiline(log.level() + ": " + log.message()));
    }
    return text.toString();
  }


  private static String prefixMultiline(String text) {
    String[] lines = text.lines().toArray(String[]::new);
    return prefixMultiline(lines) + "\n";
  }

  // visible for testing
  public static String prefixMultiline(String[] lines) {
    lines[0] = MESSAGE_FIRST_LINE_PREFIX + lines[0];
    for (int i = 1; i < lines.length; i++) {
      lines[i] = MESSAGE_OTHER_LINES_PREFIX + lines[i];
    }
    return unlines(lines);
  }

  public void println(String line) {
    printStream.println(line);
  }

  public void print(String line) {
    printStream.print(line);
  }
}
