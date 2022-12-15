package org.smoothbuild.vm.evaluate.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.evaluate.task.CombineTask;
import org.smoothbuild.vm.evaluate.task.ConstTask;
import org.smoothbuild.vm.evaluate.task.InvokeTask;
import org.smoothbuild.vm.evaluate.task.OrderTask;
import org.smoothbuild.vm.evaluate.task.PickTask;
import org.smoothbuild.vm.evaluate.task.SelectTask;
import org.smoothbuild.vm.evaluate.task.Task;

import com.google.common.truth.Truth;

public class ComputerTest extends TestContext {
  @Nested
  class _combine_task {
    @Test
    public void when_cached_in_memory_and_disk() throws ComputationCacheExc {
      var value = intB(17);
      var task = new CombineTask(combineB(intB()), traceB());
      var input = tupleB(value);
      var memory = tupleB(intB(1));
      var disk = tupleB(intB(2));

      assertComputationResult(task, input, memory, disk, TestContext.computationResult(output(memory), ResultSource.DISK));
    }

    @Test
    public void when_cached_on_disk() throws ComputationCacheExc {
      var value = intB(17);
      var task = new CombineTask(combineB(intB()), traceB());
      var input = tupleB(value);
      var disk = tupleB(intB(2));

      var expected = TestContext.computationResult(output(disk), ResultSource.DISK);
      assertComputationResult(task, input, null, disk, expected);
    }

    @Test
    public void when_not_cached() throws ComputationCacheExc {
      var value = intB(17);
      var task = new CombineTask(combineB(intB()), traceB());
      var input = tupleB(value);

      var expected = TestContext.computationResult(output(tupleB(value)), ResultSource.EXECUTION);
      assertComputationResult(task, input, null, null, expected);
    }

    @Test
    public void executed_computation_is_cached_on_disk() throws ComputationCacheExc {
      var value = intB(17);
      var task = new CombineTask(combineB(intB()), traceB());
      var input = tupleB(value);

      assertCachesState(task, input, null, tupleB(value));
    }
  }

  @Nested
  class _const_task {
    @Test
    public void when_cached_in_memory_and_disk() throws ComputationCacheExc {
      var value = intB(17);
      var task = new ConstTask(value, traceB());
      var input = tupleB();
      var memory = intB(1);
      var disk = intB(2);

      assertComputationResult(task, input, memory, disk, TestContext.computationResult(output(value), ResultSource.NOOP));
    }

    @Test
    public void when_cached_on_disk() throws ComputationCacheExc {
      var value = intB(17);
      var task = new ConstTask(value, traceB());
      var input = tupleB();
      var disk = intB(2);

      assertComputationResult(task, input, null, disk, TestContext.computationResult(output(value), ResultSource.NOOP));
    }

    @Test
    public void when_not_cached() throws ComputationCacheExc {
      var value = intB(17);
      var task = new ConstTask(value, traceB());
      var input = tupleB();

      assertComputationResult(task, input, null, null, TestContext.computationResult(output(value), ResultSource.NOOP));
    }

    @Test
    public void executed_computation_is_not_cached() throws ComputationCacheExc {
      var value = intB(17);
      var task = new ConstTask(value, traceB());
      var input = tupleB();

      assertCachesState(task, input, null, null);
    }
  }

  @Nested
  class _invoke_task_with_pure_func {
    @Test
    public void when_cached_in_memory_and_disk() throws ComputationCacheExc, IOException {
      var nativeFuncB = returnAbcNativeFuncB(true);
      var callB = callB(nativeFuncB);
      var task = new InvokeTask(callB, nativeFuncB, nativeMethodLoader(), traceB());
      var input = tupleB();
      var memory = stringB("def");
      var disk = stringB("ghi");

      assertComputationResult(task, input, memory, disk, TestContext.computationResult(output(memory), ResultSource.DISK));
    }

    @Test
    public void when_cached_on_disk() throws ComputationCacheExc, IOException {
      var nativeFuncB = returnAbcNativeFuncB(true);
      var callB = callB(nativeFuncB);
      var task = new InvokeTask(callB, nativeFuncB, nativeMethodLoader(), traceB());
      var input = tupleB();
      var disk = stringB("ghi");

      assertComputationResult(task, input, null, disk, TestContext.computationResult(output(disk)
          , ResultSource.DISK));
    }

    @Test
    public void when_not_cached() throws ComputationCacheExc, IOException {
      var nativeFuncB = returnAbcNativeFuncB(true);
      var callB = callB(nativeFuncB);
      var task = new InvokeTask(callB, nativeFuncB, nativeMethodLoader(), traceB());
      var input = tupleB();

      var expected = TestContext.computationResult(output(stringB("abc")), ResultSource.EXECUTION);
      assertComputationResult(task, input, null, null, expected);
    }

