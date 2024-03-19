package org.smoothbuild.common.dag;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.dag.Dag.apply0;
import static org.smoothbuild.common.dag.Dag.apply1;
import static org.smoothbuild.common.dag.Dag.apply2;
import static org.smoothbuild.common.dag.Dag.applyMaybeFunction;
import static org.smoothbuild.common.dag.Dag.chain;
import static org.smoothbuild.common.dag.Dag.evaluate;
import static org.smoothbuild.common.dag.Dag.inject;
import static org.smoothbuild.common.dag.Dag.prefix;
import static org.smoothbuild.common.dag.Dag.value;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.base.Try.failure;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.common.log.report.Report.report;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import jakarta.inject.Inject;
import java.util.Locale;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.log.report.Reporter;

class DagEvaluatorTest {
  @Nested
  class _value_node {
    @Test
    void evaluation_is_equal_to_value() {
      var reporter = mock(Reporter.class);
      var value = value("abc");

      var result = stepExecutor().evaluate(value, reporter);

      assertThat(result).isEqualTo(some("abc"));
      verifyNoInteractions(reporter);
    }
  }

  @Nested
  class _chain_node {
    @Test
    void evaluates_second_when_first_succeeded() {
      var reporter = mock(Reporter.class);

      TryFunction1<String, String> first = mock();
      when(first.apply(any())).thenReturn(success(""));
      var applyFirst = apply1(first, value("arg1"));

      TryFunction1<String, String> second = mock();
      when(second.apply(any())).thenReturn(success("abc"));
      var applySecond = apply1(second, value("arg2"));

      var result = stepExecutor().evaluate(chain(applyFirst, applySecond), reporter);

      assertThat(result).isEqualTo(some("abc"));
    }

    @Test
    void not_evaluates_second_when_first_failed() {
      var reporter = mock(Reporter.class);

      var first = stringNodeThatFails();

      TryFunction1<String, String> second = mock();
      when(second.apply(any())).thenReturn(success(""));
      var applySecond = apply1(second, value("def"));

      var result = stepExecutor().evaluate(chain(first, applySecond), reporter);

      assertThat(result).isEqualTo(none());
      verifyReported(reporter, list(error("error")));
      verifyNoInteractions(second);
    }
  }

  @Nested
  class _injection_node {
    @Test
    void evaluation_reports_nothing() {
      var reporter = mock(Reporter.class);

      Dag<TryFunction1<String, String>> append = inject(AppendWithInjected.class);
      stepExecutor(":suffix").evaluate(append, reporter);

      verifyNoInteractions(reporter);
    }

    @Test
    void injected_function_can_be_applied() {
      var reporter = mock(Reporter.class);

      var result = apply1(AppendWithInjected.class, value("abc"));
      var evaluation = stepExecutor(":suffix").evaluate(result, reporter);

      assertThat(evaluation).isEqualTo(some("abc:suffix"));
    }

    static class AppendWithInjected implements TryFunction1<String, String> {
      private final String string;

      @Inject
      public AppendWithInjected(String string) {
        this.string = string;
      }

      @Override
      public Try<String> apply(String arg) {
        return success(arg + string);
      }
    }
  }

  @Nested
  class _maybe_node {
    @Test
    void with_maybe_function_that_returns_some() {
      var reporter = mock(Reporter.class);
      var graph = applyMaybeFunction(MaybeFunctionReturningSome.class, value(3));

      var result = stepExecutor().evaluate(graph, reporter);

      assertThat(result).isEqualTo(some("3"));
      verifyNoInteractions(reporter);
    }

    @Test
    void with_maybe_function_that_returns_none() {
      var reporter = mock(Reporter.class);
      var graph = applyMaybeFunction(MaybeFunctionReturningNone.class, value(3));

      var result = stepExecutor().evaluate(graph, reporter);

      assertThat(result).isEqualTo(none());
      verifyNoInteractions(reporter);
    }

    @Test
    void with_maybe_function_node_which_fails_evaluation() {
      var reporter = mock(Reporter.class);
      var graph = applyMaybeFunction(maybeFunctionNodeThatFails(), value("abc"));

      var result = stepExecutor().evaluate(graph, reporter);

      assertThat(result).isEqualTo(none());
    }

