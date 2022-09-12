package org.smoothbuild.vm.job;

import java.util.function.Consumer;

import org.smoothbuild.bytecode.expr.oper.IfB;
import org.smoothbuild.bytecode.expr.val.BoolB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

public class IfJob extends ExecutingJob {
  private final IfB ifB;
  private final ExecutionContext context;

  public IfJob(IfB ifB, ExecutionContext context) {
    super(context);
    this.ifB = ifB;
    this.context = context;
  }

  @Override
  public Promise<ValB> evaluateImpl() {
    var res = new PromisedValue<ValB>();
    context.jobFor(ifB.data().condition())
        .evaluate()
        .addConsumer(val -> onConditionCalculated(val, res));
    return res;
  }

  private void onConditionCalculated(ValB condition, Consumer<ValB> res) {
    var conditionJ = ((BoolB) condition).toJ();
    IfB.Data data = ifB.data();
    context.jobFor(conditionJ ? data.then() : data.else_())
        .evaluate()
        .addConsumer(res);
  }
}
