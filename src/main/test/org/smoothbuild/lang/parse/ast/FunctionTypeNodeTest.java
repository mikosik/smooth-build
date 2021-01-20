package org.smoothbuild.lang.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.testing.common.TestingLocation.loc;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

public class FunctionTypeNodeTest {
  @Nested
  class _is_polytype {
    @Test
    public void function_with_variable_in_result_type() {
      TypeNode function = new FunctionTypeNode(variable(), ImmutableList.of(), internal());
      assertThat(function.isPolytype())
          .isTrue();
    }

    @Test
    public void function_with_variable_in_parameter_type() {
      TypeNode function = new FunctionTypeNode(
          normalType(), ImmutableList.of(variable()), internal());
      assertThat(function.isPolytype())
          .isTrue();
    }

    @Test
    public void function_without_variables() {
      TypeNode function = new FunctionTypeNode(
          normalType(), ImmutableList.of(normalType()), internal());
      assertThat(function.isPolytype())
          .isFalse();
    }

    @Test
    public void polytype_function_node_with_depth_2_in_result_type() {
      TypeNode inner = new FunctionTypeNode(variable(), ImmutableList.of(normalType()), internal());
      TypeNode function = new FunctionTypeNode(inner, ImmutableList.of(normalType()), internal());
      assertThat(function.isPolytype())
          .isTrue();
    }

    @Test
    public void polytype_function_node_with_depth_2_in_parameter_type() {
      TypeNode inner = new FunctionTypeNode(variable(), ImmutableList.of(normalType()), internal());
      TypeNode function = new FunctionTypeNode(normalType(), ImmutableList.of(inner), internal());
      assertThat(function.isPolytype())
          .isTrue();
    }

    @Test
    public void non_polytype_function_node_with_depth_2() {
      TypeNode inner = new FunctionTypeNode(
          normalType(), ImmutableList.of(normalType()), internal());
      TypeNode function = new FunctionTypeNode(inner, ImmutableList.of(inner), internal());
      assertThat(function.isPolytype())
          .isFalse();
    }
  }

  @Nested
  class _variables {
    @Test
    public void function_without_variables() {
      TypeNode function = new FunctionTypeNode(
          normalType(), ImmutableList.of(normalType()), internal());
      assertThat(function.variables())
          .isEmpty();
    }

    @Test
    public void function_with_variables() {
      TypeNode function = new FunctionTypeNode(
          variable("A"), ImmutableList.of(variable("B"), variable("C")), internal());
      assertThat(function.variables())
          .containsExactly(variable("A"), variable("B"), variable("C"));
    }

    @Test
    public void function_with_variables_at_depth_2() {
      TypeNode result = new FunctionTypeNode(
          variable("A"), ImmutableList.of(normalType()), internal());
      TypeNode param = new FunctionTypeNode(
          normalType(), ImmutableList.of(variable("B")), internal());
      TypeNode function = new FunctionTypeNode(result, ImmutableList.of(param), internal());
      assertThat(function.variables())
          .containsExactly(variable("A"), variable("B"));
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
