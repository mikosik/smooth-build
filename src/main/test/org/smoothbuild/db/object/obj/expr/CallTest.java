package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.expr.Call.CallData;
import org.smoothbuild.db.object.spec.val.LambdaSpec;
import org.smoothbuild.testing.TestingContextImpl;

import com.google.common.collect.ImmutableList;

public class CallTest extends TestingContextImpl {
  @Test
  public void spec_of_call_expr_is_inferred_correctly() {
    assertThat(call(const_(lambda()), list(stringExpr())).spec())
        .isEqualTo(callSpec(intSpec()));
  }

  @Test
  public void creating_call_with_expr_not_being_function_causes_exception() {
    assertCall(() -> call(intExpr(), list()))
        .throwsException(new IllegalArgumentException(
            "`function` component doesn't evaluate to Function."));
  }

  @Test
  public void creating_call_with_too_few_arguments_causes_exception() {
    assertCall(() -> call(const_(lambda()), list()))
        .throwsException(new IllegalArgumentException("Arguments evaluation spec {} should be "
            + "equal to function evaluation spec parameters {String}."));
  }

  @Test
  public void creating_call_with_too_many_arguments_causes_exception() {
    assertCall(() -> call(const_(lambda()), list(intExpr(), intExpr())))
        .throwsException(new IllegalArgumentException("Arguments evaluation spec {Int,Int}"
            + " should be equal to function evaluation spec parameters {String}."));
  }

  @Test
  public void creating_call_with_argument_not_matching_parameter_spec_causes_exception() {
    assertCall(() -> call(const_(lambda()), list(intExpr(3))))
        .throwsException(new IllegalArgumentException("Arguments evaluation spec {Int} should be"
            + " equal to function evaluation spec parameters {String}."));
  }

  @Test
  public void function_returns_function_expr() {
    Const function = const_(lambda());
    assertThat(call(function, list(stringExpr())).data().function())
        .isEqualTo(function);
  }

  @Test
  public void arguments_returns_argument_exprs() {
    var function = const_(lambda());
    var arguments = list(stringExpr()) ;
    assertThat(call(function, arguments).data().arguments())
        .isEqualTo(construct(arguments));
  }

  @Test
  public void call_with_equal_values_are_equal() {
    Const function = const_(lambda());
    var arguments = list(stringExpr()) ;
    assertThat(call(function, arguments))
        .isEqualTo(call(function, arguments));
  }

  @Test
  public void call_with_different_functions_are_not_equal() {
    Const function1 = const_(lambda(intExpr(1)));
    Const function2 = const_(lambda(intExpr(2)));
    var arguments = list(stringExpr()) ;
    assertThat(call(function1, arguments))
        .isNotEqualTo(call(function2, arguments));
  }

  @Test
  public void call_with_different_arguments_are_not_equal() {
    Const function = const_(lambda());
    assertThat(call(function, list(stringExpr("abc"))))
        .isNotEqualTo(call(function, list(stringExpr("def"))));
  }

  @Test
  public void hash_of_calls_with_equal_values_is_the_same() {
    Const function = const_(lambda());
    var arguments = list(stringExpr()) ;
    assertThat(call(function, arguments).hash())
        .isEqualTo(call(function, arguments).hash());
  }

  @Test
  public void hash_of_calls_with_different_function_is_not_the_same() {
    LambdaSpec spec = lambdaSpec(intSpec(), list(stringSpec()));
    Const function1 = const_(lambda(spec, intExpr(1)));
    Const function2 = const_(lambda(spec, intExpr(2)));
    var arguments = list(stringExpr()) ;
    assertThat(call(function1, arguments).hash())
        .isNotEqualTo(call(function2, arguments).hash());
  }

  @Test
  public void hash_of_calls_with_different_arguments_is_not_the_same() {
    Const function = const_(lambda());
    assertThat(call(function, list(stringExpr("abc"))).hash())
        .isNotEqualTo(call(function, list(stringExpr("def"))).hash());
  }

  @Test
  public void hash_code_of_calls_with_equal_values_is_the_same() {
    Const function = const_(lambda());
    var arguments = list(stringExpr()) ;
    assertThat(call(function, arguments).hashCode())
        .isEqualTo(call(function, arguments).hashCode());
  }

  @Test
  public void hash_code_of_calls_with_different_function_is_not_the_same() {
    Const function1 = const_(lambda(intExpr(1)));
    Const function2 = const_(lambda(intExpr(2)));
    var arguments = list(stringExpr()) ;
    assertThat(call(function1, arguments).hashCode())
        .isNotEqualTo(call(function2, arguments).hashCode());
  }

  @Test
  public void hash_code_of_calls_with_different_arguments_is_not_the_same() {
    Const function = const_(lambda());
    assertThat(call(function, list(stringExpr("abc"))).hashCode())
        .isNotEqualTo(call(function, list(stringExpr("def"))).hashCode());
  }

  @Test
  public void call_can_be_read_back_by_hash() {
    Call call = call(const_(lambda()), list(stringExpr()));
    assertThat(objectDbOther().get(call.hash()))
        .isEqualTo(call);
  }

  @Test
  public void call_read_back_by_hash_has_same_data() {
    Const function = const_(lambda());
    ImmutableList<Expr> arguments = list(stringExpr());
    Call call = call(function, arguments);
    assertThat(((Call) objectDbOther().get(call.hash())).data())
        .isEqualTo(new CallData(function, construct(arguments)));
  }

  @Test
  public void to_string() {
    Const function = const_(lambda());
    Call call = call(function, list(stringExpr()));
    assertThat(call.toString())
        .isEqualTo("Call(???)@" + call.hash());
  }
}
