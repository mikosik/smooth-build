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
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.math.BigInteger;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.compile.lang.base.ExprInfo;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.util.collect.Try;
import org.smoothbuild.vm.algorithm.NativeMethodLoader;
import org.smoothbuild.vm.algorithm.OrderAlgorithm;
import org.smoothbuild.vm.execute.TaskExecutor;

import com.google.common.collect.ImmutableMap;

public class VmTest extends TestContext {
  private final NativeMethodLoader nativeMethodLoader = mock(NativeMethodLoader.class);
  private TaskExecutor spyingExecutor;

  @Nested
  class _laziness {
    @Test
    public void learning_test() {
      // This test makes sure that it is possible to detect Task creation using a mock.
      var order = orderB(intB(7));

      assertThat(evaluate(vmWithSpyingExecutor(), order))
          .isEqualTo(arrayB(intB(7)));

      verify(spyingExecutor, times(1)).enqueue(any(), isA(OrderAlgorithm.class), any(), any());
    }

    @Test
    public void no_task_is_created_for_func_arg_that_is_not_used() {
      var func = funcB(list(arrayTB(boolTB())), intB(7));
      var call = callB(func, orderB(boolTB()));

      assertThat(evaluate(vmWithSpyingExecutor(), call))
          .isEqualTo(intB(7));

      verify(spyingExecutor, never()).enqueue(any(), isA(OrderAlgorithm.class), any(), any());
    }

    @Test
    public void no_task_is_created_for_func_arg_that_is_passed_to_func_where_it_is_not_used() {
      var innerFunc = funcB(list(arrayTB(boolTB())), intB(7));
      var outerFunc = funcB(list(arrayTB(boolTB())),
          callB(innerFunc, paramRefB(arrayTB(boolTB()), 0)));
      var call = callB(outerFunc, orderB(boolTB()));

      assertThat(evaluate(vmWithSpyingExecutor(), call))
          .isEqualTo(intB(7));

      verify(spyingExecutor, never()).enqueue(any(), isA(OrderAlgorithm.class), any(), any());
    }

    @Test
    public void only_one_task_is_created_for_func_arg_that_is_used_twice() {
      var arrayT = arrayTB(intTB());
      var func = funcB(list(arrayT), combineB(paramRefB(arrayT, 0), paramRefB(arrayT, 0)));
      var call = callB(func, orderB(intB(7)));

      assertThat(evaluate(vmWithSpyingExecutor(), call))
          .isEqualTo(tupleB(arrayB(intB(7)), arrayB(intB(7))));

      verify(spyingExecutor, times(1)).enqueue(any(), isA(OrderAlgorithm.class), any(), any());
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
    @Test
    public void call() {
      var func = funcB(intB(7));
      var call = callB(func);
      assertThat(evaluate(call))
          .isEqualTo(intB(7));
    }

    @Test
    public void call_func_passed_as_arg() {
      var func = funcB(intB(7));
      var paramT = func.type();
      var outerFunc = funcB(list(paramT),
          callB(intTB(), paramRefB(paramT, 0)));
      var call = callB(outerFunc, func);
      assertThat(evaluate(call))
          .isEqualTo(intB(7));
    }

    @Test
    public void call_func_returned_from_call() {
      var func = funcB(intB(7));
      var outerFunc = funcB(func);
      var call = callB(intTB(), callB(outerFunc));
      assertThat(evaluate(call))
          .isEqualTo(intB(7));
    }

    @Test
    public void combine() {
      var combine = combineB(intB(7));
      assertThat(evaluate(combine))
          .isEqualTo(tupleB(intB(7)));
    }

    @Test
    public void if_true_condition() {
      var if_ = ifB(intTB(), boolB(true), intB(1), intB(2));
      assertThat(evaluate(if_))
          .isEqualTo(intB(1));
    }

    @Test
    public void if_false_condition() {
      var if_ = ifB(intTB(), boolB(false), intB(1), intB(2));
      assertThat(evaluate(if_))
          .isEqualTo(intB(2));
    }

    @Test
    public void invoke_argless() throws Exception {
      var method = methodB(methodTB(intTB()), blobB(77), stringB("classBinaryName"));
      var invoke = invokeB(method);
      when(nativeMethodLoader.load(any(), eq(method)))
          .thenReturn(
              Try.result(VmTest.class.getMethod("returnInt", NativeApi.class, TupleB.class)));
      assertThat(evaluate(invoke))
          .isEqualTo(intB(173));
    }

    @Test
    public void invoke_with_param() throws Exception {
      var method = methodB(methodTB(intTB(), intTB()), blobB(77), stringB("classBinaryName"));
      var invoke = invokeB(method, intB(33));
      when(nativeMethodLoader.load(any(), eq(method)))
          .thenReturn(
              Try.result(VmTest.class.getMethod("returnIntParam", NativeApi.class, TupleB.class)));
      assertThat(evaluate(invoke))
          .isEqualTo(intB(33));
    }

    @Test
    public void map() {
      var t = intTB();
      var func = funcB(tupleTB(t), list(t), combineB(paramRefB(t, 0)));
      var map = mapB(arrayB(intB(1), intB(2)), func);
      assertThat(evaluate(map))
          .isEqualTo(arrayB(tupleB(intB(1)), tupleB(intB(2))));
    }

    @Test
    public void order() {
      var order = orderB(intB(7), intB(8));
      assertThat(evaluate(order))
          .isEqualTo(arrayB(intB(7), intB(8)));
    }

    @Test
    public void param_ref() {
      assertThat(evaluate(callB(idFuncB(), intB(7))))
          .isEqualTo(intB(7));
    }

    @Test
    public void param_ref_with_index_outside_of_func_param_bounds_causes_exception() {
      var innerFuncB = funcB(list(), paramRefB(intTB(), 0));
      var outerFuncB = funcB(list(intTB()), callB(innerFuncB));
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
    var vm = vm(nativeMethodLoader);
    return evaluate(vm, expr, ImmutableMap.of());
  }

  private ValB evaluate(Vm vm, ExprB expr) {
    return evaluate(vm, expr, ImmutableMap.of());
  }

  private ValB evaluate(Vm vm, ExprB expr, ImmutableMap<ExprB, ExprInfo> descriptions) {
    try {
      var results = vm.evaluate(list(expr), descriptions).get();
      assertThat(results.size())
          .isEqualTo(1);
      return results.get(0);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private Vm vmWithSpyingExecutor() {
    spyingExecutor = spy(new TaskExecutor(computer(), executionReporter()));
    return new Vm(() -> executionContext(spyingExecutor));
  }

  public static IntB returnInt(NativeApi nativeApi, TupleB args) {
    return nativeApi.factory().int_(BigInteger.valueOf(173));
  }

  public static IntB returnIntParam(NativeApi nativeApi, TupleB args) {
    return (IntB) args.get(0);
  }
}
