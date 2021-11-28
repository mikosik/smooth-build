package org.smoothbuild.lang.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.define.Location.internal;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ArrayTypeNodeTest {
  @Nested
  class is_polytype {
    @Test
    public void polytype_array_node() {
      TypeNode typeNode = new ArrayTypeNode(new TypeNode("B", internal()), internal());
      assertThat(typeNode.isPolytype())
          .isTrue();
    }

    @Test
    public void non_polytype_array_node() {
      TypeNode typeNode = new ArrayTypeNode(new TypeNode("MyType", internal()),
          internal());
      assertThat(typeNode.isPolytype())
          .isFalse();
    }

    @Test
    public void polytype_array_node_with_depth_2() {
      TypeNode typeNode = new ArrayTypeNode(
          new ArrayTypeNode(
              new TypeNode("B", internal()),
              internal()),
          internal());
      assertThat(typeNode.isPolytype())
          .isTrue();
    }

    @Test
    public void non_polytype_array_node_with_depth_2() {
      TypeNode typeNode = new ArrayTypeNode(
          new ArrayTypeNode(
              new TypeNode("MyType", internal()),
              internal()),
          internal());
      assertThat(typeNode.isPolytype())
          .isFalse();
    }
  }

  @Nested
  class _variables_used_once {
    @Test
    public void array_node_which_elem_is_a_variable() {
      TypeNode elemTypeNode = new TypeNode("A", internal());
      TypeNode typeNode = new ArrayTypeNode(elemTypeNode, internal());
      assertThat(typeNode.variablesUsedOnce())
          .containsExactly("A");
    }

    @Test
    public void array_node_which_elem_is_not_a_variable() {
      TypeNode elemTypeNode = new TypeNode("MyType", internal());
      TypeNode typeNode = new ArrayTypeNode(elemTypeNode, internal());
      assertThat(typeNode.variablesUsedOnce())
          .isEmpty();
    }

    @Test
    public void array_of_array_which_elem_is_a_variable() {
      TypeNode elemTypeNode = new TypeNode("A", internal());
      TypeNode typeNode =
          new ArrayTypeNode(new ArrayTypeNode(elemTypeNode, internal()), internal());
      assertThat(typeNode.variablesUsedOnce())
          .containsExactly("A");
    }

    @Test
    public void array_of_array_which_elem_is_not_a_variable() {
      TypeNode elemTypeNode = new TypeNode("MyType", internal());
      TypeNode typeNode =
          new ArrayTypeNode(new ArrayTypeNode(elemTypeNode, internal()), internal());
      assertThat(typeNode.variablesUsedOnce())
          .isEmpty();
    }
  }
}
