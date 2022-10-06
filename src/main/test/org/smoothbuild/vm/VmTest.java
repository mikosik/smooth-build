package org.smoothbuild.vm;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smoothbuild.out.log.ImmutableLogs.logs;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.val.BoolB;
import org.smoothbuild.bytecode.expr.val.InstB;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.compile.lang.base.LabeledLoc;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.accept.MemoryReporter;
import org.smoothbuild.util.collect.Try;
import org.smoothbuild.vm.job.ExecutionContext;
import org.smoothbuild.vm.job.Job;
import org.smoothbuild.vm.job.JobCreator;
import org.smoothbuild.vm.task.NativeMethodLoader;
import org.smoothbuild.vm.task.OrderTask;

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
        var paramT = func.type();
        var outerFunc = defFuncB(list(paramT), callB(intTB(), refB(paramT, 0)));
        var call = callB(outerFunc, func);
        assertThat(evaluate(call))
            .isEqualTo(intB(7));
      }

      @Test
      public void def_func_returned_from_call() {
        var func = defFuncB(intB(7));
        var outerFunc = defFuncB(func);
        var call = callB(intTB(), callB(outerFunc));
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

        var natFuncT = natFunc.type();
        var outerFunc = defFuncB(list(natFuncT), callB(intTB(), refB(natFuncT, 0), intB(7)));
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
        var call = callB(intTB(), callB(outerFunc), intB(7));
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
    public void ref_with_index_outside_of_func_param_bounds_causes_exception() {
      var innerFuncB = defFuncB(list(), refB(intTB(), 0));
      var outerFuncB = defFuncB(list(intTB()), callB(innerFuncB));
      assertCall(() -> evaluate(callB(outerFuncB, intB(7))))
          .throwsException(ArrayIndexOutOfBoundsException.class);
    }

    @Test
    public void select() {
      var tuple = tupleB(intB(7));
      var select = selectB(tuple, intB(0));
      assertThat(evaluate(select))
          .isEqualTo(intB(7));
    }
  }

  private ExprB evaluate(ExprB expr) {
    var vm = vm();
    return evaluate(vm, expr, ImmutableMap.of());
  }

  private InstB evaluate(Vm vm, ExprB expr) {
    return evaluate(vm, expr, ImmutableMap.of());
  }

  private InstB evaluate(Vm vm, ExprB expr, ImmutableMap<ExprB, LabeledLoc> labels) {
    try {
      var results = vm.evaluate(list(expr), labels).get();
      assertThat(results.size())
          .isEqualTo(1);
      return results.get(0);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private void evaluateWithFailure(Vm vm, ExprB expr, ImmutableMap<ExprB, LabeledLoc> labels) {
    try {
      var results = vm.evaluate(list(expr), labels);
      assertThat(results)
          .isEqualTo(Optional.empty());
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static IntB returnIntParam(NativeApi nativeApi, TupleB args) {
    return (IntB) args.get(0);
  }

  private static class CountingJobCreator extends JobCreator {
    private final AtomicInteger counter;
    private final Class<? extends ExprB> classToCount;

    public CountingJobCreator(Class<? extends ExprB> classToCount) {
      this(list(), classToCount, new AtomicInteger());
    }

    protected CountingJobCreator(ImmutableList<Job> bindings, Class<? extends ExprB> classToCount,
        AtomicInteger counter) {
      super(bindings);
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
    public JobCreator withEnvironment(ImmutableList<Job> environment) {
      return new CountingJobCreator(environment, classToCount, counter);
    }

    public AtomicInteger counter() {
      return counter;
    }
  }
}
