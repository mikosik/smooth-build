package org.smoothbuild.cli.base;

import static org.smoothbuild.common.tuple.Tuples.tuple;
import static org.smoothbuild.out.log.Try.success;

import jakarta.inject.Inject;
import java.util.function.Function;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.out.log.Try;
import org.smoothbuild.out.report.Reporter;

public class ReportResult implements Function<String, Try<Tuple0>> {
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
