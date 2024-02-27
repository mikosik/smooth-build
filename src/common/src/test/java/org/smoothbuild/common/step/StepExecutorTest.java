package org.smoothbuild.common.step;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.log.Log.error;
import static org.smoothbuild.common.log.Log.fatal;
import static org.smoothbuild.common.log.Log.info;
import static org.smoothbuild.common.log.Log.warning;
import static org.smoothbuild.common.log.Try.failure;
import static org.smoothbuild.common.log.Try.success;
import static org.smoothbuild.common.step.Step.constStep;
import static org.smoothbuild.common.step.Step.maybeStep;
import static org.smoothbuild.common.step.Step.step;
import static org.smoothbuild.common.step.Step.stepFactory;
import static org.smoothbuild.common.tuple.Tuples.tuple;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import jakarta.inject.Inject;
import java.util.function.Function;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.Level;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.log.Try;
import org.smoothbuild.common.tuple.Tuple0;

class StepExecutorTest {
  @Nested
  class _constant_step {
    @Test
    void executing_return_its_value() {
      var reporter = mock(StepReporter.class);
      var result = stepExecutor().execute(constStep("a"), null, reporter);
      assertThat(result).isEqualTo(some("a"));
    }

    @Test
    void creating_constant_step_with_null_fails() {
      assertCall(() -> constStep(null)).throwsException(NullPointerException.class);
    }
  }

  @Nested
  class _function_step {
    @ParameterizedTest
    @MethodSource
    void that_returns_success(List<Log> logs) {
      var reporter = mock(StepReporter.class);

      var result = stepExecutor().execute(step(s -> success(s + "b", logs)), "a", reporter);

      assertThat(result).isEqualTo(some("ab"));
      verifyReported(reporter, logs);
    }

    static List<Arguments> that_returns_success() {
      return list(
          arguments(list(warning("warning"))), arguments(list(info("info"))), arguments(list()));
    }

    @ParameterizedTest
    @MethodSource
    void that_returns_failure(List<Log> logs) {
      var reporter = mock(StepReporter.class);
      var step = step(s -> failure(logs));

      var result = stepExecutor().execute(step, "a", reporter);

      assertThat(result).isEqualTo(none());
      verifyReported(reporter, logs);
    }

    static List<Arguments> that_returns_failure() {
      return list(arguments(list(error("error"))), arguments(list(fatal("fatal"))));
    }

    @Test
    void that_returns_null_causes_execution_failure() {
      var reporter = mock(StepReporter.class);
      var step = step(s -> null);
      assertCall(() -> stepExecutor().execute(step, "a", reporter))
          .throwsException(NullPointerException.class);
    }
  }

  @Nested
  class _function_key_step {
    @Test
    void that_returns_success() {
      var reporter = mock(StepReporter.class);

      var result = stepExecutor("def").execute(step(SuffixWithInjected.class), "abc", reporter);

      assertThat(result).isEqualTo(some("abcdef"));
    }

    @ParameterizedTest
    @MethodSource
    void that_returns_failure(Log log) {
      var reporter = mock(StepReporter.class);

      var result = stepExecutor(log).execute(step(LogInjectedLog.class), tuple(), reporter);

      assertThat(result).isEqualTo(none());
      verifyReported(reporter, list(log));
    }

    static List<Arguments> that_returns_failure() {
      return list(arguments(error("error")), arguments(fatal("fatal")));
    }

    @Test
    void that_returns_null_causes_execution_failure() {
      var reporter = mock(StepReporter.class);
      assertCall(() -> stepExecutor().execute(step(ReturnNull.class), tuple(), reporter))
          .throwsException(NullPointerException.class);
    }

    private static class SuffixWithInjected implements Function<String, Try<String>> {
      private final String string;

      @Inject
      public SuffixWithInjected(String string) {
        this.string = string;
      }

      @Override
      public Try<String> apply(String arg) {
        return success(arg + string);
      }
    }

    private static class LogInjectedLog implements Function<Tuple0, Try<String>> {
      private final Log log;

      @Inject
      public LogInjectedLog(Log log) {
        this.log = log;
      }

      @Override
      public Try<String> apply(Tuple0 tuple0) {
        return failure(log);
      }
    }

    private static class ReturnNull implements Function<Tuple0, Try<String>> {
      @Override
      public Try<String> apply(Tuple0 tuple0) {
        return null;
      }
    }
  }

  @Nested
  class _named {
    @ParameterizedTest
    @MethodSource("org.smoothbuild.common.log.Level#values")
    void unnamed_step_that_logged_something_uses_empty_string_for_header(Level level) {
      var log = new Log(level, "message");
      var step = step(t -> Try.of("value", log));

      assertStepExecutionReports(step, log);
    }

