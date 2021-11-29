package org.smoothbuild.exec.job;

import static org.smoothbuild.exec.job.TaskKind.CALL;
import static org.smoothbuild.lang.base.define.FuncS.PARENTHESES;
import static org.smoothbuild.lang.base.define.IfFuncS.IF_FUNCTION_NAME;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.define.NalImpl;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

public class IfJob extends AbstractJob {
  private static final String IF_TASK_NAME = IF_FUNCTION_NAME + PARENTHESES;

  public IfJob(TypeH type, List<Job> deps, Loc loc) {
    super(type, deps, new NalImpl("building:" + IF_TASK_NAME, loc));
  }

  @Override
  public Promise<ValueH> schedule(Worker worker) {
    var res = new PromisedValue<ValueH>();
    conditionJob()
        .schedule(worker)
        .addConsumer(obj -> onConditionCalculated(obj, worker, res));
    return res;
  }

  private void onConditionCalculated(ValueH conditionH, Worker worker, Consumer<ValueH> res) {
    var conditionJ = ((BoolH) conditionH).toJ();
    var job = conditionJ ? thenJob() : elseJob();
    new VirtualJob(job, new TaskInfo(CALL, IF_TASK_NAME, loc()))
        .schedule(worker)
        .addConsumer(res);
  }

  private Job conditionJob() {
    return dependencies().get(0);
  }

  private Job thenJob() {
    return dependencies().get(1);
  }

  private Job elseJob() {
    return dependencies().get(2);
  }
}
