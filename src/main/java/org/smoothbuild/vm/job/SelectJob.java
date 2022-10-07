package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.task.SelectTask;

public class SelectJob extends ExecutingJob {
  private final SelectB selectB;

  public SelectJob(SelectB selectB, ExecutionContext context) {
    super(context);
    this.selectB = selectB;
  }

  @Override
  protected Promise<InstB> evaluateImpl() {
    var data = selectB.data();
    var selectable = data.selectable();
    var index = data.index();
    var task = new SelectTask(selectB.evalT(), context().labeledLoc(selectB));
    return evaluateTransitively(task, list(selectable, index));
  }
}
