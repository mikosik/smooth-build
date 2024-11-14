package org.smoothbuild.common.task;

import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.log.base.Log.containsFailure;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.report.Report;

public record Output<V>(Promise<Maybe<V>> result, Report report) {
  public Output(Promise<Maybe<V>> result, Report report) {
    if (containsFailure(report.logs())) {
      this.result = promise(none());
    } else {
      this.result = result;
    }
    this.report = report;
  }

  public static <V> Output<V> output(Label label, List<Log> logs) {
    return output(null, label, logs);
  }

  public static <V> Output<V> output(V value, Label label, List<Log> logs) {
    return output(value, Report.report(label, logs));
  }

  public static <V> Output<V> output(V result, Report report) {
    return new Output<>(promise(some(result)), report);
  }

  public static <V> Output<V> schedulingOutput(Promise<Maybe<V>> resultPromise, Report report) {
    return new Output<>(resultPromise, report);
  }
}
