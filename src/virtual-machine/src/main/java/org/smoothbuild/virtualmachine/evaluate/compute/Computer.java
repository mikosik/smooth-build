package org.smoothbuild.virtualmachine.evaluate.compute;

import static java.util.Arrays.asList;
import static org.smoothbuild.common.log.base.ResultSource.DISK;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.base.ResultSource.MEMORY;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.containsFatal;
import static org.smoothbuild.virtualmachine.evaluate.compute.ComputeException.computeException;
import static org.smoothbuild.virtualmachine.evaluate.task.Purity.FAST;
import static org.smoothbuild.virtualmachine.evaluate.task.Purity.PURE;
import static org.smoothbuild.virtualmachine.evaluate.task.TaskHashes.taskHash;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import java.util.concurrent.ConcurrentHashMap;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.concurrent.PromisedValue;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.evaluate.task.Output;
import org.smoothbuild.virtualmachine.evaluate.task.Purity;
import org.smoothbuild.virtualmachine.evaluate.task.Task;
import org.smoothbuild.virtualmachine.wire.Sandbox;

/**
 * This class is thread-safe.
 */
@Singleton
public class Computer {
  private final Hash sandboxHash;
  private final Provider<Container> containerProvider;
  private final ComputationCache diskCache;
  private final ConcurrentHashMap<Hash, PromisedValue<ComputationResult>> memoryCache;

  @Inject
  public Computer(
      @Sandbox Hash sandboxHash,
      Provider<Container> containerProvider,
      ComputationCache diskCache) {
    this(sandboxHash, containerProvider, diskCache, new ConcurrentHashMap<>());
  }

  public Computer(
      @Sandbox Hash sandboxHash,
      Provider<Container> containerProvider,
      ComputationCache diskCache,
      ConcurrentHashMap<Hash, PromisedValue<ComputationResult>> memoryCache) {
    this.diskCache = diskCache;
    this.sandboxHash = sandboxHash;
    this.containerProvider = containerProvider;
    this.memoryCache = memoryCache;
  }

  public ComputationResult compute(Task task, BTuple input)
      throws ComputeException, InterruptedException {
    var purity = purityOf(task, input);
    if (purity == FAST) {
      return computeFast(task, input);
    } else {
      return computeWithCache(task, purity, input);
    }
  }

  private static Purity purityOf(Task task, BTuple input) throws ComputeException {
    try {
      return task.purity(input);
    } catch (BytecodeException e) {
      throw computeException(e);
    }
  }

  private ComputationResult computeFast(Task task, BTuple input) throws ComputeException {
    var output = runComputation(task, input);
    return new ComputationResult(output, EXECUTION);
  }

  private ComputationResult computeWithCache(Task task, Purity purity, BTuple input)
      throws ComputeException, InterruptedException {
    var hash = computationHash(task, input);
    PromisedValue<ComputationResult> newPromised = new PromisedValue<>();
    PromisedValue<ComputationResult> prevPromised = memoryCache.putIfAbsent(hash, newPromised);
    if (prevPromised != null) {
      return computationResultFromPromise(prevPromised.getBlocking(), purity);
    } else {
      if (purity == PURE && diskCache.contains(hash)) {
        var output = diskCache.read(hash, task.outputType());
        var result = new ComputationResult(output, DISK);
        newPromised.accept(result);
        memoryCache.remove(hash);
        return result;
      } else {
        var output = runComputation(task, input);
        var result = new ComputationResult(output, EXECUTION);
        newPromised.accept(result);
        if (purity == PURE) {
          if (!outputContainsFatalMessage(output)) {
            diskCache.write(hash, output);
          }
          memoryCache.remove(hash);
        }
        return result;
      }
    }
  }

  private static boolean outputContainsFatalMessage(Output output) throws ComputeException {
    try {
      return containsFatal(output.storedLogs());
    } catch (BytecodeException e) {
      throw computeException(e);
    }
  }

  private static ComputationResult computationResultFromPromise(
      ComputationResult computationResult, Purity purity) {
    var resultSource =
        switch (purity) {
          case PURE -> DISK;
          case IMPURE -> MEMORY;
          case FAST -> throw new RuntimeException("shouldn't happen");
        };
    return new ComputationResult(computationResult.output(), resultSource);
  }

  private Output runComputation(Task task, BTuple input) throws ComputeException {
    var container = containerProvider.get();
    try {
      return task.run(input, container);
    } catch (BytecodeException e) {
      throw computeException(e);
    }
  }

  private Hash computationHash(Task task, BTuple args) {
    return computationHash(sandboxHash, task, args);
  }

  public static Hash computationHash(Hash sandboxHash, Task task, BTuple input) {
    return Hash.of(asList(sandboxHash, taskHash(task), input.hash()));
  }
}
