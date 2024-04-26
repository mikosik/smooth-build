package org.smoothbuild.virtualmachine.evaluate.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.log.base.ResultSource.DISK;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.base.ResultSource.MEMORY;
import static org.smoothbuild.virtualmachine.evaluate.step.InvokeStep.newInvokeStep;

import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.concurrent.PromisedValue;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.step.CombineStep;
import org.smoothbuild.virtualmachine.evaluate.step.ConstStep;
import org.smoothbuild.virtualmachine.evaluate.step.OrderStep;
import org.smoothbuild.virtualmachine.evaluate.step.PickStep;
import org.smoothbuild.virtualmachine.evaluate.step.SelectStep;
import org.smoothbuild.virtualmachine.evaluate.step.Step;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class ComputerTest extends TestingVirtualMachine {
  @Nested
  class _combine_step {
    @Test
    public void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var step = new CombineStep(bCombine(bInt()), bTrace());
      var input = bTuple(value);
      var memory = bTuple(bInt(1));
      var disk = bTuple(bInt(2));

      assertComputationResult(step, input, memory, disk, computationResult(output(memory), DISK));
    }

    @Test
    public void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var step = new CombineStep(bCombine(bInt()), bTrace());
      var input = bTuple(value);
      var disk = bTuple(bInt(2));

      var expected = computationResult(output(disk), DISK);
      assertComputationResult(step, input, null, disk, expected);
    }

    @Test
    public void when_not_cached() throws Exception {
      var value = bInt(17);
      var step = new CombineStep(bCombine(bInt()), bTrace());
      var input = bTuple(value);

      var expected = computationResult(output(bTuple(value)), EXECUTION);
      assertComputationResult(step, input, null, null, expected);
    }

    @Test
    public void executed_computation_is_cached_on_disk() throws Exception {
      var value = bInt(17);
      var step = new CombineStep(bCombine(bInt()), bTrace());
      var input = bTuple(value);

      assertCachesState(step, input, null, bTuple(value));
    }
  }

  @Nested
  class _const_step {
    @Test
    public void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var step = new ConstStep(value, bTrace());
      var input = bTuple();
      var memory = bInt(1);
      var disk = bInt(2);

      assertComputationResult(
          step, input, memory, disk, computationResult(output(value), EXECUTION));
    }

    @Test
    public void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var step = new ConstStep(value, bTrace());
      var input = bTuple();
      var disk = bInt(2);

      assertComputationResult(step, input, null, disk, computationResult(output(value), EXECUTION));
    }

    @Test
    public void when_not_cached() throws Exception {
      var value = bInt(17);
      var step = new ConstStep(value, bTrace());
      var input = bTuple();

      assertComputationResult(step, input, null, null, computationResult(output(value), EXECUTION));
    }

    @Test
    public void executed_computation_is_not_cached() throws Exception {
      var value = bInt(17);
      var step = new ConstStep(value, bTrace());
      var input = bTuple();

      assertCachesState(step, input, null, null);
    }
  }

  @Nested
  class _invoke_step {
    @Nested
    class _with_pure_invoke {
      @Test
      public void when_cached_in_memory_and_disk() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        var step = newInvokeStep(invoke, bTrace());
        var input = argumentsForInvokeStep(invoke);
        var memory = bString("def");
        var disk = bString("ghi");

        assertComputationResult(step, input, memory, disk, computationResult(output(memory), DISK));
      }

      @Test
      public void when_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        var step = newInvokeStep(invoke, bTrace());
        var input = argumentsForInvokeStep(invoke);
        var disk = bString("ghi");

        assertComputationResult(step, input, null, disk, computationResult(output(disk), DISK));
      }

      @Test
      public void when_not_cached() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        var step = newInvokeStep(invoke, bTrace());
        var input = argumentsForInvokeStep(invoke);

        var expected = computationResult(output(bString("abc")), EXECUTION);
        assertComputationResult(step, input, null, null, expected);
      }

      @Test
      public void executed_computation_is_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        var step = newInvokeStep(invoke, bTrace());
        var input = argumentsForInvokeStep(invoke);

        assertCachesState(step, input, null, bString("abc"));
      }
    }

    @Nested
    class _with_impure_invoke {
      @Test
      public void when_cached_in_memory_and_disk() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        var step = newInvokeStep(invoke, bTrace());
        var input = argumentsForInvokeStep(invoke);
        var memory = bString("def");
        var disk = bString("ghi");

        assertComputationResult(
            step, input, memory, disk, computationResult(output(memory), MEMORY));
      }

      @Test
      public void when_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        var step = newInvokeStep(invoke, bTrace());
        var input = argumentsForInvokeStep(invoke);
        var disk = bString("ghi");

        var computationResult = computationResult(output(bString("abc")), EXECUTION);
        assertComputationResult(step, input, null, disk, computationResult);
      }

      @Test
      public void when_not_cached() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        var step = newInvokeStep(invoke, bTrace());
        var input = argumentsForInvokeStep(invoke);

        var expected = computationResult(output(bString("abc")), EXECUTION);
        assertComputationResult(step, input, null, null, expected);
      }

      @Test
      public void executed_computation_is_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        var step = newInvokeStep(invoke, bTrace());
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
    public void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var step = new OrderStep(bOrder(bIntType()), bTrace());
      var input = bTuple(value);
      var memory = bArray(bInt(1));
      var disk = bArray(bInt(2));

      assertComputationResult(step, input, memory, disk, computationResult(output(memory), DISK));
    }

    @Test
    public void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var step = new OrderStep(bOrder(bIntType()), bTrace());
      var input = bTuple(value);
      var disk = bArray(bInt(2));

      var expected = computationResult(output(disk), DISK);
      assertComputationResult(step, input, null, disk, expected);
    }

    @Test
    public void when_not_cached() throws Exception {
      var value = bInt(17);
      var step = new OrderStep(bOrder(bIntType()), bTrace());
      var input = bTuple(value);

      var expected = computationResult(output(bArray(value)), EXECUTION);
      assertComputationResult(step, input, null, null, expected);
    }

    @Test
    public void executed_computation_is_cached_on_disk() throws Exception {
      var value = bInt(17);
      var step = new OrderStep(bOrder(bIntType()), bTrace());
      var input = bTuple(value);

      assertCachesState(step, input, null, bArray(value));
    }
  }

  @Nested
  class _pick_step {
    @Test
    public void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var step = new PickStep(bPick(), bTrace());
      var input = bTuple(bArray(value), bInt(0));
      var memory = bInt(1);
      var disk = bInt(2);

      assertComputationResult(step, input, memory, disk, computationResult(output(memory), DISK));
    }

    @Test
    public void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var step = new PickStep(bPick(), bTrace());
      var input = bTuple(bArray(value), bInt(0));
      var disk = bInt(2);

      var expected = computationResult(output(disk), DISK);
      assertComputationResult(step, input, null, disk, expected);
    }

    @Test
    public void when_not_cached() throws Exception {
      var value = bInt(17);
      var step = new PickStep(bPick(), bTrace());
      var input = bTuple(bArray(value), bInt(0));

      var expected = computationResult(output(value), EXECUTION);
      assertComputationResult(step, input, null, null, expected);
    }

    @Test
    public void executed_computation_is_cached_on_disk() throws Exception {
      var value = bInt(17);
      var task = new PickStep(bPick(), bTrace());
      var input = bTuple(bArray(value), bInt(0));

      assertCachesState(task, input, null, value);
    }
  }

  @Nested
  class _select_step {
    @Test
    public void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var task = new SelectStep(bSelect(), bTrace());
      var input = bTuple(bTuple(value), bInt(0));
      var memory = bInt(1);
      var disk = bInt(2);

      assertComputationResult(task, input, memory, disk, computationResult(output(memory), DISK));
    }

    @Test
    public void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var task = new SelectStep(bSelect(), bTrace());
      var input = bTuple(bTuple(value), bInt(0));
      var disk = bInt(2);

      var expected = computationResult(output(disk), DISK);
      assertComputationResult(task, input, null, disk, expected);
    }

    @Test
    public void when_not_cached() throws Exception {
      var value = bInt(17);
      var task = new SelectStep(bSelect(), bTrace());
      var input = bTuple(bTuple(value), bInt(0));

      var expected = computationResult(output(value), EXECUTION);
      assertComputationResult(task, input, null, null, expected);
    }

    @Test
    public void executed_computation_is_cached_on_disk() throws Exception {
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
    var sandboxHash = Hash.of(123);
    var computationHash = Computer.computationHash(sandboxHash, step, input);
    if (diskValue != null) {
      computationCache.write(computationHash, output(diskValue));
    }
    var memoryCache = new ConcurrentHashMap<Hash, PromisedValue<ComputationResult>>();
    if (memoryValue != null) {
      var computationResult = computationResult(memoryValue, EXECUTION);
      memoryCache.put(computationHash, new PromisedValue<>(computationResult));
    }
    return new Computer(sandboxHash, this::container, computationCache, memoryCache);
  }

  private void assertComputationResult(
      Computer computer, Step step, BTuple input, ComputationResult expected) throws Exception {
    var result = computer.compute(step, input);
    assertThat(result).isEqualTo(expected);
  }

  private void assertCachesState(
      Step step, BTuple input, ComputationResult memoryValue, BValue diskValue) throws Exception {
    var sandboxHash = Hash.of(123);
    var computationCache = computationCache();
    var memoryCache = new ConcurrentHashMap<Hash, PromisedValue<ComputationResult>>();
    var computer = new Computer(sandboxHash, this::container, computationCache, memoryCache);
    computer.compute(step, input);

    var taskHash = Computer.computationHash(sandboxHash, step, input);

    if (memoryValue == null) {
      assertThat(memoryCache.containsKey(taskHash)).isFalse();
    } else {
      assertThat(memoryCache.get(taskHash).get()).isEqualTo(memoryValue);
    }
    if (diskValue == null) {
      assertThat(computationCache.contains(taskHash)).isFalse();
    } else {
      assertThat(computationCache.read(taskHash, diskValue.type())).isEqualTo(output(diskValue));
    }
  }
}
