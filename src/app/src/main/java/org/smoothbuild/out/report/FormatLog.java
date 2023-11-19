package org.smoothbuild.out.report;

import static org.smoothbuild.common.Strings.indent;

import java.util.List;
import org.smoothbuild.out.log.Log;

public class FormatLog {
  private static final String MESSAGE_FIRST_LINE_PREFIX = "   + ";
  private static final String MESSAGE_OTHER_LINES_PREFIX = "     ";

  public static String formatLogs(String header, List<Log> logs) {
    var builder = new StringBuilder(indentHeader(header));
    for (Log log : logs) {
      builder.append("\n");
      builder.append(formatLog(log));
    }
    return builder.toString();
  }

  private static String indentHeader(String header) {
    return indent(header);
  }

  public static String formatLog(Log log) {
    String[] lines = (log.level() + ": " + log.message()).lines().toArray(String[]::new);
    return prefixMultiline(lines);
  }

  private static String prefixMultiline(String[] lines) {
    lines[0] = MESSAGE_FIRST_LINE_PREFIX + lines[0];
    for (int i = 1; i < lines.length; i++) {
      lines[i] = MESSAGE_OTHER_LINES_PREFIX + lines[i];
    }
    return String.join("\n", lines);
  }
}
