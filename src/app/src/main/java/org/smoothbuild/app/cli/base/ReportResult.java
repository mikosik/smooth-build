package org.smoothbuild.app.cli.base;

import static org.smoothbuild.common.log.Try.success;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import jakarta.inject.Inject;
import org.smoothbuild.app.report.Reporter;
import org.smoothbuild.common.log.Try;
import org.smoothbuild.common.step.TryFunction;
import org.smoothbuild.common.tuple.Tuple0;

public class ReportResult implements TryFunction<String, Tuple0> {
  private final Reporter reporter;

  @Inject
  public ReportResult(Reporter reporter) {
    this.reporter = reporter;
  }

  @Override
  public Try<Tuple0> apply(String string) {
    reporter.reportResult(string);
    return success(tuple());
  }
}
