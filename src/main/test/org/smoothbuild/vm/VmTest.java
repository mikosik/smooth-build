package org.smoothbuild.vm;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.smoothbuild.util.collect.Lists.list;

import java.math.BigInteger;
import java.util.function.Function;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.IntB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.vm.java.MethodLoader;

import com.google.common.collect.ImmutableMap;

public class VmTest extends TestingContext {
  private final MethodLoader methodLoader = Mockito.mock(MethodLoader.class);

  @Nested
  class _values {
    @Test
    public void array() throws Exception {
      assertThat(evaluate(arrayB(intB(7))))
          .isEqualTo(arrayB(intB(7)));
    }

    @Test
    public void blob() throws Exception {
      assertThat(evaluate(blobB(7)))
          .isEqualTo(blobB(7));
    }

    @Test
    public void bool() throws Exception {
      assertThat(evaluate(intB(8)))
          .isEqualTo(intB(8));
    }

    @Test
    public void string() throws Exception {
      assertThat(evaluate(stringB("abc")))
          .isEqualTo(stringB("abc"));
    }

    @Test
    public void tuple() throws Exception {
      assertThat(evaluate(tupleB(list(intB(7)))))
          .isEqualTo(tupleB(list(intB(7))));
    }
  }

  @Nested
  class _call {
    @Test
    public void call() throws Exception {
      var func = funcB(intB(7));
      var call = callB(func, combineB(list()));
      assertThat(evaluate(call))
          .isEqualTo(intB(7));
    }

