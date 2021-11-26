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
    assertThat(callH(definedFunctionH(), list(stringH())).type())
        .isEqualTo(callHT(intHT()));
  }

  @Test
  public void creating_call_with_expr_not_being_function_causes_exception() {
    assertCall(() -> callH(intH(), list()))
        .throwsException(new IllegalArgumentException(
            "`function` component doesn't evaluate to Function."));
  }

  @Test
  public void creating_call_with_too_few_arguments_causes_exception() {
    assertCall(() -> callH(definedFunctionH(), list()))
        .throwsException(argumentsNotMatchingParametersException("{}", "{String}"));
  }

  @Test
  public void creating_call_with_too_many_arguments_causes_exception() {
    assertCall(() -> callH(definedFunctionH(), list(intH(), intH())))
        .throwsException(argumentsNotMatchingParametersException("{Int,Int}", "{String}"));
  }

  @Test
  public void creating_call_with_argument_not_matching_parameter_type_causes_exception() {
    assertCall(() -> callH(definedFunctionH(), list(intH(3))))
        .throwsException(argumentsNotMatchingParametersException("{Int}", "{String}"));
  }

  private static IllegalArgumentException argumentsNotMatchingParametersException(
      String arguments, String parameters) {
    return new IllegalArgumentException("Arguments evaluation type " + arguments + " should be"
        + " equal to function evaluation type parameters " + parameters + ".");
  }

  @Test
  public void function_returns_function_expr() {
    var function = definedFunctionH();
    assertThat(callH(function, list(stringH())).data().function())
        .isEqualTo(function);
  }

  @Test
  public void arguments_returns_argument_exprs() {
    var function = definedFunctionH();
    ImmutableList<ObjectH> arguments = list(stringH()) ;
    assertThat(callH(function, arguments).data().arguments())
        .isEqualTo(constructH(arguments));
  }

  @Test
  public void call_with_equal_values_are_equal() {
    var function = definedFunctionH();
    ImmutableList<ObjectH> arguments = list(stringH()) ;
    assertThat(callH(function, arguments))
        .isEqualTo(callH(function, arguments));
  }

  @Test
  public void call_with_different_functions_are_not_equal() {
    var function1 = definedFunctionH(intH(1));
    var function2 = definedFunctionH(intH(2));
    ImmutableList<ObjectH> arguments = list(stringH()) ;
    assertThat(callH(function1, arguments))
        .isNotEqualTo(callH(function2, arguments));
  }

  @Test
  public void call_with_different_arguments_are_not_equal() {
    var function = definedFunctionH();
    assertThat(callH(function, list(stringH("abc"))))
        .isNotEqualTo(callH(function, list(stringH("def"))));
  }

  @Test
  public void hash_of_calls_with_equal_values_is_the_same() {
    var function = definedFunctionH();
    ImmutableList<ObjectH> arguments = list(stringH()) ;
    assertThat(callH(function, arguments).hash())
        .isEqualTo(callH(function, arguments).hash());
  }

  @Test
  public void hash_of_calls_with_different_function_is_not_the_same() {
    var type = definedFunctionHT(intHT(), list(stringHT()));
    var function1 = definedFunctionH(type, intH(1));
    var function2 = definedFunctionH(type, intH(2));
    ImmutableList<ObjectH> arguments = list(stringH()) ;
    assertThat(callH(function1, arguments).hash())
        .isNotEqualTo(callH(function2, arguments).hash());
  }

  @Test
  public void hash_of_calls_with_different_arguments_is_not_the_same() {
    var function = definedFunctionH();
    assertThat(callH(function, list(stringH("abc"))).hash())
        .isNotEqualTo(callH(function, list(stringH("def"))).hash());
  }

  @Test
  public void hash_code_of_calls_with_equal_values_is_the_same() {
    var function = definedFunctionH();
    ImmutableList<ObjectH> arguments = list(stringH()) ;
    assertThat(callH(function, arguments).hashCode())
        .isEqualTo(callH(function, arguments).hashCode());
  }

  @Test
  public void hash_code_of_calls_with_different_function_is_not_the_same() {
    var function1 = definedFunctionH(intH(1));
    var function2 = definedFunctionH(intH(2));
    ImmutableList<ObjectH> arguments = list(stringH()) ;
    assertThat(callH(function1, arguments).hashCode())
        .isNotEqualTo(callH(function2, arguments).hashCode());
  }

  @Test
  public void hash_code_of_calls_with_different_arguments_is_not_the_same() {
    var function = definedFunctionH();
    assertThat(callH(function, list(stringH("abc"))).hashCode())
        .isNotEqualTo(callH(function, list(stringH("def"))).hashCode());
  }

  @Test
  public void call_can_be_read_back_by_hash() {
    var call = callH(definedFunctionH(), list(stringH()));
    assertThat(objectHDbOther().get(call.hash()))
        .isEqualTo(call);
  }

  @Test
  public void call_read_back_by_hash_has_same_data() {
    var function = definedFunctionH();
    ImmutableList<ObjectH> arguments = list(stringH());
    var call = callH(function, arguments);
    assertThat(((CallH) objectHDbOther().get(call.hash())).data())
        .isEqualTo(new CallData(function, constructH(arguments)));
  }

  @Test
  public void to_string() {
    var function = definedFunctionH();
    var call = callH(function, list(stringH()));
    assertThat(call.toString())
        .isEqualTo("Call(???)@" + call.hash());
  }
}
