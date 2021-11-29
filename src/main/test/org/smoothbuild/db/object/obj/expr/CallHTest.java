package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.obj.expr.CallH.CallData;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class CallHTest extends TestingContext {
  @Test
  public void type_of_call_expr_is_inferred_correctly() {
    assertThat(callH(defFuncH(), list(stringH())).spec())
        .isEqualTo(callHT(intHT()));
  }

  @Test
  public void creating_call_with_expr_not_being_func_causes_exception() {
    assertCall(() -> callH(intH(), list()))
        .throwsException(new IllegalArgumentException(
            "`func` component doesn't evaluate to function."));
  }

  @Test
  public void creating_call_with_too_few_arguments_causes_exception() {
    assertCall(() -> callH(defFuncH(), list()))
        .throwsException(argumentsNotMatchingParamsException("{}", "{String}"));
  }

  @Test
  public void creating_call_with_too_many_arguments_causes_exception() {
    assertCall(() -> callH(defFuncH(), list(intH(), intH())))
        .throwsException(argumentsNotMatchingParamsException("{Int,Int}", "{String}"));
  }

  @Test
  public void creating_call_with_argument_not_matching_param_type_causes_exception() {
    assertCall(() -> callH(defFuncH(), list(intH(3))))
        .throwsException(argumentsNotMatchingParamsException("{Int}", "{String}"));
  }

  private static IllegalArgumentException argumentsNotMatchingParamsException(
      String arguments, String params) {
    return new IllegalArgumentException("Arguments evaluation type " + arguments + " should be"
        + " equal to function evaluation type parameters " + params + ".");
  }

  @Test
  public void func_returns_func_expr() {
    var func = defFuncH();
    assertThat(callH(func, list(stringH())).data().func())
        .isEqualTo(func);
  }

  @Test
  public void arguments_returns_argument_exprs() {
    var func = defFuncH();
    ImmutableList<ObjectH> arguments = list(stringH()) ;
    assertThat(callH(func, arguments).data().arguments())
        .isEqualTo(constructH(arguments));
  }

  @Test
  public void call_with_equal_values_are_equal() {
    var func = defFuncH();
    ImmutableList<ObjectH> arguments = list(stringH()) ;
    assertThat(callH(func, arguments))
        .isEqualTo(callH(func, arguments));
  }

  @Test
  public void call_with_different_funcs_are_not_equal() {
    var func1 = defFuncH(intH(1));
    var func2 = defFuncH(intH(2));
    ImmutableList<ObjectH> arguments = list(stringH()) ;
    assertThat(callH(func1, arguments))
        .isNotEqualTo(callH(func2, arguments));
  }

  @Test
  public void call_with_different_arguments_are_not_equal() {
    var func = defFuncH();
    assertThat(callH(func, list(stringH("abc"))))
        .isNotEqualTo(callH(func, list(stringH("def"))));
  }

  @Test
  public void hash_of_calls_with_equal_values_is_the_same() {
    var func = defFuncH();
    ImmutableList<ObjectH> arguments = list(stringH()) ;
    assertThat(callH(func, arguments).hash())
        .isEqualTo(callH(func, arguments).hash());
  }

  @Test
  public void hash_of_calls_with_different_func_is_not_the_same() {
    var type = defFuncHT(intHT(), list(stringHT()));
    var func1 = defFuncH(type, intH(1));
    var func2 = defFuncH(type, intH(2));
    ImmutableList<ObjectH> arguments = list(stringH()) ;
    assertThat(callH(func1, arguments).hash())
        .isNotEqualTo(callH(func2, arguments).hash());
  }

  @Test
  public void hash_of_calls_with_different_arguments_is_not_the_same() {
    var func = defFuncH();
    assertThat(callH(func, list(stringH("abc"))).hash())
        .isNotEqualTo(callH(func, list(stringH("def"))).hash());
  }

  @Test
  public void hash_code_of_calls_with_equal_values_is_the_same() {
    var func = defFuncH();
    ImmutableList<ObjectH> arguments = list(stringH()) ;
    assertThat(callH(func, arguments).hashCode())
        .isEqualTo(callH(func, arguments).hashCode());
  }

  @Test
  public void hash_code_of_calls_with_different_func_is_not_the_same() {
    var func1 = defFuncH(intH(1));
    var func2 = defFuncH(intH(2));
    ImmutableList<ObjectH> arguments = list(stringH()) ;
    assertThat(callH(func1, arguments).hashCode())
        .isNotEqualTo(callH(func2, arguments).hashCode());
  }

  @Test
  public void hash_code_of_calls_with_different_arguments_is_not_the_same() {
    var func = defFuncH();
    assertThat(callH(func, list(stringH("abc"))).hashCode())
        .isNotEqualTo(callH(func, list(stringH("def"))).hashCode());
  }

  @Test
  public void call_can_be_read_back_by_hash() {
    var call = callH(defFuncH(), list(stringH()));
    assertThat(objectHDbOther().get(call.hash()))
        .isEqualTo(call);
  }

  @Test
  public void call_read_back_by_hash_has_same_data() {
    var func = defFuncH();
    ImmutableList<ObjectH> arguments = list(stringH());
    var call = callH(func, arguments);
    assertThat(((CallH) objectHDbOther().get(call.hash())).data())
        .isEqualTo(new CallData(func, constructH(arguments)));
  }

  @Test
  public void to_string() {
    var func = defFuncH();
    var call = callH(func, list(stringH()));
    assertThat(call.toString())
        .isEqualTo("Call(???)@" + call.hash());
  }
}
