package org.smoothbuild.vm.compute;

import static java.util.Arrays.asList;
import static org.smoothbuild.vm.compute.ResultSource.DISK;
import static org.smoothbuild.vm.compute.ResultSource.EXECUTION;
import static org.smoothbuild.vm.compute.ResultSource.MEMORY;
import static org.smoothbuild.vm.compute.ResultSource.NOOP;
import static org.smoothbuild.vm.task.Purity.PURE;
import static org.smoothbuild.vm.task.TaskHashes.taskHash;

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
      if (task.purity() == PURE && computationCache.contains(hash)) {
        var output = computationCache.read(hash, task.outputT());
        newPromised.accept(new ComputationResult(output, DISK));
        promisedValues.remove(hash);
      } else {
        var computed = runComputation(task, input);
        boolean cacheOnDisk = task.purity() == PURE && computed.hasOutput();
        if (cacheOnDisk) {
          computationCache.write(hash, computed.output());
          promisedValues.remove(hash);
        }
        newPromised.accept(computed);
      }
    }
  }

  private static ComputationResult computationResultFromPromise(
      ComputationResult computationResult, Task task) {
    var promiseResultSource = computationResult.source();
    var resultSource = switch (promiseResultSource) {
      case EXECUTION -> switch (task.purity()) {
        case PURE -> DISK;
        case IMPURE -> MEMORY;
        case FAST -> throw new RuntimeException("shouldn't happen");
      };
      default -> promiseResultSource;
    };
    return new ComputationResult(
        computationResult.output(), computationResult.exception(), resultSource);
  }

  private ComputationResult runComputation(Task task, TupleB input) {
    var container = containerProvider.get();
    var resultSource = switch (task.purity()) {
      case PURE, IMPURE -> EXECUTION;
      case FAST -> NOOP;
    };
    Output output;
    try {
      output = task.run(input, container);
    } catch (Exception e) {
      return new ComputationResult(e, resultSource);
    }
    // This Computed instance creation is outside try-block
    // so eventual exception it could throw won't be caught by above catch.
    return new ComputationResult(output, resultSource);
  }

  private Hash computationHash(Task task, TupleB args) {
    return computationHash(sandboxHash, task, args);
  }

  public static Hash computationHash(Hash sandboxHash, Task task, TupleB input) {
    return Hash.of(asList(sandboxHash, taskHash(task), input.hash()));
  }
}
