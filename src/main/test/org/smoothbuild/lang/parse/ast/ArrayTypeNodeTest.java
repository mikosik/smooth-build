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
  class _variables {
    @Test
    public void _of_array_node_contains_only_element_node_when_it_is_a_variable() {
      TypeNode elementTypeNode = new TypeNode("A", internal());
      TypeNode typeNode = new ArrayTypeNode(elementTypeNode, internal());
      assertThat(typeNode.variables())
          .containsExactly(elementTypeNode);
    }

    @Test
    public void _of_array_node_is_empty_when_its_element_is_not_a_variable() {
      TypeNode elementTypeNode = new TypeNode("MyType", internal());
      TypeNode typeNode = new ArrayTypeNode(elementTypeNode, internal());
      assertThat(typeNode.variables())
          .isEmpty();
    }

    @Test
    public void of_array_of_array_contains_only_deepest_element_node_when_it_is_a_variable() {
      TypeNode elementTypeNode = new TypeNode("A", internal());
      TypeNode typeNode =
          new ArrayTypeNode(new ArrayTypeNode(elementTypeNode, internal()), internal());
      assertThat(typeNode.variables())
          .containsExactly(elementTypeNode);
    }

    @Test
    public void of_array_of_array_is_empty_when_its_deepest_element_node_is_not_a_variable() {
      TypeNode elementTypeNode = new TypeNode("MyType", internal());
      TypeNode typeNode =
          new ArrayTypeNode(new ArrayTypeNode(elementTypeNode, internal()), internal());
      assertThat(typeNode.variables())
          .isEmpty();
    }
  }
}
