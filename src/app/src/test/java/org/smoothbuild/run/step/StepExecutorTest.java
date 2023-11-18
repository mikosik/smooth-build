package org.smoothbuild.run.step;

import static com.google.common.truth.Truth.assertThat;
import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.out.log.Log.info;
import static org.smoothbuild.out.log.Log.warning;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.maybeLogs;
import static org.smoothbuild.run.step.Step.constStep;
import static org.smoothbuild.run.step.Step.optionStep;
import static org.smoothbuild.run.step.Step.step;
import static org.smoothbuild.run.step.Step.stepFactory;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.out.log.ImmutableLogs;
import org.smoothbuild.out.log.Level;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.Logs;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.out.report.Reporter;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import io.vavr.Tuple;
import io.vavr.Tuple0;
import io.vavr.control.Option;
import jakarta.inject.Inject;

class StepExecutorTest {
  @Nested
  class _constant_step {
    @Test
    void executing_return_its_value() {
      var reporter = mock(Reporter.class);
      var result = stepExecutor().execute(constStep("a"), null, reporter);
      assertThat(result).isEqualTo(some("a"));
    }

    @Test
    void creating_constant_step_with_null_fails() {
      assertCall(() -> constStep(null))
          .throwsException(NullPointerException.class);
    }
  }

  @Nested
  class _function_step {
    @ParameterizedTest
    @MethodSource
    void that_returns_success(Logs logs) {
      var reporter = mock(Reporter.class);

      var result = stepExecutor().execute(step(s -> maybe(s + "b", logs)), "a", reporter);

      assertThat(result).isEqualTo(some("ab"));
      verifyReported(reporter, logs.toList());
    }

    static List<Arguments> that_returns_success() {
      return List.of(
          arguments(ImmutableLogs.logs(warning("warning"))),
          arguments(ImmutableLogs.logs(info("info"))),
          arguments(ImmutableLogs.logs())
      );
    }


    @ParameterizedTest
    @MethodSource
    void that_returns_failure(Logs logs) {
      var reporter = mock(Reporter.class);
      var step = step(s -> maybeLogs(logs));

      var result = stepExecutor().execute(step, "a", reporter);

      assertThat(result).isEqualTo(none());
      verifyReported(reporter, logs.toList());
    }

    static List<Arguments> that_returns_failure() {
      return List.of(
          arguments(ImmutableLogs.logs(error("error"))),
          arguments(ImmutableLogs.logs(fatal("fatal")))
      );
    }

    @Test
    void that_returns_null_causes_execution_failure() {
      var reporter = mock(Reporter.class);
      var step = step(s -> null);
      assertCall(() -> stepExecutor().execute(step, "a", reporter))
          .throwsException(NullPointerException.class);
    }
  }

  @Nested
  class _function_key_step {
    @Test
    void that_returns_success() {
      var reporter = mock(Reporter.class);

      var result =
          stepExecutor("def").execute(step(SuffixWithInjected.class), "abc", reporter);

      assertThat(result).isEqualTo(some("abcdef"));
    }

    @ParameterizedTest
    @MethodSource
    void that_returns_failure(Log log) {
      var reporter = mock(Reporter.class);

      var result = stepExecutor(log)
          .execute(step(LogInjectedLog.class), Tuple.empty(), reporter);

      assertThat(result).isEqualTo(none());
      verifyReported(reporter, List.of(log));
    }

    static List<Arguments> that_returns_failure() {
      return List.of(
          arguments(error("error")),
          arguments(fatal("fatal"))
      );
    }

    @Test
    void that_returns_null_causes_execution_failure() {
      var reporter = mock(Reporter.class);
      assertCall(() -> stepExecutor().execute(step(ReturnNull.class), Tuple.empty(), reporter))
          .throwsException(NullPointerException.class);
    }

    private static class SuffixWithInjected implements Function<String, Maybe<String>> {
      private final String string;

      @Inject
      public SuffixWithInjected(String string) {
        this.string = string;
      }

      @Override
      public Maybe<String> apply(String arg) {
        return maybe(arg + string);
      }
    }

    private static class LogInjectedLog implements Function<Tuple0, Maybe<String>> {
      private final Log log;

      @Inject
      public LogInjectedLog(Log log) {
        this.log = log;
      }

      @Override
      public Maybe<String> apply(Tuple0 tuple0) {
        return maybeLogs(log);
      }
    }

    private static class ReturnNull implements Function<Tuple0, Maybe<String>> {
      @Override
      public Maybe<String> apply(Tuple0 tuple0) {
        return null;
      }
    }
  }

  @Nested
  class _named {
    @ParameterizedTest
    @MethodSource("org.smoothbuild.out.log.Level#values")
    void unnamed_step_that_logged_something_uses_empty_string_for_header(Level level) {
      var log = new Log(level, "message");
      var step = step(t -> Maybe.of("value", log));

      assertStepExecutionReports(step, log);
    }

