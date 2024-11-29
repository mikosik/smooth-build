package org.smoothbuild.common.log.report;

import static org.smoothbuild.common.log.base.Origin.EXECUTION;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.Origin;

public record Report(Label label, Trace trace, Origin origin, List<Log> logs) {
  public static Report report(Label label, List<Log> logs) {
    return report(label, new Trace(), logs);
  }

  public static Report report(Label label, Trace trace, List<Log> logs) {
    return report(label, trace, EXECUTION, logs);
  }

  public static Report report(Label label, Origin origin, List<Log> logs) {
    return report(label, new Trace(), origin, logs);
  }

  public static Report report(Label label, Trace trace, Origin origin, List<Log> logs) {
    return new Report(label, trace, origin, logs);
  }

  public Report withLabel(Label label) {
    return new Report(label, trace, origin, logs);
  }

  public Report withTrace(Trace trace) {
    return new Report(label, trace, origin, logs);
  }

  public Report withLogs(List<Log> logs) {
    return new Report(label, trace, origin, logs);
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
    builder.append("TaskReport [");
    builder.append(this.label());
    builder.append("] ");
    builder.append(origin);
    builder.append("\n");
    builder.append(trace);
    builder.append("\n");
    for (var log : this.logs()) {
      builder.append(log.toPrettyString());
      builder.append("\n");
    }
    return builder.toString();
  }
}
