package org.smoothbuild.vm.job;

import static org.smoothbuild.vm.execute.TaskKind.CONST;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.execute.TaskInfo;

public class ConstJob extends DummyJob {
  private final InstB inst;

  public ConstJob(InstB inst, ExecutionContext context) {
    super(createTaskInfo(inst, context), context.reporter());
    this.inst = inst;
  }

  private static TaskInfo createTaskInfo(InstB val, ExecutionContext context) {
    return new TaskInfo(CONST, context.tagLoc(val));
  }

  @Override
  protected Promise<InstB> resultPromise() {
    return new PromisedValue<>(inst);
  }
}
