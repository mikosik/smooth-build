package org.smoothbuild.exec.compute;

import static org.smoothbuild.exec.compute.TaskKind.CALL;
import static org.smoothbuild.lang.base.define.Function.PARENTHESES;
import static org.smoothbuild.lang.base.define.IfFunction.IF_FUNCTION_NAME;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

public class IfJob extends AbstractJob {
  private static final String IF_TASK_NAME = IF_FUNCTION_NAME + PARENTHESES;

  public IfJob(Type type, List<Job> dependencies, Location location) {
    super(type, dependencies, new Nal("building:" + IF_TASK_NAME, location));
  }

  @Override
  public Promise<Val> schedule(Worker worker) {
    PromisedValue<Val> result = new PromisedValue<>();
    conditionJob()
        .schedule(worker)
        .addConsumer(obj -> onConditionCalculated(obj, worker, result));
    return result;
  }

  private void onConditionCalculated(Val conditionVal, Worker worker, Consumer<Val> result) {
    boolean condition = ((Bool) conditionVal).jValue();
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
