package org.smoothbuild.common.log.report;

import java.util.Set;

public class DecoratingReporter implements Reporter {
  private final Reporter reporter;
  private final java.util.Set<ReportDecorator> decorators;

  public DecoratingReporter(Reporter reporter, Set<ReportDecorator> decorators) {
    this.reporter = reporter;
    this.decorators = decorators;
  }

  @Override
  public void submit(Report report) {
    reporter.submit(decorate(report));
  }

  private Report decorate(Report report) {
    var result = report;
    for (ReportDecorator decorator : decorators) {
      result = decorator.decorate(result);
    }
    return result;
  }
}