    @ParameterizedTest
    @MethodSource("org.smoothbuild.common.log.Level#values")
    void named_step_that_logged_something_uses_name_for_header(Level level) {
      var log = new Log(level, "message");
      var step = step(t -> Try.of("value", log)).named("name");

      var reporter = mock(StepReporter.class);

      stepExecutor().execute(step, tuple(), reporter);

      var inOrder = inOrder(reporter);
      inOrder.verify(reporter).startNewPhase("::name");
      inOrder.verify(reporter).report(log);
      inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @MethodSource("org.smoothbuild.common.log.Level#values")
    void named_inner_step_that_logged_something_uses_full_name_for_header(Level level) {
      var log = new Log(level, "message");
      var step = step(t -> Try.of("value", log)).named("name").named("outer");

      var reporter = mock(StepReporter.class);

      stepExecutor().execute(step, tuple(), reporter);

      var inOrder = inOrder(reporter);
      inOrder.verify(reporter).startNewPhase("::outer");
      inOrder.verify(reporter).startNewPhase("::outer::name");
      inOrder.verify(reporter).report(log);
      inOrder.verifyNoMoreInteractions();
    }
  }

  @Nested
  class _composed_step {
    @Test
    void result_from_first_step_is_passed_to_second_step() {
      var reporter = mock(StepReporter.class);
      var step = constStep("abc").then(step(s -> success(s + "def")));

      var result = stepExecutor().execute(step, tuple(), reporter);

      assertThat(result).isEqualTo(some("abcdef"));
    }

    @Test
    void non_failure_logs_from_each_steps_are_logged() {
      var reporter = mock(StepReporter.class);
      var step = step(v -> success("abc", info("info")))
          .then(step(s -> success(s + "def", warning("warning"))));

      var result = stepExecutor().execute(step, tuple(), reporter);

      assertThat(result).isEqualTo(some("abcdef"));
      verifyReported(reporter, list(info("info"), warning("warning")));
    }

    @Test
    void second_step_is_not_executed_when_first_fails() {
      var reporter = mock(StepReporter.class);
      Function<Object, Try<String>> function = mock();
      var step = step(v -> failure(error("error"))).then(step(function));

      var result = stepExecutor().execute(step, tuple(), reporter);

      assertThat(result).isEqualTo(none());
      verifyNoInteractions(function);
      verifyReported(reporter, list(error("error")));
    }
  }

  @Nested
  class _factory_step {
    @Test
    void step_created_by_step_factory_is_executed() {
      var reporter = mock(StepReporter.class);
      var step = stepFactory((String s) -> constStep(s + "def"));

      var result = stepExecutor().execute(step, "abc", reporter);

      assertThat(result).isEqualTo(some("abcdef"));
      verifyReported(reporter, list());
    }

    @Test
    void failures_created_by_step_factory_are_reported() {
      var reporter = mock(StepReporter.class);
      var step = stepFactory((String s) -> step((Tuple0 t) -> failure(error("error"))));

      var result = stepExecutor().execute(step, "abc", reporter);

      assertThat(result).isEqualTo(none());
      verifyReported(reporter, list(error("error")));
    }
  }

  @Nested
  class _option_step {
    @Test
    void that_returns_some() {
      var reporter = mock(StepReporter.class);
      var step = maybeStep(OptionFunctionReturningSome.class);

      var result = stepExecutor().execute(step, 3, reporter);

      assertThat(result).isEqualTo(some("3"));
      verifyReported(reporter, list());
    }

    @Test
    void that_returns_none() {
      var reporter = mock(StepReporter.class);
      var step = maybeStep(OptionFunctionReturningNone.class);

      var result = stepExecutor().execute(step, 3, reporter);

      assertThat(result).isEqualTo(none());
      verifyReported(reporter, list());
    }

    private static class OptionFunctionReturningSome implements MaybeFunction<Integer, String> {
      @Override
      public Maybe<String> apply(Integer integer) {
        return some(integer.toString());
      }
    }

    private static class OptionFunctionReturningNone implements MaybeFunction<Integer, String> {
      @Override
      public Maybe<String> apply(Integer integer) {
        return none();
      }
    }
  }

  @Nested
  class _append {
    @Test
    void adds_another_argument() {
      var step = constStep("abc").append("def");
      var result = stepExecutor().execute(step, tuple(), mock());
      assertThat(result).isEqualTo(some(tuple("abc", "def")));
    }
  }

  private static void assertStepExecutionReports(Step<Object, String> step, Log log) {
    var reporter = mock(StepReporter.class);

    stepExecutor().execute(step, tuple(), reporter);

    verifyReported(reporter, list(log));
  }

  private static void verifyReported(StepReporter reporter, List<Log> logs) {
    logs.forEach(log -> verify(reporter).report(log));
    Mockito.verifyNoMoreInteractions(reporter);
  }

  private static StepExecutor stepExecutor() {
    return new StepExecutor(Guice.createInjector());
  }

  private static StepExecutor stepExecutor(String string) {
    return new StepExecutor(Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(String.class).toInstance(string);
      }
    }));
  }

  private static StepExecutor stepExecutor(Log log) {
    return new StepExecutor(Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(Log.class).toInstance(log);
      }
    }));
  }
}