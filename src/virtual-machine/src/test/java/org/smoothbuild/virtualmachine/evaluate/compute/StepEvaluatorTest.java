package org.smoothbuild.virtualmachine.evaluate.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.log.base.ResultSource.DISK;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.base.ResultSource.MEMORY;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.virtualmachine.evaluate.step.BOutput.bOutput;

import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.ResultSource;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.step.BOutput;
import org.smoothbuild.virtualmachine.evaluate.step.CombineStep;
import org.smoothbuild.virtualmachine.evaluate.step.InvokeStep;
import org.smoothbuild.virtualmachine.evaluate.step.OrderStep;
import org.smoothbuild.virtualmachine.evaluate.step.PickStep;
import org.smoothbuild.virtualmachine.evaluate.step.SelectStep;
import org.smoothbuild.virtualmachine.evaluate.step.Step;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class StepEvaluatorTest extends TestingVirtualMachine {
  @Nested
  class _combine_step {
    @Test
    void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var step = new CombineStep(bCombine(bInt()), bTrace());
      var input = bTuple(value);
      var memory = bTuple(bInt(1));
      var disk = bTuple(bInt(2));

      assertComputationResult(step, input, memory, disk, output(memory), DISK);
    }

    @Test
    void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var step = new CombineStep(bCombine(bInt()), bTrace());
      var input = bTuple(value);
      var disk = bTuple(bInt(2));

      assertComputationResult(step, input, null, disk, output(disk), DISK);
    }

    @Test
    void when_not_cached() throws Exception {
      var value = bInt(17);
      var step = new CombineStep(bCombine(bInt()), bTrace());
      var input = bTuple(value);

      assertComputationResult(step, input, null, null, output(bTuple(value)), EXECUTION);
    }

    @Test
    void executed_computation_is_cached_on_disk() throws Exception {
      var value = bInt(17);
      var step = new CombineStep(bCombine(bInt()), bTrace());
      var input = bTuple(value);

      assertCachesState(step, input, null, bTuple(value));
    }
  }

  @Nested
  class _invoke_step {
    @Nested
    class _with_pure_invoke {
      @Test
      void when_cached_in_memory_and_disk() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        var step = new InvokeStep(invoke, bTrace());
        var input = argumentsForInvokeStep(invoke);
        var memory = bString("def");
        var disk = bString("ghi");

        assertComputationResult(step, input, memory, disk, output(memory), DISK);
      }

      @Test
      void when_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        var step = new InvokeStep(invoke, bTrace());
        var input = argumentsForInvokeStep(invoke);
        var disk = bString("ghi");

        assertComputationResult(step, input, null, disk, output(disk), DISK);
      }

      @Test
      void when_not_cached() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        var step = new InvokeStep(invoke, bTrace());
        var input = argumentsForInvokeStep(invoke);

        assertComputationResult(step, input, null, null, output(bString("abc")), EXECUTION);
      }

      @Test
      void executed_computation_is_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        var step = new InvokeStep(invoke, bTrace());
        var input = argumentsForInvokeStep(invoke);

        assertCachesState(step, input, null, bString("abc"));
      }
    }

    @Nested
    class _with_impure_invoke {
      @Test
      void when_cached_in_memory_and_disk() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        var step = new InvokeStep(invoke, bTrace());
        var input = argumentsForInvokeStep(invoke);
        var memory = bString("def");
        var disk = bString("ghi");

        assertComputationResult(step, input, memory, disk, output(memory), MEMORY);
      }

      @Test
      void when_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        var step = new InvokeStep(invoke, bTrace());
        var input = argumentsForInvokeStep(invoke);
        var disk = bString("ghi");

        assertComputationResult(step, input, null, disk, output(bString("abc")), EXECUTION);
      }

      @Test
      void when_not_cached() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        var step = new InvokeStep(invoke, bTrace());
        var input = argumentsForInvokeStep(invoke);

        assertComputationResult(step, input, null, null, output(bString("abc")), EXECUTION);
      }

      @Test
      void executed_computation_is_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        var step = new InvokeStep(invoke, bTrace());
        var input = argumentsForInvokeStep(invoke);

        assertCachesState(step, input, bOutput(bString("abc"), bLogArrayEmpty()), null);
      }
    }

    private BTuple argumentsForInvokeStep(BInvoke invoke) throws BytecodeException {
      var subExprs = invoke.subExprs();
      return bTuple(
          (BValue) subExprs.method(), (BValue) subExprs.isPure(), (BValue) subExprs.arguments());
    }
  }

  @Nested
  class _order_step {
    @Test
    void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var step = new OrderStep(bOrder(bIntType()), bTrace());
      var input = bTuple(value);
      var memory = bArray(bInt(1));
      var disk = bArray(bInt(2));

      assertComputationResult(step, input, memory, disk, output(memory), DISK);
    }

    @Test
    void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var step = new OrderStep(bOrder(bIntType()), bTrace());
      var input = bTuple(value);
      var disk = bArray(bInt(2));

      assertComputationResult(step, input, null, disk, output(disk), DISK);
    }

    @Test
    void when_not_cached() throws Exception {
      var value = bInt(17);
      var step = new OrderStep(bOrder(bIntType()), bTrace());
      var input = bTuple(value);

      assertComputationResult(step, input, null, null, output(bArray(value)), EXECUTION);
    }

    @Test
    void executed_computation_is_cached_on_disk() throws Exception {
      var value = bInt(17);
      var step = new OrderStep(bOrder(bIntType()), bTrace());
      var input = bTuple(value);

      assertCachesState(step, input, null, bArray(value));
    }
  }

  @Nested
  class _pick_step {
    @Test
    void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var step = new PickStep(bPick(), bTrace());
      var input = bTuple(bArray(value), bInt(0));
      var memory = bInt(1);
      var disk = bInt(2);

      assertComputationResult(step, input, memory, disk, output(memory), DISK);
    }

    @Test
    void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var step = new PickStep(bPick(), bTrace());
      var input = bTuple(bArray(value), bInt(0));
      var disk = bInt(2);

      assertComputationResult(step, input, null, disk, output(disk), DISK);
    }

    @Test
    void when_not_cached() throws Exception {
      var value = bInt(17);
      var step = new PickStep(bPick(), bTrace());
      var input = bTuple(bArray(value), bInt(0));

      assertComputationResult(step, input, null, null, output(value), EXECUTION);
    }

    @Test
    void executed_computation_is_cached_on_disk() throws Exception {
      var value = bInt(17);
      var task = new PickStep(bPick(), bTrace());
      var input = bTuple(bArray(value), bInt(0));

      assertCachesState(task, input, null, value);
    }
  }

  @Nested
  class _select_step {
    @Test
    void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var task = new SelectStep(bSelect(), bTrace());
      var input = bTuple(bTuple(value), bInt(0));
      var memory = bInt(1);
      var disk = bInt(2);

      assertComputationResult(task, input, memory, disk, output(memory), DISK);
    }

    @Test
    void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var task = new SelectStep(bSelect(), bTrace());
      var input = bTuple(bTuple(value), bInt(0));
      var disk = bInt(2);

      assertComputationResult(task, input, null, disk, output(disk), DISK);
    }

    @Test
    void when_not_cached() throws Exception {
      var value = bInt(17);
      var task = new SelectStep(bSelect(), bTrace());
      var input = bTuple(bTuple(value), bInt(0));

      assertComputationResult(task, input, null, null, output(value), EXECUTION);
    }

    @Test
    void executed_computation_is_cached_on_disk() throws Exception {
      var value = bInt(17);
      var task = new SelectStep(bSelect(), bTrace());
      var input = bTuple(bTuple(value), bInt(0));

      assertCachesState(task, input, null, value);
    }
  }

  private void assertComputationResult(
      Step step,
      BTuple input,
      BValue memoryValue,
      BValue diskValue,
      BOutput expectedOutput,
      ResultSource expectedResultSource)
      throws Exception {
    var stepEvaluator = stepEvaluatorWithCaches(step, input, memoryValue, diskValue);
    assertComputationResult(stepEvaluator, step, input, expectedOutput, expectedResultSource);
  }

  private StepEvaluator stepEvaluatorWithCaches(
      Step step, BTuple input, BValue memoryValue, BValue diskValue) throws Exception {
    var computationCache = computationCache();
    var computationHashFactory = computationHashFactory();
    var computationHash = computationHashFactory.create(step, input);
    if (diskValue != null) {
      computationCache.write(computationHash, output(diskValue));
    }
    var memoryCache = new ConcurrentHashMap<Hash, Promise<BOutput>>();
    if (memoryValue != null) {
      memoryCache.put(computationHash, promise(bOutput(memoryValue, bLogArrayEmpty())));
    }
    return new StepEvaluator(
        computationHashFactory,
        this::container,
        computationCache,
        taskExecutor(),
        bytecodeF(),
        memoryCache);
  }

  private void assertComputationResult(
      StepEvaluator stepEvaluator,
      Step step,
      BTuple input,
      BOutput expectedOutput,
      ResultSource expectedResultSource)
      throws Exception {
    var argPromises = input.elements().map(Promise::promise);
    var resultPromise = stepEvaluator.evaluate(step, argPromises);
    taskExecutor().waitUntilIdle();
    assertThat(resultPromise.get()).isEqualTo(expectedOutput.value());
    var report = report(step.label(), step.trace(), expectedResultSource, list());
    assertThat(reporter().reports()).contains(report);
  }

  private void assertCachesState(Step step, BTuple input, BOutput memoryValue, BValue diskValue)
      throws Exception {
    var computationCache = computationCache();
    var memoryCache = new ConcurrentHashMap<Hash, Promise<BOutput>>();
    var computationHashFactory = computationHashFactory();
    var taskExecutor = taskExecutor();
    var stepEvaluator = new StepEvaluator(
        computationHashFactory,
        this::container,
        computationCache,
        taskExecutor,
        bytecodeF(),
        memoryCache);
    stepEvaluator.evaluate(step, input.elements().map(Promise::promise));
    taskExecutor.waitUntilIdle();

    var stepHash = computationHashFactory.create(step, input);

    if (memoryValue == null) {
      assertThat(memoryCache.containsKey(stepHash)).isFalse();
    } else {
      assertThat(memoryCache.get(stepHash).get()).isEqualTo(memoryValue);
    }
    if (diskValue == null) {
      assertThat(computationCache.contains(stepHash)).isFalse();
    } else {
      assertThat(computationCache.read(stepHash, diskValue.type())).isEqualTo(output(diskValue));
    }
  }
}
