package org.smoothbuild.common.dag;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.dag.Dag.value;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.base.Try.failure;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.common.log.report.Report.report;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.init.Initializer;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.log.report.Reporter;

public class InitializingDagEvaluatorTest {
  private static final String STRING = "abc";

  @Test
  void evaluates_dag() {
    var initializer = initializer(success(null));
    var dagEvaluator = dagEvaluator();
    var reporter = mock(Reporter.class);

    var result = runEvaluate(initializer, dagEvaluator, reporter);

    assertThat(result).isEqualTo(some(STRING));
  }

  @Test
  void logs_from_failed_initializable_are_reported() {
    var error = error("message");
    var initializer = initializer(failure(error));

    var dagEvaluator = dagEvaluator();
    var reporter = mock(Reporter.class);

    var result = runEvaluate(initializer, dagEvaluator, reporter);
    verify(reporter).report(report(label("initialize"), "", EXECUTION, list(error)));
    assertThat(result).isEqualTo(none());
    verifyNoInteractions(dagEvaluator);
  }

  @Test
  void logs_from_successful_initializable_are_reported() {
    var info = info("message");
    var initializer = initializer(success(null, info));
    var dagEvaluator = dagEvaluator();
    var reporter = mock(Reporter.class);

    var result = runEvaluate(initializer, dagEvaluator, reporter);
    verify(reporter).report(report(label("initialize"), "", EXECUTION, list(info)));
    assertThat(result).isEqualTo(some(STRING));
  }

  private Initializer initializer(Try<Void> result) {
    var initializer = mock(Initializer.class);
    when(initializer.apply()).thenReturn(result);
    return initializer;
  }

  private Maybe<String> runEvaluate(
      Initializer initializer, DagEvaluator dagEvaluator, Reporter reporter) {
    var evaluator = new InitializingDagEvaluator(initializer, dagEvaluator, reporter);
    return evaluator.evaluate(value(STRING));
  }

  private static DagEvaluator dagEvaluator() {
    var dagEvaluator = mock(DagEvaluator.class);
    when(dagEvaluator.evaluate(any())).thenReturn(Maybe.some(STRING));
    return dagEvaluator;
  }
}
