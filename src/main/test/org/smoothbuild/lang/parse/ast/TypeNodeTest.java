package org.smoothbuild.lang.parse.ast;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.testing.common.TestingLocation;

public class TypeNodeTest {
  private static final Location LOCATION = TestingLocation.loc();

  @Test
  public void normal_type_node_is_not_array() {
    TypeNode typeNode = new TypeNode("MyType", LOCATION);
    assertThat(typeNode.isArray())
        .isFalse();
  }

  @Test
  public void type_variable_node_is_not_array() {
    TypeNode typeNode = new TypeNode("A", LOCATION);
    assertThat(typeNode.isArray())
        .isFalse();
  }

  @Test
  public void normal_array_type_node_is_array() {
    ArrayTypeNode typeNode = new ArrayTypeNode(new TypeNode("MyType", LOCATION), LOCATION);
    assertThat(typeNode.isArray())
        .isTrue();
  }

  @Test
  public void polytype_array_type_node_is_array() {
    ArrayTypeNode typeNode = new ArrayTypeNode(new TypeNode("A", LOCATION), LOCATION);
    assertThat(typeNode.isArray())
        .isTrue();
  }

  @Test
  public void node_with_type_variable_name_is_polytype() {
    TypeNode typeNode = new TypeNode("B", LOCATION);
    assertThat(typeNode.isPolytype())
        .isTrue();
  }

  @Test
  public void node_with_non_type_variable_name_is_not_polytype() {
    TypeNode typeNode = new TypeNode("MyType", LOCATION);
    assertThat(typeNode.isPolytype())
        .isFalse();
  }

  @Test
  public void type_node_core_type_is_that_node() {
    TypeNode typeNode = new TypeNode("MyType", LOCATION);
    assertThat(typeNode.coreType())
        .isEqualTo(typeNode);
  }
}
