package org.smoothbuild.vm.job;

import static java.util.Objects.requireNonNullElseGet;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.compile.lang.base.ExprInfo;
import org.smoothbuild.compile.lang.base.ExprInfoImpl;
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.vm.execute.ExecutionReporter;
import org.smoothbuild.vm.execute.TaskExecutor;
import org.smoothbuild.vm.task.NativeMethodLoader;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ExecutionContext {
  private final TaskExecutor taskExecutor;
  private final ExecutionReporter reporter;
  private final BytecodeF bytecodeF;
  private final NativeMethodLoader nativeMethodLoader;
  private final JobCreator jobCreator;
  private final ImmutableMap<ExprB, ExprInfo> exprInfos;

  @Inject
  public ExecutionContext(TaskExecutor taskExecutor, ExecutionReporter reporter,
      BytecodeF bytecodeF, NativeMethodLoader nativeMethodLoader, JobCreator jobCreator) {
    this(taskExecutor, reporter, bytecodeF, nativeMethodLoader, jobCreator, ImmutableMap.of());
  }

  public ExecutionContext(TaskExecutor taskExecutor, ExecutionReporter reporter,
      BytecodeF bytecodeF,
      NativeMethodLoader nativeMethodLoader, JobCreator jobCreator,
      ImmutableMap<ExprB, ExprInfo> exprInfos) {
    this.taskExecutor = taskExecutor;
    this.reporter = reporter;
    this.bytecodeF = bytecodeF;
    this.nativeMethodLoader = nativeMethodLoader;
    this.jobCreator = jobCreator;
    this.exprInfos = exprInfos;
  }

  public Job jobFor(ExprB expr) {
    return jobCreator.jobFor(expr, this);
  }

  public ExecutionContext withBindings(ImmutableList<Job> args) {
    return new ExecutionContext(
        taskExecutor, reporter, bytecodeF, nativeMethodLoader, new JobCreator(args), exprInfos);
  }

  public ExecutionContext withExprInfos(ImmutableMap<ExprB, ExprInfo> exprInfos) {
    return new ExecutionContext(
        taskExecutor, reporter, bytecodeF, nativeMethodLoader, jobCreator, exprInfos);
  }

  public ExprInfo infoFor(ExprB expr) {
    return requireNonNullElseGet(exprInfos.get(expr),
        () -> new ExprInfoImpl("@" + expr.hash(), Loc.unknown()));
  }

  public ExecutionReporter reporter() {
    return reporter;
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