    @ParameterizedTest
    @MethodSource("org.smoothbuild.out.log.Level#values")
    void named_step_that_logged_something_uses_name_for_header(Level level) {
      var log = new Log(level, "message");
      var step = step(t -> Maybe.of("value", log)).named("name");

      var reporter = mock(Reporter.class);

      stepExecutor().execute(step, Tuple.empty(), reporter);

      var inOrder = inOrder(reporter);
      inOrder.verify(reporter).startNewPhase("::name");
      inOrder.verify(reporter).report(log);
      inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @MethodSource("org.smoothbuild.out.log.Level#values")
    void named_inner_step_that_logged_something_uses_full_name_for_header(Level level) {
      var log = new Log(level, "message");
      var step = step(t -> Maybe.of("value", log)).named("name").named("outer");

      var reporter = mock(Reporter.class);

      stepExecutor().execute(step, Tuple.empty(), reporter);

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
      var reporter = mock(Reporter.class);
      var step = constStep("abc")
          .then(step(s -> maybe(s + "def")));

      var result = stepExecutor().execute(step, Tuple.empty(), reporter);

      assertThat(result).isEqualTo(some("abcdef"));
    }

    @Test
    void non_failure_logs_from_each_steps_are_logged() {
      var reporter = mock(Reporter.class);
      var step = step(v -> maybe("abc", info("info")))
          .then(step(s -> maybe(s + "def", warning("warning"))));

      var result = stepExecutor().execute(step, Tuple.empty(), reporter);

      assertThat(result).isEqualTo(some("abcdef"));
      verifyReported(reporter, List.of(info("info"), warning("warning")));
    }

    @Test
    void second_step_is_not_executed_when_first_fails() {
      var reporter = mock(Reporter.class);
      Function<Object, Maybe<String>> function = mock();
      var step = step(v -> maybeLogs(error("error")))
          .then(step(function));

      var result = stepExecutor().execute(step, Tuple.empty(), reporter);

      assertThat(result).isEqualTo(none());
      verifyNoInteractions(function);
      verifyReported(reporter, List.of(error("error")));
    }
  }

  @Nested
  class _factory_step {
    @Test
    void step_created_by_step_factory_is_executed() {
      var reporter = mock(Reporter.class);
      var step = stepFactory((String s) -> constStep(s + "def"));

      var result = stepExecutor().execute(step, "abc", reporter);

      assertThat(result).isEqualTo(some("abcdef"));
      verifyReported(reporter, List.of());
    }

    @Test
    void failures_created_by_step_factory_are_reported() {
      var reporter = mock(Reporter.class);
      var step = stepFactory((String s) -> step((Tuple0 t) -> maybeLogs(error("error"))));

      var result = stepExecutor().execute(step, "abc", reporter);

      assertThat(result).isEqualTo(none());
      verifyReported(reporter, List.of(error("error")));
    }
  }

  @Nested
  class _option_step {
    @Test
    void that_returns_some() {
      var reporter = mock(Reporter.class);
      var step = optionStep(OptionFunctionReturningSome.class);

      var result = stepExecutor().execute(step, 3, reporter);

      assertThat(result).isEqualTo(some("3"));
      verifyReported(reporter, List.of());
    }

    @Test
    void that_returns_none() {
      var reporter = mock(Reporter.class);
      var step = optionStep(OptionFunctionReturningNone.class);

      var result = stepExecutor().execute(step, 3, reporter);

      assertThat(result).isEqualTo(none());
      verifyReported(reporter, List.of());
    }

    private static class OptionFunctionReturningSome implements OptionFunction<Integer, String> {
      @Override
      public Option<String> apply(Integer integer) {
        return Option.some(integer.toString());
      }
    }

    private static class OptionFunctionReturningNone implements OptionFunction<Integer, String> {
      @Override
      public Option<String> apply(Integer integer) {
        return Option.none();
      }
    }
  }

  @Nested
  class _append {
    @Test
    void adds_another_argument() {
      var step = constStep("abc")
          .append("def");
      var result = stepExecutor().execute(step, Tuple.empty(), mock());
      assertThat(result).isEqualTo(some(Tuple.of("abc", "def")));
    }
  }

  private static void assertStepExecutionReports(Step<Object, String> step, Log log) {
    var reporter = mock(Reporter.class);

    stepExecutor().execute(step, Tuple.empty(), reporter);

    verifyReported(reporter, List.of(log));
  }

  private static void verifyReported(Reporter reporter, List<Log> logs) {
    logs.forEach(log -> verify(reporter).report(log));
    verifyNoMoreInteractions(reporter);
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
