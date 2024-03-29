package org.smoothbuild.vm.evaluate;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Optionals.pullUp;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.job.ExecutionContext;
import org.smoothbuild.vm.evaluate.job.Job;

import com.google.common.collect.ImmutableList;

public class EvaluatorB {
  private final Provider<ExecutionContext> contextProv;

  @Inject
  public EvaluatorB(Provider<ExecutionContext> contextProv) {
    this.contextProv = contextProv;
  }

  public Optional<ImmutableList<ValueB>> evaluate(ImmutableList<ExprB> exprs)
      throws InterruptedException {
    var context = contextProv.get();
    var jobs = map(exprs, context::jobFor);
    return pullUp(evaluate(context, jobs));
  }

  // Visible for testing
  public static ImmutableList<Optional<ValueB>> evaluate(ExecutionContext context,
      ImmutableList<Job> jobs) throws InterruptedException {
    var executor = context.taskExecutor();
    var evaluationResults = map(jobs, Job::evaluate);
    runWhenAllAvailable(evaluationResults, executor::terminate);
    executor.awaitTermination();
    return map(evaluationResults, r -> Optional.ofNullable(r.get()));
  }
}
