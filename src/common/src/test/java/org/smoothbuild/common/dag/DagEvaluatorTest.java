package org.smoothbuild.common.dag;

import static com.google.common.truth.Truth.assertThat;
import static com.google.inject.multibindings.Multibinder.newSetBinder;
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
import org.smoothbuild.common.init.Initializable;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.log.report.Trace;

class DagEvaluatorTest {
  @Nested
  class _value_node {
    @Test
    void evaluation_is_equal_to_value() {
      var reporter = mock(Reporter.class);
      var value = value("abc");

      var result = dagEvaluator(reporter).evaluate(value);

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

      var result = dagEvaluator(reporter).evaluate(chain(applyFirst, applySecond));

      assertThat(result).isEqualTo(some("abc"));
    }

    @Test
    void not_evaluates_second_when_first_failed() {
      var reporter = mock(Reporter.class);

      var first = stringNodeThatFails();

      TryFunction1<String, String> second = mock();
      when(second.apply(any())).thenReturn(success(""));
      var applySecond = apply1(second, value("def"));

      var result = dagEvaluator(reporter).evaluate(chain(first, applySecond));

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
      dagEvaluator(reporter, ":suffix").evaluate(append);

      verifyNoInteractions(reporter);
    }

    @Test
    void injected_function_can_be_applied() {
      var reporter = mock(Reporter.class);

      var result = apply1(AppendWithInjected.class, value("abc"));
      var evaluation = dagEvaluator(reporter, ":suffix").evaluate(result);

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

      var result = dagEvaluator(reporter).evaluate(graph);

      assertThat(result).isEqualTo(some("3"));
      verifyNoInteractions(reporter);
    }

    @Test
    void with_maybe_function_that_returns_none() {
      var reporter = mock(Reporter.class);
      var graph = applyMaybeFunction(MaybeFunctionReturningNone.class, value(3));

      var result = dagEvaluator(reporter).evaluate(graph);

      assertThat(result).isEqualTo(none());
      verifyNoInteractions(reporter);
    }

    @Test
    void with_maybe_function_node_which_fails_evaluation() {
      var reporter = mock(Reporter.class);
      var graph = applyMaybeFunction(maybeFunctionNodeThatFails(), value("abc"));

      var result = dagEvaluator(reporter).evaluate(graph);

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
      Dag<TryFunction0<String>> tryFunction = value(new ReturnSuccessString());

      var result = dagEvaluator(reporter).evaluate(apply0(tryFunction));

      assertThat(result).isEqualTo(some("success"));
      verifyReported(label("returnSuccessString"), reporter, list(info("message")));
    }

    public static class ReturnSuccessString implements TryFunction0<String> {
      @Override
      public Try<String> apply() {
        return success("success", info("message"));
      }

      @Override
      public Label label() {
        return Label.label("returnSuccessString");
      }
    }

    @Test
    void with_function_that_returns_failure() {
      var reporter = mock(Reporter.class);
      Dag<TryFunction0<String>> tryFunction = value(() -> failure(error("message")));

      var result = dagEvaluator(reporter).evaluate(apply0(tryFunction));

      assertThat(result).isEqualTo(none());
      verifyReported(reporter, list(error("message")));
    }

    @Test
    void with_function_node_which_evaluation_fails() {
      var reporter = mock(Reporter.class);
      Dag<TryFunction0<String>> tryFunction = tryFunction0NodeThatFails();

      var result = dagEvaluator(reporter).evaluate(apply0(tryFunction));

      assertThat(result).isEqualTo(none());
      verifyReported(reporter, list(error("error")));
    }
  }

  @Nested
  class _application1_node {
    @Test
    void with_function_that_returns_success() {
      var reporter = mock(Reporter.class);
      var apply1 = apply1(new ToUpperCase(), value("abc"));

      var result = dagEvaluator(reporter).evaluate(apply1);

      assertThat(result).isEqualTo(some("ABC"));
      verifyReported(label("toUpperCase"), reporter, list(info("message")));
    }

