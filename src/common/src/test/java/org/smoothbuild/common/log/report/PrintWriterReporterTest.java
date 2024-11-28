package org.smoothbuild.common.log.report;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.testing.TestingLog.logsWithAllLevels;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.testing.CommonTestContext;

public class PrintWriterReporterTest extends CommonTestContext {
  @Test
  void test_format_logs() {
    var stringWriter = new StringWriter();
    var printWriterReporter = new PrintWriterReporter(new PrintWriter(stringWriter));

    printWriterReporter.submit(newReport());

    assertThat(stringWriter.toString())
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

  private Report newReport() {
    var trace = new Trace("called", location(alias()));
    return report(label("labelName"), trace, logsWithAllLevels());
  }
}
