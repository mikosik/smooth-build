package org.smoothbuild.vm;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.Optional;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.vm.job.JobCreator;
import org.smoothbuild.vm.parallel.ParallelJobExecutor;

import com.google.common.collect.ImmutableList;

public class Vm {
  private final JobCreator jobCreator;
  private final ParallelJobExecutor parallelExecutor;

  public Vm(JobCreator jobCreator, ParallelJobExecutor parallelExecutor) {
    this.jobCreator = jobCreator;
    this.parallelExecutor = parallelExecutor;
  }

  public Optional<ImmutableList<ValB>> evaluate(ImmutableList<ExprB> exprs)
      throws InterruptedException {
    var jobs = map(exprs, jobCreator::eagerJobFor);
    var result = parallelExecutor.executeAll(jobs);
    return pullUp(result);
  }
}
