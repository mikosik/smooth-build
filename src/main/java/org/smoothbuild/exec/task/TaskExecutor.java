package org.smoothbuild.exec.task;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.outputs.OutputDb;
import org.smoothbuild.db.outputs.OutputDbException;
import org.smoothbuild.exec.RuntimeHash;
import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.exec.comp.Output;

public class TaskExecutor {
  private final OutputDb outputDb;
  private final TaskReporter reporter;
  private final Hash runtimeHash;
  private final Provider<Container> containerProvider;

  @Inject
  public TaskExecutor(OutputDb outputDb, TaskReporter reporter, @RuntimeHash Hash runtimeHash,
      Provider<Container> containerProvider) {
    this.outputDb = outputDb;
    this.reporter = reporter;
    this.runtimeHash = runtimeHash;
    this.containerProvider = containerProvider;
  }

  public void execute(Task task, Input input) throws IOException,
      OutputDbException {
    Hash hash = taskHash(task, input);
    if (outputDb.contains(hash)) {
      Output output = outputDb.read(hash, task.type());
      task.setResult(new TaskResult(output, true));
    } else {
      Container container = containerProvider.get();
      try {
        task.execute(container, input);
      } finally {
        container.destroy();
      }
      if (task.shouldCacheOutput()) {
        outputDb.write(hash, task.output());
      }
    }
    reporter.report(task);
  }

  private Hash taskHash(Task task, Input input) {
    return taskHash(task, input, runtimeHash);
  }

  public static Hash taskHash(Task task, Input input, Hash runtimeHash) {
    return Hash.of(runtimeHash, task.hash(), input.hash());
  }
}
