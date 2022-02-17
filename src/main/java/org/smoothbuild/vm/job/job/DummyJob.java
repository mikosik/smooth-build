package org.smoothbuild.vm.job.job;

import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

public class DummyJob extends AbstractJob {
  private final PromisedValue<ValB> promisedValue;

  public DummyJob(ValB val, Loc loc) {
    super(val.type(), loc);
    this.promisedValue = new PromisedValue<>();
    this.promisedValue.accept(val);
  }

  @Override
  protected Promise<ValB> scheduleImpl(Worker worker) {
    return promisedValue;
  }
}
