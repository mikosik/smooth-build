package org.smoothbuild.vm.job;

import static org.smoothbuild.vm.execute.TaskKind.CONST;

import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.execute.TaskInfo;

public class ConstJob extends DummyJob {
  private final ValB val;

  public ConstJob(ValB val, ExecutionContext context) {
    super(createTaskInfo(val, context), context.reporter());
    this.val = val;
  }

  private static TaskInfo createTaskInfo(ValB val, ExecutionContext context) {
    return new TaskInfo(CONST, context.labeledLoc(val));
  }

  @Override
  protected Promise<ValB> resultPromise() {
    return new PromisedValue<>(val);
  }
}
