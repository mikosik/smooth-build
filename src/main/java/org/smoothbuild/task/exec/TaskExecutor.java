package org.smoothbuild.task.exec;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.outputs.OutputsDb;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.task.base.Input;
import org.smoothbuild.task.base.Output;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.TaskResult;

public class TaskExecutor {
  private final OutputsDb outputsDb;
  private final TaskReporter reporter;
  private final Provider<Container> containerProvider;

  @Inject
  public TaskExecutor(OutputsDb outputsDb, TaskReporter reporter,
      Provider<Container> containerProvider) {
    this.outputsDb = outputsDb;
    this.reporter = reporter;
    this.containerProvider = containerProvider;
  }

  public <T extends SObject> void execute(Task task, Input input) throws IOException {
    Hash hash = task.hash(input);
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
}
