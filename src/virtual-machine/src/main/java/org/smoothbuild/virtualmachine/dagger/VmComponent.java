package org.smoothbuild.virtualmachine.dagger;

import dagger.BindsInstance;
import dagger.Subcomponent;
import org.smoothbuild.virtualmachine.evaluate.BEvaluateTask;
import org.smoothbuild.virtualmachine.evaluate.base.BExprAttributes;

@Subcomponent
@PerVm
public interface VmComponent {
  BEvaluateTask bEvaluateTask();

  @Subcomponent.Builder
  interface Builder {
    @BindsInstance
    Builder bExprAttributes(BExprAttributes bExprAttributes);

    VmComponent build();
  }
}
