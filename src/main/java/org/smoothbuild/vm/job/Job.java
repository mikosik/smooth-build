package org.smoothbuild.vm.job;

import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

public interface Job {
  public TypeB type();

  public Loc loc();

  public Promise<CnstB> schedule(Worker worker);
}
