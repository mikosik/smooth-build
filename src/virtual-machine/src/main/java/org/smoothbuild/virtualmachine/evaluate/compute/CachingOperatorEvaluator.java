package org.smoothbuild.virtualmachine.evaluate.compute;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.Origin.DISK;
import static org.smoothbuild.common.log.base.Origin.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.common.schedule.Output.schedulingOutput;
import static org.smoothbuild.virtualmachine.VmConstants.VM_EVALUATE;
import static org.smoothbuild.virtualmachine.VmConstants.VM_LABEL;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.containsFatal;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.level;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.message;
import static org.smoothbuild.virtualmachine.evaluate.evaluator.Purity.PURE;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.MutablePromise;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.dagger.PerCommand;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.Origin;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Scheduler;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOutput;
import org.smoothbuild.virtualmachine.evaluate.evaluator.OperationEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.Purity;

/**
 * This class is thread-safe.
 */
@PerCommand
public class CachingOperatorEvaluator {
  private final ComputationHashFactory computationHashFactory;
  private final Provider<Container> containerProvider;
  private final ComputationCache diskCache;
  private final ConcurrentHashMap<Hash, Promise<BOutput>> memoryCache;
  private final Scheduler scheduler;
  private final BytecodeFactory bytecodeFactory;

  @Inject
  public CachingOperatorEvaluator(
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

  public CachingOperatorEvaluator(
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

  public Output<BValue> evaluate(OperationEvaluator evaluator, List<BValue> bValues) {
    try {
      return evaluate(evaluator, toInput(bValues));
    } catch (IOException e) {
      return outputForException(evaluator, e);
    }
  }

  private BTuple toInput(List<BValue> depResults) throws BytecodeException {
    return bytecodeFactory.tuple(depResults);
  }

  protected Output<BValue> evaluate(OperationEvaluator evaluator, BTuple subExprValues)
      throws IOException {
    var purity = evaluator.purity(subExprValues);
    var hash = computationHashFactory.create(evaluator.operation(), subExprValues);
    var resultPromise = Promise.<BOutput>promise();
    var existingPromise = memoryCache.putIfAbsent(hash, resultPromise);
    if (existingPromise != null) {
      var result = scheduleTaskWaitingForOtherTaskResult(evaluator, purity, existingPromise);
      var label = VM_LABEL.append(":scheduleJoin");
      return schedulingOutput(result, report(label, evaluator.trace(), list()));
    } else if (purity == PURE && diskCache.contains(hash)) {
      return readEvaluationFromDiskCache(evaluator, hash, resultPromise);
    } else {
      return evaluateNow(evaluator, subExprValues, resultPromise, purity, hash);
    }
  }

  private Promise<Maybe<BValue>> scheduleTaskWaitingForOtherTaskResult(
      OperationEvaluator evaluator, Purity purity, Promise<BOutput> otherTaskResult) {
    Task1<BOutput, BValue> task = (bOutput) -> {
      try {
        return newOutput(evaluator, bOutput, purity.cacheLevel());
      } catch (BytecodeException e) {
        return outputForException(evaluator, e);
      }
    };
    return scheduler.submit(task, otherTaskResult.map(Maybe::some));
  }

  private Output<BValue> readEvaluationFromDiskCache(
      OperationEvaluator evaluator, Hash hash, MutablePromise<BOutput> resultPromise)
      throws IOException {
    var bOutput = diskCache.read(hash, evaluator.operation().evaluationType());
    resultPromise.accept(bOutput);
    memoryCache.remove(hash);
    return newOutput(evaluator, bOutput, DISK);
  }

  private Output<BValue> evaluateNow(
      OperationEvaluator evaluator,
      BTuple subExprValues,
      MutablePromise<BOutput> resultPromise,
      Purity purity,
      Hash hash)
      throws IOException {
    var container = containerProvider.get();
    var bOutput = evaluator.evaluate(subExprValues, container);
    resultPromise.accept(bOutput);
    if (purity == PURE) {
      if (!containsFatal(bOutput.storedLogs())) {
        diskCache.write(hash, bOutput);
      }
      memoryCache.remove(hash);
    }
    return newOutput(evaluator, bOutput, EXECUTION);
  }

  private static Output<BValue> newOutput(
      OperationEvaluator evaluator, BOutput bOutput, Origin source) throws BytecodeException {
    var report = newReport(evaluator, bOutput, source);
    return bOutput.value().map(v -> output(v, report)).getOr(output(report));
  }

  private static Output<BValue> outputForException(OperationEvaluator evaluator, Exception e) {
    var fatal = fatal("Vm evaluation Task failed with exception:", e);
    return output(report(VM_EVALUATE, evaluator.trace(), list(fatal)));
  }

  private static Report newReport(OperationEvaluator evaluator, BOutput bOutput, Origin origin)
      throws BytecodeException {
    var logs = bOutput
        .storedLogs()
        .elements(BTuple.class)
        .map(message -> new Log(level(message), message(message)));
    var label = VM_EVALUATE.append(":" + evaluator.operation().name());
    return report(label, evaluator.trace(), origin, logs);
  }
}