    @Test
    public void executed_computation_is_cached_on_disk() throws ComputationCacheExc, IOException {
      var nativeFuncB = returnAbcNativeFuncB(true);
      var callB = callB(nativeFuncB);
      var task = new InvokeTask(callB, nativeFuncB, nativeMethodLoader(), traceB());
      var input = tupleB();

      assertCachesState(task, input, null, stringB("abc"));
    }
  }

  @Nested
  class _invoke_task_with_impure_func {
    @Test
    public void when_cached_in_memory_and_disk() throws ComputationCacheExc, IOException {
      var nativeFuncB = returnAbcNativeFuncB(false);
      var callB = callB(nativeFuncB);
      var task = new InvokeTask(callB, nativeFuncB, nativeMethodLoader(), traceB());
      var input = tupleB();
      var memory = stringB("def");
      var disk = stringB("ghi");

      assertComputationResult(task, input, memory, disk, TestContext.computationResult(output(memory), ResultSource.MEMORY));
    }

    @Test
    public void when_cached_on_disk() throws ComputationCacheExc, IOException {
      var nativeFuncB = returnAbcNativeFuncB(false);
      var callB = callB(nativeFuncB);
      var task = new InvokeTask(callB, nativeFuncB, nativeMethodLoader(), traceB());
      var input = tupleB();
      var disk = stringB("ghi");

      var computationResult = TestContext.computationResult(output(stringB("abc")), ResultSource.EXECUTION);
      assertComputationResult(task, input, null, disk, computationResult);
    }

    @Test
    public void when_not_cached() throws ComputationCacheExc, IOException {
      var nativeFuncB = returnAbcNativeFuncB(false);
      var callB = callB(nativeFuncB);
      var task = new InvokeTask(callB, nativeFuncB, nativeMethodLoader(), traceB());
      var input = tupleB();

      var expected = TestContext.computationResult(output(stringB("abc")), ResultSource.EXECUTION);
      assertComputationResult(task, input, null, null, expected);
    }

    @Test
    public void executed_computation_is_cached_on_disk() throws ComputationCacheExc, IOException {
      var nativeFuncB = returnAbcNativeFuncB(false);
      var callB = callB(nativeFuncB);
      var task = new InvokeTask(callB, nativeFuncB, nativeMethodLoader(), traceB());
      var input = tupleB();

      assertCachesState(task, input, computationResult(stringB("abc"), ResultSource.EXECUTION), null);
    }
  }

  @Nested
  class _order_task {
    @Test
    public void when_cached_in_memory_and_disk() throws ComputationCacheExc {
      var value = intB(17);
      var task = new OrderTask(orderB(intTB()), traceB());
      var input = tupleB(value);
      var memory = arrayB(intB(1));
      var disk = arrayB(intB(2));

      assertComputationResult(task, input, memory, disk, TestContext.computationResult(output(memory), ResultSource.DISK));
    }

    @Test
    public void when_cached_on_disk() throws ComputationCacheExc {
      var value = intB(17);
      var task = new OrderTask(orderB(intTB()), traceB());
      var input = tupleB(value);
      var disk = arrayB(intB(2));

      var expected = TestContext.computationResult(output(disk), ResultSource.DISK);
      assertComputationResult(task, input, null, disk, expected);
    }

    @Test
    public void when_not_cached() throws ComputationCacheExc {
      var value = intB(17);
      var task = new OrderTask(orderB(intTB()), traceB());
      var input = tupleB(value);

      var expected = TestContext.computationResult(output(arrayB(value)), ResultSource.EXECUTION);
      assertComputationResult(task, input, null, null, expected);
    }

    @Test
    public void executed_computation_is_cached_on_disk() throws ComputationCacheExc {
      var value = intB(17);
      var task = new OrderTask(orderB(intTB()), traceB());
      var input = tupleB(value);

      assertCachesState(task, input, null, arrayB(value));
    }
  }

  @Nested
  class _pick_task {
    @Test
    public void when_cached_in_memory_and_disk() throws ComputationCacheExc {
      var value = intB(17);
      var task = new PickTask(pickB(), traceB());
      var input = tupleB(arrayB(value), intB(0));
      var memory = intB(1);
      var disk = intB(2);

      assertComputationResult(task, input, memory, disk, TestContext.computationResult(output(memory), ResultSource.DISK));
    }

    @Test
    public void when_cached_on_disk() throws ComputationCacheExc {
      var value = intB(17);
      var task = new PickTask(pickB(), traceB());
      var input = tupleB(arrayB(value), intB(0));
      var disk = intB(2);

      var expected = TestContext.computationResult(output(disk), ResultSource.DISK);
      assertComputationResult(task, input, null, disk, expected);
    }

