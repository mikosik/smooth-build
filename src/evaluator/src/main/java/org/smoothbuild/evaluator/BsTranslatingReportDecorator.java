package org.smoothbuild.evaluator;

import static org.smoothbuild.common.log.report.Report.report;

import jakarta.inject.Inject;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.log.report.ReportDecorator;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;

public class BsTranslatingReportDecorator implements ReportDecorator {
  private final BsTranslator bsTranslator;

  @Inject
  public BsTranslatingReportDecorator(BsTranslator bsTranslator) {
    this.bsTranslator = bsTranslator;
  }

  @Override
  public Report decorate(Report report) {
    if (report.trace() instanceof BTrace bTrace) {
      var sTrace = bsTranslator.translate(bTrace);
      return report(report.label(), sTrace, report.source(), report.logs());
    }
    return report;
  }
}
