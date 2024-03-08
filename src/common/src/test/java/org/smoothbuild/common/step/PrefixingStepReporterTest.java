package org.smoothbuild.common.step;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.Label.label;
import static org.smoothbuild.common.log.ResultSource.EXECUTION;
import static org.smoothbuild.common.testing.TestingLog.ERROR_LOG;

import org.junit.jupiter.api.Test;

public class PrefixingStepReporterTest {
  @Test
  void report_call_is_forwarded_with_prefixed_label() {
    var stepReporter = mock(StepReporter.class);
    var prefixingStepReporter = new PrefixingStepReporter(stepReporter, label("prefix"));
    var details = "details";
    var resultSource = EXECUTION;
    var logs = list(ERROR_LOG);

    prefixingStepReporter.report(label("my-label"), details, resultSource, logs);

    verify(stepReporter).report(label("prefix", "my-label"), details, resultSource, logs);
  }
}
