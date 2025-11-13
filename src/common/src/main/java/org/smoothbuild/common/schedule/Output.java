package org.smoothbuild.common.schedule;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.log.base.Log.fatal;

import org.jspecify.annotations.Nullable;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.log.report.Trace;

public record Output<V>(Promise<Maybe<V>> result, Report report) {
  public static <V> Output<V> output(V value, Report report) {
    return output(some(value), report);
  }

  public static <V> Output<V> output(Report report) {
    return output(none(), report);
  }

  public static <V> Output<V> outputWithMaybeValue(@Nullable V value, Report report) {
    return output(report.containsFailures() ? none() : some(requireNonNull(value)), report);
  }

  private static <V> Output<V> output(Maybe<V> result, Report report) {
    return new Output<>(promise(result), report);
  }

  public static <T> Output<T> successOutput(Promise<Maybe<T>> resultPromise, Label label) {
    return schedulingOutput(resultPromise, Report.report(label, none(), list()));
  }

  public static <T> Output<T> successOutput(
      Promise<Maybe<T>> resultPromise, Label label, Trace trace) {
    return schedulingOutput(resultPromise, Report.report(label, trace, list()));
  }

  public static <V> Output<V> schedulingOutput(Promise<Maybe<V>> resultPromise, Report report) {
    return new Output<>(resultPromise, report);
  }

  public static <T> Output<T> failedOutput(
      Label label, Maybe<Trace> trace, String message, Throwable e) {
    return output(Report.report(label, trace, list(fatal(message, e))));
  }
}
