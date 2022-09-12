package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.vm.execute.TaskKind.CALL;

import java.util.function.Consumer;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.val.FuncB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.execute.TaskInfo;

public class CallJob extends ExecutingJob {
  private static final String PARENTHESES = "()";
  private final CallB callB;

  public CallJob(CallB callB, ExecutionContext context) {
    super(context);
    this.callB = callB;
  }

  @Override
  public Promise<ValB> evaluateImpl() {
    var funcJ = context().jobFor(callB.data().callable());
    var result = new PromisedValue<ValB>();
    funcJ.evaluate()
        .addConsumer(valB -> onFuncJobCompleted(valB, result));
    return result;
  }

  private void onFuncJobCompleted(ValB valB, Consumer<ValB> res) {
    var argsJ = map(callB.data().args().items(), context()::jobFor);
    var bodyJob = context().withBindings(argsJ)
        .jobFor(((FuncB) valB).body());
    var taskInfo = callTaskInfo(valB);
    new VirtualJob(bodyJob, taskInfo, context().reporter())
        .evaluate()
        .addConsumer(res);
  }

  private TaskInfo callTaskInfo(ValB funcB) {
    var info = context().infoFor(funcB);
    return new TaskInfo(CALL, info.label() + PARENTHESES, locFor(callB));
  }

  private Loc locFor(ExprB expr) {
    var info = context().infoFor(expr);
    if (info == null) {
      return Loc.unknown();
    } else {
      return info.loc();
    }
  }
}