    public static class ToUpperCase implements TryFunction1<String, String> {
      @Override
      public Try<String> apply(String string) {
        return success(string.toUpperCase(Locale.ROOT), info("message"));
      }

      @Override
      public Label label() {
        return Label.label("toUpperCase");
      }
    }

    @Test
    void with_function_that_returns_failure() {
      var reporter = mock(Reporter.class);
      var apply1 = apply1(s -> failure(error("message")), value("abc"));

      var result = dagEvaluator(reporter).evaluate(apply1);

      assertThat(result).isEqualTo(none());
      verifyReported(reporter, list(error("message")));
    }

    @Test
    void with_function_node_which_evaluation_fails() {
      var reporter = mock(Reporter.class);
      var apply1 = apply1(tryFunction1NodeThatFails(), value("abc"));

      var result = dagEvaluator(reporter).evaluate(apply1);

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

      var result = dagEvaluator(reporter).evaluate(apply1);

      assertThat(result).isEqualTo(some("string:suffix"));
      var inOrder = inOrder(reporter);
      inOrder
          .verify(reporter)
          .report(report(label(), new Trace<>(), EXECUTION, list(info("factory"))));
      inOrder
          .verify(reporter)
          .report(report(label(), new Trace<>(), EXECUTION, list(info("appender"))));
      verifyNoMoreInteractions(reporter);
    }
  }

  @Nested
  class _application2_node {
    @Test
    void with_function_that_returns_success() {
      var reporter = mock(Reporter.class);
      var apply2 = apply2(new Concatenate(), value("abc"), value("def"));

      var result = dagEvaluator(reporter).evaluate(apply2);

      assertThat(result).isEqualTo(some("abc:def"));
      verifyReported(label("concatenate"), reporter, list(info("message")));
    }

    public static class Concatenate implements TryFunction2<String, String, String> {
      @Override
      public Try<String> apply(String string1, String string2) {
        return success(string1 + ":" + string2, info("message"));
      }

      @Override
      public Label label() {
        return Label.label("concatenate");
      }
    }

    @Test
    void with_function_that_returns_failure() {
      var reporter = mock(Reporter.class);
      var apply2 = apply2((a, b) -> failure(error("message")), value("abc"), value("def"));

      var result = dagEvaluator(reporter).evaluate(apply2);

      assertThat(result).isEqualTo(none());
      verifyReported(reporter, list(error("message")));
    }

    @Test
    void with_function_node_which_evaluation_fails() {
      var reporter = mock(Reporter.class);
      var apply2 = apply2(tryFunction2NodeThatFails(), value("abc"), value("def"));

      var result = dagEvaluator(reporter).evaluate(apply2);

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

      var result = dagEvaluator(reporter).evaluate(evaluate);

      assertThat(result).isEqualTo(some("abc"));
    }
  }

  private static void verifyReported(Reporter reporter, List<Log> logs) {
    verifyReported(label(), reporter, logs);
  }

  private static void verifyReported(Label label, Reporter reporter, List<Log> logs) {
    verify(reporter).report(report(label, new Trace<>(), EXECUTION, logs));
    verifyNoMoreInteractions(reporter);
  }

  private static DagEvaluator dagEvaluator(Reporter reporter) {
    return new DagEvaluator(Guice.createInjector(), reporter);
  }

  private static DagEvaluator dagEvaluator(Reporter reporter, String string) {
    var injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        binder().bind(String.class).toInstance(string);
      }
    });
    return new DagEvaluator(injector, reporter);
  }

  private static DagEvaluator dagEvaluator(Reporter reporter, Initializable initializable) {
    var injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        var setBinder = newSetBinder(binder(), Initializable.class);
        setBinder.addBinding().toInstance(initializable);
      }
    });
    return new DagEvaluator(injector, reporter);
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
