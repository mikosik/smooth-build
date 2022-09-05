package org.smoothbuild.vm.job;

import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

public interface Job {
  public TypeB type();

  public Loc loc();

  public Promise<ValB> schedule(Worker worker);
}
