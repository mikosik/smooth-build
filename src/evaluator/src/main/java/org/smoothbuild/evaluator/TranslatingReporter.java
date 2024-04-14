package org.smoothbuild.evaluator;

import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;

public class TranslatingReporter implements Reporter {
  private final Reporter reporter;
  private final BsTranslator bsTranslator;

  public TranslatingReporter(Reporter reporter, BsTranslator bsTranslator) {
    this.reporter = reporter;
    this.bsTranslator = bsTranslator;
  }

  @Override
  public void report(Report report) {
    reporter.report(translate(report));
  }

  private Report translate(Report report) {
    if (report.trace() instanceof BTrace bTrace) {
      var sTrace = bsTranslator.translate(bTrace);
      return Report.report(report.label(), sTrace, report.source(), report.logs());
    }
    return report;
  }
}
