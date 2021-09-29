package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.expr.Call.CallData;
import org.smoothbuild.db.object.spec.val.DefinedLambdaSpec;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class CallTest extends TestingContext {
  @Test
  public void spec_of_call_expr_is_inferred_correctly() {
    assertThat(callExpr(constExpr(definedLambdaVal()), list(strExpr())).spec())
        .isEqualTo(callSpec(intSpec()));
  }

  @Test
  public void creating_call_with_expr_not_being_function_causes_exception() {
    assertCall(() -> callExpr(intExpr(), list()))
        .throwsException(new IllegalArgumentException(
            "`function` component doesn't evaluate to Function."));
  }

  @Test
  public void creating_call_with_too_few_arguments_causes_exception() {
    assertCall(() -> callExpr(constExpr(definedLambdaVal()), list()))
        .throwsException(new IllegalArgumentException("Arguments evaluation spec {} should be "
            + "equal to function evaluation spec parameters {STRING}."));
  }

  @Test
  public void creating_call_with_too_many_arguments_causes_exception() {
    assertCall(() -> callExpr(constExpr(definedLambdaVal()), list(intExpr(), intExpr())))
        .throwsException(new IllegalArgumentException("Arguments evaluation spec {INT,INT}"
            + " should be equal to function evaluation spec parameters {STRING}."));
  }

  @Test
  public void creating_call_with_argument_not_matching_parameter_spec_causes_exception() {
    assertCall(() -> callExpr(constExpr(definedLambdaVal()), list(intExpr(3))))
        .throwsException(new IllegalArgumentException("Arguments evaluation spec {INT} should be"
            + " equal to function evaluation spec parameters {STRING}."));
  }

  @Test
  public void function_returns_function_expr() {
    Const function = constExpr(definedLambdaVal());
    assertThat(callExpr(function, list(strExpr())).data().function())
        .isEqualTo(function);
  }

  @Test
  public void arguments_returns_argument_exprs() {
    Const function = constExpr(definedLambdaVal());
    List<Const> arguments = list(strExpr()) ;
    assertThat(callExpr(function, arguments).data().arguments())
        .isEqualTo(eRecExpr(arguments));
  }

  @Test
  public void call_with_equal_values_are_equal() {
    Const function = constExpr(definedLambdaVal());
    List<Const> arguments = list(strExpr()) ;
    assertThat(callExpr(function, arguments))
        .isEqualTo(callExpr(function, arguments));
  }

  @Test
  public void call_with_different_functions_are_not_equal() {
    Const function1 = constExpr(definedLambdaVal(intExpr(1)));
    Const function2 = constExpr(definedLambdaVal(intExpr(2)));
    List<Const> arguments = list(strExpr()) ;
    assertThat(callExpr(function1, arguments))
        .isNotEqualTo(callExpr(function2, arguments));
  }

  @Test
  public void call_with_different_arguments_are_not_equal() {
    Const function = constExpr(definedLambdaVal());
    assertThat(callExpr(function, list(strExpr("abc"))))
        .isNotEqualTo(callExpr(function, list(strExpr("def"))));
  }

  @Test
  public void hash_of_calls_with_equal_values_is_the_same() {
    Const function = constExpr(definedLambdaVal());
    List<Const> arguments = list(strExpr()) ;
    assertThat(callExpr(function, arguments).hash())
        .isEqualTo(callExpr(function, arguments).hash());
  }

  @Test
  public void hash_of_calls_with_different_function_is_not_the_same() {
    DefinedLambdaSpec spec = definedLambdaSpec(intSpec(), strSpec());
    Const function1 = constExpr(definedLambdaVal(spec, intExpr(1), list(strExpr())));
    Const function2 = constExpr(definedLambdaVal(spec, intExpr(2), list(strExpr())));
    List<Const> arguments = list(strExpr()) ;
    assertThat(callExpr(function1, arguments).hash())
        .isNotEqualTo(callExpr(function2, arguments).hash());
  }

  @Test
  public void hash_of_calls_with_different_arguments_is_not_the_same() {
    Const function = constExpr(definedLambdaVal());
    assertThat(callExpr(function, list(strExpr("abc"))).hash())
        .isNotEqualTo(callExpr(function, list(strExpr("def"))).hash());
  }

  @Test
  public void hash_code_of_calls_with_equal_values_is_the_same() {
    Const function = constExpr(definedLambdaVal());
    List<Const> arguments = list(strExpr()) ;
    assertThat(callExpr(function, arguments).hashCode())
        .isEqualTo(callExpr(function, arguments).hashCode());
  }

  @Test
  public void hash_code_of_calls_with_different_function_is_not_the_same() {
    Const function1 = constExpr(definedLambdaVal(intExpr(1)));
    Const function2 = constExpr(definedLambdaVal(intExpr(2)));
    List<Const> arguments = list(strExpr()) ;
    assertThat(callExpr(function1, arguments).hashCode())
        .isNotEqualTo(callExpr(function2, arguments).hashCode());
  }

  @Test
  public void hash_code_of_calls_with_different_arguments_is_not_the_same() {
    Const function = constExpr(definedLambdaVal());
    assertThat(callExpr(function, list(strExpr("abc"))).hashCode())
        .isNotEqualTo(callExpr(function, list(strExpr("def"))).hashCode());
  }

  @Test
  public void call_can_be_read_back_by_hash() {
    Call call = callExpr(constExpr(definedLambdaVal()), list(strExpr()));
    assertThat(objectDbOther().get(call.hash()))
        .isEqualTo(call);
  }

  @Test
  public void call_read_back_by_hash_has_same_data() {
    Const function = constExpr(definedLambdaVal());
    ImmutableList<Expr> arguments = list(strExpr());
    Call call = callExpr(function, arguments);
    assertThat(((Call) objectDbOther().get(call.hash())).data())
        .isEqualTo(new CallData(function, eRecExpr(arguments)));
  }

  @Test
  public void to_string() {
    Const function = constExpr(definedLambdaVal());
    Call call = callExpr(function, list(strExpr()));
    assertThat(call.toString())
        .isEqualTo("Call(???):" + call.hash());
  }
}
