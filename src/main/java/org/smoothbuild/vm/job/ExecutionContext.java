package org.smoothbuild.vm.job;

import static java.util.Objects.requireNonNullElseGet;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.base.Trace;
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
  private final ImmutableMap<ExprB, TagLoc> tagLocs;

  @Inject
  public ExecutionContext(TaskExecutor taskExecutor, ExecutionReporter reporter,
      BytecodeF bytecodeF, NativeMethodLoader nativeMethodLoader, JobCreator jobCreator) {
    this(taskExecutor, reporter, bytecodeF, nativeMethodLoader, jobCreator, ImmutableMap.of());
  }

  public ExecutionContext(TaskExecutor taskExecutor, ExecutionReporter reporter,
      BytecodeF bytecodeF, NativeMethodLoader nativeMethodLoader, JobCreator jobCreator,
      ImmutableMap<ExprB, TagLoc> tagLocs) {
    this.taskExecutor = taskExecutor;
    this.reporter = reporter;
    this.bytecodeF = bytecodeF;
    this.nativeMethodLoader = nativeMethodLoader;
    this.jobCreator = jobCreator;
    this.tagLocs = tagLocs;
  }

  public Job jobFor(ExprB expr) {
    return jobCreator.jobFor(expr, this);
  }

  public ExecutionContext withEnvironment(ImmutableList<Job> args, Trace trace) {
    return new ExecutionContext(taskExecutor, reporter, bytecodeF, nativeMethodLoader,
        jobCreator.withEnvironment(args, trace), tagLocs);
  }

  public ExecutionContext withTagLocs(ImmutableMap<ExprB, TagLoc> tagLocs) {
    return new ExecutionContext(
        taskExecutor, reporter, bytecodeF, nativeMethodLoader, jobCreator, tagLocs);
  }

  public TagLoc tagLoc(ExprB expr) {
    return requireNonNullElseGet(tagLocs.get(expr),
        () -> new TagLoc("#" + expr.hash(), Loc.unknown()));
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

  public Trace trace() {
    return jobCreator.trace();
  }
}
