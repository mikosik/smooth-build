package org.smoothbuild.db.object.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class CallTest extends TestingContext {
  @Test
  public void spec_of_call_expr_is_call_expr() {
    assertThat(callE(constE(), list()).spec())
        .isEqualTo(callS());
  }

  @Test
  public void function_returns_function_expr() {
    Const function = constE();
    assertThat(callE(function, list()).function())
        .isEqualTo(function);
  }

  @Test
  public void arguments_returns_argument_exprs() {
    Const function = constE(intV(1));
    List<Const> arguments = list(constE(intV(2))) ;
    assertThat(callE(function, arguments).arguments())
        .isEqualTo(arguments);
  }

  @Test
  public void call_with_equal_values_are_equal() {
    Const function = constE(intV(1));
    List<Const> arguments = list(constE(intV(2))) ;
    assertThat(callE(function, arguments))
        .isEqualTo(callE(function, arguments));
  }

  @Test
  public void call_with_different_functions_are_not_equal() {
    List<Const> arguments = list(constE(intV(2))) ;
    assertThat(callE(constE(intV(1)), arguments))
        .isNotEqualTo(callE(constE(intV(2)), arguments));
  }

  @Test
  public void call_with_different_arguments_are_not_equal() {
    Const function = constE(intV(1));
    assertThat(callE(function, list(constE(intV(1)))))
        .isNotEqualTo(callE(function, list(constE(intV(2)))));
  }

  @Test
  public void hash_of_calls_with_equal_values_is_the_same() {
    Const function = constE(intV(1));
    List<Const> arguments = list(constE(intV(2))) ;
    assertThat(callE(function, arguments).hash())
        .isEqualTo(callE(function, arguments).hash());
  }

  @Test
  public void hash_of_calls_with_different_function_is_not_the_same() {
    List<Const> arguments = list(constE(intV(2))) ;
    assertThat(callE(constE(intV(1)), arguments).hash())
        .isNotEqualTo(callE(constE(intV(2)), arguments).hash());
  }

  @Test
  public void hash_of_calls_with_different_arguments_is_not_the_same() {
    Const function = constE(intV(1));
    assertThat(callE(function, list(constE(intV(1)))).hash())
        .isNotEqualTo(callE(function, list(constE(intV(2)))).hash());
  }

  @Test
  public void hash_code_of_calls_with_equal_values_is_the_same() {
    Const function = constE(intV(1));
    List<Const> arguments = list(constE(intV(2))) ;
    assertThat(callE(function, arguments).hashCode())
        .isEqualTo(callE(function, arguments).hashCode());
  }

  @Test
  public void hash_code_of_calls_with_different_function_is_not_the_same() {
    List<Const> arguments = list(constE(intV(2))) ;
    assertThat(callE(constE(intV(1)), arguments).hashCode())
        .isNotEqualTo(callE(constE(intV(2)), arguments).hashCode());
  }

  @Test
  public void hash_code_of_calls_with_different_arguments_is_not_the_same() {
    Const function = constE(intV(1));
    assertThat(callE(function, list(constE(intV(1)))).hashCode())
        .isNotEqualTo(callE(function, list(constE(intV(2)))).hashCode());
  }

  @Test
  public void call_can_be_read_back_by_hash() {
    Call call = callE(constE(intV(1)), list(constE(intV(1))));
    assertThat(objectDbOther().get(call.hash()))
        .isEqualTo(call);
  }

  @Test
  public void call_read_back_by_hash_has_same_tuple() {
    Const function = constE(intV(1));
    Call call = callE(function, list(constE(intV(1))));
    assertThat(((Call) objectDbOther().get(call.hash())).function())
        .isEqualTo(function);
  }

  @Test
  public void call_read_back_by_hash_has_same_arguments() {
    ImmutableList<Const> arguments = list(constE(intV(1)));
    Call call = callE(constE(intV(1)), arguments);
    assertThat(((Call) objectDbOther().get(call.hash())).arguments())
        .isEqualTo(arguments);
  }

  @Test
  public void to_string() {
    Call call = callE(constE(intV(1)), list(constE(intV(1))));
    assertThat(call.toString())
        .isEqualTo("Call(???):" + call.hash());
  }
}
