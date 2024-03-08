package org.smoothbuild.app.report;

import static com.google.common.base.Strings.padStart;
import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.log.Log.containsAnyFailure;

import java.util.List;
import org.smoothbuild.common.log.Label;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.log.ResultSource;

public class FormatLog {
  public static String formatLogs(
      Label label, String details, ResultSource source, List<Log> logs) {
    var labelString = label.toString();
    var builder = new StringBuilder(labelString);
    builder.append(padStart(source.toString(), 79 - labelString.length(), ' '));
    if (containsAnyFailure(logs) && !details.isEmpty()) {
      builder.append("\n");
      builder.append(indent(details));
    }

    for (Log log : logs) {
      builder.append("\n");
      builder.append(formatLog(log));
    }
    return builder.toString();
  }

  static String formatLog(Log log) {
    return indent(log.toPrettyString());
  }
}
