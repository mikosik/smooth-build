package org.smoothbuild.exec.task.base;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.outputs.OutputDb;
import org.smoothbuild.db.outputs.OutputDbException;
import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.exec.comp.MaybeOutput;
import org.smoothbuild.exec.comp.Output;
import org.smoothbuild.exec.task.SandboxHash;
import org.smoothbuild.util.concurrent.Feeder;

/**
 * This class is thread-safe.
 */
public class Computer {
  private final OutputDb outputDb;
  private final Hash sandboxHash;
  private final Provider<Container> containerProvider;
  private final ConcurrentHashMap<Hash, Feeder<Computed>> feeders;

  @Inject
  public Computer(OutputDb outputDb, @SandboxHash Hash sandboxHash,
      Provider<Container> containerProvider) {
    this.outputDb = outputDb;
    this.sandboxHash = sandboxHash;
    this.containerProvider = containerProvider;
    this.feeders = new ConcurrentHashMap<>();
  }

  public void compute(ComputableTask task, Input input, Consumer<Computed> consumer) throws
      OutputDbException, IOException {
    Algorithm algorithm = task.algorithm();
    Hash hash = computationHash(algorithm, input);
    Feeder<Computed> newFeeder = new Feeder<>();
    Feeder<Computed> prevFeeder = feeders.putIfAbsent(hash, newFeeder);
    if (prevFeeder != null) {
      prevFeeder.addConsumer(consumer);
    } else {
      newFeeder.addConsumer(consumer);
      if (outputDb.contains(hash)) {
        Output output = outputDb.read(hash, algorithm.type());
        newFeeder.accept(new Computed(new MaybeOutput(output), true));
        feeders.remove(hash);
      } else {
        MaybeOutput result = doCompute(algorithm, input);
        boolean cacheOnDisk = task.cacheable() && result.hasOutput();
        if (cacheOnDisk) {
          outputDb.write(hash, result.output());
        }
        newFeeder.accept(new Computed(result, false));
        if (cacheOnDisk) {
          feeders.remove(hash);
        }
      }
    }
  }

  private MaybeOutput doCompute(Algorithm algorithm, Input input) throws IOException {
    try (Container container = containerProvider.get()) {
      try {
        return new MaybeOutput(algorithm.run(input, container));
      } catch (Exception e) {
        return new MaybeOutput(e);
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
