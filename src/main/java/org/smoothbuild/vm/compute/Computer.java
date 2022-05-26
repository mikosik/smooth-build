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

import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.SandboxHash;
import org.smoothbuild.vm.algorithm.Algorithm;
import org.smoothbuild.vm.algorithm.Output;

/**
 * This class is thread-safe.
 */
public class Computer {
  private final ComputationCache computationCache;
  private final Hash sandboxHash;
  private final Provider<Container> containerProvider;
  private final ConcurrentHashMap<Hash, PromisedValue<CompRes>> promisedValues;

  @Inject
  public Computer(ComputationCache computationCache, @SandboxHash Hash sandboxHash,
      Provider<Container> containerProvider) {
    this.computationCache = computationCache;
    this.sandboxHash = sandboxHash;
    this.containerProvider = containerProvider;
    this.promisedValues = new ConcurrentHashMap<>();
  }

  public void compute(Algorithm algorithm, TupleB input, Consumer<CompRes> consumer)
      throws ComputationCacheExc, IOException {
    Hash hash = computationHash(algorithm, input);
    PromisedValue<CompRes> newPromised = new PromisedValue<>();
    PromisedValue<CompRes> prevPromised = promisedValues.putIfAbsent(hash, newPromised);
    if (prevPromised != null) {
      prevPromised
          .chain(c -> asCachedComputation(c, algorithm.isPure()))
          .addConsumer(consumer);
    } else {
      newPromised.addConsumer(consumer);
      if (computationCache.contains(hash)) {
        var output = computationCache.read(hash, algorithm.outputT());
        newPromised.accept(new CompRes(output, DISK));
        promisedValues.remove(hash);
      } else {
        var computed = runComputation(algorithm, input);
        boolean cacheOnDisk = algorithm.isPure() && computed.hasOutput();
        if (cacheOnDisk) {
          computationCache.write(hash, computed.output());
          promisedValues.remove(hash);
        }
        newPromised.accept(computed);
      }
    }
  }

  private static CompRes asCachedComputation(CompRes compRes, boolean isPure) {
    return new CompRes(
        compRes.output(),
        compRes.exception(),
        compRes.resSource() == EXECUTION && isPure ? DISK : MEMORY);
  }

  private CompRes runComputation(Algorithm algorithm, TupleB input) {
    var container = containerProvider.get();
    Output output;
    try {
      output = algorithm.run(input, container);
    } catch (Exception e) {
      return new CompRes(e, EXECUTION);
    }
    // This Computed instance creation is outside try-block
    // so eventual exception it could throw won't be caught by above catch.
    return new CompRes(output, EXECUTION);
  }

  private Hash computationHash(Algorithm algorithm, TupleB args) {
    return computationHash(sandboxHash, algorithm, args);
  }

  public static Hash computationHash(Hash sandboxHash, Algorithm algorithm, TupleB args) {
    return Hash.of(asList(sandboxHash, algorithm.hash(), args.hash()));
  }
}
