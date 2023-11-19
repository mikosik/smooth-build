package org.smoothbuild.vm.evaluate.compute;

import static java.util.Arrays.asList;
import static org.smoothbuild.run.eval.MessageStruct.containsFatal;
import static org.smoothbuild.vm.evaluate.compute.ResultSource.DISK;
import static org.smoothbuild.vm.evaluate.compute.ResultSource.EXECUTION;
import static org.smoothbuild.vm.evaluate.compute.ResultSource.MEMORY;
import static org.smoothbuild.vm.evaluate.compute.ResultSource.NOOP;
import static org.smoothbuild.vm.evaluate.task.Purity.FAST;
import static org.smoothbuild.vm.evaluate.task.Purity.PURE;
import static org.smoothbuild.vm.evaluate.task.TaskHashes.taskHash;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.smoothbuild.common.concurrent.PromisedValue;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.evaluate.SandboxHash;
import org.smoothbuild.vm.evaluate.task.Output;
import org.smoothbuild.vm.evaluate.task.Task;

/**
 * This class is thread-safe.
 */
public class Computer {
  private final Hash sandboxHash;
  private final Provider<Container> containerProvider;
  private final ComputationCache diskCache;
  private final ConcurrentHashMap<Hash, PromisedValue<ComputationResult>> memoryCache;

  @Inject
  public Computer(
      @SandboxHash Hash sandboxHash,
      Provider<Container> containerProvider,
      ComputationCache diskCache) {
    this(sandboxHash, containerProvider, diskCache, new ConcurrentHashMap<>());
  }

  public Computer(
      @SandboxHash Hash sandboxHash,
      Provider<Container> containerProvider,
      ComputationCache diskCache,
      ConcurrentHashMap<Hash, PromisedValue<ComputationResult>> memoryCache) {
    this.diskCache = diskCache;
    this.sandboxHash = sandboxHash;
    this.containerProvider = containerProvider;
    this.memoryCache = memoryCache;
  }

  public void compute(Task task, TupleB input, Consumer<ComputationResult> consumer)
      throws ComputationCacheException {
    if (task.purity() == FAST) {
      computeFast(task, input, consumer);
    } else {
      computeWithCache(task, input, consumer);
    }
  }

  private void computeFast(Task task, TupleB input, Consumer<ComputationResult> consumer) {
    var output = runComputation(task, input);
    consumer.accept(new ComputationResult(output, NOOP));
  }

  private void computeWithCache(Task task, TupleB input, Consumer<ComputationResult> consumer)
      throws ComputationCacheException {
    var hash = computationHash(task, input);
    PromisedValue<ComputationResult> newPromised = new PromisedValue<>();
    PromisedValue<ComputationResult> prevPromised = memoryCache.putIfAbsent(hash, newPromised);
    if (prevPromised != null) {
      prevPromised.chain(c -> computationResultFromPromise(c, task)).addConsumer(consumer);
    } else {
      newPromised.addConsumer(consumer);
      var isPure = task.purity() == PURE;
      if (isPure && diskCache.contains(hash)) {
        var output = diskCache.read(hash, task.outputT());
        newPromised.accept(new ComputationResult(output, DISK));
        memoryCache.remove(hash);
      } else {
        var output = runComputation(task, input);
        if (isPure) {
          if (!containsFatal(output.messages())) {
            diskCache.write(hash, output);
          }
          memoryCache.remove(hash);
        }
        newPromised.accept(new ComputationResult(output, EXECUTION));
      }
    }
  }

  private static ComputationResult computationResultFromPromise(
      ComputationResult computationResult, Task task) {
    var resultSource =
        switch (task.purity()) {
          case PURE -> DISK;
          case IMPURE -> MEMORY;
          case FAST -> throw new RuntimeException("shouldn't happen");
        };
    return new ComputationResult(computationResult.output(), resultSource);
  }

  private Output runComputation(Task task, TupleB input) {
    var container = containerProvider.get();
    return task.run(input, container);
  }

  private Hash computationHash(Task task, TupleB args) {
    return computationHash(sandboxHash, task, args);
  }

  public static Hash computationHash(Hash sandboxHash, Task task, TupleB input) {
    return Hash.of(asList(sandboxHash, taskHash(task), input.hash()));
  }
}
