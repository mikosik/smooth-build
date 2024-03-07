package org.smoothbuild.app.report;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.app.report.FormatLog.formatLog;
import static org.smoothbuild.app.report.FormatLog.formatLogs;
import static org.smoothbuild.common.log.Log.error;
import static org.smoothbuild.common.log.Log.fatal;
import static org.smoothbuild.common.log.Log.info;
import static org.smoothbuild.common.log.Log.warning;
import static org.smoothbuild.common.testing.TestingLog.logsWithAllLevels;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.log.Log;

public class FormatLogTest {
  @Test
  void test_format_logs() {
    assertThat(formatLogs("header", logsWithAllLevels()) + "\n")
        .isEqualTo(
            """
            header
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

  public static List<Arguments> test_format_log() {
    return List.of(
        arguments(fatal("message"), "  [FATAL] message"),
        arguments(error("message"), "  [ERROR] message"),
        arguments(warning("message"), "  [WARNING] message"),
        arguments(info("message"), "  [INFO] message"));
  }
}
