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

import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.SandboxHash;
import org.smoothbuild.vm.task.Output;
import org.smoothbuild.vm.task.Task;

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

  public void compute(Task task, TupleB input, Consumer<CompRes> consumer)
      throws ComputationCacheExc, IOException {
    Hash hash = computationHash(task, input);
    PromisedValue<CompRes> newPromised = new PromisedValue<>();
    PromisedValue<CompRes> prevPromised = promisedValues.putIfAbsent(hash, newPromised);
    if (prevPromised != null) {
      prevPromised
          .chain(c -> asCachedComputation(c, task.isPure()))
          .addConsumer(consumer);
    } else {
      newPromised.addConsumer(consumer);
      if (computationCache.contains(hash)) {
        var output = computationCache.read(hash, task.outputT());
        newPromised.accept(new CompRes(output, DISK));
        promisedValues.remove(hash);
      } else {
        var computed = runComputation(task, input);
        boolean cacheOnDisk = task.isPure() && computed.hasOutput();
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

  private CompRes runComputation(Task task, TupleB input) {
    var container = containerProvider.get();
    Output output;
    try {
      output = task.run(input, container);
    } catch (Exception e) {
      return new CompRes(e, EXECUTION);
    }
    // This Computed instance creation is outside try-block
    // so eventual exception it could throw won't be caught by above catch.
    return new CompRes(output, EXECUTION);
  }

  private Hash computationHash(Task task, TupleB args) {
    return computationHash(sandboxHash, task, args);
  }

  public static Hash computationHash(Hash sandboxHash, Task task, TupleB args) {
    return Hash.of(asList(sandboxHash, task.hash(), args.hash()));
  }
}
