package org.smoothbuild.common.log.report;

import static com.google.common.base.Strings.padStart;
import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.log.base.Origin.EXECUTION;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.Origin;

public record Report(Label label, Maybe<Trace> trace, Origin origin, List<Log> logs) {
  public static Report report(Label label) {
    return report(label, list());
  }

  public static Report report(Label label, Log log) {
    return report(label, list(log));
  }

  public static Report report(Label label, List<Log> logs) {
    return report(label, none(), logs);
  }

  public static Report report(Label label, Trace trace, List<Log> logs) {
    return report(label, trace, EXECUTION, logs);
  }

  public static Report report(Label label, Maybe<Trace> trace, List<Log> logs) {
    return report(label, trace, EXECUTION, logs);
  }

  public static Report report(Label label, Trace trace, Origin origin, List<Log> logs) {
    return new Report(label, some(trace), origin, logs);
  }

  public static Report report(Label label, Maybe<Trace> trace, Origin origin, List<Log> logs) {
    return new Report(label, trace, origin, logs);
  }

  public Report withLabel(Label label) {
    return new Report(label, trace, origin, logs);
  }

  public Report withTrace(Maybe<Trace> trace) {
    return new Report(label, trace, origin, logs);
  }

  public Report withLogs(List<Log> logs) {
    return new Report(label, trace, origin, logs);
  }

  public boolean containsFailures() {
    return Log.containsFailure(logs);
  }

  public <T extends Throwable> Report mapLabel(Function1<Label, Label, T> function1) throws T {
    return new Report(function1.apply(label), trace, origin, logs);
  }

  public <T extends Throwable> Report mapLogs(Function1<List<Log>, List<Log>, T> function1)
      throws T {
    return new Report(label, trace, origin, function1.apply(logs));
  }

  @Override
  public String toString() {
    return reportToString(label, trace, origin, logs);
  }

  static String reportToString(Label label, Maybe<Trace> trace, Origin origin, List<Log> logs) {
    var builder = new StringBuilder(labelPlusOrigin(label, origin));
    trace.ifPresent(t -> {
      builder.append("\n");
      builder.append(indent(t.toString()));
    });

    for (Log log : logs) {
      builder.append("\n");
      builder.append(formatLog(log));
    }
    builder.append("\n");
    return builder.toString();
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

  private static String formatLog(Log log) {
    return indent(log.toPrettyString());
  }
}
