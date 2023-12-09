package org.smoothbuild.cli.base;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.run.step.Step.step;

import com.google.inject.Injector;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.out.report.Console;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.run.step.Step;
import org.smoothbuild.run.step.StepExecutor;
import org.smoothbuild.vm.bytecode.BytecodeException;

public class RunStepExecutor {
  public static <T> Integer runStepExecutor(Injector injector, Step<T, String> step, T argument) {
    var reporter = injector.getInstance(Reporter.class);
    var console = injector.getInstance(Console.class);
    try {
      Maybe<Tuple0> message = injector
          .getInstance(StepExecutor.class)
          .execute(step.then(step(PrintResult.class)), argument, reporter);
      reporter.printSummary();
      return message.map(v -> EXIT_CODE_SUCCESS).getOr(EXIT_CODE_ERROR);
    } catch (BytecodeException e) {
      console.error(e.getMessage());
      return EXIT_CODE_ERROR;
    }
  }
}
