package org.smoothbuild.vm;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.Optional;

import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.cnst.CnstB;
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

  public Optional<ImmutableList<CnstB>> evaluate(ImmutableList<ObjB> objs)
      throws InterruptedException {
    var jobs = map(objs, jobCreator::eagerJobFor);
    var result = parallelExecutor.executeAll(jobs);
    return pullUp(result);
  }
}