    @Test
    public void when_not_cached() throws ComputationCacheExc {
      var value = intB(17);
      var task = new PickTask(pickB(), traceB());
      var input = tupleB(arrayB(value), intB(0));

      var expected = TestContext.computationResult(output(value), ResultSource.EXECUTION);
      assertComputationResult(task, input, null, null, expected);
    }

    @Test
    public void executed_computation_is_cached_on_disk() throws ComputationCacheExc {
      var value = intB(17);
      var task = new PickTask(pickB(), traceB());
      var input = tupleB(arrayB(value), intB(0));

      assertCachesState(task, input, null, value);
    }
  }

  @Nested
  class _select_task {
    @Test
    public void when_cached_in_memory_and_disk() throws ComputationCacheExc {
      var value = intB(17);
      var task = new SelectTask(selectB(), traceB());
      var input = tupleB(tupleB(value), intB(0));
      var memory = intB(1);
      var disk = intB(2);

      assertComputationResult(task, input, memory, disk, TestContext.computationResult(output(memory), ResultSource.DISK));
    }

    @Test
    public void when_cached_on_disk() throws ComputationCacheExc {
      var value = intB(17);
      var task = new SelectTask(selectB(), traceB());
      var input = tupleB(tupleB(value), intB(0));
      var disk = intB(2);

      var expected = TestContext.computationResult(output(disk), ResultSource.DISK);
      assertComputationResult(task, input, null, disk, expected);
    }

    @Test
    public void when_not_cached() throws ComputationCacheExc {
      var value = intB(17);
      var task = new SelectTask(selectB(), traceB());
      var input = tupleB(tupleB(value), intB(0));

      var expected = TestContext.computationResult(output(value), ResultSource.EXECUTION);
      assertComputationResult(task, input, null, null, expected);
    }

    @Test
    public void executed_computation_is_cached_on_disk() throws ComputationCacheExc {
      var value = intB(17);
      var task = new SelectTask(selectB(), traceB());
      var input = tupleB(tupleB(value), intB(0));

      assertCachesState(task, input, null, value);
    }
  }

  private void assertComputationResult(
      Task task, TupleB input, ValueB memoryValue, ValueB diskValue, ComputationResult expected)
      throws ComputationCacheExc {
    var computer = computerWithCaches(task, input, memoryValue, diskValue);
    assertComputationResult(computer, task, input, expected);
  }

  private Computer computerWithCaches(Task task, TupleB input, ValueB memoryValue, ValueB diskValue)
      throws ComputationCacheExc {
    var computationCache = computationCache();
    var sandboxHash = Hash.of(123);
    var computationHash = Computer.computationHash(sandboxHash, task, input);
    if (diskValue != null) {
      computationCache.write(computationHash, output(diskValue));
    }
    var memoryCache = new ConcurrentHashMap<Hash, PromisedValue<ComputationResult>>();
    if (memoryValue != null) {
      var computationResult = computationResult(memoryValue, ResultSource.EXECUTION);
      memoryCache.put(computationHash, new PromisedValue<>(computationResult));
    }
    return new Computer(sandboxHash, () -> container(), computationCache, memoryCache);
  }

  private void assertComputationResult(Computer computer, Task task, TupleB input,
      ComputationResult expected) throws ComputationCacheExc {
    var consumer = new MemoizingConsumer<ComputationResult>();

    computer.compute(task, input, consumer);

    assertThat(consumer.values())
        .isEqualTo(list(expected));
  }

  private void assertCachesState(
      Task task, TupleB input, ComputationResult memoryValue, ValueB diskValue)
      throws ComputationCacheExc {
    var sandboxHash = Hash.of(123);
    var computationCache = computationCache();
    var memoryCache = new ConcurrentHashMap<Hash, PromisedValue<ComputationResult>>();
    var computer = new Computer(sandboxHash, () -> container(), computationCache, memoryCache);
    computer.compute(task, input, new MemoizingConsumer<>());

    var taskHash = Computer.computationHash(sandboxHash, task, input);

    if (memoryValue == null) {
      assertThat(memoryCache.containsKey(taskHash))
          .isFalse();
    } else {
      assertThat(memoryCache.get(taskHash).get())
          .isEqualTo(memoryValue);
    }
    if (diskValue == null) {
      Truth.assertThat(computationCache.contains(taskHash))
          .isFalse();
    } else {
      Truth.assertThat(computationCache.read(taskHash, diskValue.type()))
          .isEqualTo(output(diskValue));
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
