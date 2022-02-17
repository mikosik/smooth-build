package org.smoothbuild.vm.job.job;

import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

public interface Job {
  public TypeB type();

  public Loc loc();

  public Promise<ValB> schedule(Worker worker);
}
