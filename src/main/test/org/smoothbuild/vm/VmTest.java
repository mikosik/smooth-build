package org.smoothbuild.vm;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.smoothbuild.util.collect.Lists.list;

import java.math.BigInteger;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.IntB;
import org.smoothbuild.bytecode.type.val.MethodTB;
import org.smoothbuild.bytecode.type.val.VarTB;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.vm.java.MethodLoader;

import com.google.common.collect.ImmutableMap;

public class VmTest extends TestingContext {
  private MethodLoader methodLoader = Mockito.mock(MethodLoader.class);

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
    public void call_with_result_conversion() throws Exception {
      var func = funcB(arrayB(nothingTB()));
      var call = callB(arrayTB(intTB()), func, combineB(list()));
      assertThat(evaluate(call))
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void call_polymorphic() throws Exception {
      var a = varTB("A");
      var func = funcB(list(a), orderB(a, list(paramRefB(a, 0))));
      var call = callB(func, list(intB(7)));
      assertThat(evaluate(call))
          .isEqualTo(arrayB(intTB(), intB(7)));
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
    public void combine_with_item_conversion() throws Exception {
      var combine = combineB(tupleTB(list(arrayTB(intTB()))), list(arrayB(nothingTB())));
      assertThat(evaluate(combine))
          .isEqualTo(tupleB(list(arrayB(intTB()))));
    }
  }

  @Nested
  class _if {
    @Test
    public void if_with_true_condition() throws Exception {
      var if_ = ifB(boolB(true), intB(1), intB(2));
      assertThat(evaluate(if_))
          .isEqualTo(intB(1));
    }

    @Test
    public void if_with_false_condition() throws Exception {
      var if_ = ifB(boolB(false), intB(1), intB(2));
      assertThat(evaluate(if_))
          .isEqualTo(intB(2));
    }

    @Test
    public void if_with_then_conversion() throws Exception {
      var if_ = ifB(boolB(true), arrayB(nothingTB()), arrayB(intB(7)));
      assertThat(evaluate(if_))
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void if_else_conversion() throws Exception {
      var if_ = ifB(boolB(false), arrayB(intB(7)), arrayB(nothingTB()));
      assertThat(evaluate(if_))
          .isEqualTo(arrayB(intTB()));
    }
  }

  @Nested
  class _invoke {
    @Test
    public void invoke_argless() throws Exception {
      var method = methodB(methodTB(intTB(), list()), blobB(77), stringB("classBinaryName"));
      var invoke = invokeB(method, list());
      when(methodLoader.load(any(), eq(method)))
          .thenReturn(VmTest.class.getMethod("justReturnInt", NativeApi.class));
      assertThat(evaluate(invoke))
          .isEqualTo(intB(173));
    }

    @Test
    public void invoke_with_param() throws Exception {
      var method = methodB(methodTB(intTB(), list(intTB())), blobB(77), stringB("classBinaryName"));
      var invoke = invokeB(method, list(intB(33)));
      when(methodLoader.load(any(), eq(method)))
          .thenReturn(VmTest.class.getMethod(
              "justReturnIntParam", NativeApi.class, IntB.class));
      assertThat(evaluate(invoke))
          .isEqualTo(intB(33));
    }

    @Test
    public void invoke_with_param_conversion() throws Exception {
      MethodTB methodT = methodTB(arrayTB(intTB()), list(arrayTB(intTB())));
      var method = methodB(methodT, blobB(77), stringB("classBinaryName"));
      var invoke = invokeB(arrayTB(intTB()), method, list(arrayB(nothingTB())));
      when(methodLoader.load(any(), eq(method)))
          .thenReturn(VmTest.class.getMethod(
              "justReturnArrayParamWithCheck", NativeApi.class, ArrayB.class));
      assertThat(evaluate(invoke))
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void invoke_with_res_conversion() throws Exception {
      var methodT = methodTB(arrayTB(nothingTB()), list());
      var method = methodB(methodT, blobB(77), stringB("classBinaryName"));
      var invoke = invokeB(arrayTB(intTB()), method, list());
      when(methodLoader.load(any(), eq(method)))
          .thenReturn(VmTest.class.getMethod("justReturnNothingArray", NativeApi.class));
      assertThat(evaluate(invoke))
          .isEqualTo(arrayB(intTB()));
    }
  }

  public static IntB justReturnInt(NativeApi nativeApi) {
    return nativeApi.factory().int_(BigInteger.valueOf(173));
  }

  public static IntB justReturnIntParam(NativeApi nativeApi, IntB param) {
    return param;
  }

  public static ArrayB justReturnArrayParamWithCheck(NativeApi nativeApi, ArrayB param) {
    checkArgument(param.type().name().equals("[Int]"));
    return param;
  }

  public static ArrayB justReturnNothingArray(NativeApi nativeApi) {
    var f = nativeApi.factory();
    var arrayT = f.arrayT(f.nothingT());
    return f.arrayBuilder(arrayT).build();
  }

  @Nested
  class _map {
    @Test
    public void map() throws Exception{
      var t = intTB();
      var func = funcB(tupleTB(list(t)), list(t), combineB(list(paramRefB(t, 0))));
      var map = mapB(arrayB(intB(1), intB(2)), func);
      assertThat(evaluate(map))
          .isEqualTo(arrayB(tupleB(list(intB(1))), tupleB(list(intB(2)))));
    }

    @Test
    public void map_with_polymorphic_func() throws Exception{
      VarTB a = varTB("A");
      var func = funcB(tupleTB(list(a)), list(a), combineB(list(paramRefB(a, 0))));
      var map = mapB(arrayB(intB(1), intB(2)), func);
      assertThat(evaluate(map))
          .isEqualTo(arrayB(tupleB(list(intB(1))), tupleB(list(intB(2)))));
    }

    @Test
    public void map_with_input_array_conversion() throws Exception{
      var func = funcB(tupleTB(list(intTB())), list(intTB()), combineB(list(paramRefB(intTB(), 0))));
      var map = mapB(arrayB(nothingTB()), func);
      assertThat(evaluate(map))
          .isEqualTo(arrayB(tupleTB(list(intTB()))));
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
    public void order_with_element_conversion() throws Exception {
      var order = orderB(arrayTB(intTB()), list(arrayB(nothingTB())));
      assertThat(evaluate(order))
          .isEqualTo(arrayB(arrayTB(intTB()), arrayB(intTB())));
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
    public void select_with_conversion() throws Exception {
      var tuple = tupleB(list(arrayB(nothingTB())));
      var select = selectB(arrayTB(intTB()), tuple, intB(0));
      assertThat(evaluate(select))
          .isEqualTo(arrayB(intTB()));
    }
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
