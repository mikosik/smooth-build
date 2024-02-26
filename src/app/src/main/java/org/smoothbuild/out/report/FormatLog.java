package org.smoothbuild.out.report;

import static org.smoothbuild.common.Strings.indent;

import java.util.List;
import org.smoothbuild.common.log.Log;

public class FormatLog {
  private static final String MESSAGE_PREFIX = "  ";

  public static String formatLogs(String header, List<Log> logs) {
    var builder = new StringBuilder(header);
    for (Log log : logs) {
      builder.append("\n");
      builder.append(formatLog(log));
    }
    return builder.toString();
  }

  public static String formatLog(Log log) {
    return indent(log.toPrettyString());
  }

  private static String prefixMultiline(String[] lines) {
    for (int i = 0; i < lines.length; i++) {
      lines[i] = MESSAGE_PREFIX + lines[i];
    }
    return String.join("\n", lines);
  }
}
