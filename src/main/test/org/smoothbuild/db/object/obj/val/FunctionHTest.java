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
    FunctionTypeH functionType = functionHT(intHT(), list(stringHT()));
    assertCall(() -> {
      list(stringHE());
      functionH(functionType, boolHE());
    })
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_body_to_null_throws_exception() {
    FunctionTypeH functionType = functionHT(intHT(), list(boolHT()));
    assertCall(() -> {
      list(stringHE());
      functionH(functionType, null);
    })
        .throwsException(NullPointerException.class);
  }


  @Test
  public void type_of_function_is_function_type() {
    FunctionTypeH functionType = functionHT(intHT(), list(stringHT()));
    stringHE();
    assertThat(functionH(functionType, intHE()).type())
        .isEqualTo(functionType);
  }

  @Test
  public void body_contains_object_passed_during_construction() {
    FunctionTypeH functionType = functionHT(intHT(), list(boolHT()));
    ConstH body = intHE(33);
    assertThat(functionH(functionType, body).body())
        .isEqualTo(body);
  }

  @Test
  public void functions_with_equal_body_and_default_arguments_are_equal() {
    FunctionTypeH functionType = functionHT(intHT(), list(stringHT()));
    stringHE();
    FunctionH function1 = functionH(functionType, intHE());
    stringHE();
    FunctionH function2 = functionH(functionType, intHE());
    assertThat(function1)
        .isEqualTo(function2);
  }

  @Test
  public void functions_with_different_body_are_not_equal() {
    FunctionTypeH functionType = functionHT(intHT(), list(stringHT()));
    stringHE();
    FunctionH function1 = functionH(functionType, intHE(1));
    stringHE();
    FunctionH function2 = functionH(functionType, intHE(2));
    assertThat(function1)
        .isNotEqualTo(function2);
  }

  @Test
  public void functions_with_equal_body_and_default_arguments_have_equal_hashes() {
    FunctionTypeH functionType = functionHT(intHT(), list(intHT()));
    intHE();
    FunctionH function1 = functionH(functionType, intHE());
    intHE();
    FunctionH function2 = functionH(functionType, intHE());
    assertThat(function1.hash())
        .isEqualTo(function2.hash());
  }

  @Test
  public void functions_with_different_bodies_have_not_equal_hashes() {
    FunctionTypeH functionType = functionHT(intHT(), list(stringHT()));
    stringHE();
    FunctionH function1 = functionH(functionType, intHE(1));
    stringHE();
    FunctionH function2 = functionH(functionType, intHE(2));
    assertThat(function1.hash())
        .isNotEqualTo(function2.hash());
  }

  @Test
  public void functions_with_equal_body_and_default_arguments_have_equal_hash_code() {
    FunctionTypeH functionType = functionHT(intHT(), list(stringHT()));
    stringHE();
    FunctionH function1 = functionH(functionType, intHE());
    stringHE();
    FunctionH function2 = functionH(functionType, intHE());
    assertThat(function1.hashCode())
        .isEqualTo(function2.hashCode());
  }

  @Test
  public void functions_with_different_bodies_have_not_equal_hash_code() {
    FunctionTypeH functionType = functionHT(intHT(), list(intHT()));
    intHE();
    FunctionH function1 = functionH(functionType, intHE(1));
    intHE();
    FunctionH function2 = functionH(functionType, intHE(2));
    assertThat(function1.hashCode())
        .isNotEqualTo(function2.hashCode());
  }

  @Test
  public void function_can_be_read_by_hash() {
    FunctionTypeH functionType = functionHT(intHT(), list(stringHT()));
    stringHE();
    FunctionH function = functionH(functionType, intHE());
    assertThat(objectHDbOther().get(function.hash()))
        .isEqualTo(function);
  }

  @Test
  public void functions_read_by_hash_have_equal_bodies() {
    FunctionTypeH functionType = functionHT(intHT(), list(stringHT()));
    stringHE();
    FunctionH function = functionH(functionType, intHE());
    FunctionH functionRead = (FunctionH) objectHDbOther().get(function.hash());
    assertThat(function.body())
        .isEqualTo(functionRead.body());
  }

  @Test
  public void to_string() {
    FunctionTypeH functionType = functionHT(intHT(), list(stringHT()));
    stringHE();
    FunctionH function = functionH(functionType, intHE());
    assertThat(function.toString())
        .isEqualTo("Lambda(Int(String))@" + function.hash());
  }
}
