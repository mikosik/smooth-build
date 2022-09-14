package org.smoothbuild.vm.job;

import static org.smoothbuild.vm.execute.TaskKind.ORDER;

import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.execute.TaskInfo;
import org.smoothbuild.vm.task.OrderTask;

public class OrderJob extends ExecutingJob {
  private final OrderB orderB;

  public OrderJob(OrderB orderB, ExecutionContext context) {
    super(context);
    this.orderB = orderB;
  }

  @Override
  protected Promise<ValB> evaluateImpl() {
    var task = new OrderTask(orderB.type());
    var taskInfo = new TaskInfo(ORDER, context().infoFor(orderB));
    return evaluateTransitively(taskInfo, task, orderB.elems());
  }
}
