package org.smoothbuild.vm.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.vm.compute.Computer.computationHash;
import static org.smoothbuild.vm.compute.ResultSource.DISK;
import static org.smoothbuild.vm.compute.ResultSource.EXECUTION;
import static org.smoothbuild.vm.compute.ResultSource.MEMORY;
import static org.smoothbuild.vm.compute.ResultSource.NOOP;
import static org.smoothbuild.vm.execute.TaskKind.ORDER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.compute.ComputerTest._caching.MemoizingConsumer;
import org.smoothbuild.vm.task.CombineTask;
import org.smoothbuild.vm.task.ConstTask;
import org.smoothbuild.vm.task.IdentityTask;
import org.smoothbuild.vm.task.NativeCallTask;
import org.smoothbuild.vm.task.OrderTask;
import org.smoothbuild.vm.task.PickTask;
import org.smoothbuild.vm.task.SelectTask;
import org.smoothbuild.vm.task.Task;

public class ComputerTest extends TestContext {
  @Nested
  class _computation_result_for {
    @Test
    public void combine_task() throws ComputationCacheExc {
      var task = new CombineTask(tupleTB(intTB(), intTB()), tagLoc(), traceS());
      var input = tupleB(intB(17), intB(19));
      var expectedValue = tupleB(intB(17), intB(19));
      assertComputationResult(task, input, expectedValue, list(EXECUTION, DISK, DISK));
    }

    @Test
    public void const_task() throws ComputationCacheExc {
      var task = new ConstTask(intB(17), tagLoc(), traceS());
      var input = tupleB();
      var expectedValue = intB(17);
      assertComputationResult(task, input, expectedValue, list(NOOP, NOOP, NOOP));
    }

    @Test
    public void identity_task() throws ComputationCacheExc {
      var task = new IdentityTask(intTB(), ORDER, tagLoc(), traceS());
      var input = tupleB(intB(17));
      var expectedValue = intB(17);
      assertComputationResult(task, input, expectedValue, list(NOOP, NOOP, NOOP));
    }

    @Test
    public void native_call_task_for_pure_func() throws ComputationCacheExc, IOException {
      var task = new NativeCallTask(
          stringTB(), "", returnAbcNatFuncB(true), nativeMethodLoader(), tagLoc(), traceS());
      var input = tupleB();
      var expectedValue = stringB("abc");
      assertComputationResult(task, input, expectedValue, list(EXECUTION, DISK, DISK));
    }

    @Test
    public void native_call_task_for_impure_func() throws ComputationCacheExc, IOException {
      var task = new NativeCallTask(
          stringTB(), "", returnAbcNatFuncB(false), nativeMethodLoader(), tagLoc(), traceS());
      var input = tupleB();
      var expectedValue = stringB("abc");
      assertComputationResult(task, input, expectedValue, list(EXECUTION, MEMORY, MEMORY));
    }

    @Test
    public void order_task() throws ComputationCacheExc {
      var task = new OrderTask(arrayTB(intTB()), tagLoc(), traceS());
      var input = tupleB(intB(17), intB(19));
      var expectedValue = arrayB(intB(17), intB(19));
      assertComputationResult(task, input, expectedValue, list(EXECUTION, DISK, DISK));
    }

    @Test
    public void pick_task() throws ComputationCacheExc {
      var task = new PickTask(intTB(), tagLoc(), traceS());
      var input = tupleB(arrayB(intB(17), intB(19)), intB(0));
      var expectedValue = intB(17);
      assertComputationResult(task, input, expectedValue, list(EXECUTION, DISK, DISK));
    }

    @Test
    public void select_task() throws ComputationCacheExc {
      var task = new SelectTask(intTB(), tagLoc(), traceS());
      var input = tupleB(tupleB(intB(17), stringB()), intB(0));
      var expectedValue = intB(17);
      assertComputationResult(task, input, expectedValue, list(EXECUTION, DISK, DISK));
    }

