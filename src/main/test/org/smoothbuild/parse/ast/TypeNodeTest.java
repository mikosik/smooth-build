package org.smoothbuild.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.quackery.Case;
import org.quackery.Quackery;
import org.quackery.Suite;
import org.quackery.junit.QuackeryRunner;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.testing.common.TestingLocation;

@RunWith(QuackeryRunner.class)
public class TypeNodeTest {
  private static final Location LOCATION = TestingLocation.loc();

  @Quackery
  public static Suite is_array() {
    return suite("isArray")
        .add(testIsNotArray(new TypeNode("MyType", LOCATION)))
        .add(testIsNotArray(new TypeNode("a", LOCATION)))
        .add(testIsArray(new ArrayTypeNode(new TypeNode("MyType", LOCATION), LOCATION)))
        .add(testIsArray(new ArrayTypeNode(new TypeNode("a", LOCATION), LOCATION)));
  }

  private static Case testIsArray(TypeNode type) {
    return newCase(type.name() + " is array type", () -> assertTrue(type.isArray()));
  }

  private static Case testIsNotArray(TypeNode type) {
    return newCase(type.name() + " is NOT array type", () -> assertFalse(type.isArray()));
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
