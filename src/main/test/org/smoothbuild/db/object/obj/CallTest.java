package org.smoothbuild.db.object.obj;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class CallTest extends TestingContext {
  @Test
  public void spec_of_call_expr_is_call_spec() {
    assertThat(callExpr(constExpr(), list()).spec())
        .isEqualTo(callSpec());
  }

  @Test
  public void function_returns_function_expr() {
    Const function = constExpr();
    assertThat(callExpr(function, list()).function())
        .isEqualTo(function);
  }

  @Test
  public void arguments_returns_argument_exprs() {
    Const function = constExpr(intVal(1));
    List<Const> arguments = list(constExpr(intVal(2))) ;
    assertThat(callExpr(function, arguments).arguments())
        .isEqualTo(arguments);
  }

  @Test
  public void call_with_equal_values_are_equal() {
    Const function = constExpr(intVal(1));
    List<Const> arguments = list(constExpr(intVal(2))) ;
    assertThat(callExpr(function, arguments))
        .isEqualTo(callExpr(function, arguments));
  }

  @Test
  public void call_with_different_functions_are_not_equal() {
    List<Const> arguments = list(constExpr(intVal(2))) ;
    assertThat(callExpr(constExpr(intVal(1)), arguments))
        .isNotEqualTo(callExpr(constExpr(intVal(2)), arguments));
  }

  @Test
  public void call_with_different_arguments_are_not_equal() {
    Const function = constExpr(intVal(1));
    assertThat(callExpr(function, list(constExpr(intVal(1)))))
        .isNotEqualTo(callExpr(function, list(constExpr(intVal(2)))));
  }

  @Test
  public void hash_of_calls_with_equal_values_is_the_same() {
    Const function = constExpr(intVal(1));
    List<Const> arguments = list(constExpr(intVal(2))) ;
    assertThat(callExpr(function, arguments).hash())
        .isEqualTo(callExpr(function, arguments).hash());
  }

  @Test
  public void hash_of_calls_with_different_function_is_not_the_same() {
    List<Const> arguments = list(constExpr(intVal(2))) ;
    assertThat(callExpr(constExpr(intVal(1)), arguments).hash())
        .isNotEqualTo(callExpr(constExpr(intVal(2)), arguments).hash());
  }

  @Test
  public void hash_of_calls_with_different_arguments_is_not_the_same() {
    Const function = constExpr(intVal(1));
    assertThat(callExpr(function, list(constExpr(intVal(1)))).hash())
        .isNotEqualTo(callExpr(function, list(constExpr(intVal(2)))).hash());
  }

  @Test
  public void hash_code_of_calls_with_equal_values_is_the_same() {
    Const function = constExpr(intVal(1));
    List<Const> arguments = list(constExpr(intVal(2))) ;
    assertThat(callExpr(function, arguments).hashCode())
        .isEqualTo(callExpr(function, arguments).hashCode());
  }

  @Test
  public void hash_code_of_calls_with_different_function_is_not_the_same() {
    List<Const> arguments = list(constExpr(intVal(2))) ;
    assertThat(callExpr(constExpr(intVal(1)), arguments).hashCode())
        .isNotEqualTo(callExpr(constExpr(intVal(2)), arguments).hashCode());
  }

  @Test
  public void hash_code_of_calls_with_different_arguments_is_not_the_same() {
    Const function = constExpr(intVal(1));
    assertThat(callExpr(function, list(constExpr(intVal(1)))).hashCode())
        .isNotEqualTo(callExpr(function, list(constExpr(intVal(2)))).hashCode());
  }

  @Test
  public void call_can_be_read_back_by_hash() {
    Call call = callExpr(constExpr(intVal(1)), list(constExpr(intVal(1))));
    assertThat(objectDbOther().get(call.hash()))
        .isEqualTo(call);
  }

  @Test
  public void call_read_back_by_hash_has_same_rec() {
    Const function = constExpr(intVal(1));
    Call call = callExpr(function, list(constExpr(intVal(1))));
    assertThat(((Call) objectDbOther().get(call.hash())).function())
        .isEqualTo(function);
  }

  @Test
  public void call_read_back_by_hash_has_same_arguments() {
    ImmutableList<Const> arguments = list(constExpr(intVal(1)));
    Call call = callExpr(constExpr(intVal(1)), arguments);
    assertThat(((Call) objectDbOther().get(call.hash())).arguments())
        .isEqualTo(arguments);
  }

  @Test
  public void to_string() {
    Call call = callExpr(constExpr(intVal(1)), list(constExpr(intVal(1))));
    assertThat(call.toString())
        .isEqualTo("Call(???):" + call.hash());
  }
}
