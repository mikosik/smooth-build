package org.smoothbuild.virtualmachine.evaluate.cache;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.log.base.Origin.DISK;
import static org.smoothbuild.common.log.base.Origin.EXECUTION;
import static org.smoothbuild.common.log.base.Origin.MEMORY;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.testing.AwaitHelper.await;
import static org.smoothbuild.virtualmachine.VmConstants.VM_EVALUATE;

import java.util.concurrent.ConcurrentHashMap;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Origin;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOperation;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BCombineEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BInvokeEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOrderEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BPickEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BSelectEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.OperationEvaluator;
import org.smoothbuild.virtualmachine.evaluate.plugin.BOutput;

public class CachingOperatorEvaluatorTest extends VmTestContext {
  @Nested
  class _combine_evaluator {
    @Test
    void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var evaluator = new BCombineEvaluator(bCombine(bInt()), trace());
      var input = bTuple(value);
      var memory = bTuple(bInt(1));
      var disk = bTuple(bInt(2));

      assertComputationResult(evaluator, input, memory, disk, bOutput(memory), DISK);
    }

    @Test
    void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var evaluator = new BCombineEvaluator(bCombine(bInt()), trace());
      var input = bTuple(value);
      var disk = bTuple(bInt(2));

      assertComputationResult(evaluator, input, null, disk, bOutput(disk), DISK);
    }

    @Test
    void when_not_cached() throws Exception {
      var value = bInt(17);
      var evaluator = new BCombineEvaluator(bCombine(bInt()), trace());
      var input = bTuple(value);

      assertComputationResult(evaluator, input, null, null, bOutput(bTuple(value)), EXECUTION);
    }

    @Test
    void executed_computation_is_cached_on_disk() throws Exception {
      var value = bInt(17);
      var evaluator = new BCombineEvaluator(bCombine(bInt()), trace());
      var input = bTuple(value);

      assertCachesState(evaluator, input, null, bTuple(value));
    }
  }

  @Nested
  class _invoke_evaluator {
    @Nested
    class _with_pure_invoke {
      @Test
      void when_cached_in_memory_and_disk() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        var evaluator = new BInvokeEvaluator(invoke, trace());
        var input = argumentsForInvokeEvaluator(invoke);
        var memory = bString("def");
        var disk = bString("ghi");

        assertComputationResult(evaluator, input, memory, disk, bOutput(memory), DISK);
      }

      @Test
      void when_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        var evaluator = new BInvokeEvaluator(invoke, trace());
        var input = argumentsForInvokeEvaluator(invoke);
        var disk = bString("ghi");

        assertComputationResult(evaluator, input, null, disk, bOutput(disk), DISK);
      }

      @Test
      void when_not_cached() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        var evaluator = new BInvokeEvaluator(invoke, trace());
        var input = argumentsForInvokeEvaluator(invoke);

        assertComputationResult(evaluator, input, null, null, bOutput(bString("abc")), EXECUTION);
      }

      @Test
      void executed_computation_is_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        var evaluator = new BInvokeEvaluator(invoke, trace());
        var input = argumentsForInvokeEvaluator(invoke);

        assertCachesState(evaluator, input, null, bString("abc"));
      }
    }

    @Nested
    class _with_impure_invoke {
      @Test
      void when_cached_in_memory_and_disk() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        var evaluator = new BInvokeEvaluator(invoke, trace());
        var input = argumentsForInvokeEvaluator(invoke);
        var memory = bString("def");
        var disk = bString("ghi");

        assertComputationResult(evaluator, input, memory, disk, bOutput(memory), MEMORY);
      }

      @Test
      void when_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        var evaluator = new BInvokeEvaluator(invoke, trace());
        var input = argumentsForInvokeEvaluator(invoke);
        var disk = bString("ghi");

        assertComputationResult(evaluator, input, null, disk, bOutput(bString("abc")), EXECUTION);
      }

      @Test
      void when_not_cached() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        var evaluator = new BInvokeEvaluator(invoke, trace());
        var input = argumentsForInvokeEvaluator(invoke);

        assertComputationResult(evaluator, input, null, null, bOutput(bString("abc")), EXECUTION);
      }

      @Test
      void executed_computation_is_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        var evaluator = new BInvokeEvaluator(invoke, trace());
        var input = argumentsForInvokeEvaluator(invoke);

        assertCachesState(evaluator, input, bOutput(bString("abc"), bLogArrayEmpty()), null);
      }
    }

    private BTuple argumentsForInvokeEvaluator(BInvoke invoke) throws BytecodeException {
      return bTuple(
          (BValue) invoke.method(), (BValue) invoke.isPure(), (BValue) invoke.arguments());
    }
  }

  @Nested
  class _order_evaluator {
    @Test
    void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var evaluator = new BOrderEvaluator(bOrder(bIntType()), trace());
      var input = bTuple(value);
      var memory = bArray(bInt(1));
      var disk = bArray(bInt(2));

      assertComputationResult(evaluator, input, memory, disk, bOutput(memory), DISK);
    }

    @Test
    void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var evaluator = new BOrderEvaluator(bOrder(bIntType()), trace());
      var input = bTuple(value);
      var disk = bArray(bInt(2));

      assertComputationResult(evaluator, input, null, disk, bOutput(disk), DISK);
    }

    @Test
    void when_not_cached() throws Exception {
      var value = bInt(17);
      var evaluator = new BOrderEvaluator(bOrder(bIntType()), trace());
      var input = bTuple(value);

      assertComputationResult(evaluator, input, null, null, bOutput(bArray(value)), EXECUTION);
    }

    @Test
    void executed_computation_is_cached_on_disk() throws Exception {
      var value = bInt(17);
      var evaluator = new BOrderEvaluator(bOrder(bIntType()), trace());
      var input = bTuple(value);

      assertCachesState(evaluator, input, null, bArray(value));
    }
  }

  @Nested
  class _pick_evaluator {
    @Test
    void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var evaluator = new BPickEvaluator(bPick(), trace());
      var input = bTuple(bArray(value), bInt(0));
      var memory = bInt(1);
      var disk = bInt(2);

      assertComputationResult(evaluator, input, memory, disk, bOutput(memory), DISK);
    }

    @Test
    void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var evaluator = new BPickEvaluator(bPick(), trace());
      var input = bTuple(bArray(value), bInt(0));
      var disk = bInt(2);

      assertComputationResult(evaluator, input, null, disk, bOutput(disk), DISK);
    }

    @Test
    void when_not_cached() throws Exception {
      var value = bInt(17);
      var evaluator = new BPickEvaluator(bPick(), trace());
      var input = bTuple(bArray(value), bInt(0));

      assertComputationResult(evaluator, input, null, null, bOutput(value), EXECUTION);
    }

    @Test
    void executed_computation_is_cached_on_disk() throws Exception {
      var value = bInt(17);
      var task = new BPickEvaluator(bPick(), trace());
      var input = bTuple(bArray(value), bInt(0));

      assertCachesState(task, input, null, value);
    }
  }

  @Nested
  class _select_evaluator {
    @Test
    void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var evaluator = new BSelectEvaluator(bSelect(), trace());
      var input = bTuple(bTuple(value), bInt(0));
      var memory = bInt(1);
      var disk = bInt(2);

      assertComputationResult(evaluator, input, memory, disk, bOutput(memory), DISK);
    }

    @Test
    void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var evaluator = new BSelectEvaluator(bSelect(), trace());
      var input = bTuple(bTuple(value), bInt(0));
      var disk = bInt(2);

      assertComputationResult(evaluator, input, null, disk, bOutput(disk), DISK);
    }

    @Test
    void when_not_cached() throws Exception {
      var value = bInt(17);
      var evaluator = new BSelectEvaluator(bSelect(), trace());
      var input = bTuple(bTuple(value), bInt(0));

      assertComputationResult(evaluator, input, null, null, bOutput(value), EXECUTION);
    }

    @Test
    void executed_computation_is_cached_on_disk() throws Exception {
      var value = bInt(17);
      var evaluator = new BSelectEvaluator(bSelect(), trace());
      var input = bTuple(bTuple(value), bInt(0));

      assertCachesState(evaluator, input, null, value);
    }
  }

  private void assertComputationResult(
      OperationEvaluator<?> evaluator,
      BTuple subExprValues,
      @Nullable BValue memoryValue,
      @Nullable BValue diskValue,
      BOutput expectedOutput,
      Origin expectedOrigin)
      throws Exception {
    var evaluationScheduler = cachingOperatorEvaluatorWithCaches(
        evaluator.operation(), subExprValues, memoryValue, diskValue);
    assertComputationResult(
        evaluationScheduler, evaluator, subExprValues, expectedOutput, expectedOrigin);
  }

  private CachingOperatorEvaluator cachingOperatorEvaluatorWithCaches(
      BOperation operation,
      BTuple subExprValues,
      @Nullable BValue memoryValue,
      @Nullable BValue diskValue)
      throws Exception {
    var computationCache = provide().computationCache();
    var computationHashFactory = provide().computationHashFactory();
    var computationHash = computationHashFactory.create(operation, subExprValues);
    if (diskValue != null) {
      computationCache.write(computationHash, bOutput(diskValue));
    }
    var memoryCache = new ConcurrentHashMap<Hash, Promise<BOutput>>();
    if (memoryValue != null) {
      memoryCache.put(computationHash, promise(bOutput(memoryValue, bLogArrayEmpty())));
    }
    return new CachingOperatorEvaluator(
        computationHashFactory,
        () -> provide().container(),
        computationCache,
        provide().scheduler(),
        provide().bytecodeFactory(),
        memoryCache);
  }

  private void assertComputationResult(
      CachingOperatorEvaluator cachingOperatorEvaluator,
      OperationEvaluator<?> evaluator,
      BTuple subExprValues,
      BOutput expectedOutput,
      Origin expectedOrigin)
      throws Exception {
    var output = cachingOperatorEvaluator.evaluate(evaluator, subExprValues);
    var promise = output.result();

    await().until(() -> promise.toMaybe().isSome());
    provide().reporter().submit(output.report());

    assertThat(promise.get()).isEqualTo(expectedOutput.value());
    var label = VM_EVALUATE.append(":" + evaluator.operation().name());
    var report = report(label, evaluator.trace(), expectedOrigin, list());
    assertThat(provide().reporter().reports()).contains(report);
  }

  private void assertCachesState(
      OperationEvaluator<?> evaluator,
      BTuple subExprValues,
      @Nullable BOutput memoryValue,
      @Nullable BValue diskValue)
      throws Exception {
    var computationCache = provide().computationCache();
    var memoryCache = new ConcurrentHashMap<Hash, Promise<BOutput>>();
    var computationHashFactory = provide().computationHashFactory();
    var scheduler = provide().scheduler();
    var cachingOperatorEvaluator = new CachingOperatorEvaluator(
        computationHashFactory,
        () -> provide().container(),
        computationCache,
        scheduler,
        provide().bytecodeFactory(),
        memoryCache);
    var output = cachingOperatorEvaluator.evaluate(evaluator, subExprValues);
    var promise = output.result();
    await().until(() -> promise.toMaybe().isSome());

    var evaluationHash = computationHashFactory.create(evaluator.operation(), subExprValues);

    if (memoryValue == null) {
      assertThat(memoryCache.containsKey(evaluationHash)).isFalse();
    } else {
      assertThat(requireNonNull(memoryCache.get(evaluationHash)).get()).isEqualTo(memoryValue);
    }
    if (diskValue == null) {
      assertThat(computationCache.contains(evaluationHash)).isFalse();
    } else {
      assertThat(computationCache.read(evaluationHash, diskValue.type()))
          .isEqualTo(bOutput(diskValue));
    }
  }
}
