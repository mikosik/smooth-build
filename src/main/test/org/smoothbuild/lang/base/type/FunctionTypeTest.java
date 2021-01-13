package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.TestingTypes.A;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.Type.toItemSignatures;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;

public class FunctionTypeTest {
  @Test
  public void function_without_type_variables_is_not_polytype() {
    FunctionType functionType = functionType(STRING, STRING);
    assertThat(functionType.isPolytype())
        .isFalse();
  }

  @Test
  public void function_with_type_variable_as_result_type_is_polytype() {
    FunctionType functionType = functionType(A);
    assertThat(functionType.isPolytype())
        .isTrue();
  }

  @Test
  public void function_with_type_variable_as_result_type_of_result_type_is_polytype() {
    FunctionType functionType = functionType(functionType(A));
    assertThat(functionType.isPolytype())
        .isTrue();
  }

  @Test
  public void function_with_type_variable_as_parameter_type_is_polytype() {
    FunctionType functionType = functionType(STRING, A);
    assertThat(functionType.isPolytype())
        .isTrue();
  }

  @Test
  public void function_with_type_variable_as_parameter_type_of_parameter_type_is_polytype() {
    FunctionType functionType = functionType(STRING, functionType(STRING, A));
    assertThat(functionType.isPolytype())
        .isTrue();
  }

  private FunctionType functionType(Type resultType, Type... parameterTypes) {
    return new FunctionType(resultType, toItemSignatures(list(parameterTypes)));
  }
}
