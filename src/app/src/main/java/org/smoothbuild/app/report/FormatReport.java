package org.smoothbuild.app.report;

import static com.google.common.base.Strings.padStart;
import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.log.base.Log.containsAnyFailure;

import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.report.Report;

public class FormatReport {
  public static String formatReport(Report report) {
    var labelString = report.label().toString();
    var builder = new StringBuilder(labelString);
    builder.append(padStart(report.source().toString(), 79 - labelString.length(), ' '));
    if (containsAnyFailure(report.logs()) && !report.details().isEmpty()) {
      builder.append("\n");
      builder.append(indent(report.details()));
    }

    for (Log log : report.logs()) {
      builder.append("\n");
      builder.append(formatLog(log));
    }
    return builder.toString();
  }

  static String formatLog(Log log) {
    return indent(log.toPrettyString());
  }
}
