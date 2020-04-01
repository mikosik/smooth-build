package org.smoothbuild.exec.task.base;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.outputs.OutputDb;
import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.exec.comp.Output;
import org.smoothbuild.exec.task.SandboxHash;
import org.smoothbuild.util.concurrent.Feeder;

/**
 * This class is thread-safe.
 */
public class TaskExecutor {
  private final OutputDb outputDb;
  private final Hash sandboxHash;
  private final Provider<Container> containerProvider;
  private final ConcurrentHashMap<Hash, Feeder<ExecutionResult>> feeders;

  @Inject
  public TaskExecutor(OutputDb outputDb, @SandboxHash Hash sandboxHash,
      Provider<Container> containerProvider) {
    this.outputDb = outputDb;
    this.sandboxHash = sandboxHash;
    this.containerProvider = containerProvider;
    this.feeders = new ConcurrentHashMap<>();
  }

  public void execute(Task task, Input input, Consumer<ExecutionResult> consumer) {
    Hash hash = executionHash(task, input);
    Feeder<ExecutionResult> newFeeder = new Feeder<>();
    Feeder<ExecutionResult> prevFeeder = feeders.putIfAbsent(hash, newFeeder);
    if (prevFeeder != null) {
      prevFeeder.addConsumer(consumer);
    } else {
      newFeeder.addConsumer(consumer);
      try {
        if (outputDb.contains(hash)) {
          Output output = outputDb.read(hash, task.type());
          newFeeder.accept(new ExecutionResult(new TaskResult(output, true)));
          feeders.remove(hash);
        } else {
          TaskResult taskResult = executeTask(task, input);
          boolean cacheOnDisk = task.isComputationCacheable() && taskResult.hasOutput();
          if (cacheOnDisk) {
            outputDb.write(hash, taskResult.output());
          }
          newFeeder.accept(new ExecutionResult(taskResult));
          if (cacheOnDisk) {
            feeders.remove(hash);
          }
        }
      } catch (Throwable e) {
        newFeeder.accept(new ExecutionResult(e));
      }
    }
  }

  private TaskResult executeTask(Task task, Input input) throws IOException {
    try (Container container = containerProvider.get()) {
      return task.execute(container, input);
    }
  }

  private Hash executionHash(Task task, Input input) {
    return executionHash(task, input, sandboxHash);
  }

  public static Hash executionHash(Task task, Input input, Hash sandboxHash) {
    return Hash.of(sandboxHash, task.hash(), input.hash());
  }
}
