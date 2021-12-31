package org.smoothbuild.vm.job.job;

import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

public interface Job extends Nal {
  public TypeB type();

  public Promise<ValB> schedule(Worker worker);
}
