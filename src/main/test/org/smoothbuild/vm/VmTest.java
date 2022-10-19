package org.smoothbuild.vm;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Collections.nCopies;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smoothbuild.out.log.ImmutableLogs.logs;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.vm.compute.ResSource.DISK;
import static org.smoothbuild.vm.compute.ResSource.EXECUTION;
import static org.smoothbuild.vm.compute.ResSource.MEMORY;
import static org.smoothbuild.vm.execute.TaskKind.CALL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.inst.BoolB;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.IntB;
import org.smoothbuild.bytecode.expr.inst.StringB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.define.TraceS;
import org.smoothbuild.out.log.Level;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.accept.MemoryReporter;
import org.smoothbuild.util.collect.Try;
import org.smoothbuild.vm.compute.CompRes;
import org.smoothbuild.vm.compute.Computer;
import org.smoothbuild.vm.compute.ResSource;
import org.smoothbuild.vm.execute.ExecutionReporter;
import org.smoothbuild.vm.execute.TaskReporter;
import org.smoothbuild.vm.job.ExecutionContext;
import org.smoothbuild.vm.job.Job;
import org.smoothbuild.vm.job.JobCreator;
import org.smoothbuild.vm.task.ExecutableTask;
import org.smoothbuild.vm.task.NativeMethodLoader;
import org.smoothbuild.vm.task.OrderTask;
import org.smoothbuild.vm.task.PickTask;
import org.smoothbuild.vm.task.Task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class VmTest extends TestContext {
  @Nested
  class _laziness {
    @Nested
    class _task_execution {
      @Test
      public void learning_test() {
        // This test makes sure that it is possible to detect Task creation using a mock.
        var order = orderB(intB(7));

        var spyingExecutor = spy(taskExecutor());
        assertThat(evaluate(vm(spyingExecutor), order))
            .isEqualTo(arrayB(intB(7)));

        verify(spyingExecutor, times(1)).enqueue(isA(OrderTask.class), any(), any());
      }

      @Test
      public void no_task_is_executed_for_func_arg_that_is_not_used() {
        var func = defFuncB(list(arrayTB(boolTB())), intB(7));
        var call = callB(func, orderB(boolTB()));

        var spyingExecutor = spy(taskExecutor());
        assertThat(evaluate(vm(spyingExecutor), call))
            .isEqualTo(intB(7));

        verify(spyingExecutor, never()).enqueue(isA(OrderTask.class), any(), any());
      }

      @Test
      public void no_task_is_executed_for_func_expr_in_call_to_map_when_array_expr_is_empty() {
        var mapFuncB = mapFuncB(intTB(), intTB());
        var mappingFunc = pickB(orderB(idFuncB()), intB(0));
        var emptyIntArray = arrayB(intTB());
        var callB = callB(mapFuncB, emptyIntArray, mappingFunc);

        var spyingExecutor = spy(taskExecutor());
        assertThat(evaluate(vm(spyingExecutor), callB))
            .isEqualTo(arrayB(intTB()));

        verify(spyingExecutor, never()).enqueue(isA(PickTask.class), any(), any());
      }

      @Test
      public void no_task_is_executed_for_func_arg_that_is_passed_to_func_where_it_is_not_used() {
        var innerFunc = defFuncB(list(arrayTB(boolTB())), intB(7));
        var outerFunc = defFuncB(list(arrayTB(boolTB())),
            callB(innerFunc, refB(arrayTB(boolTB()), 0)));
        var call = callB(outerFunc, orderB(boolTB()));

        var spyingExecutor = spy(taskExecutor());
        assertThat(evaluate(vm(spyingExecutor), call))
            .isEqualTo(intB(7));

        verify(spyingExecutor, never()).enqueue(isA(OrderTask.class), any(), any());
      }

      @Test
      public void task_for_func_arg_that_is_used_twice_is_executed_only_once() {
        var arrayT = arrayTB(intTB());
        var func = defFuncB(list(arrayT), combineB(refB(arrayT, 0), refB(arrayT, 0)));
        var call = callB(func, orderB(intB(7)));

        var spyingExecutor = spy(taskExecutor());
        assertThat(evaluate(vm(spyingExecutor), call))
            .isEqualTo(tupleB(arrayB(intB(7)), arrayB(intB(7))));

        verify(spyingExecutor, times(1)).enqueue(isA(OrderTask.class), any(), any());
      }
    }

    @Nested
    class _job_creation {
      @Test
      public void learning_test() {
        // Learning test verifies that job creation is counted also inside func body.
        var func = defFuncB(orderB(intB(7)));
        var call = callB(func);

        var countingJobCreator = new CountingJobCreator(IntB.class);
        var spyingJobCreator = spy(countingJobCreator);
        assertThat(evaluate(vm(spyingJobCreator), call))
            .isEqualTo(arrayB(intB(7)));

        assertThat(countingJobCreator.counter().get())
            .isEqualTo(1);
      }

      @Test
      public void job_for_unused_func_arg_is_created_but_not_jobs_for_its_dependencies() {
        var func = defFuncB(list(arrayTB(boolTB())), intB(7));
        var call = callB(func, orderB(boolTB()));

        var countingJobCreator = new CountingJobCreator(BoolB.class);
        var spyingJobCreator = spy(countingJobCreator);
        assertThat(evaluate(vm(spyingJobCreator), call))
            .isEqualTo(intB(7));

        assertThat(countingJobCreator.counter().get())
            .isEqualTo(0);
      }
    }
  }

  @Nested
  class _evaluation {
    @Nested
    class _values {
      @Test
      public void array() {
        assertThat(evaluate(arrayB(intB(7))))
            .isEqualTo(arrayB(intB(7)));
      }

      @Test
      public void blob() {
        assertThat(evaluate(blobB(7)))
            .isEqualTo(blobB(7));
      }

      @Test
      public void bool() {
        assertThat(evaluate(intB(8)))
            .isEqualTo(intB(8));
      }

      @Test
      public void string() {
        assertThat(evaluate(stringB("abc")))
            .isEqualTo(stringB("abc"));
      }

      @Test
      public void tuple() {
        assertThat(evaluate(tupleB(intB(7))))
            .isEqualTo(tupleB(intB(7)));
      }
    }

    @Nested
    class _operators {
      @Nested
      class _call {
        @Test
        public void def_func() {
          var func = defFuncB(intB(7));
          var call = callB(func);
          assertThat(evaluate(call))
              .isEqualTo(intB(7));
        }

        @Test
        public void def_func_passed_as_arg() {
          var func = defFuncB(intB(7));
          var paramT = func.evalT();
          var outerFunc = defFuncB(list(paramT), callB(refB(paramT, 0)));
          var call = callB(outerFunc, func);
          assertThat(evaluate(call))
              .isEqualTo(intB(7));
        }

        @Test
        public void def_func_returned_from_call() {
          var func = defFuncB(intB(7));
          var outerFunc = defFuncB(func);
          var call = callB(callB(outerFunc));
          assertThat(evaluate(call))
              .isEqualTo(intB(7));
        }

        @Test
        public void if_func_with_true_condition() {
          var ifFunc = ifFuncB(intTB());
          var call = callB(ifFunc, boolB(true), intB(7), intB(0));
          assertThat(evaluate(call))
              .isEqualTo(intB(7));
        }

        @Test
        public void if_func_with_false_condition() {
          var ifFunc = ifFuncB(intTB());
          var call = callB(ifFunc, boolB(false), intB(7), intB(0));
          assertThat(evaluate(call))
              .isEqualTo(intB(0));
        }

        @Test
        public void map_func() {
          var s = intTB();
          var r = tupleTB(s);
          var func = defFuncB(funcTB(r, s), combineB(refB(s, 0)));
          var mapFunc = mapFuncB(r, s);
          var map = callB(mapFunc, arrayB(intB(1), intB(2)), func);
          assertThat(evaluate(map))
              .isEqualTo(arrayB(tupleB(intB(1)), tupleB(intB(2))));
        }

        @Test
        public void nat_func() throws Exception {
          var natFunc = natFuncB(funcTB(intTB(), intTB()), blobB(77), stringB("classBinaryName"));
          var call = callB(natFunc, intB(33));
          var nativeMethodLoader = mock(NativeMethodLoader.class);
          when(nativeMethodLoader.load(any(), eq(natFunc)))
              .thenReturn(
                  Try.result(VmTest.class.getMethod("returnIntParam", NativeApi.class, TupleB.class)));
          assertThat(evaluate(vm(nativeMethodLoader), call))
              .isEqualTo(intB(33));
        }

        @Test
        public void nat_func_passed_as_arg() throws NoSuchMethodException {
          var natFunc = natFuncB(funcTB(intTB(), intTB()), blobB(77), stringB("classBinaryName"));
          var nativeMethodLoader = mock(NativeMethodLoader.class);
          when(nativeMethodLoader.load(any(), eq(natFunc)))
              .thenReturn(
                  Try.result(VmTest.class.getMethod("returnIntParam", NativeApi.class, TupleB.class)));

          var natFuncT = natFunc.evalT();
          var outerFunc = defFuncB(list(natFuncT), callB(refB(natFuncT, 0), intB(7)));
          var call = callB(outerFunc, natFunc);
          assertThat(evaluate(vm(nativeMethodLoader), call))
              .isEqualTo(intB(7));
        }

        @Test
        public void nat_func_returned_from_call() throws NoSuchMethodException {
          var natFunc = natFuncB(funcTB(intTB(), intTB()), blobB(77), stringB("classBinaryName"));
          var nativeMethodLoader = mock(NativeMethodLoader.class);
          when(nativeMethodLoader.load(any(), eq(natFunc)))
              .thenReturn(
                  Try.result(VmTest.class.getMethod("returnIntParam", NativeApi.class, TupleB.class)));

          var outerFunc = defFuncB(natFunc);
          var call = callB(callB(outerFunc), intB(7));
          assertThat(evaluate(vm(nativeMethodLoader), call))
              .isEqualTo(intB(7));
        }
      }

      @Test
      public void combine() {
        var combine = combineB(intB(7));
        assertThat(evaluate(combine))
            .isEqualTo(tupleB(intB(7)));
      }

      @Test
      public void order() {
        var order = orderB(intB(7), intB(8));
        assertThat(evaluate(order))
            .isEqualTo(arrayB(intB(7), intB(8)));
      }

      @Test
      public void pick() {
        var tuple = arrayB(intB(10), intB(11), intB(12), intB(13));
        var pick = pickB(tuple, intB(2));
        assertThat(evaluate(pick))
            .isEqualTo(intB(12));
      }

      @Test
      public void pick_with_index_outside_of_bounds() {
        var pick = pickB(
            arrayB(intB(10), intB(11), intB(12), intB(13)),
            intB(4));
        var memoryReporter = new MemoryReporter();
        evaluateWithFailure(vm(memoryReporter), pick, ImmutableMap.of());
        assertThat(memoryReporter.logs())
            .isEqualTo(logs(error("Index (4) out of bounds. Array size = 4.")));
      }

      @Test
      public void pick_with_index_negative() {
        var pick = pickB(
            arrayB(intB(10), intB(11), intB(12), intB(13)),
            intB(-1));
        var memoryReporter = new MemoryReporter();
        evaluateWithFailure(vm(memoryReporter), pick, ImmutableMap.of());
        assertThat(memoryReporter.logs())
            .isEqualTo(logs(error("Index (-1) out of bounds. Array size = 4.")));
      }

      @Test
      public void ref() {
        assertThat(evaluate(callB(idFuncB(), intB(7))))
            .isEqualTo(intB(7));
      }

      @Test
      public void ref_with_index_outside_of_func_param_bounds_causes_fatal()
          throws InterruptedException {
        var innerFuncB = defFuncB(list(), refB(intTB(), 0));
        var outerFuncB = defFuncB(list(intTB()), callB(innerFuncB));
        var taskReporter = mock(TaskReporter.class);
        var vm = vm(taskReporter);
        vm.evaluate(list(callB(outerFuncB, intB(7))), ImmutableMap.of());
        verify(taskReporter).report(any(), any(), argThat(isLogListWithFatalOutOfBounds()));
      }

      private ArgumentMatcher<List<Log>> isLogListWithFatalOutOfBounds() {
        return argument -> argument.size() == 1
            && argument.get(0).level() == Level.FATAL
            && argument.get(0).message().startsWith("Internal smooth error, "
            + "computation failed with:java.lang.ArrayIndexOutOfBoundsException");
      }

      @Test
      public void select() {
        var tuple = tupleB(intB(7));
        var select = selectB(tuple, intB(0));
        assertThat(evaluate(select))
            .isEqualTo(intB(7));
      }
    }

    @Nested
    class _errors {
      @Test
      public void task_throwing_runtime_exception_causes_error() throws Exception {
        var reporter = mock(TaskReporter.class);
        var context = executionContext(new ExecutionReporter(reporter), 4);
        var exprB = throwExceptionCall();
        evaluateWithFailure(new Vm(() -> context), exprB, ImmutableMap.of());
        verify(reporter).report(
            any(),
            any(),
            argThat(this::containsErrorCausedByRuntimeException));
      }

      private boolean containsErrorCausedByRuntimeException(List<Log> logs) {
        return logs.size() == 1
            && logs.get(0).level() == ERROR
            && logs.get(0).message().startsWith(
            "Execution failed with:\njava.lang.RuntimeException: ");
      }

      private CallB throwExceptionCall() throws IOException {
        var funcTB = funcTB(stringTB());
        var natFuncB = natFuncB(funcTB, ThrowException.class);
        return callB(natFuncB);
      }

      public static class ThrowException {
        public static InstB func(NativeApi nativeApi, TupleB args) {
          throw new ArithmeticException();
        }
      }

      @Test
      public void computer_that_throws_exception_is_detected() {
        var reporter = mock(ExecutionReporter.class);
        var exprB = stringB("abc");
        var runtimeException = new RuntimeException();
        var computer = new Computer(null, null, null) {
          @Override
          public void compute(ExecutableTask task, TupleB input, Consumer<CompRes> consumer) {
            throw runtimeException;
          }
        };
        var context = executionContext(computer, reporter, 4);

        evaluateWithFailure(new Vm(() -> context), exprB, ImmutableMap.of());
        verify(reporter, only())
            .reportComputerException(any(), same(runtimeException));
      }
    }
  }

  @Nested
  class _parallelism {
    @Test
    public void tasks_are_executed_in_parallel() throws Exception {
      String counterA = "tasks_are_executed_in_parallel-A";
      String counterB = "tasks_are_executed_in_parallel-B";
      COUNTERS.put(counterA, new AtomicInteger(10));
      COUNTERS.put(counterB, new AtomicInteger(20));
      var expr = orderB(
          WriteSleepReadCall(counterB, counterA),
          WriteSleepReadCall(counterA, counterB));
      assertThat(evaluate(expr))
          .isEqualTo(arrayB(stringB("11"), stringB("21")));
    }

    @Test
    public void execution_waits_and_reuses_computation_with_equal_hash_that_is_being_executed()
        throws Exception {
      var counterName = "execution_waits_and_reuses_computation_with_equal_hash";
      COUNTERS.put(counterName, new AtomicInteger());
      var exprB = orderB(
          sleepGetIncrementCall(counterName),
          sleepGetIncrementCall(counterName),
          sleepGetIncrementCall(counterName),
          sleepGetIncrementCall(counterName)
      );

      var reporter = mock(ExecutionReporter.class);
      var vm = new Vm(() -> executionContext(reporter, 4));
      assertThat(evaluate(vm, exprB))
          .isEqualTo(arrayB(stringB("0"), stringB("0"), stringB("0"), stringB("0")));

      verifyConstTasksResSource(4, DISK, reporter);
    }

    @Test
    public void result_source_for_computation_of_impure_func_is_memory() throws Exception {
      do_test_res_source_of_cached_computation(
          "result_source_for_computation_of_impure_func_is_memory", false, MEMORY);
    }

    @Test
    public void result_source_for_computation_of_pure_func_is_disk() throws Exception {
      do_test_res_source_of_cached_computation(
          "result_source_for_computation_of_pure_func_is_disk", true, DISK);
    }

    private void do_test_res_source_of_cached_computation(
        String counterName, boolean isPure, ResSource resSource) throws IOException {
      COUNTERS.put(counterName, new AtomicInteger());
      var exprB = orderB(
          sleepGetIncrementCall(counterName, isPure),
          sleepGetIncrementCall(counterName, isPure)
      );
      var reporter = mock(ExecutionReporter.class);
      var vm = new Vm(() -> executionContext(reporter, 2));

      assertThat(evaluate(vm, exprB))
          .isEqualTo(arrayB(stringB("0"), stringB("0")));
      verifyConstTasksResSource(2, resSource, reporter);
    }

    @Test
    public void waiting_for_result_of_other_task_with_equal_hash_doesnt_block_executor_thread()
        throws Exception {
      var counterName = "waiting_for_computation_with_same_hash_doesnt_block_executor_thread";
      COUNTERS.put(counterName, new AtomicInteger());
      var exprB = orderB(
          sleepGetIncrementCall(counterName),
          sleepGetIncrementCall(counterName),
          getIncrementCall(counterName));

      var reporter = mock(ExecutionReporter.class);
      var vm = new Vm(() -> executionContext(reporter, 4));
      assertThat(evaluate(vm, exprB))
          .isEqualTo(arrayB(stringB("1"), stringB("1"), stringB("0")));
    }

    private CallB WriteSleepReadCall(String writeCounterName, String readCounterName)
        throws IOException {
      var funcTB = funcTB(stringTB(), stringTB(), stringTB());
      var natFuncB = natFuncB(funcTB, WriteSleepRead.class);
      return callB(natFuncB, stringB(writeCounterName), stringB(readCounterName));
    }

    public static class WriteSleepRead {
      public static InstB func(NativeApi nativeApi, TupleB args) {
        var write = COUNTERS.get(((StringB) args.get(0)).toJ());
        var read = COUNTERS.get(((StringB) args.get(1)).toJ());
        write.incrementAndGet();
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        return nativeApi.factory().string(Integer.toString(read.getAndIncrement()));
      }
    }

    private CallB sleepGetIncrementCall(String counterName) throws IOException {
      return sleepGetIncrementCall(counterName, true);
    }

    private CallB sleepGetIncrementCall(String counterName, boolean isPure) throws IOException {
      var funcTB = funcTB(stringTB(), stringTB());
      var natFuncB = natFuncB(funcTB, SleepGetIncrement.class, isPure);
      return callB(natFuncB, stringB(counterName));
    }

    public static class SleepGetIncrement {
      public static InstB func(NativeApi nativeApi, TupleB args) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        var counter = COUNTERS.get(((StringB) args.get(0)).toJ());
        return nativeApi.factory().string(Integer.toString(counter.getAndIncrement()));
      }
    }

    private CallB getIncrementCall(String counterName) throws IOException {
      var funcTB = funcTB(stringTB(), stringTB());
      var natFuncB = natFuncB(funcTB, GetIncrement.class);
      return callB(natFuncB, stringB(counterName));
    }

    public static class GetIncrement {
      public static InstB func(NativeApi nativeApi, TupleB args) {
        var counter = COUNTERS.get(((StringB) args.get(0)).toJ());
        return nativeApi.factory().string(Integer.toString(counter.getAndIncrement()));
      }
    }
  }

  public static final ConcurrentHashMap<String, AtomicInteger> COUNTERS =
      new ConcurrentHashMap<>();

  private ExprB evaluate(ExprB expr) {
    return evaluate(vm(), expr, ImmutableMap.of());
  }

  private InstB evaluate(Vm vm, ExprB expr) {
    return evaluate(vm, expr, ImmutableMap.of());
  }

  private InstB evaluate(Vm vm, ExprB expr, ImmutableMap<ExprB, TagLoc> tagLocs) {
    try {
      var results = vm.evaluate(list(expr), tagLocs).get();
      assertThat(results.size())
          .isEqualTo(1);
      return results.get(0);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private void evaluateWithFailure(Vm vm, ExprB expr, ImmutableMap<ExprB, TagLoc> tagLocs) {
    try {
      var results = vm.evaluate(list(expr), tagLocs);
      assertThat(results)
          .isEqualTo(Optional.empty());
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static IntB returnIntParam(NativeApi nativeApi, TupleB args) {
    return (IntB) args.get(0);
  }

  private static void verifyConstTasksResSource(
      int size, ResSource expectedResSource, ExecutionReporter reporter) {
    var argCaptor = ArgumentCaptor.forClass(CompRes.class);
    verify(reporter, times(size)).report(taskMatcher(), argCaptor.capture());
    var resSources = map(argCaptor.getAllValues(), CompRes::resSource);
    assertThat(resSources)
        .containsExactlyElementsIn(resSourceList(size, expectedResSource));
  }

  private static Task taskMatcher() {
    return argThat(a -> a.kind() == CALL);
  }

  private static ArrayList<ResSource> resSourceList(int size, ResSource expectedResSource) {
    var expected = new ArrayList<>(nCopies(size, expectedResSource));
    expected.set(0, EXECUTION);
    return expected;
  }

  private static class CountingJobCreator extends JobCreator {
    private final AtomicInteger counter;
    private final Class<? extends ExprB> classToCount;

    public CountingJobCreator(Class<? extends ExprB> classToCount) {
      this(list(), classToCount, new AtomicInteger());
    }

    protected CountingJobCreator(ImmutableList<Job> bindings, Class<? extends ExprB> classToCount,
        AtomicInteger counter) {
      super(bindings, null);
      this.classToCount = classToCount;
      this.counter = counter;
    }

    @Override
    public Job jobFor(ExprB expr, ExecutionContext context) {
      if (expr.getClass().equals(classToCount)) {
        counter.incrementAndGet();
      }
      return super.jobFor(expr, context);
    }

    @Override
    public JobCreator withEnvironment(ImmutableList<Job> environment, TraceS trace) {
      return new CountingJobCreator(environment, classToCount, counter);
    }

    public AtomicInteger counter() {
      return counter;
    }
  }
}
