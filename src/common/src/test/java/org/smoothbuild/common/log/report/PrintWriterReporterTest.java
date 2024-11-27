package org.smoothbuild.common.log.report;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.base.Log.warning;
import static org.smoothbuild.common.log.base.Origin.DISK;
import static org.smoothbuild.common.log.report.PrintWriterReporter.formatLog;
import static org.smoothbuild.common.log.report.PrintWriterReporter.formatReport;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.testing.TestingLog.logsWithAllLevels;

import java.io.PrintWriter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.testing.CommonTestContext;

public class PrintWriterReporterTest extends CommonTestContext {
  @Test
  void submit_single_log_prints_log_when_its_level_exceeds_threshold() {
    var systemOut = mock(PrintWriter.class);
    var report = report(label("name"), DISK, logsWithAllLevels());
    var reporter = new PrintWriterReporter(systemOut);

    reporter.submit(report);

    verify(systemOut).println(formatReport(report));
  }

  @Test
  void test_format_logs() {
    var trace = new Trace("called", location(alias()));
    var report = report(label("labelName"), trace, logsWithAllLevels());
    assertThat(formatReport(report) + "\n")
        .isEqualTo(
            """
            :labelName
              @ {t-alias}/path:17 called
              [FATAL] fatal message
              [ERROR] error message
              [WARNING] warning message
              [INFO] info message
            """);
  }

  @ParameterizedTest
  @MethodSource
  void test_format_log(Log log, String string) {
    assertThat(formatLog(log)).isEqualTo(string);
  }

  static List<Arguments> test_format_log() {
    return List.of(
        arguments(fatal("message"), "  [FATAL] message"),
        arguments(error("message"), "  [ERROR] message"),
        arguments(warning("message"), "  [WARNING] message"),
        arguments(info("message"), "  [INFO] message"));
  }
}
