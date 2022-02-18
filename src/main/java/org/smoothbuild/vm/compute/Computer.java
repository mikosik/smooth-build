package org.smoothbuild.vm.compute;

import static java.util.Arrays.asList;
import static org.smoothbuild.vm.compute.ResSource.DISK;
import static org.smoothbuild.vm.compute.ResSource.EXECUTION;
import static org.smoothbuild.vm.compute.ResSource.MEMORY;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.db.Hash;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.SandboxHash;
import org.smoothbuild.vm.job.algorithm.Algorithm;
import org.smoothbuild.vm.job.algorithm.Input;
import org.smoothbuild.vm.job.algorithm.Output;

/**
 * This class is thread-safe.
 */
public class Computer {
  private final ComputationCache computationCache;
  private final Hash sandboxHash;
  private final Provider<Container> containerProvider;
  private final ConcurrentHashMap<Hash, PromisedValue<Computed>> promisedValues;

  @Inject
  public Computer(ComputationCache computationCache, @SandboxHash Hash sandboxHash,
      Provider<Container> containerProvider) {
    this.computationCache = computationCache;
    this.sandboxHash = sandboxHash;
    this.containerProvider = containerProvider;
    this.promisedValues = new ConcurrentHashMap<>();
  }

  public void compute(Algorithm algorithm, Input input, Consumer<Computed> consumer)
      throws ComputationCacheExc, IOException {
    Hash hash = computationHash(algorithm, input);
    PromisedValue<Computed> newPromised = new PromisedValue<>();
    PromisedValue<Computed> prevPromised = promisedValues.putIfAbsent(hash, newPromised);
    if (prevPromised != null) {
      prevPromised
          .chain((computed) -> computedFromCache(algorithm.isPure(), computed))
          .addConsumer(consumer);
    } else {
      newPromised.addConsumer(consumer);
      if (computationCache.contains(hash)) {
        Output output = computationCache.read(hash, algorithm.outputT());
        newPromised.accept(new Computed(output, DISK));
        promisedValues.remove(hash);
      } else {
        Computed computed = runAlgorithm(algorithm, input);
        boolean cacheOnDisk = algorithm.isPure() && computed.hasOutput();
        if (cacheOnDisk) {
          computationCache.write(hash, computed.output());
          promisedValues.remove(hash);
        }
        newPromised.accept(computed);
      }
    }
  }

  private static Computed computedFromCache(boolean isPure, Computed computed) {
    return new Computed(
        computed.output(),
        computed.exception(),
        computed.resSource() == EXECUTION && isPure ? DISK : MEMORY);
  }

  private Computed runAlgorithm(Algorithm algorithm, Input input) {
    Container container = containerProvider.get();
    Output output;
    try {
      output = algorithm.run(input, container);
    } catch (Exception e) {
      return new Computed(e, EXECUTION);
    }
    // This Computed instance creation is outside of try-block
    // so eventual exception it could throw won't be caught by above catch.
    return new Computed(output, EXECUTION);
  }

  private Hash computationHash(Algorithm algorithm, Input input) {
    return computationHash(sandboxHash, algorithm, input);
  }

  public static Hash computationHash(Hash sandboxHash, Algorithm algorithm, Input input) {
    return Hash.of(asList(sandboxHash, algorithm.hash(), input.hash()));
  }
}