    private void assertComputationResult(
        Task task, TupleB input, InstB expectedValue, List<ResultSource> expectedSources)
        throws ComputationCacheExc {
      var computer = computer();
      var consumer = new MemoizingConsumer<ComputationResult>();
      for (int i = 0; i < expectedSources.size(); i++) {
        computer.compute(task, input, consumer);
      }
      var expected = map(expectedSources, s -> computationResult(expectedValue, s));
      assertThat(consumer.values())
          .isEqualTo(expected);
    }
  }

  @Nested
  class _caching {
    @Test
    public void combine_task_computation_is_read_from_cache() throws ComputationCacheExc {
      var value = intB(17);
      var task = new CombineTask(tupleTB(intTB()), tagLoc(), traceS());
      var input = tupleB(value);
      var cacheValue = tupleB(intB(19));

      assertTaskIsReadFromCache(task, input, cacheValue);
    }

    @Test
    public void combine_task_computation_is_stored_in_cache() throws ComputationCacheExc {
      var value = intB(17);
      var task = new CombineTask(tupleTB(intTB()), tagLoc(), traceS());
      var input = tupleB(value);

      assertTaskIsStoredInCache(task, input, tupleB(value));
    }

    @Test
    public void const_task_computation_is_not_read_from_cache() throws ComputationCacheExc {
      var value = intB(17);
      var task = new ConstTask(value, tagLoc(), traceS());
      var input = tupleB();
      var cacheValue = intB(19);
      assertTaskComputationIsNotReadFromCache(
          task, input, cacheValue, computationResult(value, NOOP));
    }

    @Test
    public void const_task_computation_is_not_written_to_cache() throws ComputationCacheExc {
      var task = new ConstTask(intB(17), tagLoc(), traceS());
      var input = tupleB();
      assertComputationIsNotWrittenToCache(task, input);
    }

    @Test
    public void identity_task_computation_is_not_read_from_cache() throws ComputationCacheExc {
      var value = intB(17);
      var task = new IdentityTask(intTB(), ORDER, tagLoc(), traceS());
      var input = tupleB(value);
      var cacheValue = intB(19);
      assertTaskComputationIsNotReadFromCache(
          task, input, cacheValue, computationResult(value, NOOP));
    }

    @Test
    public void identity_task_computation_is_not_written_to_cache() throws ComputationCacheExc {
      var task = new IdentityTask(intTB(), ORDER, tagLoc(), traceS());
      var input = tupleB(intB(17));
      assertComputationIsNotWrittenToCache(task, input);
    }

    @Test
    public void native_call_to_pure_func_task_computation_is_read_from_cache()
        throws ComputationCacheExc, IOException {
      var task = new NativeCallTask(
          stringTB(), "", returnAbcNatFuncB(true), nativeMethodLoader(), tagLoc(), traceS());
      var input = tupleB();
      var cacheValue = stringB("def");

      assertTaskIsReadFromCache(task, input, cacheValue);
    }

    @Test
    public void native_call_to_pure_func_task_computation_is_stored_in_cache()
        throws ComputationCacheExc, IOException {
      var value = stringB("abc");
      var task = new NativeCallTask(
          stringTB(), "", returnAbcNatFuncB(true), nativeMethodLoader(), tagLoc(), traceS());
      var input = tupleB();

      assertTaskIsStoredInCache(task, input, value);
    }

    @Test
    public void native_call_to_impure_func_task_computation_is_not_read_from_cache()
        throws ComputationCacheExc, IOException {
      var task = new NativeCallTask(
          stringTB(), "", returnAbcNatFuncB(false), nativeMethodLoader(), tagLoc(), traceS());
      var input = tupleB();
      var cacheValue = stringB("def");

      assertTaskComputationIsNotReadFromCache(task, input, cacheValue,
          computationResult(stringB("abc"), EXECUTION));
    }

    @Test
    public void native_call_to_impure_func_task_computation_is_not_stored_in_cache()
        throws ComputationCacheExc, IOException {
      var task = new NativeCallTask(
          stringTB(), "", returnAbcNatFuncB(false), nativeMethodLoader(), tagLoc(), traceS());
      var input = tupleB();

      assertComputationIsNotWrittenToCache(task, input);
    }

