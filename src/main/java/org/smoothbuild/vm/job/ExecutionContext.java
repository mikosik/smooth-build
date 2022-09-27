package org.smoothbuild.vm.job;

import static java.util.Objects.requireNonNullElseGet;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.compile.lang.base.LabeledLoc;
import org.smoothbuild.compile.lang.base.LabeledLocImpl;
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
  private final ImmutableMap<ExprB, LabeledLoc> labels;

  @Inject
  public ExecutionContext(TaskExecutor taskExecutor, ExecutionReporter reporter,
      BytecodeF bytecodeF, NativeMethodLoader nativeMethodLoader, JobCreator jobCreator) {
    this(taskExecutor, reporter, bytecodeF, nativeMethodLoader, jobCreator, ImmutableMap.of());
  }

  public ExecutionContext(TaskExecutor taskExecutor, ExecutionReporter reporter,
      BytecodeF bytecodeF,
      NativeMethodLoader nativeMethodLoader, JobCreator jobCreator,
      ImmutableMap<ExprB, LabeledLoc> labels) {
    this.taskExecutor = taskExecutor;
    this.reporter = reporter;
    this.bytecodeF = bytecodeF;
    this.nativeMethodLoader = nativeMethodLoader;
    this.jobCreator = jobCreator;
    this.labels = labels;
  }

  public Job jobFor(ExprB expr) {
    return jobCreator.jobFor(expr, this);
  }

  public ExecutionContext withEnvironment(ImmutableList<Job> args) {
    return new ExecutionContext(taskExecutor, reporter, bytecodeF, nativeMethodLoader,
        jobCreator.withEnvironment(args), labels);
  }

  public ExecutionContext withLabels(ImmutableMap<ExprB, LabeledLoc> labels) {
    return new ExecutionContext(
        taskExecutor, reporter, bytecodeF, nativeMethodLoader, jobCreator, labels);
  }

  public LabeledLoc labeledLoc(ExprB expr) {
    return requireNonNullElseGet(labels.get(expr),
        () -> new LabeledLocImpl("@" + expr.hash(), Loc.unknown()));
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