    @Test
    public void with_res_conversion() throws Exception {
      var func = funcB(arrayB(nothingTB()));
      var call = callB(arrayTB(intTB()), func, combineB(list()));
      assertThat(evaluate(call))
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void with_arg_conversion() throws Exception {
      var func = funcB(list(arrayTB(intTB())), paramRefB(arrayTB(intTB()), 0));
      var call = callB(func, list(arrayB(nothingTB())));
      assertThat(evaluate(call))
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void with_polymorphic_func() throws Exception {
      var a = oVarTB("A");
      var func = funcB(list(a), orderB(a, list(paramRefB(a, 0))));
      var call = callB(func, list(intB(7)));
      assertThat(evaluate(call))
          .isEqualTo(arrayB(intTB(), intB(7)));
    }

    @Test
    public void with_polymorphic_evalT() throws Exception {
      var evaluated = evaluatePolymorphicExpr(p -> {
        var b = oVarTB("B");
        var funcB = funcB(list(b), orderB(b, list(paramRefB(b, 0))));
        return callB(funcB, list(p));
      }, intB(7));
      assertThat(evaluated)
          .isEqualTo(arrayB(intB(7)));
    }
  }

  @Nested
  class _combine {
    @Test
    public void combine() throws Exception {
      var combine = combineB(list(intB(7)));
      assertThat(evaluate(combine))
          .isEqualTo(tupleB(list(intB(7))));
    }

    @Test
    public void with_item_conversion() throws Exception {
      var combine = combineB(tupleTB(list(arrayTB(intTB()))), list(arrayB(nothingTB())));
      assertThat(evaluate(combine))
          .isEqualTo(tupleB(list(arrayB(intTB()))));
    }

    @Test
    public void with_polymorphic_evalT() throws Exception {
      assertThat(evaluatePolymorphicExpr(p -> combineB(list(p)), intB(7)))
          .isEqualTo(tupleB(list(intB(7))));
    }
  }

  @Nested
  class _if {
    @Test
    public void true_condition() throws Exception {
      var if_ = ifB(boolB(true), intB(1), intB(2));
      assertThat(evaluate(if_))
          .isEqualTo(intB(1));
    }

    @Test
    public void false_condition() throws Exception {
      var if_ = ifB(boolB(false), intB(1), intB(2));
      assertThat(evaluate(if_))
          .isEqualTo(intB(2));
    }

    @Test
    public void then_conversion() throws Exception {
      var if_ = ifB(boolB(true), arrayB(nothingTB()), arrayB(intB(7)));
      assertThat(evaluate(if_))
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void else_conversion() throws Exception {
      var if_ = ifB(boolB(false), arrayB(intB(7)), arrayB(nothingTB()));
      assertThat(evaluate(if_))
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void polymorphic_then() throws Exception {
      assertThat(evaluatePolymorphicExpr(p -> ifB(boolB(true), p, p), intB(7)))
          .isEqualTo(intB(7));
    }

    @Test
    public void polymorphic_else() throws Exception {
      assertThat(evaluatePolymorphicExpr(p -> ifB(boolB(false), p, p), intB(7)))
          .isEqualTo(intB(7));
    }
  }

  @Nested
  class _invoke {
    @Test
    public void argless() throws Exception {
      var method = methodB(methodTB(intTB(), list()), blobB(77), stringB("classBinaryName"));
      var invoke = invokeB(method, list());
      when(methodLoader.load(any(), eq(method)))
          .thenReturn(VmTest.class.getMethod("returnInt", NativeApi.class));
      assertThat(evaluate(invoke))
          .isEqualTo(intB(173));
    }

    @Test
    public void with_param() throws Exception {
      var method = methodB(methodTB(intTB(), list(intTB())), blobB(77), stringB("classBinaryName"));
      var invoke = invokeB(method, list(intB(33)));
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
      var invoke = invokeB(arrayTB(intTB()), method, list(arrayB(nothingTB())));
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
      var invoke = invokeB(arrayTB(intTB()), method, list());
      when(methodLoader.load(any(), eq(method)))
          .thenReturn(VmTest.class.getMethod("returnNothingArray", NativeApi.class));
      assertThat(evaluate(invoke))
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void with_polymorphic_evalT() throws Exception {
      var evaluated = evaluatePolymorphicExpr(p -> {
        var b = oVarTB("B");
        var methodT = methodTB(arrayTB(b), list(b));
        var method = methodB(methodT, blobB(77), stringB("classBinaryName"));
        try {
          when(methodLoader.load(any(), eq(method)))
              .thenReturn(VmTest.class.getMethod(
                  "returnSingleElemArray", NativeApi.class, ValB.class));
        } catch (NoSuchMethodException e) {
          throw new RuntimeException(e);
        }
        return invokeB(method, list(p));
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
    public void map() throws Exception {
      var t = intTB();
      var func = funcB(tupleTB(list(t)), list(t), combineB(list(paramRefB(t, 0))));
      var map = mapB(arrayB(intB(1), intB(2)), func);
      assertThat(evaluate(map))
          .isEqualTo(arrayB(tupleB(list(intB(1))), tupleB(list(intB(2)))));
    }

    @Test
    public void with_polymorphic_func() throws Exception {
      var evaluated = evaluatePolymorphicExpr(p -> {
        var t = p.type();
        var mappingFunc = funcB(tupleTB(list(t)), list(t), combineB(list(paramRefB(t, 0))));
        return mapB(orderB(t, list(paramRefB(t, 0))), mappingFunc);
      }, intB(7));
      assertThat(evaluated)
          .isEqualTo(arrayB(tupleB(list(intB(7)))));
    }

    @Test
    public void with_input_array_conversion() throws Exception {
      var resT = tupleTB(list(intTB()));
      var func = funcB(resT, list(intTB()), combineB(list(paramRefB(intTB(), 0))));
      var map = mapB(arrayB(nothingTB()), func);
      assertThat(evaluate(map))
          .isEqualTo(arrayB(tupleTB(list(intTB()))));
    }

    @Test
    public void with_polymorphic_evalT() throws Exception {
      var evaluated = evaluatePolymorphicExpr(p -> {
        var t = p.type();
        var func = funcB(tupleTB(list(t)), list(t), combineB(list(paramRefB(t, 0))));
        return mapB(orderB(list(p)), func);
      }, intB(7));
      assertThat(evaluated)
          .isEqualTo(arrayB(tupleB(list(intB(7)))));
    }
  }

  @Nested
  class _order {
    @Test
    public void order() throws Exception {
      var order = orderB(list(intB(7), intB(8)));
      assertThat(evaluate(order))
          .isEqualTo(arrayB(intB(7), intB(8)));
    }

    @Test
    public void with_element_conversion() throws Exception {
      var order = orderB(arrayTB(intTB()), list(arrayB(nothingTB())));
      assertThat(evaluate(order))
          .isEqualTo(arrayB(arrayTB(intTB()), arrayB(intTB())));
    }

    @Test
    public void with_polymorphic_evalT() throws Exception {
      assertThat(evaluatePolymorphicExpr(p -> orderB(list(p)), intB(7)))
          .isEqualTo(arrayB(intB(7)));
    }
  }

  @Nested
  class _param_ref {
    @Test
    public void referencing_param_of_enclosing_func_from_enclosed_func() throws Exception {
      var inner = funcB(paramRefB(intTB(), 0));
      var outer = funcB(list(intTB()), callB(inner, list()));
      assertThat(evaluate(callB(outer, list(intB(7)))))
          .isEqualTo(intB(7));
    }
  }

  @Nested
  class _select {
    @Test
    public void select() throws Exception {
      var tuple = tupleB(list(intB(7)));
      var select = selectB(tuple, intB(0));
      assertThat(evaluate(select))
          .isEqualTo(intB(7));
    }

    @Test
    public void with_conversion() throws Exception {
      var tuple = tupleB(list(arrayB(nothingTB())));
      var select = selectB(arrayTB(intTB()), tuple, intB(0));
      assertThat(evaluate(select))
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void with_polymorphic_evalT() throws Exception {
      assertThat(evaluatePolymorphicExpr(p -> selectB(combineB(list(p)), intB(0)), intB(7)))
          .isEqualTo(intB(7));
    }
  }

  private ObjB evaluatePolymorphicExpr(Function<ObjB, ObjB> exprCreator, ObjB val)
      throws Exception {
    var a = oVarTB("A");
    var paramRef = paramRefB(a, 0);
    var expr = exprCreator.apply(paramRef);
    var func = funcB(list(a), expr);
    var call = callB(func, list(val));
    return evaluate(call);
  }

  private ObjB evaluate(ObjB obj) throws Exception {
    var key = "a";
    var vm = vmProv(methodLoader).get(ImmutableMap.of());
    var resultMap = vm.evaluate(ImmutableMap.of(key, obj));
    assertThat(resultMap.size())
        .isEqualTo(1);
    return resultMap.get(key).get();
  }
}
