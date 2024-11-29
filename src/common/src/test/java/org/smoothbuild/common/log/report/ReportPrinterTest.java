package org.smoothbuild.common.log.report;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Origin.DISK;
import static org.smoothbuild.common.testing.TestingLog.logsWithAllLevels;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.testing.CommonTestContext;

public class ReportPrinterTest extends CommonTestContext {
  @Test
  void report_is_printed() {
    var stringWriter = new StringWriter();
    var printWriterReporter = new ReportPrinter(new PrintWriter(stringWriter));

    printWriterReporter.print(
        label("labelName"),
        some(new Trace("called", location(alias()))),
        DISK,
        logsWithAllLevels());

    assertThat(stringWriter.toString())
        .isEqualTo(
            """
            :labelName                                                              d-cache
              @ {t-alias}/path:17 called
              [FATAL] fatal message
              [ERROR] error message
              [WARNING] warning message
              [INFO] info message
            """);
  }

  @Test
  void when_trace_is_none_then_it_is_not_printed() {
    var stringWriter = new StringWriter();
    var printWriterReporter = new ReportPrinter(new PrintWriter(stringWriter));

    printWriterReporter.print(label("labelName"), none(), DISK, logsWithAllLevels());

    assertThat(stringWriter.toString())
        .isEqualTo(
            """
            :labelName                                                              d-cache
              [FATAL] fatal message
              [ERROR] error message
              [WARNING] warning message
              [INFO] info message
            """);
  }

  @Test
  void consecutive_reports_are_separated_by_new_line() {
    var stringWriter = new StringWriter();
    var printWriterReporter = new ReportPrinter(new PrintWriter(stringWriter));

    printWriterReporter.print(label("labelA"), none(), DISK, logsWithAllLevels());
    printWriterReporter.print(label("labelB"), none(), DISK, logsWithAllLevels());

    assertThat(stringWriter.toString())
        .isEqualTo(
            """
            :labelA                                                                 d-cache
              [FATAL] fatal message
              [ERROR] error message
              [WARNING] warning message
              [INFO] info message
            :labelB                                                                 d-cache
              [FATAL] fatal message
              [ERROR] error message
              [WARNING] warning message
              [INFO] info message
            """);
  }
}
