package org.smoothbuild.vm.evaluate.job;

import javax.inject.Inject;

import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.evaluate.execute.TaskExecutor;
import org.smoothbuild.vm.evaluate.execute.TraceB;
import org.smoothbuild.vm.evaluate.task.NativeMethodLoader;

import com.google.common.collect.ImmutableList;

public class ExecutionContext {
  private final TaskExecutor taskExecutor;
  private final BytecodeF bytecodeF;
  private final NativeMethodLoader nativeMethodLoader;
  private final JobCreator jobCreator;

  @Inject
  public ExecutionContext(TaskExecutor taskExecutor, BytecodeF bytecodeF,
      NativeMethodLoader nativeMethodLoader, JobCreator jobCreator) {
    this.taskExecutor = taskExecutor;
    this.bytecodeF = bytecodeF;
    this.nativeMethodLoader = nativeMethodLoader;
    this.jobCreator = jobCreator;
  }

  public Job jobFor(ExprB expr) {
    return jobCreator.jobFor(expr, this);
  }

  public ExecutionContext withEnvironment(ImmutableList<Job> args, TraceB trace) {
    return new ExecutionContext(taskExecutor, bytecodeF, nativeMethodLoader,
        jobCreator.withEnvironment(args, trace));
  }

  public TaskExecutor taskExecutor() {
    return taskExecutor;
  }

  public BytecodeF bytecodeF() {
    return bytecodeF;
  }

  public NativeMethodLoader nativeMethodLoader() {
    return nativeMethodLoader;
  }

  public TraceB trace() {
    return jobCreator.trace();
  }
}
