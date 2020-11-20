package org.smoothbuild.lang.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.testing.common.TestingLocation.loc;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ArrayTypeNodeTest {
  @Nested
  class is_array {
    @Test
    public void normal_array_type_node_is_array() {
      ArrayTypeNode typeNode = new ArrayTypeNode(new TypeNode("MyType", loc()), loc());
      assertThat(typeNode.isArray())
          .isTrue();
    }

    @Test
    public void polytype_array_type_node_is_array() {
      ArrayTypeNode typeNode = new ArrayTypeNode(new TypeNode("A", loc()), loc());
      assertThat(typeNode.isArray())
          .isTrue();
    }
  }

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
  class core_type {
    @Test
    public void array_type_node_core_type_is_element_node() {
      TypeNode elementTypeNode = new TypeNode("MyType", internal());
      TypeNode typeNode = new ArrayTypeNode(elementTypeNode, internal());
      assertThat(typeNode.coreType())
          .isEqualTo(elementTypeNode);
    }

    @Test
    public void array_of_array_type_node_core_type_is_element_node() {
      TypeNode elementTypeNode = new TypeNode("MyType", internal());
      TypeNode typeNode = new ArrayTypeNode(new ArrayTypeNode(elementTypeNode, internal()),
          internal());
      assertThat(typeNode.coreType())
          .isEqualTo(elementTypeNode);
    }
  }
}
