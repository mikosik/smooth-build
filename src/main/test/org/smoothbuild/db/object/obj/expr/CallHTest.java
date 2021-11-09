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
    assertThat(call(const_(function()), list(stringExpr())).type())
        .isEqualTo(callOT(intOT()));
  }

  @Test
  public void creating_call_with_expr_not_being_function_causes_exception() {
    assertCall(() -> call(intExpr(), list()))
        .throwsException(new IllegalArgumentException(
            "`function` component doesn't evaluate to Function."));
  }

  @Test
  public void creating_call_with_too_few_arguments_causes_exception() {
    assertCall(() -> call(const_(function()), list()))
        .throwsException(argumentsNotMatchingParametersException("{}", "{String}"));
  }

  @Test
  public void creating_call_with_too_many_arguments_causes_exception() {
    assertCall(() -> call(const_(function()), list(intExpr(), intExpr())))
        .throwsException(argumentsNotMatchingParametersException("{Int,Int}", "{String}"));
  }

  @Test
  public void creating_call_with_argument_not_matching_parameter_type_causes_exception() {
    assertCall(() -> call(const_(function()), list(intExpr(3))))
        .throwsException(argumentsNotMatchingParametersException("{Int}", "{String}"));
  }

  private static IllegalArgumentException argumentsNotMatchingParametersException(
      String arguments, String parameters) {
    return new IllegalArgumentException("Arguments evaluation type " + arguments + " should be"
        + " equal to function evaluation type parameters " + parameters + ".");
  }

  @Test
  public void function_returns_function_expr() {
    ConstH function = const_(function());
    assertThat(call(function, list(stringExpr())).data().function())
        .isEqualTo(function);
  }

  @Test
  public void arguments_returns_argument_exprs() {
    var function = const_(function());
    ImmutableList<ExprH> arguments = list(stringExpr()) ;
    assertThat(call(function, arguments).data().arguments())
        .isEqualTo(construct(arguments));
  }

  @Test
  public void call_with_equal_values_are_equal() {
    ConstH function = const_(function());
    ImmutableList<ExprH> arguments = list(stringExpr()) ;
    assertThat(call(function, arguments))
        .isEqualTo(call(function, arguments));
  }

  @Test
  public void call_with_different_functions_are_not_equal() {
    ConstH function1 = const_(function(intExpr(1)));
    ConstH function2 = const_(function(intExpr(2)));
    ImmutableList<ExprH> arguments = list(stringExpr()) ;
    assertThat(call(function1, arguments))
        .isNotEqualTo(call(function2, arguments));
  }

  @Test
  public void call_with_different_arguments_are_not_equal() {
    ConstH function = const_(function());
    assertThat(call(function, list(stringExpr("abc"))))
        .isNotEqualTo(call(function, list(stringExpr("def"))));
  }

  @Test
  public void hash_of_calls_with_equal_values_is_the_same() {
    ConstH function = const_(function());
    ImmutableList<ExprH> arguments = list(stringExpr()) ;
    assertThat(call(function, arguments).hash())
        .isEqualTo(call(function, arguments).hash());
  }

  @Test
  public void hash_of_calls_with_different_function_is_not_the_same() {
    FunctionTypeH type = functionOT(intOT(), list(stringOT()));
    ConstH function1 = const_(function(type, intExpr(1)));
    ConstH function2 = const_(function(type, intExpr(2)));
    ImmutableList<ExprH> arguments = list(stringExpr()) ;
    assertThat(call(function1, arguments).hash())
        .isNotEqualTo(call(function2, arguments).hash());
  }

  @Test
  public void hash_of_calls_with_different_arguments_is_not_the_same() {
    ConstH function = const_(function());
    assertThat(call(function, list(stringExpr("abc"))).hash())
        .isNotEqualTo(call(function, list(stringExpr("def"))).hash());
  }

  @Test
  public void hash_code_of_calls_with_equal_values_is_the_same() {
    ConstH function = const_(function());
    ImmutableList<ExprH> arguments = list(stringExpr()) ;
    assertThat(call(function, arguments).hashCode())
        .isEqualTo(call(function, arguments).hashCode());
  }

  @Test
  public void hash_code_of_calls_with_different_function_is_not_the_same() {
    ConstH function1 = const_(function(intExpr(1)));
    ConstH function2 = const_(function(intExpr(2)));
    ImmutableList<ExprH> arguments = list(stringExpr()) ;
    assertThat(call(function1, arguments).hashCode())
        .isNotEqualTo(call(function2, arguments).hashCode());
  }

  @Test
  public void hash_code_of_calls_with_different_arguments_is_not_the_same() {
    ConstH function = const_(function());
    assertThat(call(function, list(stringExpr("abc"))).hashCode())
        .isNotEqualTo(call(function, list(stringExpr("def"))).hashCode());
  }

  @Test
  public void call_can_be_read_back_by_hash() {
    CallH call = call(const_(function()), list(stringExpr()));
    assertThat(objectDbOther().get(call.hash()))
        .isEqualTo(call);
  }

  @Test
  public void call_read_back_by_hash_has_same_data() {
    ConstH function = const_(function());
    ImmutableList<ExprH> arguments = list(stringExpr());
    CallH call = call(function, arguments);
    assertThat(((CallH) objectDbOther().get(call.hash())).data())
        .isEqualTo(new CallData(function, construct(arguments)));
  }

  @Test
  public void to_string() {
    ConstH function = const_(function());
    CallH call = call(function, list(stringExpr()));
    assertThat(call.toString())
        .isEqualTo("Call(???)@" + call.hash());
  }
}
