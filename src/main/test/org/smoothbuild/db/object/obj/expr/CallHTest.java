package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.expr.CallH.CallData;
import org.smoothbuild.db.object.type.val.FunctionTypeH;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class CallHTest extends TestingContext {
  @Test
  public void type_of_call_expr_is_inferred_correctly() {
    assertThat(callH(constH(functionH()), list(stringHE())).type())
        .isEqualTo(callHT(intHT()));
  }

  @Test
  public void creating_call_with_expr_not_being_function_causes_exception() {
    assertCall(() -> callH(intHE(), list()))
        .throwsException(new IllegalArgumentException(
            "`function` component doesn't evaluate to Function."));
  }

  @Test
  public void creating_call_with_too_few_arguments_causes_exception() {
    assertCall(() -> callH(constH(functionH()), list()))
        .throwsException(argumentsNotMatchingParametersException("{}", "{String}"));
  }

  @Test
  public void creating_call_with_too_many_arguments_causes_exception() {
    assertCall(() -> callH(constH(functionH()), list(intHE(), intHE())))
        .throwsException(argumentsNotMatchingParametersException("{Int,Int}", "{String}"));
  }

  @Test
  public void creating_call_with_argument_not_matching_parameter_type_causes_exception() {
    assertCall(() -> callH(constH(functionH()), list(intHE(3))))
        .throwsException(argumentsNotMatchingParametersException("{Int}", "{String}"));
  }

  private static IllegalArgumentException argumentsNotMatchingParametersException(
      String arguments, String parameters) {
    return new IllegalArgumentException("Arguments evaluation type " + arguments + " should be"
        + " equal to function evaluation type parameters " + parameters + ".");
  }

  @Test
  public void function_returns_function_expr() {
    ConstH function = constH(functionH());
    assertThat(callH(function, list(stringHE())).data().function())
        .isEqualTo(function);
  }

  @Test
  public void arguments_returns_argument_exprs() {
    var function = constH(functionH());
    ImmutableList<ExprH> arguments = list(stringHE()) ;
    assertThat(callH(function, arguments).data().arguments())
        .isEqualTo(constructH(arguments));
  }

  @Test
  public void call_with_equal_values_are_equal() {
    ConstH function = constH(functionH());
    ImmutableList<ExprH> arguments = list(stringHE()) ;
    assertThat(callH(function, arguments))
        .isEqualTo(callH(function, arguments));
  }

  @Test
  public void call_with_different_functions_are_not_equal() {
    ConstH function1 = constH(functionH(intHE(1)));
    ConstH function2 = constH(functionH(intHE(2)));
    ImmutableList<ExprH> arguments = list(stringHE()) ;
    assertThat(callH(function1, arguments))
        .isNotEqualTo(callH(function2, arguments));
  }

  @Test
  public void call_with_different_arguments_are_not_equal() {
    ConstH function = constH(functionH());
    assertThat(callH(function, list(stringHE("abc"))))
        .isNotEqualTo(callH(function, list(stringHE("def"))));
  }

  @Test
  public void hash_of_calls_with_equal_values_is_the_same() {
    ConstH function = constH(functionH());
    ImmutableList<ExprH> arguments = list(stringHE()) ;
    assertThat(callH(function, arguments).hash())
        .isEqualTo(callH(function, arguments).hash());
  }

  @Test
  public void hash_of_calls_with_different_function_is_not_the_same() {
    FunctionTypeH type = functionHT(intHT(), list(stringHT()));
    ConstH function1 = constH(functionH(type, intHE(1)));
    ConstH function2 = constH(functionH(type, intHE(2)));
    ImmutableList<ExprH> arguments = list(stringHE()) ;
    assertThat(callH(function1, arguments).hash())
        .isNotEqualTo(callH(function2, arguments).hash());
  }

  @Test
  public void hash_of_calls_with_different_arguments_is_not_the_same() {
    ConstH function = constH(functionH());
    assertThat(callH(function, list(stringHE("abc"))).hash())
        .isNotEqualTo(callH(function, list(stringHE("def"))).hash());
  }

  @Test
  public void hash_code_of_calls_with_equal_values_is_the_same() {
    ConstH function = constH(functionH());
    ImmutableList<ExprH> arguments = list(stringHE()) ;
    assertThat(callH(function, arguments).hashCode())
        .isEqualTo(callH(function, arguments).hashCode());
  }

  @Test
  public void hash_code_of_calls_with_different_function_is_not_the_same() {
    ConstH function1 = constH(functionH(intHE(1)));
    ConstH function2 = constH(functionH(intHE(2)));
    ImmutableList<ExprH> arguments = list(stringHE()) ;
    assertThat(callH(function1, arguments).hashCode())
        .isNotEqualTo(callH(function2, arguments).hashCode());
  }

  @Test
  public void hash_code_of_calls_with_different_arguments_is_not_the_same() {
    ConstH function = constH(functionH());
    assertThat(callH(function, list(stringHE("abc"))).hashCode())
        .isNotEqualTo(callH(function, list(stringHE("def"))).hashCode());
  }

  @Test
  public void call_can_be_read_back_by_hash() {
    CallH call = callH(constH(functionH()), list(stringHE()));
    assertThat(objectHDbOther().get(call.hash()))
        .isEqualTo(call);
  }

  @Test
  public void call_read_back_by_hash_has_same_data() {
    ConstH function = constH(functionH());
    ImmutableList<ExprH> arguments = list(stringHE());
    CallH call = callH(function, arguments);
    assertThat(((CallH) objectHDbOther().get(call.hash())).data())
        .isEqualTo(new CallData(function, constructH(arguments)));
  }

  @Test
  public void to_string() {
    ConstH function = constH(functionH());
    CallH call = callH(function, list(stringHE()));
    assertThat(call.toString())
        .isEqualTo("Call(???)@" + call.hash());
  }
}
