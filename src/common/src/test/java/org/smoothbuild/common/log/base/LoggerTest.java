package org.smoothbuild.common.log.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.base.Log.warning;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LoggerTest {
  private Logger logger;

  @BeforeEach
  public void before() {
    logger = new Logger();
  }

  @Nested
  class _size {
    @Test
    void should_be_zero_for_empty_logger() {
      var logger = new Logger();
      assertThat(logger.size()).isEqualTo(0);
    }

    @Test
    void should_be_equal_to_log_count() {
      var logger = new Logger();
      logger.logAll(list(info("message 1"), info("message 2")));
      assertThat(logger.size()).isEqualTo(2);
    }
  }

  @Nested
  class _iterator {
    @Test
    void should_be_unmodifiable() {
      logger = new Logger();
      logger.info("message");

      var iterator = logger.iterator();
      iterator.next();
      assertCall(iterator::remove).throwsException(UnsupportedOperationException.class);
    }

    @Test
    void should_return_all_logs_in_order() {
      var logs = list(info("message 1"), info("message"));
      logger = new Logger();
      logger.logAll(logs);

      assertThat(logger).containsExactlyElementsIn(logs).inOrder();
    }
  }

  @Nested
  class _containsFailure {
    @Test
    void when_nothing_has_been_logged_returns_false() {
      assertThat(logger.containsFailure()).isFalse();
    }

    @Test
    void after_logging_fatal_returns_true() {
      logger.log(fatal("message"));
      assertThat(logger.containsFailure()).isTrue();
    }

    @Test
    void after_logging_error_returns_true() {
      logger.log(error("message"));
      assertThat(logger.containsFailure()).isTrue();
    }

    @Test
    void after_logging_warning_returns_false() {
      logger.log(warning("message"));
      assertThat(logger.containsFailure()).isFalse();
    }

    @Test
    void after_logging_info_returns_false() {
      logger.log(info("message"));
      assertThat(logger.containsFailure()).isFalse();
    }

    @Test
    void after_adding_logs_that_contains_at_least_error_returns_true() {
      logger.logAll(list(error("message")));
      assertThat(logger.containsFailure()).isTrue();
    }

    @Test
    void after_logging_fatal_and_adding_logs_that_contains_at_least_error_returns_true() {
      logger.log(fatal("message"));
      logger.logAll(list(info("message")));
      assertThat(logger.containsFailure()).isTrue();
    }

    @Test
    void after_logging_error_and_adding_logs_without_problems_returns_true() {
      logger.log(error("message"));
      logger.logAll(list(info("message")));
      assertThat(logger.containsFailure()).isTrue();
    }

    @Test
    void after_logging_warning_and_adding_logs_with_error_returns_true() {
      logger.log(warning("message"));
      logger.logAll(list(error("message")));
      assertThat(logger.containsFailure()).isTrue();
    }
  }

  @Test
  void logs_contains_all_logs() {
    Log fatal = fatal("fatal");
    Log error = error("error");
    Log warning = warning("warning");
    Log info = info("info");
    Log other = info("info");

    logger.log(fatal);
    logger.log(error);
    logger.log(warning);
    logger.log(info);
    logger.logAll(list(other));

    assertThat(logger).containsExactly(fatal, error, warning, info, other).inOrder();
  }
}
