package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.task.SelectTask;

public class SelectJob extends ExecutingJob {
  private final SelectB selectB;

  public SelectJob(SelectB selectB, ExecutionContext context) {
    super(context);
    this.selectB = selectB;
  }

  @Override
  protected Promise<ValB> evaluateImpl() {
    var data = selectB.data();
    var selectable = data.selectable();
    var index = data.index();
    var task = new SelectTask(outputT(selectable, index), context().labeledLoc(selectB));
    return evaluateTransitively(task, list(selectable, index));
  }

  private static TypeB outputT(ExprB selectable, IntB index) {
    return ((TupleTB) selectable.type()).items().get(index.toJ().intValue());
  }
}
