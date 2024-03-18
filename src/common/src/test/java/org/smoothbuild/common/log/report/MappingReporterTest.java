package org.smoothbuild.common.log.report;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.MappingReporter.labelPrefixingReporter;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.testing.TestingLog.ERROR_LOG;

import org.junit.jupiter.api.Test;

public class MappingReporterTest {
  @Test
  void report_call_is_forwarded_with_mapped_report() {
    var stepReporter = mock(Reporter.class);
    var mappingStepReporter = new MappingReporter(stepReporter, r -> r.withLogs(list(info("i"))));
    var report = report(label("label"), "details", EXECUTION, list(ERROR_LOG));

    mappingStepReporter.report(report);

    verify(stepReporter).report(report.withLogs(list(info("i"))));
  }

  @Test
  void report_call_is_forwarded_with_prefixed_label() {
    var stepReporter = mock(Reporter.class);
    var prefixingStepReporter = labelPrefixingReporter(stepReporter, label("prefix"));
    var report = report(label("label"), "details", EXECUTION, list(ERROR_LOG));

    prefixingStepReporter.report(report);

    verify(stepReporter).report(report.withLabel(label("prefix", "label")));
  }
}
