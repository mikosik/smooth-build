package org.smoothbuild.vm.job;

import java.util.function.Consumer;

import org.smoothbuild.bytecode.expr.val.BoolB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

public class IfJob extends AbstractJob {
  private final Job conditionJ;
  private final Job thenJ;
  private final Job elseJ;

  public IfJob(TypeB type, Job conditionJ, Job thenJ, Job elseJ, Loc loc) {
    super(type, loc);
    this.conditionJ = conditionJ;
    this.thenJ = thenJ;
    this.elseJ = elseJ;
  }

  @Override
  public Promise<ValB> scheduleImpl(Worker worker) {
    var res = new PromisedValue<ValB>();
    conditionJ.schedule(worker)
        .addConsumer(val -> onConditionCalculated(val, worker, res));
    return res;
  }

  private void onConditionCalculated(ValB condition, Worker worker, Consumer<ValB> res) {
    var conditionJ = ((BoolB) condition).toJ();
    var job = conditionJ ? thenJ : elseJ;
    job.schedule(worker)
        .addConsumer(res);
  }
}
