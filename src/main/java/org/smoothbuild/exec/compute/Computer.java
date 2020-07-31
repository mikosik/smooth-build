package org.smoothbuild.exec.compute;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.SandboxHash;
import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.MaybeOutput;
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
    Algorithm algorithm = task.algorithm();
    Hash hash = computationHash(algorithm, input);
    FeedingConsumer<Computed> newFeeder = new FeedingConsumer<>();
    FeedingConsumer<Computed> prevFeeder = feeders.putIfAbsent(hash, newFeeder);
    if (prevFeeder != null) {
      prevFeeder
          .chain((computed) -> new Computed(computed.computed(), ResultSource.CACHE))
          .addConsumer(consumer);
    } else {
      newFeeder.addConsumer(consumer);
      if (computationCache.contains(hash)) {
        Output output = computationCache.read(hash, algorithm.type());
        newFeeder.accept(new Computed(new MaybeOutput(output), ResultSource.CACHE));
        feeders.remove(hash);
      } else {
        MaybeOutput result = doCompute(algorithm, input);
        boolean cacheOnDisk = task.cacheable() && result.hasOutput();
        if (cacheOnDisk) {
          computationCache.write(hash, result.output());
        }
        newFeeder.accept(new Computed(result, ResultSource.EXECUTION));
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
    return computationHash(sandboxHash, algorithm, input);
  }

  public static Hash computationHash(Hash sandboxHash, Algorithm algorithm, Input input) {
    return Hash.of(sandboxHash, algorithm.hash(), input.hash());
  }
}
