package org.smoothbuild.virtualmachine.evaluate.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.ResultSource.DISK;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.base.ResultSource.MEMORY;
import static org.smoothbuild.common.log.base.ResultSource.NOOP;
import static org.smoothbuild.virtualmachine.evaluate.task.InvokeTask.newInvokeTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.concurrent.PromisedValue;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.task.CombineTask;
import org.smoothbuild.virtualmachine.evaluate.task.ConstTask;
import org.smoothbuild.virtualmachine.evaluate.task.OrderTask;
import org.smoothbuild.virtualmachine.evaluate.task.PickTask;
import org.smoothbuild.virtualmachine.evaluate.task.SelectTask;
import org.smoothbuild.virtualmachine.evaluate.task.Task;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class ComputerTest extends TestingVirtualMachine {
  @Nested
  class _combine_task {
    @Test
    public void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var task = new CombineTask(bCombine(bInt()), bTrace());
      var input = bTuple(value);
      var memory = bTuple(bInt(1));
      var disk = bTuple(bInt(2));

      assertComputationResult(task, input, memory, disk, computationResult(output(memory), DISK));
    }

    @Test
    public void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var task = new CombineTask(bCombine(bInt()), bTrace());
      var input = bTuple(value);
      var disk = bTuple(bInt(2));

      var expected = computationResult(output(disk), DISK);
      assertComputationResult(task, input, null, disk, expected);
    }

    @Test
    public void when_not_cached() throws Exception {
      var value = bInt(17);
      var task = new CombineTask(bCombine(bInt()), bTrace());
      var input = bTuple(value);

      var expected = computationResult(output(bTuple(value)), EXECUTION);
      assertComputationResult(task, input, null, null, expected);
    }

    @Test
    public void executed_computation_is_cached_on_disk() throws Exception {
      var value = bInt(17);
      var task = new CombineTask(bCombine(bInt()), bTrace());
      var input = bTuple(value);

      assertCachesState(task, input, null, bTuple(value));
    }
  }

  @Nested
  class _const_task {
    @Test
    public void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var task = new ConstTask(value, bTrace());
      var input = bTuple();
      var memory = bInt(1);
      var disk = bInt(2);

      assertComputationResult(task, input, memory, disk, computationResult(output(value), NOOP));
    }

    @Test
    public void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var task = new ConstTask(value, bTrace());
      var input = bTuple();
      var disk = bInt(2);

      assertComputationResult(task, input, null, disk, computationResult(output(value), NOOP));
    }

    @Test
    public void when_not_cached() throws Exception {
      var value = bInt(17);
      var task = new ConstTask(value, bTrace());
      var input = bTuple();

      assertComputationResult(task, input, null, null, computationResult(output(value), NOOP));
    }

    @Test
    public void executed_computation_is_not_cached() throws Exception {
      var value = bInt(17);
      var task = new ConstTask(value, bTrace());
      var input = bTuple();

      assertCachesState(task, input, null, null);
    }
  }

  @Nested
  class _invoke_task {
    @Nested
    class _with_pure_invoke {
      @Test
      public void when_cached_in_memory_and_disk() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        var task = newInvokeTask(invoke, bTrace());
        var input = argumentsForInvokeTask(invoke);
        var memory = bString("def");
        var disk = bString("ghi");

        assertComputationResult(task, input, memory, disk, computationResult(output(memory), DISK));
      }

      @Test
      public void when_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        var task = newInvokeTask(invoke, bTrace());
        var input = argumentsForInvokeTask(invoke);
        var disk = bString("ghi");

        assertComputationResult(task, input, null, disk, computationResult(output(disk), DISK));
      }

      @Test
      public void when_not_cached() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        var task = newInvokeTask(invoke, bTrace());
        var input = argumentsForInvokeTask(invoke);

        var expected = computationResult(output(bString("abc")), EXECUTION);
        assertComputationResult(task, input, null, null, expected);
      }

      @Test
      public void executed_computation_is_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(true);
        var task = newInvokeTask(invoke, bTrace());
        var input = argumentsForInvokeTask(invoke);

        assertCachesState(task, input, null, bString("abc"));
      }
    }

    @Nested
    class _with_impure_invoke {
      @Test
      public void when_cached_in_memory_and_disk() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        var task = newInvokeTask(invoke, bTrace());
        var input = argumentsForInvokeTask(invoke);
        var memory = bString("def");
        var disk = bString("ghi");

        assertComputationResult(
            task, input, memory, disk, computationResult(output(memory), MEMORY));
      }

      @Test
      public void when_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        var task = newInvokeTask(invoke, bTrace());
        var input = argumentsForInvokeTask(invoke);
        var disk = bString("ghi");

        var computationResult = computationResult(output(bString("abc")), EXECUTION);
        assertComputationResult(task, input, null, disk, computationResult);
      }

      @Test
      public void when_not_cached() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        var task = newInvokeTask(invoke, bTrace());
        var input = argumentsForInvokeTask(invoke);

        var expected = computationResult(output(bString("abc")), EXECUTION);
        assertComputationResult(task, input, null, null, expected);
      }

      @Test
      public void executed_computation_is_cached_on_disk() throws Exception {
        var invoke = bReturnAbcInvoke(false);
        var task = newInvokeTask(invoke, bTrace());
        var input = argumentsForInvokeTask(invoke);

        assertCachesState(task, input, computationResult(bString("abc"), EXECUTION), null);
      }
    }

    private BTuple argumentsForInvokeTask(BInvoke invoke) throws BytecodeException {
      var subExprs = invoke.subExprs();
      return bTuple(
          (BValue) subExprs.method(), (BValue) subExprs.isPure(), (BValue) subExprs.arguments());
    }
  }

  @Nested
  class _order_task {
    @Test
    public void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var task = new OrderTask(bOrder(bIntType()), bTrace());
      var input = bTuple(value);
      var memory = bArray(bInt(1));
      var disk = bArray(bInt(2));

      assertComputationResult(task, input, memory, disk, computationResult(output(memory), DISK));
    }

    @Test
    public void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var task = new OrderTask(bOrder(bIntType()), bTrace());
      var input = bTuple(value);
      var disk = bArray(bInt(2));

      var expected = computationResult(output(disk), DISK);
      assertComputationResult(task, input, null, disk, expected);
    }

    @Test
    public void when_not_cached() throws Exception {
      var value = bInt(17);
      var task = new OrderTask(bOrder(bIntType()), bTrace());
      var input = bTuple(value);

      var expected = computationResult(output(bArray(value)), EXECUTION);
      assertComputationResult(task, input, null, null, expected);
    }

    @Test
    public void executed_computation_is_cached_on_disk() throws Exception {
      var value = bInt(17);
      var task = new OrderTask(bOrder(bIntType()), bTrace());
      var input = bTuple(value);

      assertCachesState(task, input, null, bArray(value));
    }
  }

  @Nested
  class _pick_task {
    @Test
    public void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var task = new PickTask(bPick(), bTrace());
      var input = bTuple(bArray(value), bInt(0));
      var memory = bInt(1);
      var disk = bInt(2);

      assertComputationResult(task, input, memory, disk, computationResult(output(memory), DISK));
    }

    @Test
    public void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var task = new PickTask(bPick(), bTrace());
      var input = bTuple(bArray(value), bInt(0));
      var disk = bInt(2);

      var expected = computationResult(output(disk), DISK);
      assertComputationResult(task, input, null, disk, expected);
    }

    @Test
    public void when_not_cached() throws Exception {
      var value = bInt(17);
      var task = new PickTask(bPick(), bTrace());
      var input = bTuple(bArray(value), bInt(0));

      var expected = computationResult(output(value), EXECUTION);
      assertComputationResult(task, input, null, null, expected);
    }

    @Test
    public void executed_computation_is_cached_on_disk() throws Exception {
      var value = bInt(17);
      var task = new PickTask(bPick(), bTrace());
      var input = bTuple(bArray(value), bInt(0));

      assertCachesState(task, input, null, value);
    }
  }

  @Nested
  class _select_task {
    @Test
    public void when_cached_in_memory_and_disk() throws Exception {
      var value = bInt(17);
      var task = new SelectTask(bSelect(), bTrace());
      var input = bTuple(bTuple(value), bInt(0));
      var memory = bInt(1);
      var disk = bInt(2);

      assertComputationResult(task, input, memory, disk, computationResult(output(memory), DISK));
    }

    @Test
    public void when_cached_on_disk() throws Exception {
      var value = bInt(17);
      var task = new SelectTask(bSelect(), bTrace());
      var input = bTuple(bTuple(value), bInt(0));
      var disk = bInt(2);

      var expected = computationResult(output(disk), DISK);
      assertComputationResult(task, input, null, disk, expected);
    }

    @Test
    public void when_not_cached() throws Exception {
      var value = bInt(17);
      var task = new SelectTask(bSelect(), bTrace());
      var input = bTuple(bTuple(value), bInt(0));

      var expected = computationResult(output(value), EXECUTION);
      assertComputationResult(task, input, null, null, expected);
    }

    @Test
    public void executed_computation_is_cached_on_disk() throws Exception {
      var value = bInt(17);
      var task = new SelectTask(bSelect(), bTrace());
      var input = bTuple(bTuple(value), bInt(0));

      assertCachesState(task, input, null, value);
    }
  }

  private void assertComputationResult(
      Task task, BTuple input, BValue memoryValue, BValue diskValue, ComputationResult expected)
      throws Exception {
    var computer = computerWithCaches(task, input, memoryValue, diskValue);
    assertComputationResult(computer, task, input, expected);
  }

  private Computer computerWithCaches(Task task, BTuple input, BValue memoryValue, BValue diskValue)
      throws Exception {
    var computationCache = computationCache();
    var sandboxHash = Hash.of(123);
    var computationHash = Computer.computationHash(sandboxHash, task, input);
    if (diskValue != null) {
      computationCache.write(computationHash, output(diskValue));
    }
    var memoryCache = new ConcurrentHashMap<Hash, PromisedValue<ComputationResult>>();
    if (memoryValue != null) {
      var computationResult = computationResult(memoryValue, EXECUTION);
      memoryCache.put(computationHash, new PromisedValue<>(computationResult));
    }
    return new Computer(sandboxHash, () -> container(), computationCache, memoryCache);
  }

  private void assertComputationResult(
      Computer computer, Task task, BTuple input, ComputationResult expected) throws Exception {
    var consumer = new MemoizingConsumer<ComputationResult>();

    computer.compute(task, input, consumer);

    assertThat(consumer.values()).isEqualTo(list(expected));
  }

  private void assertCachesState(
      Task task, BTuple input, ComputationResult memoryValue, BValue diskValue) throws Exception {
    var sandboxHash = Hash.of(123);
    var computationCache = computationCache();
    var memoryCache = new ConcurrentHashMap<Hash, PromisedValue<ComputationResult>>();
    var computer = new Computer(sandboxHash, () -> container(), computationCache, memoryCache);
    computer.compute(task, input, new MemoizingConsumer<>());

    var taskHash = Computer.computationHash(sandboxHash, task, input);

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

  public static record MemoizingConsumer<T>(List<T> values) implements Consumer<T> {
    public MemoizingConsumer() {
      this(new ArrayList<>());
    }

    @Override
    public void accept(T value) {
      values.add(value);
    }
  }
}
