package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class DefinedFunctionHTest extends TestingContext {
  @Test
  public void creating_function_with_body_evaluation_type_not_equal_result_type_causes_exception() {
    var functionType = definedFunctionHT(intHT(), list(stringHT()));
    assertCall(() -> definedFunctionH(functionType, boolH(true)))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_body_to_null_throws_exception() {
    var functionType = definedFunctionHT(intHT(), list(boolHT()));
    assertCall(() -> definedFunctionH(functionType, null))
        .throwsException(NullPointerException.class);
  }


  @Test
  public void type_of_function_is_function_type() {
    var functionType = definedFunctionHT(intHT(), list(stringHT()));
    assertThat(definedFunctionH(functionType, intH()).type())
        .isEqualTo(functionType);
  }

  @Test
  public void body_contains_object_passed_during_construction() {
    var functionType = definedFunctionHT(intHT(), list(boolHT()));
    var body = intH(33);
    assertThat(definedFunctionH(functionType, body).body())
        .isEqualTo(body);
  }

  @Test
  public void functions_with_equal_body_are_equal() {
    var functionType = definedFunctionHT(intHT(), list(stringHT()));
    var function1 = definedFunctionH(functionType, intH());
    var function2 = definedFunctionH(functionType, intH());
    assertThat(function1)
        .isEqualTo(function2);
  }

  @Test
  public void functions_with_different_body_are_not_equal() {
    var functionType = definedFunctionHT(intHT(), list(stringHT()));
    var function1 = definedFunctionH(functionType, intH(1));
    var function2 = definedFunctionH(functionType, intH(2));
    assertThat(function1)
        .isNotEqualTo(function2);
  }

  @Test
  public void functions_with_equal_body_have_equal_hashes() {
    var functionType = definedFunctionHT(intHT(), list(intHT()));
    var function1 = definedFunctionH(functionType, intH());
    var function2 = definedFunctionH(functionType, intH());
    assertThat(function1.hash())
        .isEqualTo(function2.hash());
  }

  @Test
  public void functions_with_different_bodies_have_not_equal_hashes() {
    var functionType = definedFunctionHT(intHT(), list(stringHT()));
    var function1 = definedFunctionH(functionType, intH(1));
    var function2 = definedFunctionH(functionType, intH(2));
    assertThat(function1.hash())
        .isNotEqualTo(function2.hash());
  }

  @Test
  public void functions_with_equal_body_have_equal_hash_code() {
    var functionType = definedFunctionHT(intHT(), list(stringHT()));
    var function1 = definedFunctionH(functionType, intH());
    var function2 = definedFunctionH(functionType, intH());
    assertThat(function1.hashCode())
        .isEqualTo(function2.hashCode());
  }

  @Test
  public void functions_with_different_bodies_have_not_equal_hash_code() {
    var functionType = definedFunctionHT(intHT(), list(intHT()));
    var function1 = definedFunctionH(functionType, intH(1));
    var function2 = definedFunctionH(functionType, intH(2));
    assertThat(function1.hashCode())
        .isNotEqualTo(function2.hashCode());
  }

  @Test
  public void function_can_be_read_by_hash() {
    var functionType = definedFunctionHT(intHT(), list(stringHT()));
    var function = definedFunctionH(functionType, intH());
    assertThat(objectHDbOther().get(function.hash()))
        .isEqualTo(function);
  }

  @Test
  public void functions_read_by_hash_have_equal_bodies() {
    var functionType = definedFunctionHT(intHT(), list(stringHT()));
    var function = definedFunctionH(functionType, intH());
    var functionRead = (DefinedFunctionH) objectHDbOther().get(function.hash());
    assertThat(function.body())
        .isEqualTo(functionRead.body());
  }

  @Test
  public void to_string() {
    var functionType = definedFunctionHT(intHT(), list(stringHT()));
    var function = definedFunctionH(functionType, intH());
    assertThat(function.toString())
        .isEqualTo("Lambda(Int(String))@" + function.hash());
  }
}
