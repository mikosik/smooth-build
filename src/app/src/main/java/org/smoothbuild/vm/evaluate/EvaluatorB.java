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
import org.smoothbuild.vm.evaluate.job.JobCreator;

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
    var jobCreator = jobCreator();
    var jobs = map(exprs, jobCreator::jobFor);
    return pullUp(evaluate(context, jobs));
  }

  // Visible for testing
  protected JobCreator jobCreator() {
    return new JobCreator();
  }

  // Visible for testing
  public static ImmutableList<Optional<ValueB>> evaluate(ExecutionContext context,
      ImmutableList<Job> jobs) throws InterruptedException {
    var evaluationResults = map(jobs, j -> j.evaluate(context));
    var executor = context.taskExecutor();
    runWhenAllAvailable(evaluationResults, executor::terminate);
    executor.awaitTermination();
    return map(evaluationResults, r -> Optional.ofNullable(r.get()));
  }
}
