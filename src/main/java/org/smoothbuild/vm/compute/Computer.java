package org.smoothbuild.vm.compute;

import static java.util.Arrays.asList;
import static org.smoothbuild.vm.compute.ResultSource.DISK;
import static org.smoothbuild.vm.compute.ResultSource.EXECUTION;
import static org.smoothbuild.vm.compute.ResultSource.MEMORY;
import static org.smoothbuild.vm.compute.ResultSource.NOOP;
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
      if (isCacheableTask(task) && computationCache.contains(hash)) {
        var output = computationCache.read(hash, task.outputT());
        newPromised.accept(new ComputationResult(output, resSourceOrNoop(DISK, task)));
        promisedValues.remove(hash);
      } else {
        var computed = runComputation(task, input);
        boolean cacheOnDisk = isCacheableTask(task) && computed.hasOutput();
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
    var source = resSourceTakenFromPromise(result, task);
    return new ComputationResult(result.output(), result.exception(), source);
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

  private static ResultSource resSourceTakenFromPromise(
      ComputationResult result, Task task) {
    if (result.source() == EXECUTION) {
      return task.isPure() ? DISK : MEMORY;
    } else {
      return resSourceOrNoop(result.source(), task);
    }
  }

  private static ResultSource resSourceOrNoop(ResultSource source, Task task) {
    return isNonExecutingTask(task) ? NOOP : source;
  }

  private static boolean isCacheableTask(Task task) {
    return isExecutingTask(task) && task.isPure();
  }

  private static boolean isExecutingTask(Task task) {
    return !isNonExecutingTask(task);
  }

  private static boolean isNonExecutingTask(Task task) {
    return task instanceof ConstTask || task instanceof IdentityTask;
  }

  private Hash computationHash(Task task, TupleB args) {
    return computationHash(sandboxHash, task, args);
  }

  public static Hash computationHash(Hash sandboxHash, Task task, TupleB input) {
    return Hash.of(asList(sandboxHash, taskHash(task), input.hash()));
  }
}
