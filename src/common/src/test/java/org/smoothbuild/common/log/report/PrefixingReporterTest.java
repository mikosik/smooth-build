package org.smoothbuild.common.log.report;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.testing.TestingLog.ERROR_LOG;

import org.junit.jupiter.api.Test;

public class PrefixingReporterTest {
  @Test
  void report_call_is_forwarded_with_prefixed_label() {
    var stepReporter = mock(Reporter.class);
    var prefixingStepReporter = new PrefixingReporter(stepReporter, label("prefix"));
    var details = "details";
    var resultSource = EXECUTION;
    var logs = list(ERROR_LOG);

    prefixingStepReporter.report(label("my-label"), details, resultSource, logs);

    verify(stepReporter).report(label("prefix", "my-label"), details, resultSource, logs);
  }
}
