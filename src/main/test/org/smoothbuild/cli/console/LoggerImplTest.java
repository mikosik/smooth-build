package org.smoothbuild.cli.console;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.cli.console.Log.fatal;
import static org.smoothbuild.cli.console.Log.info;
import static org.smoothbuild.cli.console.Log.warning;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LoggerImplTest {
  private static final String HEADER = "header";

  private Reporter reporter;
  private LoggerImpl logger;

  @BeforeEach
  public void before() {
    reporter = mock(Reporter.class);
    logger = new LoggerImpl(HEADER, reporter);
  }

  @Nested
  class hasProblems {
    @Test
    void returns_false_when_nothing_has_been_logged() {
      assertThat(logger.hasProblems())
          .isFalse();
    }

    @Test
    void returns_true_after_logging_fatal() {
      logger.log(fatal("message"));
      assertThat(logger.hasProblems())
          .isTrue();
    }

    @Test
    void returns_true_after_warning_error() {
      logger.log(error("message"));
      assertThat(logger.hasProblems())
          .isTrue();
    }

    @Test
    void returns_false_after_logging_warning() {
      logger.log(warning("message"));
      assertThat(logger.hasProblems())
          .isFalse();
    }

    @Test
    void returns_false_after_logging_info() {
      logger.log(info("message"));
      assertThat(logger.hasProblems())
          .isFalse();
    }
  }

  @Test
  void close_passes_all_logs_to_reporter() {
    Log fatal = fatal("fatal");
    Log error = error("error");
    Log warning = warning("warning");
    Log info = info("info");

    logger.log(fatal);
    logger.log(error);
    logger.log(warning);
    logger.log(info);

    logger.close();

    verify(reporter, only())
        .report(HEADER, List.of(fatal, error, warning, info));
  }
}

