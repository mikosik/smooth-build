package org.smoothbuild.exec.compute;

import static org.smoothbuild.exec.compute.ResultSource.DISK;
import static org.smoothbuild.exec.compute.ResultSource.EXECUTION;
import static org.smoothbuild.exec.compute.ResultSource.MEMORY;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.SandboxHash;
import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.util.concurrent.FeedingConsumer;

/**
 * This class is thread-safe.
 */
public class Computer {
  private final ComputationCache computationCache;
  private final Hash sandboxHash;
  private final Provider<Container> containerProvider;
  private final ConcurrentHashMap<Hash, FeedingConsumer<Computed>> feeders;

  @Inject
  public Computer(ComputationCache computationCache, @SandboxHash Hash sandboxHash,
      Provider<Container> containerProvider) {
    this.computationCache = computationCache;
    this.sandboxHash = sandboxHash;
    this.containerProvider = containerProvider;
    this.feeders = new ConcurrentHashMap<>();
  }

  public void compute(ComputableTask task, Input input, Consumer<Computed> consumer)
      throws ComputationCacheException, IOException {
    Hash hash = computationHash(task.algorithm(), input);
    FeedingConsumer<Computed> newFeeder = new FeedingConsumer<>();
    FeedingConsumer<Computed> prevFeeder = feeders.putIfAbsent(hash, newFeeder);
    if (prevFeeder != null) {
      prevFeeder
          .chain((computed) -> computedFromCache(task.algorithm().isPure(), computed))
          .addConsumer(consumer);
    } else {
      newFeeder.addConsumer(consumer);
      if (computationCache.contains(hash)) {
        Output output = computationCache.read(hash, task.algorithm().outputSpec());
        newFeeder.accept(new Computed(output, DISK));
        feeders.remove(hash);
      } else {
        Computed computed = runAlgorithm(task.algorithm(), input);
        boolean cacheOnDisk = task.algorithm().isPure() && computed.hasOutput();
        if (cacheOnDisk) {
          computationCache.write(hash, computed.output());
        }
        newFeeder.accept(computed);
        if (cacheOnDisk) {
          feeders.remove(hash);
        }
      }
    }
  }

  private static Computed computedFromCache(boolean isPure, Computed computed) {
    return new Computed(
        computed.output(),
        computed.exception(),
        computed.resultSource() == EXECUTION && isPure ? DISK : MEMORY);
  }

  private Computed runAlgorithm(Algorithm algorithm, Input input) throws IOException {
    try (Container container = containerProvider.get()) {
      Output output;
      try {
        output = algorithm.run(input, container);
      } catch (Exception e) {
        return new Computed(e, EXECUTION);
      }
      return new Computed(output, EXECUTION);
    }
  }

  private Hash computationHash(Algorithm algorithm, Input input) {
    return computationHash(sandboxHash, algorithm, input);
  }

  public static Hash computationHash(Hash sandboxHash, Algorithm algorithm, Input input) {
    return Hash.of(sandboxHash, algorithm.hash(), input.hash());
  }
}
