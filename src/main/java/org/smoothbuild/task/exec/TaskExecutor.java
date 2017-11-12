package org.smoothbuild.task.exec;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.outputs.OutputsDb;
import org.smoothbuild.io.util.SmoothJar;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Input;
import org.smoothbuild.task.base.Output;
import org.smoothbuild.task.base.Task;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

public class TaskExecutor {
  private final HashCode smoothJarHash;
  private final OutputsDb outputsDb;
  private final TaskReporter reporter;
  private final Provider<ContainerImpl> containerProvider;

  @Inject
  public TaskExecutor(@SmoothJar HashCode smoothJarHash, OutputsDb outputsDb, TaskReporter reporter,
      Provider<ContainerImpl> containerProvider) {
    this.smoothJarHash = smoothJarHash;
    this.outputsDb = outputsDb;
    this.reporter = reporter;
    this.containerProvider = containerProvider;
  }

  public <T extends Value> void execute(Task task, Input input) {
    HashCode hash = taskHash(task, input);
    boolean isAlreadyCached = outputsDb.contains(hash);
    if (isAlreadyCached) {
      Output output = outputsDb.read(hash, task.resultType());
      task.setOutput(output);
    } else {
      ContainerImpl container = containerProvider.get();
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

  private <T extends Value> HashCode taskHash(Task task, Input input) {
    Hasher hasher = Hash.newHasher();
    hasher.putBytes(smoothJarHash.asBytes());
    hasher.putBytes(task.hash().asBytes());
    hasher.putBytes(input.hash().asBytes());
    return hasher.hash();
  }
}
