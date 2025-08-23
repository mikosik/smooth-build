package org.smoothbuild.common.log.report;

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

  public String toPrettyString() {
    var builder = new StringBuilder();
    builder.append(this.label());
    builder.append(" ");
    builder.append(origin);
    trace.ifPresent(t -> {
      builder.append("\n  ");
      builder.append(t);
    });
    for (var log : this.logs()) {
      builder.append("\n  ");
      builder.append(log.toPrettyString());
    }
    return builder.toString();
  }
}
