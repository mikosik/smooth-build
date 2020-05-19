package org.smoothbuild.parse.ast;

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
  public void generic_type_node_is_not_array() {
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
  public void generic_array_type_node_is_array() {
    ArrayTypeNode typeNode = new ArrayTypeNode(new TypeNode("A", LOCATION), LOCATION);
    assertThat(typeNode.isArray())
        .isTrue();
  }

  @Test
  public void node_with_generic_name_is_generic() {
    TypeNode typeNode = new TypeNode("B", LOCATION);
    assertThat(typeNode.isGeneric())
        .isTrue();
  }

  @Test
  public void node_with_non_generic_name_is_not_generic() {
    TypeNode typeNode = new TypeNode("MyType", LOCATION);
    assertThat(typeNode.isGeneric())
        .isFalse();
  }

  @Test
  public void type_node_core_type_is_that_node() {
    TypeNode typeNode = new TypeNode("MyType", LOCATION);
    assertThat(typeNode.coreType())
        .isEqualTo(typeNode);
  }
}
