package org.smoothbuild.exec.job;

import static org.smoothbuild.exec.job.TaskKind.CALL;
import static org.smoothbuild.lang.base.define.FunctionS.PARENTHESES;
import static org.smoothbuild.lang.base.define.IfFunction.IF_FUNCTION_NAME;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.NalImpl;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

public class IfJob extends AbstractJob {
  private static final String IF_TASK_NAME = IF_FUNCTION_NAME + PARENTHESES;

  public IfJob(TypeS type, List<Job> dependencies, Location location) {
    super(type, dependencies, new NalImpl("building:" + IF_TASK_NAME, location));
  }

  @Override
  public Promise<ValueH> schedule(Worker worker) {
    PromisedValue<ValueH> result = new PromisedValue<>();
    conditionJob()
        .schedule(worker)
        .addConsumer(obj -> onConditionCalculated(obj, worker, result));
    return result;
  }

  private void onConditionCalculated(ValueH conditionVal, Worker worker, Consumer<ValueH> result) {
    boolean condition = ((BoolH) conditionVal).jValue();
    Job job = condition ? thenJob() : elseJob();
    new VirtualJob(job, new TaskInfo(CALL, IF_TASK_NAME, location()))
        .schedule(worker)
        .addConsumer(result);
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
