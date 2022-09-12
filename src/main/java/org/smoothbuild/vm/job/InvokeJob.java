package org.smoothbuild.vm.job;

import static org.smoothbuild.vm.execute.TaskKind.INVOKE;

import org.smoothbuild.bytecode.expr.oper.InvokeB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.algorithm.InvokeAlgorithm;
import org.smoothbuild.vm.execute.TaskInfo;

public class InvokeJob extends ExecutingJob {
  private static final String PARENTHESES_INVOKE = "()~";
  private final InvokeB invokeB;

  public InvokeJob(InvokeB invokeB, ExecutionContext context) {
    super(context);
    this.invokeB = invokeB;
  }

  @Override
  protected Promise<ValB> evaluateImpl() {
    var exprInfo = context().infoFor(invokeB);
    var name = exprInfo.label();
    var data = invokeB.data();
    var resT = data.method().type().res();
    var algorithm = new InvokeAlgorithm(
        resT, name, data.method(), context().nativeMethodLoader());
    var taskInfo = new TaskInfo(INVOKE, name + PARENTHESES_INVOKE, exprInfo.loc());
    return evaluateTransitively(taskInfo, algorithm, data.args().items());
  }
}