    private static class MaybeFunctionReturningSome implements MaybeFunction<Integer, String> {
      @Override
      public Maybe<String> apply(Integer integer) {
        return some(integer.toString());
      }
    }

    private static class MaybeFunctionReturningNone implements MaybeFunction<Integer, String> {
      @Override
      public Maybe<String> apply(Integer integer) {
        return none();
      }
    }
  }

  @Nested
  class _application0_node {
    @Test
    void with_function_that_returns_success() {
      var reporter = mock(Reporter.class);
      Dag<TryFunction0<String>> tryFunction = value(() -> success("success", info("message")));

      var result = stepExecutor().evaluate(apply0(tryFunction), reporter);

      assertThat(result).isEqualTo(some("success"));
      verifyReported(reporter, list(info("message")));
    }

    @Test
    void with_function_that_returns_failure() {
      var reporter = mock(Reporter.class);
      Dag<TryFunction0<String>> tryFunction = value(() -> failure(error("message")));

      var result = stepExecutor().evaluate(apply0(tryFunction), reporter);

      assertThat(result).isEqualTo(none());
      verifyReported(reporter, list(error("message")));
    }

    @Test
    void with_function_node_which_evaluation_fails() {
      var reporter = mock(Reporter.class);
      Dag<TryFunction0<String>> tryFunction = tryFunction0NodeThatFails();

      var result = stepExecutor().evaluate(apply0(tryFunction), reporter);

      assertThat(result).isEqualTo(none());
      verifyReported(reporter, list(error("error")));
    }
  }

  @Nested
  class _application1_node {
    @Test
    void with_function_that_returns_success() {
      var reporter = mock(Reporter.class);
      var apply1 = apply1(s -> success(s.toUpperCase(Locale.ROOT), info("message")), value("abc"));

      var result = stepExecutor().evaluate(apply1, reporter);

      assertThat(result).isEqualTo(some("ABC"));
      verifyReported(reporter, list(info("message")));
    }

    @Test
    void with_function_that_returns_failure() {
      var reporter = mock(Reporter.class);
      var apply1 = apply1(s -> failure(error("message")), value("abc"));

      var result = stepExecutor().evaluate(apply1, reporter);

      assertThat(result).isEqualTo(none());
      verifyReported(reporter, list(error("message")));
    }

    @Test
    void with_function_node_which_evaluation_fails() {
      var reporter = mock(Reporter.class);
      var apply1 = apply1(tryFunction1NodeThatFails(), value("abc"));

      var result = stepExecutor().evaluate(apply1, reporter);

      assertThat(result).isEqualTo(none());
      verifyReported(reporter, list(error("error")));
    }

    @Test
    void with_function_created_by_other_function() {
      var reporter = mock(Reporter.class);
      TryFunction1<String, TryFunction1<String, String>> factory =
          s1 -> success(s2 -> success(s2 + s1, info("appender")), info("factory"));
      var appender = apply1(factory, value(":suffix"));
      var apply1 = apply1(appender, value("string"));

      var result = stepExecutor().evaluate(apply1, reporter);

      assertThat(result).isEqualTo(some("string:suffix"));
      var inOrder = inOrder(reporter);
      inOrder.verify(reporter).report(report(label(), "", EXECUTION, list(info("factory"))));
      inOrder.verify(reporter).report(report(label(), "", EXECUTION, list(info("appender"))));
      verifyNoMoreInteractions(reporter);
    }
  }

  @Nested
  class _application2_node {
    @Test
    void with_function_that_returns_success() {
      var reporter = mock(Reporter.class);
      var apply2 =
          apply2((a, b) -> success(a + ":" + b, info("message")), value("abc"), value("def"));

      var result = stepExecutor().evaluate(apply2, reporter);

      assertThat(result).isEqualTo(some("abc:def"));
      verifyReported(reporter, list(info("message")));
    }

