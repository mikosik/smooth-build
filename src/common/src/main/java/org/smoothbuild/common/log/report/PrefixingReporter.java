package org.smoothbuild.common.log.report;

import org.smoothbuild.common.log.base.Label;

public class PrefixingReporter implements Reporter {
  private final Reporter reporter;
  private final Label prefix;

  public PrefixingReporter(Reporter reporter, Label prefix) {
    this.reporter = reporter;
    this.prefix = prefix;
  }

  @Override
  public void report(Report report) {
    reporter.report(report.mapLabel(prefix::append));
  }
}
