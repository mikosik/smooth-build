package org.smoothbuild.task.exec;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.outputs.OutputsDb;
import org.smoothbuild.db.outputs.OutputsDbException;
import org.smoothbuild.task.RuntimeHash;
import org.smoothbuild.task.base.Input;
import org.smoothbuild.task.base.Output;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.TaskResult;

public class TaskExecutor {
  private final OutputsDb outputsDb;
  private final TaskReporter reporter;
  private final Hash runtimeHash;
  private final Provider<Container> containerProvider;

  @Inject
  public TaskExecutor(OutputsDb outputsDb, TaskReporter reporter, @RuntimeHash Hash runtimeHash,
      Provider<Container> containerProvider) {
    this.outputsDb = outputsDb;
    this.reporter = reporter;
    this.runtimeHash = runtimeHash;
    this.containerProvider = containerProvider;
  }

  public void execute(Task task, Input input) throws IOException,
      OutputsDbException {
    Hash hash = taskHash(task, input);
    if (outputsDb.contains(hash)) {
      Output output = outputsDb.read(hash, task.type());
      task.setResult(new TaskResult(output, true));
    } else {
      Container container = containerProvider.get();
      try {
        task.execute(container, input);
      } finally {
        container.destroy();
      }
      if (task.shouldCacheOutput()) {
        outputsDb.write(hash, task.output());
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
