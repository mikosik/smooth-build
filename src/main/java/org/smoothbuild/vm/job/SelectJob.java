package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.vm.execute.TaskKind.SELECT;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.algorithm.SelectAlgorithm;
import org.smoothbuild.vm.execute.TaskInfo;

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
    var algorithm = new SelectAlgorithm(outputT(selectable, index));
    var taskInfo = new TaskInfo(SELECT, context().infoFor(selectB));
    return evaluateTransitively(taskInfo, algorithm, list(selectable, index));
  }

  private static TypeB outputT(ExprB selectable, IntB index) {
    return ((TupleTB) selectable.type()).items().get(index.toJ().intValue());
  }
}
