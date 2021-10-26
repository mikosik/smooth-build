package org.smoothbuild.lang.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.lang.base.define.TestingLocation.loc;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class FunctionTypeNodeTest {
  @Nested
  class _is_polytype {
    @Test
    public void function_with_variable_in_result_type() {
      TypeNode function = new FunctionTypeNode(variable(), list(), internal());
      assertThat(function.isPolytype())
          .isTrue();
    }

    @Test
    public void function_with_variable_in_parameter_type() {
      TypeNode function = new FunctionTypeNode(
          normalType(), list(variable()), internal());
      assertThat(function.isPolytype())
          .isTrue();
    }

    @Test
    public void function_without_variables() {
      TypeNode function = new FunctionTypeNode(
          normalType(), list(normalType()), internal());
      assertThat(function.isPolytype())
          .isFalse();
    }

    @Test
    public void polytype_function_node_with_depth_2_in_result_type() {
      TypeNode inner = new FunctionTypeNode(variable(), list(normalType()), internal());
      TypeNode function = new FunctionTypeNode(inner, list(normalType()), internal());
      assertThat(function.isPolytype())
          .isTrue();
    }

    @Test
    public void polytype_function_node_with_depth_2_in_parameter_type() {
      TypeNode inner = new FunctionTypeNode(variable(), list(normalType()), internal());
      TypeNode function = new FunctionTypeNode(normalType(), list(inner), internal());
      assertThat(function.isPolytype())
          .isTrue();
    }

    @Test
    public void non_polytype_function_node_with_depth_2() {
      TypeNode inner = new FunctionTypeNode(
          normalType(), list(normalType()), internal());
      TypeNode function = new FunctionTypeNode(inner, list(inner), internal());
      assertThat(function.isPolytype())
          .isFalse();
    }
  }

  @Nested
  class _variables {
    @Test
    public void function_without_variables() {
      TypeNode function = new FunctionTypeNode(
          normalType(), list(normalType()), internal());
      assertThat(function.variablesUsedOnce())
          .isEmpty();
    }

    @Test
    public void function_with_variable_in_result_type() {
      TypeNode function = new FunctionTypeNode(
          variable("A"), list(normalType()), internal());
      assertThat(function.variablesUsedOnce())
          .containsExactly("A");
    }

    @Test
    public void function_with_variable_in_parameter_type() {
      TypeNode function = new FunctionTypeNode(
          normalType(), list(variable("A")), internal());
      assertThat(function.variablesUsedOnce())
          .containsExactly("A");
    }

    @Test
    public void function_with_same_variable_in_result_type_and_parameter_type() {
      TypeNode function = new FunctionTypeNode(
          variable("A"), list(variable("A")), internal());
      assertThat(function.variablesUsedOnce())
          .isEmpty();
    }

    @Test
    public void function_with_same_variable_in_two_parameter_types() {
      TypeNode function = new FunctionTypeNode(
          normalType(), list(variable("A"), variable("A")), internal());
      assertThat(function.variablesUsedOnce())
          .isEmpty();
    }
  }

  private static TypeNode normalType() {
    return new TypeNode("MyType", internal());
  }

  private static TypeNode variable() {
    return variable("A");
  }

  private static TypeNode variable(String name) {
    return new TypeNode(name, loc());
  }
}
