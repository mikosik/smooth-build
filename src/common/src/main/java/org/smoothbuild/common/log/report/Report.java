package org.smoothbuild.common.log.report;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.ResultSource;

public record Report(Label label, Trace<?> trace, ResultSource source, List<Log> logs) {
  public static Report report(Label label, Trace<?> trace, ResultSource source, List<Log> logs) {
    return new Report(label, trace, source, logs);
  }

  public Report withLabel(Label label) {
    return new Report(label, trace, source, logs);
  }

  public Report withLogs(List<Log> logs) {
    return new Report(label, trace, source, logs);
  }

  public <T extends Throwable> Report mapLabel(Function1<Label, Label, T> function1) throws T {
    return new Report(function1.apply(label), trace, source, logs);
  }

  public <T extends Throwable> Report mapLogs(Function1<List<Log>, List<Log>, T> function1)
      throws T {
    return new Report(label, trace, source, function1.apply(logs));
  }

  public String toPrettyString() {
    var builder = new StringBuilder();
    builder.append("TaskReport [");
    builder.append(this.label());
    builder.append("] ");
    builder.append(source);
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
