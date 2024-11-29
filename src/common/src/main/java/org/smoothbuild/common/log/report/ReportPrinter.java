package org.smoothbuild.common.log.report;

import static com.google.common.base.Strings.padStart;
import static org.smoothbuild.common.base.Strings.indent;

import java.io.PrintWriter;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.Origin;

/**
 * This class is thread-safe.
 */
public class ReportPrinter {
  private final PrintWriter printWriter;

  public ReportPrinter(PrintWriter printWriter) {
    this.printWriter = printWriter;
  }

  public void print(Label label, Trace trace, Origin origin, List<Log> logs) {
    var builder = new StringBuilder(labelPlusOrigin(label, origin));
    if (!(trace.isEmpty())) {
      builder.append("\n");
      builder.append(indent(trace.toString()));
    }

    for (Log log : logs) {
      builder.append("\n");
      builder.append(formatLog(log));
    }
    printWriter.println(builder);

    // We need to flush here. Otherwise, Junit test run from intellij won't show any output
    // when it fails. That's because junit doesn't flush System.out.
    printWriter.flush();
  }

  private static String labelPlusOrigin(Label label, Origin origin) {
    var labelString = label.toString();
    var originString = origin.toString();
    if (originString.isEmpty()) {
      return labelString;
    } else {
      return labelString + padStart(originString, 79 - labelString.length(), ' ');
    }
  }

  static String formatLog(Log log) {
    return indent(log.toPrettyString());
  }
}
