package org.smoothbuild.vm.evaluate.job;

import javax.inject.Inject;

import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.evaluate.execute.TaskExecutor;
import org.smoothbuild.vm.evaluate.task.NativeMethodLoader;

public class ExecutionContext {
  private final TaskExecutor taskExecutor;
  private final BytecodeF bytecodeF;
  private final NativeMethodLoader nativeMethodLoader;

  @Inject
  public ExecutionContext(
      TaskExecutor taskExecutor,
      BytecodeF bytecodeF,
      NativeMethodLoader nativeMethodLoader) {
    this.taskExecutor = taskExecutor;
    this.bytecodeF = bytecodeF;
    this.nativeMethodLoader = nativeMethodLoader;
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
}