    @Test
    public void order_task_computation_is_read_from_cache() throws ComputationCacheExc {
      var value = intB(17);
      var task = new OrderTask(arrayTB(intTB()), tagLoc(), traceS());
      var input = tupleB(value);
      var cacheValue = arrayB(intB(19));

      assertTaskIsReadFromCache(task, input, cacheValue);
    }

    @Test
    public void order_task_computation_is_stored_in_cache() throws ComputationCacheExc {
      var value = intB(17);
      var task = new OrderTask(arrayTB(intTB()), tagLoc(), traceS());
      var input = tupleB(value);

      assertTaskIsStoredInCache(task, input, arrayB(value));
    }

    @Test
    public void pick_task_computation_is_read_from_cache() throws ComputationCacheExc {
      var value = intB(17);
      var task = new PickTask(intTB(), tagLoc(), traceS());
      var input = tupleB(arrayB(value), intB(0));
      var cacheValue = intB(19);

      assertTaskIsReadFromCache(task, input, cacheValue);
    }

    @Test
    public void pick_task_computation_is_stored_in_cache() throws ComputationCacheExc {
      var value = intB(17);
      var task = new PickTask(intTB(), tagLoc(), traceS());
      var input = tupleB(arrayB(value), intB(0));

      assertTaskIsStoredInCache(task, input, value);
    }

    @Test
    public void select_task_computation_is_read_from_cache() throws ComputationCacheExc {
      var value = intB(17);
      var task = new SelectTask(intTB(), tagLoc(), traceS());
      var input = tupleB(tupleB(value));
      var cacheValue = intB(19);

      assertTaskIsReadFromCache(task, input, cacheValue);
    }

    @Test
    public void select_task_computation_is_stored_in_cache() throws ComputationCacheExc {
      var value = intB(17);
      var task = new SelectTask(intTB(), tagLoc(), traceS());
      var input = tupleB(tupleB(value), intB(0));

      assertTaskIsStoredInCache(task, input, value);
    }

    private void assertTaskIsReadFromCache(Task task, TupleB input, InstB cacheValue)
        throws ComputationCacheExc {
      var computer = computerWithCachedComputation(task, input, cacheValue);
      var expected = computationResult(output(cacheValue), DISK);
      assertComputationResult(computer, task, input, expected);
    }

    private void assertTaskIsStoredInCache(Task task, TupleB input, InstB expectedValue)
        throws ComputationCacheExc {
      var computationCache = computationCache();
      var sandboxHash = Hash.of(123);
      var computer = new Computer(computationCache, sandboxHash, () -> container());
      computer.compute(task, input, new MemoizingConsumer<>());

      var taskHash = computationHash(sandboxHash, task, input);
      assertThat(computationCache.read(taskHash, expectedValue.type()))
          .isEqualTo(output(expectedValue));
    }

    private void assertTaskComputationIsNotReadFromCache(Task task, TupleB input,
        InstB cacheValue, ComputationResult expected) throws ComputationCacheExc {
      var computer = computerWithCachedComputation(task, input, cacheValue);
      assertComputationResult(computer, task, input, expected);
    }

    private Computer computerWithCachedComputation(Task task, TupleB input, InstB cacheValue)
        throws ComputationCacheExc {
      var computationCache = computationCache();
      var cachedOutput = output(cacheValue);
      var sandboxHash = Hash.of(123);
      computationCache.write(computationHash(sandboxHash, task, input), cachedOutput);
      return new Computer(computationCache, sandboxHash, () -> container());
    }

    private void assertComputationIsNotWrittenToCache(Task task, TupleB input)
        throws ComputationCacheExc {
      var computationCache = mock(ComputationCache.class);
      var sandboxHash = Hash.of(123);
      var computer = new Computer(computationCache, sandboxHash, () -> container());

      computer.compute(task, input, x -> {});

      verifyNoInteractions(computationCache);
    }

    private void assertComputationResult(Computer computer, Task task, TupleB input,
        ComputationResult expected) throws ComputationCacheExc {
      var consumer = new MemoizingConsumer<ComputationResult>();

      computer.compute(task, input, consumer);

      assertThat(consumer.values())
          .isEqualTo(list(expected));
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
}
