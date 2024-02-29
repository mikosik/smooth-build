package org.smoothbuild.cli.base;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.common.step.Step.tryStep;

import com.google.inject.Injector;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.step.Step;
import org.smoothbuild.common.step.StepExecutor;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.out.report.Reporter;

public class RunStepExecutor {
  public static <T> Integer runStepExecutor(Injector injector, Step<T, String> step, T argument) {
    var reporter = injector.getInstance(Reporter.class);
    Maybe<Tuple0> message = injector
        .getInstance(StepExecutor.class)
        .execute(step.then(tryStep(ReportResult.class)), argument, reporter);
    reporter.printSummary();
    return message.map(v -> EXIT_CODE_SUCCESS).getOr(EXIT_CODE_ERROR);
  }
}
