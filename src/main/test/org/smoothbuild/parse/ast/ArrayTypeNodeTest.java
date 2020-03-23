package org.smoothbuild.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Location.unknownLocation;

import org.junit.jupiter.api.Test;

public class ArrayTypeNodeTest {
  @Test
  public void array_node_with_generic_element_name_is_generic() {
    TypeNode typeNode = new ArrayTypeNode(new TypeNode("B", unknownLocation()), unknownLocation());
    assertThat(typeNode.isGeneric())
        .isTrue();
  }

  @Test
  public void array_node_with_non_generic_element_name_is_not_generic() {
    TypeNode typeNode = new ArrayTypeNode(new TypeNode("MyType", unknownLocation()),
        unknownLocation());
    assertThat(typeNode.isGeneric())
        .isFalse();
  }

  @Test
  public void array_node_of_depth_2_with_generic_element_name_is_generic() {
    TypeNode typeNode = new ArrayTypeNode(
        new ArrayTypeNode(
            new TypeNode("B", unknownLocation()),
            unknownLocation()),
        unknownLocation());
    assertThat(typeNode.isGeneric())
        .isTrue();
  }

  @Test
  public void array_node_of_depth_2_with_non_generic_element_name_is_not_generic() {
    TypeNode typeNode = new ArrayTypeNode(
        new ArrayTypeNode(
            new TypeNode("MyType", unknownLocation()),
            unknownLocation()),
        unknownLocation());
    assertThat(typeNode.isGeneric())
        .isFalse();
  }

  @Test
  public void array_type_node_core_type_is_element_node() {
    TypeNode elementTypeNode = new TypeNode("MyType", unknownLocation());
    TypeNode typeNode = new ArrayTypeNode(elementTypeNode, unknownLocation());
    assertThat(typeNode.coreType())
        .isEqualTo(elementTypeNode);
  }

  @Test
  public void array_of_array_type_node_core_type_is_element_node() {
    TypeNode elementTypeNode = new TypeNode("MyType", unknownLocation());
    TypeNode typeNode = new ArrayTypeNode(new ArrayTypeNode(elementTypeNode, unknownLocation()),
        unknownLocation());
    assertThat(typeNode.coreType())
        .isEqualTo(elementTypeNode);
  }
}
