package org.smoothbuild.vm.compute;

import static java.util.Arrays.asList;
import static org.smoothbuild.vm.compute.ResSource.DISK;
import static org.smoothbuild.vm.compute.ResSource.EXECUTION;
import static org.smoothbuild.vm.compute.ResSource.MEMORY;
import static org.smoothbuild.vm.compute.ResSource.NOOP;
import static org.smoothbuild.vm.task.TaskHashes.taskHash;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.SandboxHash;
import org.smoothbuild.vm.task.ConstTask;
import org.smoothbuild.vm.task.IdentityTask;
import org.smoothbuild.vm.task.Output;
import org.smoothbuild.vm.task.Task;

/**
 * This class is thread-safe.
 */
public class Computer {
  private final ComputationCache computationCache;
  private final Hash sandboxHash;
  private final Provider<Container> containerProvider;
  private final ConcurrentHashMap<Hash, PromisedValue<ComputationResult>> promisedValues;

  @Inject
  public Computer(ComputationCache computationCache, @SandboxHash Hash sandboxHash,
      Provider<Container> containerProvider) {
    this.computationCache = computationCache;
    this.sandboxHash = sandboxHash;
    this.containerProvider = containerProvider;
    this.promisedValues = new ConcurrentHashMap<>();
  }

  public void compute(Task task, TupleB input, Consumer<ComputationResult> consumer)
      throws ComputationCacheExc {
    Hash hash = computationHash(task, input);
    PromisedValue<ComputationResult> newPromised = new PromisedValue<>();
    PromisedValue<ComputationResult> prevPromised = promisedValues.putIfAbsent(hash, newPromised);
    if (prevPromised != null) {
      prevPromised
          .chain(c -> computationResultFromPromise(c, task))
          .addConsumer(consumer);
    } else {
      newPromised.addConsumer(consumer);
      if (computationCache.contains(hash)) {
        var output = computationCache.read(hash, task.outputT());
        newPromised.accept(new ComputationResult(output, resSourceOrNoop(DISK, task)));
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

  private static ComputationResult computationResultFromPromise(
      ComputationResult result, Task task) {
    ResSource resSource = resSourceTakenFromPromise(result, task);
    return new ComputationResult(
        result.output(),
        result.exception(),
        resSource);
  }

  private ComputationResult runComputation(Task task, TupleB input) {
    var container = containerProvider.get();
    Output output;
    var resSource = resSourceOrNoop(EXECUTION, task);
    try {
      output = task.run(input, container);
    } catch (Exception e) {
      return new ComputationResult(e, resSource);
    }
    // This Computed instance creation is outside try-block
    // so eventual exception it could throw won't be caught by above catch.
    return new ComputationResult(output, resSource);
  }

  private static ResSource resSourceTakenFromPromise(
      ComputationResult result, Task task) {
    if (result.resSource() == EXECUTION) {
      return task.isPure() ? DISK : MEMORY;
    } else {
      return resSourceOrNoop(result.resSource(), task);
    }
  }

  private static ResSource resSourceOrNoop(ResSource resSource, Task task) {
    return isNonExecutingTask(task) ? NOOP : resSource;
  }

  private static boolean isNonExecutingTask(Task task) {
    return task instanceof ConstTask || task instanceof IdentityTask;
  }

  private Hash computationHash(Task task, TupleB args) {
    return computationHash(sandboxHash, task, args);
  }

  public static Hash computationHash(Hash sandboxHash, Task task, TupleB args) {
    return Hash.of(asList(sandboxHash, taskHash(task), args.hash()));
  }
}
