package org.smoothbuild.common.task;

import static org.smoothbuild.common.concurrent.Promise.promise;

import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.report.Report;

public record Output<V>(Promise<V> result, Report report) {
  public static <V> Output<V> output(V result, Report report) {
    return new Output<>(promise(result), report);
  }

  public static <V> Output<V> schedulingOutput(Promise<V> resultPromise, Report report) {
    return new Output<>(resultPromise, report);
  }
}
