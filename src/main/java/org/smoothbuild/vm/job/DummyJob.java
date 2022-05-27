package org.smoothbuild.vm.job;

import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

public class DummyJob extends AbstractJob {
  private final PromisedValue<CnstB> promisedValue;

  public DummyJob(CnstB cnst, Loc loc) {
    super(cnst.type(), loc);
    this.promisedValue = new PromisedValue<>();
    this.promisedValue.accept(cnst);
  }

  @Override
  protected Promise<CnstB> scheduleImpl(Worker worker) {
    return promisedValue;
  }
}
