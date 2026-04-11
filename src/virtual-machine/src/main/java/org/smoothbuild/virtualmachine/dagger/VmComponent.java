package org.smoothbuild.virtualmachine.dagger;

import dagger.BindsInstance;
import dagger.Subcomponent;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.virtualmachine.evaluate.BEvaluateTask;
import org.smoothbuild.virtualmachine.evaluate.base.DebugSymbols;
import org.smoothbuild.virtualmachine.evaluate.job.JobContext;

@Subcomponent
@PerVm
public interface VmComponent {
  BEvaluateTask bEvaluateTask();

  JobContext jobContext();

  @Subcomponent.Builder
  interface Builder {
    @BindsInstance
    Builder debugSymbols(Map<Hash, DebugSymbols> debugSymbols);

    VmComponent build();
  }
}
