package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;

import java.util.function.Consumer;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.expr.oper.MapB;
import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.FuncB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

public class MapJob extends ExecutingJob {
  private final MapB mapB;
  private final ExecutionContext context;

  public MapJob(MapB mapB, ExecutionContext context) {
    super(context);
    this.mapB = mapB;
    this.context = context;
  }

  @Override
  public Promise<ValB> evaluateImpl() {
    var result = new PromisedValue<ValB>();
    var data = mapB.data();
    var array = context.jobFor(data.array()).evaluate();
    var func = context.jobFor(data.func()).evaluate();
    runWhenAllAvailable(list(array, func),
        () -> onDepsCompleted((ArrayB) array.get(), (FuncB) func.get(), result));
    return result;
  }

  private void onDepsCompleted(ArrayB array, FuncB funcB, Consumer<ValB> result) {
    var callBs = map(array.elems(ValB.class), e -> newCallB(funcB, e));
    var orderB = bytecodeF().order(mapB.type(), callBs);
    context.jobFor(orderB)
        .evaluate()
        .addConsumer(result);
  }

  private ExprB newCallB(FuncB funcB, ValB val) {
    return bytecodeF().call(funcB.type().res(), funcB, singleArg(val));
  }

  private CombineB singleArg(ValB val) {
    return bytecodeF().combine(bytecodeF().tupleT(list(val.type())), list(val));
  }

  private BytecodeF bytecodeF() {
    return context().bytecodeF();
  }
}
