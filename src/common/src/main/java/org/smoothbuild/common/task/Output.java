package org.smoothbuild.common.task;

import org.smoothbuild.common.log.report.Report;

public record Output<V>(V result, Report report) {
  public static <V> Output<V> output(V result, Report report) {
    return new Output<>(result, report);
  }
}
