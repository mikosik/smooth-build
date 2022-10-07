package org.smoothbuild.vm;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Optionals.pullUp;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.compile.lang.base.LabeledLoc;
import org.smoothbuild.vm.execute.TaskExecutor;
import org.smoothbuild.vm.job.ExecutionContext;
import org.smoothbuild.vm.job.Job;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Vm {
  private final Provider<ExecutionContext> contextProv;

  @Inject
  public Vm(Provider<ExecutionContext> contextProv) {
    this.contextProv = contextProv;
  }

  public Optional<ImmutableList<InstB>> evaluate(ImmutableList<ExprB> exprs,
      ImmutableMap<ExprB, LabeledLoc> labels)
      throws InterruptedException {
    var context = contextProv.get().withLabels(labels);
    var executor = context.taskExecutor();
    var jobs = map(exprs, context::jobFor);
    return pullUp(evaluate(executor, jobs));
  }

  // Visible for testing
  public static ImmutableList<Optional<InstB>> evaluate(TaskExecutor executor, List<Job> jobs)
      throws InterruptedException {
    var evaluationResults = map(jobs, Job::evaluate);
    runWhenAllAvailable(evaluationResults, executor::terminate);
    executor.awaitTermination();
    return map(evaluationResults, r -> Optional.ofNullable(r.get()));
  }
}
