package org.smoothbuild.common.schedule;

import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.concurrent.Promise.promise;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.report.Report;

public record Output<V>(Promise<Maybe<V>> result, Report report) {
  public static <V> Output<V> output(V value, Report report) {
    return output(some(value), report);
  }

  public static <V> Output<V> output(Report report) {
    return output(none(), report);
  }

  public static <V> Output<V> outputWithMaybeValue(V value, Report report) {
    return output(report.containsFailures() ? none() : some(value), report);
  }

  private static <V> Output<V> output(Maybe<V> result, Report report) {
    return new Output<>(promise(result), report);
  }

  public static <V> Output<V> schedulingOutput(Promise<Maybe<V>> resultPromise, Report report) {
    return new Output<>(resultPromise, report);
  }
}
