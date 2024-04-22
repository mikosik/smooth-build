package org.smoothbuild.evaluator;

import static org.smoothbuild.common.log.report.Report.report;

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
  public void submit(Report report) {
    reporter.submit(translate(report));
  }

  private Report translate(Report report) {
    if (report.trace() instanceof BTrace bTrace) {
      var sTrace = bsTranslator.translate(bTrace);
      return report(report.label(), sTrace, report.source(), report.logs());
    }
    return report;
  }
}
