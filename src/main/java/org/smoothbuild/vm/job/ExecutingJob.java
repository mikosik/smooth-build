package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.execute.TaskInfo;
import org.smoothbuild.vm.task.Task;

import com.google.common.collect.ImmutableList;

public abstract class ExecutingJob extends Job {
  private final ExecutionContext context;

  public ExecutingJob(ExecutionContext context) {
    this.context = context;
  }

  protected PromisedValue<ValB> evaluateTransitively(
      TaskInfo info, Task task, ImmutableList<ExprB> deps) {
    var result = new PromisedValue<ValB>();
    var depJs = map(deps, context::jobFor);
    var depResults = map(depJs, Job::evaluate);
    runWhenAllAvailable(depResults,
        () -> context.taskExecutor().enqueue(info, task, toInput(depResults), result));
    return result;
  }

  private TupleB toInput(ImmutableList<Promise<ValB>> depResults) {
    return context.bytecodeF().tuple(map(depResults, Promise::get));
  }

  protected ExecutionContext context() {
    return context;
  }
}
