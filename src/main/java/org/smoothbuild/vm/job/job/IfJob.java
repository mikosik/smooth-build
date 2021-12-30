package org.smoothbuild.vm.job.job;

import static org.smoothbuild.lang.base.define.FuncS.PARENTHESES;
import static org.smoothbuild.lang.base.define.IfFuncS.IF_FUNCTION_NAME;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.function.Consumer;

import org.smoothbuild.db.bytecode.obj.val.BoolB;
import org.smoothbuild.db.bytecode.obj.val.ValB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.define.NalImpl;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

public class IfJob extends AbstractJob {
  private static final String IF_TASK_NAME = IF_FUNCTION_NAME + PARENTHESES;
  private final Job conditionJ;
  private final Job thenJ;
  private final Job elseJ;

  public IfJob(TypeB type, Job conditionJ, Job thenJ, Job elseJ, Loc loc) {
    super(type, list(conditionJ, thenJ, elseJ), new NalImpl("building:" + IF_TASK_NAME, loc));
    this.conditionJ = conditionJ;
    this.thenJ = thenJ;
    this.elseJ = elseJ;
  }

  @Override
  public Promise<ValB> schedule(Worker worker) {
    var res = new PromisedValue<ValB>();
    conditionJ.schedule(worker)
        .addConsumer(obj -> onConditionCalculated(obj, worker, res));
    return res;
  }

  private void onConditionCalculated(ValB condition, Worker worker, Consumer<ValB> res) {
    var conditionJ = ((BoolB) condition).toJ();
    var job = conditionJ ? thenJ : elseJ;
    new VirtualJob(job, new TaskInfo(TaskKind.INTERNAL, IF_TASK_NAME, loc()))
        .schedule(worker)
        .addConsumer(res);
  }
}
