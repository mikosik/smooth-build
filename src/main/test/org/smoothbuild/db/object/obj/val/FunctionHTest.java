package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.expr.ConstH;
import org.smoothbuild.db.object.type.val.FunctionTypeH;
import org.smoothbuild.testing.TestingContext;

public class FunctionHTest extends TestingContext {
  @Test
  public void creating_function_with_body_evaluation_type_not_equal_result_type_causes_exception() {
    FunctionTypeH functionType = functionOT(intOT(), list(stringOT()));
    assertCall(() -> {
      list(stringExpr());
      function(functionType, boolExpr());
    })
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_body_to_null_throws_exception() {
    FunctionTypeH functionType = functionOT(intOT(), list(boolOT()));
    assertCall(() -> {
      list(stringExpr());
      function(functionType, null);
    })
        .throwsException(NullPointerException.class);
  }


  @Test
  public void type_of_function_is_function_type() {
    FunctionTypeH functionType = functionOT(intOT(), list(stringOT()));
    stringExpr();
    assertThat(function(functionType, intExpr()).type())
        .isEqualTo(functionType);
  }

  @Test
  public void body_contains_object_passed_during_construction() {
    FunctionTypeH functionType = functionOT(intOT(), list(boolOT()));
    ConstH body = intExpr(33);
    assertThat(function(functionType, body).body())
        .isEqualTo(body);
  }

  @Test
  public void functions_with_equal_body_and_default_arguments_are_equal() {
    FunctionTypeH functionType = functionOT(intOT(), list(stringOT()));
    stringExpr();
    FunctionH function1 = function(functionType, intExpr());
    stringExpr();
    FunctionH function2 = function(functionType, intExpr());
    assertThat(function1)
        .isEqualTo(function2);
  }

  @Test
  public void functions_with_different_body_are_not_equal() {
    FunctionTypeH functionType = functionOT(intOT(), list(stringOT()));
    stringExpr();
    FunctionH function1 = function(functionType, intExpr(1));
    stringExpr();
    FunctionH function2 = function(functionType, intExpr(2));
    assertThat(function1)
        .isNotEqualTo(function2);
  }

  @Test
  public void functions_with_equal_body_and_default_arguments_have_equal_hashes() {
    FunctionTypeH functionType = functionOT(intOT(), list(intOT()));
    intExpr();
    FunctionH function1 = function(functionType, intExpr());
    intExpr();
    FunctionH function2 = function(functionType, intExpr());
    assertThat(function1.hash())
        .isEqualTo(function2.hash());
  }

  @Test
  public void functions_with_different_bodies_have_not_equal_hashes() {
    FunctionTypeH functionType = functionOT(intOT(), list(stringOT()));
    stringExpr();
    FunctionH function1 = function(functionType, intExpr(1));
    stringExpr();
    FunctionH function2 = function(functionType, intExpr(2));
    assertThat(function1.hash())
        .isNotEqualTo(function2.hash());
  }

  @Test
  public void functions_with_equal_body_and_default_arguments_have_equal_hash_code() {
    FunctionTypeH functionType = functionOT(intOT(), list(stringOT()));
    stringExpr();
    FunctionH function1 = function(functionType, intExpr());
    stringExpr();
    FunctionH function2 = function(functionType, intExpr());
    assertThat(function1.hashCode())
        .isEqualTo(function2.hashCode());
  }

  @Test
  public void functions_with_different_bodies_have_not_equal_hash_code() {
    FunctionTypeH functionType = functionOT(intOT(), list(intOT()));
    intExpr();
    FunctionH function1 = function(functionType, intExpr(1));
    intExpr();
    FunctionH function2 = function(functionType, intExpr(2));
    assertThat(function1.hashCode())
        .isNotEqualTo(function2.hashCode());
  }

  @Test
  public void function_can_be_read_by_hash() {
    FunctionTypeH functionType = functionOT(intOT(), list(stringOT()));
    stringExpr();
    FunctionH function = function(functionType, intExpr());
    assertThat(objectDbOther().get(function.hash()))
        .isEqualTo(function);
  }

  @Test
  public void functions_read_by_hash_have_equal_bodies() {
    FunctionTypeH functionType = functionOT(intOT(), list(stringOT()));
    stringExpr();
    FunctionH function = function(functionType, intExpr());
    FunctionH functionRead = (FunctionH) objectDbOther().get(function.hash());
    assertThat(function.body())
        .isEqualTo(functionRead.body());
  }

  @Test
  public void to_string() {
    FunctionTypeH functionType = functionOT(intOT(), list(stringOT()));
    stringExpr();
    FunctionH function = function(functionType, intExpr());
    assertThat(function.toString())
        .isEqualTo("Lambda(Int(String))@" + function.hash());
  }
}
