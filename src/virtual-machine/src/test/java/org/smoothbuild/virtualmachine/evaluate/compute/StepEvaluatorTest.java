package org.smoothbuild.virtualmachine.evaluate.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.log.base.Origin.DISK;
import static org.smoothbuild.common.log.base.Origin.EXECUTION;
import static org.smoothbuild.common.log.base.Origin.MEMORY;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.testing.AwaitHelper.await;
import static org.smoothbuild.virtualmachine.evaluate.step.BOutput.bOutput;

import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Origin;
import org.smoothbuild.common.schedule.Tasks;
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
import org.smoothbuild.virtualmachine.testing.VmTestContext;

public class StepEvaluatorTest extends VmTestContext {
  @Nested
  class _combine_step {
    @Test
    void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var step = new CombineStep(bCombine(bInt()), trace());
      var input = bTuple(value);
      var memory = bTuple(bInt(1));
      var disk = bTuple(bInt(2));

      assertComputationResult(step, input, memory, disk, output(memory), DISK);
    }

    @Test
    void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var step = new CombineStep(bCombine(bInt()), trace());
      var input = bTuple(value);
      var disk = bTuple(bInt(2));

      assertComputationResult(step, input, null, disk, output(disk), DISK);
    }

    @Test
    void when_not_cached() throws Exception {
      var value = bInt(17);
      var step = new CombineStep(bCombine(bInt()), trace());
      var input = bTuple(value);

      assertComputationResult(step, input, null, null, output(bTuple(value)), EXECUTION);
    }

    @Test
    void executed_computation_is_cached_on_disk() throws Exception {
      var value = bInt(17);
      var step = new CombineStep(bCombine(bInt()), trace());
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
        var step = new InvokeStep(invoke, trace());
        var input = argumentsForInvokeStep(invoke);
        var memory = bString("def");
        var disk = bString("ghi");

        assertComputationResult(step, input, memory, disk, output(memory), DISK);
      }

      @Test
      void when_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        var step = new InvokeStep(invoke, trace());
        var input = argumentsForInvokeStep(invoke);
        var disk = bString("ghi");

        assertComputationResult(step, input, null, disk, output(disk), DISK);
      }

      @Test
      void when_not_cached() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        var step = new InvokeStep(invoke, trace());
        var input = argumentsForInvokeStep(invoke);

        assertComputationResult(step, input, null, null, output(bString("abc")), EXECUTION);
      }

      @Test
      void executed_computation_is_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        var step = new InvokeStep(invoke, trace());
        var input = argumentsForInvokeStep(invoke);

        assertCachesState(step, input, null, bString("abc"));
      }
    }

    @Nested
    class _with_impure_invoke {
      @Test
      void when_cached_in_memory_and_disk() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        var step = new InvokeStep(invoke, trace());
        var input = argumentsForInvokeStep(invoke);
        var memory = bString("def");
        var disk = bString("ghi");

        assertComputationResult(step, input, memory, disk, output(memory), MEMORY);
      }

      @Test
      void when_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        var step = new InvokeStep(invoke, trace());
        var input = argumentsForInvokeStep(invoke);
        var disk = bString("ghi");

        assertComputationResult(step, input, null, disk, output(bString("abc")), EXECUTION);
      }

      @Test
      void when_not_cached() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        var step = new InvokeStep(invoke, trace());
        var input = argumentsForInvokeStep(invoke);

        assertComputationResult(step, input, null, null, output(bString("abc")), EXECUTION);
      }

      @Test
      void executed_computation_is_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        var step = new InvokeStep(invoke, trace());
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
      var step = new OrderStep(bOrder(bIntType()), trace());
      var input = bTuple(value);
      var memory = bArray(bInt(1));
      var disk = bArray(bInt(2));

      assertComputationResult(step, input, memory, disk, output(memory), DISK);
    }

    @Test
    void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var step = new OrderStep(bOrder(bIntType()), trace());
      var input = bTuple(value);
      var disk = bArray(bInt(2));

      assertComputationResult(step, input, null, disk, output(disk), DISK);
    }

    @Test
    void when_not_cached() throws Exception {
      var value = bInt(17);
      var step = new OrderStep(bOrder(bIntType()), trace());
      var input = bTuple(value);

      assertComputationResult(step, input, null, null, output(bArray(value)), EXECUTION);
    }

    @Test
    void executed_computation_is_cached_on_disk() throws Exception {
      var value = bInt(17);
      var step = new OrderStep(bOrder(bIntType()), trace());
      var input = bTuple(value);

      assertCachesState(step, input, null, bArray(value));
    }
  }

  @Nested
  class _pick_step {
    @Test
    void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var step = new PickStep(bPick(), trace());
      var input = bTuple(bArray(value), bInt(0));
      var memory = bInt(1);
      var disk = bInt(2);

      assertComputationResult(step, input, memory, disk, output(memory), DISK);
    }

    @Test
    void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var step = new PickStep(bPick(), trace());
      var input = bTuple(bArray(value), bInt(0));
      var disk = bInt(2);

      assertComputationResult(step, input, null, disk, output(disk), DISK);
    }

    @Test
    void when_not_cached() throws Exception {
      var value = bInt(17);
      var step = new PickStep(bPick(), trace());
      var input = bTuple(bArray(value), bInt(0));

      assertComputationResult(step, input, null, null, output(value), EXECUTION);
    }

    @Test
    void executed_computation_is_cached_on_disk() throws Exception {
      var value = bInt(17);
      var task = new PickStep(bPick(), trace());
      var input = bTuple(bArray(value), bInt(0));

      assertCachesState(task, input, null, value);
    }
  }

  @Nested
  class _select_step {
    @Test
    void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var task = new SelectStep(bSelect(), trace());
      var input = bTuple(bTuple(value), bInt(0));
      var memory = bInt(1);
      var disk = bInt(2);

      assertComputationResult(task, input, memory, disk, output(memory), DISK);
    }

    @Test
    void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var task = new SelectStep(bSelect(), trace());
      var input = bTuple(bTuple(value), bInt(0));
      var disk = bInt(2);

      assertComputationResult(task, input, null, disk, output(disk), DISK);
    }

    @Test
    void when_not_cached() throws Exception {
      var value = bInt(17);
      var task = new SelectStep(bSelect(), trace());
      var input = bTuple(bTuple(value), bInt(0));

      assertComputationResult(task, input, null, null, output(value), EXECUTION);
    }

    @Test
    void executed_computation_is_cached_on_disk() throws Exception {
      var value = bInt(17);
      var task = new SelectStep(bSelect(), trace());
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
      Origin expectedOrigin)
      throws Exception {
    var stepEvaluator = stepEvaluatorWithCaches(step, input, memoryValue, diskValue);
    assertComputationResult(stepEvaluator, step, input, expectedOutput, expectedOrigin);
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
        scheduler(),
        bytecodeF(),
        memoryCache);
  }

  private void assertComputationResult(
      StepEvaluator stepEvaluator,
      Step step,
      BTuple input,
      BOutput expectedOutput,
      Origin expectedOrigin)
      throws Exception {
    var arg = input.elements().map(Tasks::argument);
    var result = stepEvaluator.evaluate(step, arg);
    await().until(() -> result.toMaybe().isSome());

    assertThat(result.get().get()).isEqualTo(expectedOutput.value());
    var report = report(step.label(), step.trace(), expectedOrigin, list());
    assertThat(reporter().reports()).contains(report);
  }

  private void assertCachesState(Step step, BTuple input, BOutput memoryValue, BValue diskValue)
      throws Exception {
    var computationCache = computationCache();
    var memoryCache = new ConcurrentHashMap<Hash, Promise<BOutput>>();
    var computationHashFactory = computationHashFactory();
    var scheduler = scheduler();
    var stepEvaluator = new StepEvaluator(
        computationHashFactory,
        this::container,
        computationCache,
        scheduler,
        bytecodeF(),
        memoryCache);
    var result = stepEvaluator.evaluate(step, input.elements().map(Tasks::argument));
    await().until(() -> result.toMaybe().isSome());

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
