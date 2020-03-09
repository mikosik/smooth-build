package org.smoothbuild.exec.task;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.outputs.OutputDb;
import org.smoothbuild.db.outputs.OutputDbException;
import org.smoothbuild.exec.SandboxHash;
import org.smoothbuild.exec.comp.ComputationException;
import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.exec.comp.Output;

public class TaskExecutor {
  private final OutputDb outputDb;
  private final TaskReporter reporter;
  private final Hash sandboxHash;
  private final Provider<Container> containerProvider;

  @Inject
  public TaskExecutor(OutputDb outputDb, TaskReporter reporter, @SandboxHash Hash sandboxHash,
      Provider<Container> containerProvider) {
    this.outputDb = outputDb;
    this.reporter = reporter;
    this.sandboxHash = sandboxHash;
    this.containerProvider = containerProvider;
  }

  public TaskResult execute(Task task, Input input) throws IOException,
      OutputDbException {
    TaskResult taskResult = executeImpl2(task, input);
    // TODO reporter should be invoked from code that calls us
    reporter.report(task, taskResult);
    return taskResult;
  }

  private TaskResult executeImpl2(Task task, Input input) throws OutputDbException,
      IOException {
    Hash hash = taskHash(task, input);
    if (outputDb.contains(hash)) {
      Output output = outputDb.read(hash, task.type());
      return new TaskResult(output, true);
    } else {
      TaskResult taskResult = executeImpl(task, input);
      if (task.isComputationCacheable() && taskResult.hasOutput()) {
        outputDb.write(hash, taskResult.output());
      }
      return taskResult;
    }
  }

  private TaskResult executeImpl(Task task, Input input) throws IOException {
    Container container = containerProvider.get();
    try {
      return new TaskResult(task.execute(container, input), false);
    } catch (ComputationException e) {
      return new TaskResult(e);
    } finally {
      container.close();
    }
  }

  private Hash taskHash(Task task, Input input) {
    return taskHash(task, input, sandboxHash);
  }

  public static Hash taskHash(Task task, Input input, Hash runtimeHash) {
    return Hash.of(runtimeHash, task.hash(), input.hash());
  }
}
