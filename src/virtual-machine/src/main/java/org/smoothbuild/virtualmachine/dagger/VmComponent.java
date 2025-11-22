package org.smoothbuild.virtualmachine.dagger;

import dagger.BindsInstance;
import dagger.Subcomponent;
import org.smoothbuild.virtualmachine.evaluate.BEvaluateTask;
import org.smoothbuild.virtualmachine.evaluate.base.BExprAttributes;
import org.smoothbuild.virtualmachine.evaluate.job.JobContext;

@Subcomponent
@PerVm
public interface VmComponent {
  BEvaluateTask bEvaluateTask();

  JobContext jobContext();

  @Subcomponent.Builder
  interface Builder {
    @BindsInstance
    Builder bExprAttributes(BExprAttributes bExprAttributes);

    VmComponent build();
  }
}
