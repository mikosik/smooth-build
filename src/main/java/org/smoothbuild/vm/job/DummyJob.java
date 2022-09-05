package org.smoothbuild.vm.job;

import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

public class DummyJob extends AbstractJob {
  private final PromisedValue<ValB> promisedValue;

  public DummyJob(ValB valB, Loc loc) {
    super(valB.type(), loc);
    this.promisedValue = new PromisedValue<>();
    this.promisedValue.accept(valB);
  }

  @Override
  protected Promise<ValB> scheduleImpl(Worker worker) {
    return promisedValue;
  }
}
