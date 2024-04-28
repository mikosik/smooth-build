package org.smoothbuild.virtualmachine.evaluate.compute;

import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.log.base.ResultSource.DISK;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.base.ResultSource.MEMORY;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.containsFatal;
import static org.smoothbuild.virtualmachine.evaluate.compute.ComputeException.computeException;
import static org.smoothbuild.virtualmachine.evaluate.step.Purity.FAST;
import static org.smoothbuild.virtualmachine.evaluate.step.Purity.PURE;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import java.util.concurrent.ConcurrentHashMap;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.concurrent.MutablePromise;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.evaluate.step.Output;
import org.smoothbuild.virtualmachine.evaluate.step.Purity;
import org.smoothbuild.virtualmachine.evaluate.step.Step;

/**
 * This class is thread-safe.
 */
@Singleton
public class Computer {
  private final ComputationHashFactory computationHashFactory;
  private final Provider<Container> containerProvider;
  private final ComputationCache diskCache;
  private final ConcurrentHashMap<Hash, Promise<ComputationResult>> memoryCache;

  @Inject
  public Computer(
      ComputationHashFactory computationHashFactory,
      Provider<Container> containerProvider,
      ComputationCache diskCache) {
    this(computationHashFactory, containerProvider, diskCache, new ConcurrentHashMap<>());
  }

  public Computer(
      ComputationHashFactory computationHashFactory,
      Provider<Container> containerProvider,
      ComputationCache diskCache,
      ConcurrentHashMap<Hash, Promise<ComputationResult>> memoryCache) {
    this.computationHashFactory = computationHashFactory;
    this.diskCache = diskCache;
    this.containerProvider = containerProvider;
    this.memoryCache = memoryCache;
  }

  public ComputationResult compute(Step step, BTuple input)
      throws ComputeException, InterruptedException {
    var purity = purityOf(step, input);
    if (purity == FAST) {
      return computeFast(step, input);
    } else {
      return computeWithCache(step, purity, input);
    }
  }

  private static Purity purityOf(Step step, BTuple input) throws ComputeException {
    try {
      return step.purity(input);
    } catch (BytecodeException e) {
      throw computeException(e);
    }
  }

  private ComputationResult computeFast(Step step, BTuple input) throws ComputeException {
    var output = runComputation(step, input);
    return new ComputationResult(output, EXECUTION);
  }

  private ComputationResult computeWithCache(Step step, Purity purity, BTuple input)
      throws ComputeException, InterruptedException {
    var hash = computationHashFactory.create(step, input);
    MutablePromise<ComputationResult> newPromised = promise();
    Promise<ComputationResult> prevPromised = memoryCache.putIfAbsent(hash, newPromised);
    if (prevPromised != null) {
      return computationResultFromPromise(prevPromised.getBlocking(), purity);
    } else {
      if (purity == PURE && diskCache.contains(hash)) {
        var output = diskCache.read(hash, step.outputType());
        var result = new ComputationResult(output, DISK);
        newPromised.accept(result);
        memoryCache.remove(hash);
        return result;
      } else {
        var output = runComputation(step, input);
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

  private Output runComputation(Step step, BTuple input) throws ComputeException {
    var container = containerProvider.get();
    try {
      return step.run(input, container);
    } catch (BytecodeException e) {
      throw computeException(e);
    }
  }
}
