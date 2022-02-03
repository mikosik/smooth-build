package org.smoothbuild.vm;

import static com.google.common.base.Preconditions.checkArgument;
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
import static org.smoothbuild.util.collect.Lists.list;

import java.math.BigInteger;
import java.util.function.Function;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.IntB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.vm.java.MethodLoader;
import org.smoothbuild.vm.java.MethodLoaderExc;
import org.smoothbuild.vm.job.JobCreator;
import org.smoothbuild.vm.job.JobCreator.TaskCreator;
import org.smoothbuild.vm.job.algorithm.Algorithm;
import org.smoothbuild.vm.job.algorithm.OrderAlgorithm;
import org.smoothbuild.vm.job.job.Job;
import org.smoothbuild.vm.job.job.JobInfo;
import org.smoothbuild.vm.job.job.Task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class VmTest extends TestingContext {
  private final MethodLoader methodLoader = mock(MethodLoader.class);
  private TaskCreator taskCreator;

  @Nested
  class _laziness {
    @Test
    public void learning_test() {
      // This test makes sure that it is possible to detect Task creation using a mock.
      var expr = orderB(intB(7));

      assertThat(evaluate(spyingVm(), expr))
          .isEqualTo(arrayB(intB(7)));

      verify(taskCreator, times(1)).newTask(isA(OrderAlgorithm.class), any(), any());
    }

    @Test
    public void no_task_is_created_for_func_arg_that_is_not_used() {
      var func = funcB(list(arrayTB(boolTB())), intB(7));
      var expr = callB(func, orderB(boolTB()));

      assertThat(evaluate(spyingVm(), expr))
          .isEqualTo(intB(7));

      verify(taskCreator, never()).newTask(isA(OrderAlgorithm.class), any(), any());
    }

    @Test
    public void only_one_task_is_created_for_func_arg_that_is_used_twice() {
      var arrayT = arrayTB(intTB());
      var func = funcB(list(arrayT), combineB(paramRefB(arrayT, 0), paramRefB(arrayT, 0)));
      var expr = callB(func, orderB(intB(7)));

      assertThat(evaluate(spyingVm(), expr))
          .isEqualTo(tupleB(arrayB(intB(7)), arrayB(intB(7))));

      verify(taskCreator, times(1)).newTask(isA(OrderAlgorithm.class), any(), any());
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
  class _call {
    @Test
    public void call() {
      var func = funcB(intB(7));
      var call = callB(func);
      assertThat(evaluate(call))
          .isEqualTo(intB(7));
    }

    @Test
    public void with_res_conversion() {
      var func = funcB(arrayB(nothingTB()));
      var call = callB(arrayTB(intTB()), func);
      assertThat(evaluate(call))
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void with_arg_conversion() {
      var func = funcB(list(arrayTB(intTB())), paramRefB(arrayTB(intTB()), 0));
      var call = callB(func, arrayB(nothingTB()));
      assertThat(evaluate(call))
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void with_polymorphic_func() {
      var a = oVarTB("A");
      var ca = close(a);
      var func = funcB(list(a), orderB(ca, paramRefB(ca, 0)));
      var call = callB(func, intB(7));
      assertThat(evaluate(call))
          .isEqualTo(arrayB(intTB(), intB(7)));
    }

    @Test
    public void with_polymorphic_evalT() {
      var evaluated = evaluatePolymorphicExpr(p -> {
        var b = oVarTB("B");
        var cb = cVarTB("B");
        var funcB = funcB(list(b), orderB(cb, paramRefB(cb, 0)));
        return callB(funcB, p);
      }, intB(7));
      assertThat(evaluated)
          .isEqualTo(arrayB(intB(7)));
    }
  }

  @Nested
  class _combine {
    @Test
    public void combine() {
      var combine = combineB(intB(7));
      assertThat(evaluate(combine))
          .isEqualTo(tupleB(intB(7)));
    }

    @Test
    public void with_item_conversion() {
      var combine = combineB(tupleTB(arrayTB(intTB())), arrayB(nothingTB()));
      assertThat(evaluate(combine))
          .isEqualTo(tupleB(arrayB(intTB())));
    }

    @Test
    public void with_polymorphic_evalT() {
      assertThat(evaluatePolymorphicExpr(p -> combineB(p), intB(7)))
          .isEqualTo(tupleB(intB(7)));
    }
  }

  @Nested
  class _if {
    @Test
    public void true_condition() {
      var if_ = ifB(boolB(true), intB(1), intB(2));
      assertThat(evaluate(if_))
          .isEqualTo(intB(1));
    }

    @Test
    public void false_condition() {
      var if_ = ifB(boolB(false), intB(1), intB(2));
      assertThat(evaluate(if_))
          .isEqualTo(intB(2));
    }

    @Test
    public void then_conversion() {
      var if_ = ifB(boolB(true), arrayB(nothingTB()), arrayB(intB(7)));
      assertThat(evaluate(if_))
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void else_conversion() {
      var if_ = ifB(boolB(false), arrayB(intB(7)), arrayB(nothingTB()));
      assertThat(evaluate(if_))
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void polymorphic_then() {
      assertThat(evaluatePolymorphicExpr(p -> ifB(boolB(true), p, p), intB(7)))
          .isEqualTo(intB(7));
    }

    @Test
    public void polymorphic_else() {
      assertThat(evaluatePolymorphicExpr(p -> ifB(boolB(false), p, p), intB(7)))
          .isEqualTo(intB(7));
    }
  }

  @Nested
  class _invoke {
    @Test
    public void argless() throws Exception {
      var method = methodB(methodTB(intTB(), list()), blobB(77), stringB("classBinaryName"));
      var invoke = invokeB(method);
      when(methodLoader.load(any(), eq(method)))
          .thenReturn(VmTest.class.getMethod("returnInt", NativeApi.class));
      assertThat(evaluate(invoke))
          .isEqualTo(intB(173));
    }

    @Test
    public void with_param() throws Exception {
      var method = methodB(methodTB(intTB(), list(intTB())), blobB(77), stringB("classBinaryName"));
      var invoke = invokeB(method, intB(33));
      when(methodLoader.load(any(), eq(method)))
          .thenReturn(VmTest.class.getMethod(
              "returnIntParam", NativeApi.class, IntB.class));
      assertThat(evaluate(invoke))
          .isEqualTo(intB(33));
    }

    @Test
    public void with_param_conversion() throws Exception {
      var methodT = methodTB(arrayTB(intTB()), list(arrayTB(intTB())));
      var method = methodB(methodT, blobB(77), stringB("classBinaryName"));
      var invoke = invokeB(arrayTB(intTB()), method, arrayB(nothingTB()));
      when(methodLoader.load(any(), eq(method)))
          .thenReturn(VmTest.class.getMethod(
              "returnArrayParamWithCheck", NativeApi.class, ArrayB.class));
      assertThat(evaluate(invoke))
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void with_res_conversion() throws Exception {
      var methodT = methodTB(arrayTB(nothingTB()), list());
      var method = methodB(methodT, blobB(77), stringB("classBinaryName"));
      var invoke = invokeB(arrayTB(intTB()), method);
      when(methodLoader.load(any(), eq(method)))
          .thenReturn(VmTest.class.getMethod("returnNothingArray", NativeApi.class));
      assertThat(evaluate(invoke))
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void with_polymorphic_evalT() {
      var evaluated = evaluatePolymorphicExpr(p -> {
        var b = oVarTB("B");
        var methodT = methodTB(arrayTB(b), list(b));
        var method = methodB(methodT, blobB(77), stringB("classBinaryName"));
        try {
          when(methodLoader.load(any(), eq(method)))
              .thenReturn(VmTest.class.getMethod(
                  "returnSingleElemArray", NativeApi.class, ValB.class));
        } catch (NoSuchMethodException | MethodLoaderExc e) {
          throw new RuntimeException(e);
        }
        return invokeB(method, p);
      }, intB(7));
      assertThat(evaluated)
          .isEqualTo(arrayB(intB(7)));
    }
  }

  public static IntB returnInt(NativeApi nativeApi) {
    return nativeApi.factory().int_(BigInteger.valueOf(173));
  }

  public static IntB returnIntParam(NativeApi nativeApi, IntB param) {
    return param;
  }

  public static ArrayB returnArrayParamWithCheck(NativeApi nativeApi, ArrayB param) {
    checkArgument(param.type().name().equals("[Int]"));
    return param;
  }

  public static ArrayB returnNothingArray(NativeApi nativeApi) {
    var f = nativeApi.factory();
    var arrayT = f.arrayT(f.nothingT());
    return f.arrayBuilder(arrayT).build();
  }

  public static ArrayB returnSingleElemArray(NativeApi nativeApi, ValB elem) {
    var f = nativeApi.factory();
    var arrayT = f.arrayT(elem.type());
    return f.arrayBuilder(arrayT)
        .add(elem)
        .build();
  }

  @Nested
  class _map {
    @Test
    public void map() {
      var t = intTB();
      var func = funcB(tupleTB(t), list(t), combineB(paramRefB(t, 0)));
      var map = mapB(arrayB(intB(1), intB(2)), func);
      assertThat(evaluate(map))
          .isEqualTo(arrayB(tupleB(intB(1)), tupleB(intB(2))));
    }

    @Test
    public void with_polymorphic_func() {
      var evaluated = evaluatePolymorphicExpr(p -> {
        var t = p.type();
        var mappingFunc = funcB(tupleTB(t), list(t), combineB(paramRefB(t, 0)));
        return mapB(orderB(t, paramRefB(t, 0)), mappingFunc);
      }, intB(7));
      assertThat(evaluated)
          .isEqualTo(arrayB(tupleB(intB(7))));
    }

    @Test
    public void with_input_array_conversion() {
      var resT = tupleTB(intTB());
      var func = funcB(resT, list(intTB()), combineB(paramRefB(intTB(), 0)));
      var map = mapB(arrayB(nothingTB()), func);
      assertThat(evaluate(map))
          .isEqualTo(arrayB(tupleTB(intTB())));
    }

    @Test
    public void with_polymorphic_evalT() {
      var evaluated = evaluatePolymorphicExpr(p -> {
        var t = p.type();
        var func = funcB(tupleTB(t), list(t), combineB(paramRefB(t, 0)));
        return mapB(orderB(p), func);
      }, intB(7));
      assertThat(evaluated)
          .isEqualTo(arrayB(tupleB(intB(7))));
    }
  }

  @Nested
  class _order {
    @Test
    public void order() {
      var order = orderB(intB(7), intB(8));
      assertThat(evaluate(order))
          .isEqualTo(arrayB(intB(7), intB(8)));
    }

    @Test
    public void with_element_conversion() {
      var order = orderB(arrayTB(intTB()), arrayB(nothingTB()));
      assertThat(evaluate(order))
          .isEqualTo(arrayB(arrayTB(intTB()), arrayB(intTB())));
    }

    @Test
    public void with_polymorphic_evalT() {
      assertThat(evaluatePolymorphicExpr(p -> orderB(p), intB(7)))
          .isEqualTo(arrayB(intB(7)));
    }
  }

  @Nested
  class _param_ref {
    @Test
    public void referencing_param_inside_func() {
      var func = funcB(list(intTB()), paramRefB(intTB(), 0));
      assertThat(evaluate(callB(func, intB(7))))
          .isEqualTo(intB(7));
    }
  }

  @Nested
  class _select {
    @Test
    public void select() {
      var tuple = tupleB(intB(7));
      var select = selectB(tuple, intB(0));
      assertThat(evaluate(select))
          .isEqualTo(intB(7));
    }

    @Test
    public void with_conversion() {
      var tuple = tupleB(arrayB(nothingTB()));
      var select = selectB(arrayTB(intTB()), tuple, intB(0));
      assertThat(evaluate(select))
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void with_polymorphic_evalT() {
      assertThat(evaluatePolymorphicExpr(p -> selectB(combineB(p), intB(0)), intB(7)))
          .isEqualTo(intB(7));
    }
  }

  private ObjB evaluatePolymorphicExpr(Function<ObjB, ObjB> exprCreator, ObjB val) {
    var oa = oVarTB("A");
    var ca = close(oa);
    var paramRef = paramRefB(ca, 0);
    var expr = exprCreator.apply(paramRef);
    var func = funcB(list(oa), expr);
    var call = callB(func, val);
    return evaluate(call);
  }

  private ObjB evaluate(ObjB obj) {
    var vm = vmProv(methodLoader).get(ImmutableMap.of());
    return evaluate(vm, obj);
  }

  private ValB evaluate(Vm vm, ObjB obj) {
    try {
      var results = vm.evaluate(list(obj)).get();
      assertThat(results.size())
          .isEqualTo(1);
      return results.get(0);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private Vm spyingVm() {
    // This anonymous TaskCreator cannot be replaced with Task::new because
    // the latter is final and cannot be mocked by Mockito.
    taskCreator = spy(new TaskCreator() {
      @Override
      public Task newTask(Algorithm algorithm, ImmutableList<Job> depJs, JobInfo info) {
        return new Task(algorithm, depJs, info);
      }
    });
    var jobCreator = new JobCreator(null, typingB(), ImmutableMap.of(), taskCreator);
    return new Vm(jobCreator, parallelJobExecutor());
  }
}
