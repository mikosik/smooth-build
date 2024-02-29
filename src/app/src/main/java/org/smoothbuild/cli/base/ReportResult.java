package org.smoothbuild.cli.base;

import static org.smoothbuild.common.log.Try.success;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import jakarta.inject.Inject;
import org.smoothbuild.common.log.Try;
import org.smoothbuild.common.step.TryFunction;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.out.report.Reporter;

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