    @Test
    void with_function_that_returns_failure() {
      var reporter = mock(Reporter.class);
      var apply2 = apply2((a, b) -> failure(error("message")), value("abc"), value("def"));

      var result = stepExecutor().evaluate(apply2, reporter);

      assertThat(result).isEqualTo(none());
      verifyReported(reporter, list(error("message")));
    }

    @Test
    void with_function_node_which_evaluation_fails() {
      var reporter = mock(Reporter.class);
      var apply2 = apply2(tryFunction2NodeThatFails(), value("abc"), value("def"));

      var result = stepExecutor().evaluate(apply2, reporter);

      assertThat(result).isEqualTo(none());
      verifyReported(reporter, list(error("error")));
    }
  }

  @Nested
  class _evaluation_node {
    @Test
    void with_wrapped_value_node() {
      var reporter = mock(Reporter.class);
      Dag<Dag<String>> dag = apply0(value(() -> success(value("abc"))));
      var evaluate = evaluate(dag);

      var result = stepExecutor().evaluate(evaluate, reporter);

      assertThat(result).isEqualTo(some("abc"));
    }
  }

  @Nested
  class _prefix_node {
    @Test
    void prefixes_labels() {
      var reporter = mock(Reporter.class);
      var evaluate = prefix(label("name"), stringNodeThatFails());

      stepExecutor().evaluate(evaluate, reporter);

      verify(reporter).report(report(label("name"), "", EXECUTION, list(error("error"))));
      verifyNoMoreInteractions(reporter);
    }

    @Test
    void prefixes_labels_twice() {
      var reporter = mock(Reporter.class);
      var evaluate = prefix(label("outer"), prefix(label("inner"), stringNodeThatFails()));

      stepExecutor().evaluate(evaluate, reporter);

      verify(reporter).report(report(label("outer", "inner"), "", EXECUTION, list(error("error"))));
      verifyNoMoreInteractions(reporter);
    }
  }

  private static void verifyReported(Reporter reporter, List<Log> logs) {
    verify(reporter).report(report(label(), "", EXECUTION, logs));
    verifyNoMoreInteractions(reporter);
  }

  private static DagEvaluator stepExecutor() {
    return new DagEvaluator(Guice.createInjector());
  }

  private static DagEvaluator stepExecutor(String string) {
    return new DagEvaluator(Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(String.class).toInstance(string);
      }
    }));
  }

  private static Dag<MaybeFunction<String, String>> maybeFunctionNodeThatFails() {
    return apply0(MaybeFunctionFunctionThatFails.class);
  }

  private static class MaybeFunctionFunctionThatFails
      implements TryFunction0<MaybeFunction<String, String>> {
    @Override
    public Try<MaybeFunction<String, String>> apply() {
      return failure(error("error"));
    }
  }

  private static Dag<TryFunction2<String, String, String>> tryFunction2NodeThatFails() {
    return apply0(TryFunction2FunctionThatFails.class);
  }

  private static class TryFunction2FunctionThatFails
      implements TryFunction0<TryFunction2<String, String, String>> {
    @Override
    public Try<TryFunction2<String, String, String>> apply() {
      return failure(error("error"));
    }
  }

  private static Dag<TryFunction1<String, String>> tryFunction1NodeThatFails() {
    return apply0(TryFunction1FunctionThatFails.class);
  }

  private static class TryFunction1FunctionThatFails
      implements TryFunction0<TryFunction1<String, String>> {
    @Override
    public Try<TryFunction1<String, String>> apply() {
      return failure(error("error"));
    }
  }

  private static Dag<TryFunction0<String>> tryFunction0NodeThatFails() {
    return apply0(TryFunction0FunctionThatFails.class);
  }

  private static class TryFunction0FunctionThatFails implements TryFunction0<TryFunction0<String>> {
    @Override
    public Try<TryFunction0<String>> apply() {
      return failure(error("error"));
    }
  }

  private static Dag<String> stringNodeThatFails() {
    return apply0(StringFunctionThatFails.class);
  }

  private static class StringFunctionThatFails implements TryFunction0<String> {
    @Override
    public Try<String> apply() {
      return failure(error("error"));
    }
  }
}
