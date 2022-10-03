package org.smoothbuild.vm.job;

import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.val.InstB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.task.OrderTask;

public class OrderJob extends ExecutingJob {
  private final OrderB orderB;

  public OrderJob(OrderB orderB, ExecutionContext context) {
    super(context);
    this.orderB = orderB;
  }

  @Override
  protected Promise<InstB> evaluateImpl() {
    var task = new OrderTask(orderB.type(), context().labeledLoc(orderB));
    return evaluateTransitively(task, orderB.elems());
  }
}
