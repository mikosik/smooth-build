package org.smoothbuild.common.task;

import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.log.report.Trace;

public record Output<V>(Promise<V> result, Report report) {
  public static Output<Void> output(Label label, List<Log> logs) {
    return output(null, Report.report(label, new Trace(), EXECUTION, logs));
  }

  public static <V> Output<V> output(V result, Report report) {
    return new Output<>(promise(result), report);
  }

  public static <V> Output<V> schedulingOutput(Promise<V> resultPromise, Report report) {
    return new Output<>(resultPromise, report);
  }
}
