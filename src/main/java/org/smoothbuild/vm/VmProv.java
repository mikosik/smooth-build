package org.smoothbuild.vm;

import javax.inject.Inject;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.lang.base.Nal;
import org.smoothbuild.vm.job.JobCreatorProv;
import org.smoothbuild.vm.parallel.ParallelJobExecutor;

import com.google.common.collect.ImmutableMap;

public class VmProv {
  private final JobCreatorProv jobCreatorProv;
  private final ParallelJobExecutor parallelExecutor;

  @Inject
  public VmProv(JobCreatorProv jobCreatorProv, ParallelJobExecutor parallelExecutor) {
    this.jobCreatorProv = jobCreatorProv;
    this.parallelExecutor = parallelExecutor;
  }

  public Vm get(ImmutableMap<ExprB, Nal> nals) {
    return new Vm(jobCreatorProv.get(nals), parallelExecutor);
  }
}
