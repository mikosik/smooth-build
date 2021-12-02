package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.expr.CallH.CallData;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class CallHTest extends TestingContext {
  @Test
  public void type_of_call_expr_is_inferred_correctly() {
    assertThat(callH(defFuncH(list(stringHT()), intH()), list(stringH())).spec())
        .isEqualTo(callHT(intHT()));
  }

  @Test
  public void creating_call_with_expr_not_being_func_causes_exception() {
    assertCall(() -> callH(intH(), list()))
        .throwsException(new IllegalArgumentException(
            "`func` component doesn't evaluate to function."));
  }

  @Test
  public void creating_call_with_too_few_args_causes_exception() {
    assertCall(() -> callH(defFuncH(list(stringHT()), intH()), list()))
        .throwsException(argsNotMatchingParamsException("{}", "{String}"));
  }

  @Test
  public void creating_call_with_too_many_args_causes_exception() {
    assertCall(() -> callH(defFuncH(list(stringHT()), intH()), list(intH(), intH())))
        .throwsException(argsNotMatchingParamsException("{Int,Int}", "{String}"));
  }

  @Test
  public void creating_call_with_arg_not_matching_param_type_causes_exception() {
    assertCall(() -> callH(defFuncH(list(stringHT()), intH()), list(intH(3))))
        .throwsException(argsNotMatchingParamsException("{Int}", "{String}"));
  }

  private static IllegalArgumentException argsNotMatchingParamsException(
      String args, String params) {
    return new IllegalArgumentException("Arguments evaluation type " + args + " should be"
        + " equal to function evaluation type parameters " + params + ".");
  }

  @Test
  public void func_returns_func_expr() {
    var func = defFuncH(list(stringHT()), intH());
    assertThat(callH(func, list(stringH())).data().func())
        .isEqualTo(func);
  }

  @Test
  public void args_returns_arg_exprs() {
    var func = defFuncH(list(stringHT()), intH());
    ImmutableList<ObjH> args = list(stringH()) ;
    assertThat(callH(func, args).data().args())
        .isEqualTo(combineH(args));
  }

  @Test
  public void call_with_equal_values_are_equal() {
    var func = defFuncH(list(stringHT()), intH());
    ImmutableList<ObjH> args = list(stringH()) ;
    assertThat(callH(func, args))
        .isEqualTo(callH(func, args));
  }

  @Test
  public void call_with_different_funcs_are_not_equal() {
    var func1 = defFuncH(list(stringHT()), intH(1));
    var func2 = defFuncH(list(stringHT()), intH(2));
    ImmutableList<ObjH> args = list(stringH()) ;
    assertThat(callH(func1, args))
        .isNotEqualTo(callH(func2, args));
  }

  @Test
  public void call_with_different_args_are_not_equal() {
    var func = defFuncH(list(stringHT()), intH());
    assertThat(callH(func, list(stringH("abc"))))
        .isNotEqualTo(callH(func, list(stringH("def"))));
  }

  @Test
  public void hash_of_calls_with_equal_values_is_the_same() {
    var func = defFuncH(list(stringHT()), intH());
    ImmutableList<ObjH> args = list(stringH()) ;
    assertThat(callH(func, args).hash())
        .isEqualTo(callH(func, args).hash());
  }

  @Test
  public void hash_of_calls_with_different_func_is_not_the_same() {
    var type = defFuncHT(intHT(), list(stringHT()));
    var func1 = defFuncH(type, intH(1));
    var func2 = defFuncH(type, intH(2));
    ImmutableList<ObjH> args = list(stringH()) ;
    assertThat(callH(func1, args).hash())
        .isNotEqualTo(callH(func2, args).hash());
  }

  @Test
  public void hash_of_calls_with_different_args_is_not_the_same() {
    var func = defFuncH(list(stringHT()), intH());
    assertThat(callH(func, list(stringH("abc"))).hash())
        .isNotEqualTo(callH(func, list(stringH("def"))).hash());
  }

  @Test
  public void hash_code_of_calls_with_equal_values_is_the_same() {
    var func = defFuncH(list(stringHT()), intH());
    ImmutableList<ObjH> args = list(stringH()) ;
    assertThat(callH(func, args).hashCode())
        .isEqualTo(callH(func, args).hashCode());
  }

  @Test
  public void hash_code_of_calls_with_different_func_is_not_the_same() {
    var func1 = defFuncH(list(stringHT()), intH(1));
    var func2 = defFuncH(list(stringHT()), intH(2));
    ImmutableList<ObjH> args = list(stringH());
    assertThat(callH(func1, args).hashCode())
        .isNotEqualTo(callH(func2, args).hashCode());
  }

  @Test
  public void hash_code_of_calls_with_different_args_is_not_the_same() {
    var func = defFuncH(list(stringHT()), intH());
    assertThat(callH(func, list(stringH("abc"))).hashCode())
        .isNotEqualTo(callH(func, list(stringH("def"))).hashCode());
  }

  @Test
  public void call_can_be_read_back_by_hash() {
    var call = callH(defFuncH(list(stringHT()), intH()), list(stringH()));
    assertThat(objDbOther().get(call.hash()))
        .isEqualTo(call);
  }

  @Test
  public void call_read_back_by_hash_has_same_data() {
    var func = defFuncH(list(stringHT()), intH());
    ImmutableList<ObjH> args = list(stringH());
    var call = callH(func, args);
    assertThat(((CallH) objDbOther().get(call.hash())).data())
        .isEqualTo(new CallData(func, combineH(args)));
  }

  @Test
  public void to_string() {
    var func = defFuncH(list(stringHT()), intH());
    var call = callH(func, list(stringH()));
    assertThat(call.toString())
        .isEqualTo("Call:Int(???)@" + call.hash());
  }
}
