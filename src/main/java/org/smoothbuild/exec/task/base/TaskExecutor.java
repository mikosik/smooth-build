package org.smoothbuild.exec.task.base;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.outputs.OutputDb;
import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.ComputationException;
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

  public void compute(Algorithm algorithm, Input input, Consumer<ExecutionResult> consumer,
      boolean cacheable) {
    Hash hash = computationHash(algorithm, input);
    Feeder<ExecutionResult> newFeeder = new Feeder<>();
    Feeder<ExecutionResult> prevFeeder = feeders.putIfAbsent(hash, newFeeder);
    if (prevFeeder != null) {
      prevFeeder.addConsumer(consumer);
    } else {
      newFeeder.addConsumer(consumer);
      try {
        if (outputDb.contains(hash)) {
          Output output = outputDb.read(hash, algorithm.type());
          newFeeder.accept(new ExecutionResult(new Result(output, true)));
          feeders.remove(hash);
        } else {
          Result result = doCompute(algorithm, input);
          boolean cacheOnDisk = cacheable && result.hasOutput();
          if (cacheOnDisk) {
            outputDb.write(hash, result.output());
          }
          newFeeder.accept(new ExecutionResult(result));
          if (cacheOnDisk) {
            feeders.remove(hash);
          }
        }
      } catch (Throwable e) {
        newFeeder.accept(new ExecutionResult(e));
      }
    }
  }

  private Result doCompute(Algorithm algorithm, Input input) throws IOException {
    try (Container container = containerProvider.get()) {
      try {
        return new Result(algorithm.run(input, container), false);
      } catch (ComputationException e) {
        return new Result(e);
      }
    }
  }

  private Hash computationHash(Algorithm algorithm, Input input) {
    return computationHash(algorithm, input, sandboxHash);
  }

  public static Hash computationHash(Algorithm algorithm, Input input, Hash sandboxHash) {
    return Hash.of(sandboxHash, algorithm.hash(), input.hash());
  }
}
