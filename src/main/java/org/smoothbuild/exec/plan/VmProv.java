package org.smoothbuild.exec.plan;

import javax.inject.Inject;

import org.smoothbuild.db.bytecode.obj.base.ObjB;
import org.smoothbuild.exec.parallel.ParallelJobExecutor;
import org.smoothbuild.lang.base.define.Nal;

import com.google.common.collect.ImmutableMap;

public class VmProv {
  private final JobCreatorProvider jobCreatorProvider;
  private final ParallelJobExecutor parallelExecutor;

  @Inject
  public VmProv(JobCreatorProvider jobCreatorProvider, ParallelJobExecutor parallelExecutor) {
    this.jobCreatorProvider = jobCreatorProvider;
    this.parallelExecutor = parallelExecutor;
  }

  public Vm get(ImmutableMap<ObjB, Nal> nals) {
    return new Vm(jobCreatorProvider.get(nals), parallelExecutor);
  }
}
