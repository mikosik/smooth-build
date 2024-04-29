package org.smoothbuild.virtualmachine.evaluate.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.log.base.ResultSource.DISK;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.base.ResultSource.MEMORY;

import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;
import org.smoothbuild.virtualmachine.evaluate.step.CombineStep;
import org.smoothbuild.virtualmachine.evaluate.step.InvokeStep;
import org.smoothbuild.virtualmachine.evaluate.step.OrderStep;
import org.smoothbuild.virtualmachine.evaluate.step.PickStep;
import org.smoothbuild.virtualmachine.evaluate.step.SelectStep;
import org.smoothbuild.virtualmachine.evaluate.step.Step;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class ComputerTest extends TestingVirtualMachine {
  @Nested
  class _combine_step {
    @Test
    void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var step = new CombineStep(bCombine(bInt()), bTrace());
      var input = bTuple(value);
      var memory = bTuple(bInt(1));
      var disk = bTuple(bInt(2));

      assertComputationResult(step, input, memory, disk, computationResult(output(memory), DISK));
    }

    @Test
    void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var step = new CombineStep(bCombine(bInt()), bTrace());
      var input = bTuple(value);
      var disk = bTuple(bInt(2));

      var expected = computationResult(output(disk), DISK);
      assertComputationResult(step, input, null, disk, expected);
    }

    @Test
    void when_not_cached() throws Exception {
      var value = bInt(17);
      var step = new CombineStep(bCombine(bInt()), bTrace());
      var input = bTuple(value);

      var expected = computationResult(output(bTuple(value)), EXECUTION);
      assertComputationResult(step, input, null, null, expected);
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
        BTrace trace = bTrace();
        var step = new InvokeStep(invoke, trace);
        var input = argumentsForInvokeStep(invoke);
        var memory = bString("def");
        var disk = bString("ghi");

        assertComputationResult(step, input, memory, disk, computationResult(output(memory), DISK));
      }

      @Test
      void when_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        BTrace trace = bTrace();
        var step = new InvokeStep(invoke, trace);
        var input = argumentsForInvokeStep(invoke);
        var disk = bString("ghi");

        assertComputationResult(step, input, null, disk, computationResult(output(disk), DISK));
      }

      @Test
      void when_not_cached() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        BTrace trace = bTrace();
        var step = new InvokeStep(invoke, trace);
        var input = argumentsForInvokeStep(invoke);

        var expected = computationResult(output(bString("abc")), EXECUTION);
        assertComputationResult(step, input, null, null, expected);
      }

      @Test
      void executed_computation_is_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        BTrace trace = bTrace();
        var step = new InvokeStep(invoke, trace);
        var input = argumentsForInvokeStep(invoke);

        assertCachesState(step, input, null, bString("abc"));
      }
    }

    @Nested
    class _with_impure_invoke {
      @Test
      void when_cached_in_memory_and_disk() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        BTrace trace = bTrace();
        var step = new InvokeStep(invoke, trace);
        var input = argumentsForInvokeStep(invoke);
        var memory = bString("def");
        var disk = bString("ghi");

        assertComputationResult(
            step, input, memory, disk, computationResult(output(memory), MEMORY));
      }

      @Test
      void when_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        BTrace trace = bTrace();
        var step = new InvokeStep(invoke, trace);
        var input = argumentsForInvokeStep(invoke);
        var disk = bString("ghi");

        var computationResult = computationResult(output(bString("abc")), EXECUTION);
        assertComputationResult(step, input, null, disk, computationResult);
      }

      @Test
      void when_not_cached() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        BTrace trace = bTrace();
        var step = new InvokeStep(invoke, trace);
        var input = argumentsForInvokeStep(invoke);

        var expected = computationResult(output(bString("abc")), EXECUTION);
        assertComputationResult(step, input, null, null, expected);
      }

      @Test
      void executed_computation_is_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        BTrace trace = bTrace();
        var step = new InvokeStep(invoke, trace);
        var input = argumentsForInvokeStep(invoke);

        assertCachesState(step, input, computationResult(bString("abc"), EXECUTION), null);
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

      assertComputationResult(step, input, memory, disk, computationResult(output(memory), DISK));
    }

    @Test
    void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var step = new OrderStep(bOrder(bIntType()), bTrace());
      var input = bTuple(value);
      var disk = bArray(bInt(2));

      var expected = computationResult(output(disk), DISK);
      assertComputationResult(step, input, null, disk, expected);
    }

    @Test
    void when_not_cached() throws Exception {
      var value = bInt(17);
      var step = new OrderStep(bOrder(bIntType()), bTrace());
      var input = bTuple(value);

      var expected = computationResult(output(bArray(value)), EXECUTION);
      assertComputationResult(step, input, null, null, expected);
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

      assertComputationResult(step, input, memory, disk, computationResult(output(memory), DISK));
    }

    @Test
    void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var step = new PickStep(bPick(), bTrace());
      var input = bTuple(bArray(value), bInt(0));
      var disk = bInt(2);

      var expected = computationResult(output(disk), DISK);
      assertComputationResult(step, input, null, disk, expected);
    }

    @Test
    void when_not_cached() throws Exception {
      var value = bInt(17);
      var step = new PickStep(bPick(), bTrace());
      var input = bTuple(bArray(value), bInt(0));

      var expected = computationResult(output(value), EXECUTION);
      assertComputationResult(step, input, null, null, expected);
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

      assertComputationResult(task, input, memory, disk, computationResult(output(memory), DISK));
    }

    @Test
    void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var task = new SelectStep(bSelect(), bTrace());
      var input = bTuple(bTuple(value), bInt(0));
      var disk = bInt(2);

      var expected = computationResult(output(disk), DISK);
      assertComputationResult(task, input, null, disk, expected);
    }

    @Test
    void when_not_cached() throws Exception {
      var value = bInt(17);
      var task = new SelectStep(bSelect(), bTrace());
      var input = bTuple(bTuple(value), bInt(0));

      var expected = computationResult(output(value), EXECUTION);
      assertComputationResult(task, input, null, null, expected);
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
      Step step, BTuple input, BValue memoryValue, BValue diskValue, ComputationResult expected)
      throws Exception {
    var computer = computerWithCaches(step, input, memoryValue, diskValue);
    assertComputationResult(computer, step, input, expected);
  }

  private Computer computerWithCaches(Step step, BTuple input, BValue memoryValue, BValue diskValue)
      throws Exception {
    var computationCache = computationCache();
    var computationHashFactory = computationHashFactory();
    var computationHash = computationHashFactory.create(step, input);
    if (diskValue != null) {
      computationCache.write(computationHash, output(diskValue));
    }
    var memoryCache = new ConcurrentHashMap<Hash, Promise<ComputationResult>>();
    if (memoryValue != null) {
      var computationResult = computationResult(memoryValue, EXECUTION);
      memoryCache.put(computationHash, promise(computationResult));
    }
    return new Computer(computationHashFactory, this::container, computationCache, memoryCache);
  }

  private void assertComputationResult(
      Computer computer, Step step, BTuple input, ComputationResult expected) throws Exception {
    var result = computer.compute(step, input);
    assertThat(result).isEqualTo(expected);
  }

  private void assertCachesState(
      Step step, BTuple input, ComputationResult memoryValue, BValue diskValue) throws Exception {
    var computationCache = computationCache();
    var memoryCache = new ConcurrentHashMap<Hash, Promise<ComputationResult>>();
    var computationHashFactory = computationHashFactory();
    var computer =
        new Computer(computationHashFactory, this::container, computationCache, memoryCache);
    computer.compute(step, input);

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
