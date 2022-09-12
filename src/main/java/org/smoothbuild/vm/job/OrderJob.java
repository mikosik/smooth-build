package org.smoothbuild.vm.job;

import static org.smoothbuild.vm.execute.TaskKind.ORDER;

import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.algorithm.OrderAlgorithm;
import org.smoothbuild.vm.execute.TaskInfo;

public class OrderJob extends ExecutingJob {
  private final OrderB orderB;

  public OrderJob(OrderB orderB, ExecutionContext context) {
    super(context);
    this.orderB = orderB;
  }

  @Override
  protected Promise<ValB> evaluateImpl() {
    var algorithm = new OrderAlgorithm(orderB.type());
    var taskInfo = new TaskInfo(ORDER, context().infoFor(orderB));
    return evaluateTransitively(taskInfo, algorithm, orderB.elems());
  }
}
