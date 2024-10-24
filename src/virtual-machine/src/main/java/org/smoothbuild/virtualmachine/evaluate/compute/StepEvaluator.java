package org.smoothbuild.virtualmachine.evaluate.compute;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.ResultSource.DISK;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.task.Output.output;
import static org.smoothbuild.common.task.Output.schedulingOutput;
import static org.smoothbuild.virtualmachine.VmConstants.VM_EVALUATE;
import static org.smoothbuild.virtualmachine.VmConstants.VM_SCHEDULE;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.containsFatal;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.level;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.message;
import static org.smoothbuild.virtualmachine.evaluate.step.Purity.PURE;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import java.util.concurrent.ConcurrentHashMap;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.MutablePromise;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.ResultSource;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Scheduler;
import org.smoothbuild.common.task.Task1;
import org.smoothbuild.common.task.TaskX;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.step.BOutput;
import org.smoothbuild.virtualmachine.evaluate.step.Purity;
import org.smoothbuild.virtualmachine.evaluate.step.Step;

/**
 * This class is thread-safe.
 */
@Singleton
public class StepEvaluator {
  private final ComputationHashFactory computationHashFactory;
  private final Provider<Container> containerProvider;
  private final ComputationCache diskCache;
  private final ConcurrentHashMap<Hash, Promise<BOutput>> memoryCache;
  private final Scheduler scheduler;
  private final BytecodeFactory bytecodeFactory;

  @Inject
  public StepEvaluator(
      ComputationHashFactory computationHashFactory,
      Provider<Container> containerProvider,
      ComputationCache diskCache,
      Scheduler scheduler,
      BytecodeFactory bytecodeFactory) {
    this(
        computationHashFactory,
        containerProvider,
        diskCache,
        scheduler,
        bytecodeFactory,
        new ConcurrentHashMap<>());
  }

  public StepEvaluator(
      ComputationHashFactory computationHashFactory,
      Provider<Container> containerProvider,
      ComputationCache diskCache,
      Scheduler scheduler,
      BytecodeFactory bytecodeFactory,
      ConcurrentHashMap<Hash, Promise<BOutput>> memoryCache) {
    this.computationHashFactory = computationHashFactory;
    this.diskCache = diskCache;
    this.containerProvider = containerProvider;
    this.scheduler = scheduler;
    this.bytecodeFactory = bytecodeFactory;
    this.memoryCache = memoryCache;
  }

  public Promise<Maybe<BValue>> evaluate(
      Step step, List<? extends Promise<? extends Maybe<BValue>>> subExprResults) {
    TaskX<BValue, BValue> taskX = (bValues) -> {
      try {
        return evaluateStep(step, toInput(bValues));
      } catch (ComputeCacheException | BytecodeException | InterruptedException e) {
        return outputForException(step, e);
      }
    };
    return scheduler.submit(taskX, subExprResults);
  }

  private BTuple toInput(List<BValue> depResults) throws BytecodeException {
    return bytecodeFactory.tuple(depResults);
  }

  protected Output<BValue> evaluateStep(Step step, BTuple input)
      throws ComputeCacheException, InterruptedException, BytecodeException {
    var purity = step.purity(input);
    var hash = computationHashFactory.create(step, input);
    var resultPromise = Promise.<BOutput>promise();
    var existingPromise = memoryCache.putIfAbsent(hash, resultPromise);
    if (existingPromise != null) {
      var result = scheduleTaskWaitingForOtherTaskResult(step, purity, existingPromise);
      return schedulingOutput(result, report(VM_SCHEDULE, step.trace(), EXECUTION, list()));
    } else if (purity == PURE && diskCache.contains(hash)) {
      return readEvaluationFromDiskCache(step, hash, resultPromise);
    } else {
      return evaluateNow(step, input, resultPromise, purity, hash);
    }
  }

  private Promise<Maybe<BValue>> scheduleTaskWaitingForOtherTaskResult(
      Step step, Purity purity, Promise<BOutput> promise) {
    Task1<BOutput, BValue> task = (bOutput) -> {
      try {
        return newOutput(step, bOutput, purity.cacheLevel());
      } catch (BytecodeException e) {
        return outputForException(step, e);
      }
    };
    return scheduler.submit(task, promise.map(Maybe::some));
  }

  private Output<BValue> readEvaluationFromDiskCache(
      Step step, Hash hash, MutablePromise<BOutput> resultPromise)
      throws ComputeCacheException, BytecodeException {
    var bOutput = diskCache.read(hash, step.evaluationType());
    resultPromise.accept(bOutput);
    memoryCache.remove(hash);
    return newOutput(step, bOutput, DISK);
  }

  private Output<BValue> evaluateNow(
      Step step, BTuple input, MutablePromise<BOutput> resultPromise, Purity purity, Hash hash)
      throws BytecodeException, ComputeCacheException {
    var container = containerProvider.get();
    var bOutput = step.run(input, container);
    resultPromise.accept(bOutput);
    if (purity == PURE) {
      if (!containsFatal(bOutput.storedLogs())) {
        diskCache.write(hash, bOutput);
      }
      memoryCache.remove(hash);
    }
    return newOutput(step, bOutput, EXECUTION);
  }

  private static Output<BValue> newOutput(Step step, BOutput bOutput, ResultSource source)
      throws BytecodeException {
    return output(bOutput.value(), newReport(step, bOutput, source));
  }

  private static Output<BValue> outputForException(Step step, Exception e) {
    var fatal = fatal("Vm evaluation Task failed with exception:", e);
    var report = report(VM_EVALUATE, step.trace(), EXECUTION, list(fatal));
    return output(null, report);
  }

  private static Report newReport(Step step, BOutput bOutput, ResultSource resultSource)
      throws BytecodeException {
    var logs = bOutput
        .storedLogs()
        .elements(BTuple.class)
        .map(message -> new Log(level(message), message(message)));
    return report(step.label(), step.trace(), resultSource, logs);
  }
}
