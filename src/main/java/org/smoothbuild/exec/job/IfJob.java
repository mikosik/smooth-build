package org.smoothbuild.exec.job;

import static org.smoothbuild.exec.job.TaskKind.INTERNAL;
import static org.smoothbuild.lang.base.define.FuncS.PARENTHESES;
import static org.smoothbuild.lang.base.define.IfFuncS.IF_FUNCTION_NAME;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.define.NalImpl;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

public class IfJob extends AbstractJob {
  private static final String IF_TASK_NAME = IF_FUNCTION_NAME + PARENTHESES;
  private final Job conditionJ;
  private final Job thenJ;
  private final Job elseJ;

  public IfJob(TypeH type, Job conditionJ, Job thenJ, Job elseJ, Loc loc) {
    super(type, list(conditionJ, thenJ, elseJ), new NalImpl("building:" + IF_TASK_NAME, loc));
    this.conditionJ = conditionJ;
    this.thenJ = thenJ;
    this.elseJ = elseJ;
  }

  @Override
  public Promise<ValH> schedule(Worker worker) {
    var res = new PromisedValue<ValH>();
    conditionJ.schedule(worker)
        .addConsumer(obj -> onConditionCalculated(obj, worker, res));
    return res;
  }

  private void onConditionCalculated(ValH condition, Worker worker, Consumer<ValH> res) {
    var conditionJ = ((BoolH) condition).toJ();
    var job = conditionJ ? thenJ : elseJ;
    new VirtualJob(job, new TaskInfo(INTERNAL, IF_TASK_NAME, loc()))
        .schedule(worker)
        .addConsumer(res);
  }
}
