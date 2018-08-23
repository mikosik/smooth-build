package org.smoothbuild.task.exec;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.outputs.OutputsDb;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Input;
import org.smoothbuild.task.base.Output;
import org.smoothbuild.task.base.Task;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

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

  public <T extends Value> void execute(Task task, Input input) throws IOException {
    HashCode hash = taskHash(task, input);
    boolean isAlreadyCached = outputsDb.contains(hash);
    if (isAlreadyCached) {
      Output output = outputsDb.read(hash, task.type());
      task.setOutput(output);
    } else {
      Container container = containerProvider.get();
      try {
        task.execute(container, input);
      } finally {
        container.destroy();
      }
      if (task.isCacheable()) {
        outputsDb.write(hash, task.output());
      }
    }
    reporter.report(task, isAlreadyCached);
  }

  public static <T extends Value> HashCode taskHash(Task task, Input input) {
    Hasher hasher = Hash.newHasher();
    hasher.putBytes(task.hash().asBytes());
    hasher.putBytes(input.hash().asBytes());
    return hasher.hash();
  }
}
