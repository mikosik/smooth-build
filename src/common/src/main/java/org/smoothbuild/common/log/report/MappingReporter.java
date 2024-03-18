package org.smoothbuild.common.log.report;

import java.util.function.Function;
import org.smoothbuild.common.log.base.Label;

public class MappingReporter implements Reporter {
  private final Reporter reporter;
  private final Function<Report, Report> mapper;

  public static Reporter labelPrefixingReporter(Reporter reporter, Label prefix) {
    return new MappingReporter(reporter, r -> r.mapLabel(prefix::append));
  }

  public MappingReporter(Reporter reporter, Function<Report, Report> mapper) {
    this.reporter = reporter;
    this.mapper = mapper;
  }

  @Override
  public void report(Report report) {
    reporter.report(mapper.apply(report));
  }
}
