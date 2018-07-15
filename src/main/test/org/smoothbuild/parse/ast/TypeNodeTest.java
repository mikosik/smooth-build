package org.smoothbuild.parse.ast;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.smoothbuild.lang.base.Location.location;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quackery.Case;
import org.quackery.Quackery;
import org.quackery.Suite;
import org.quackery.junit.QuackeryRunner;
import org.smoothbuild.lang.base.Location;

@RunWith(QuackeryRunner.class)
public class TypeNodeTest {
  private static final Location LOCATION = location(Paths.get("file.txt"), 3);
  private TypeNode typeNode;

  @Quackery
  public static Suite is_array() throws Exception {
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
  public void node_with_generic_name_is_generic() throws Exception {
    given(typeNode = new TypeNode("b", LOCATION));
    when(() -> typeNode.isGeneric());
    thenReturned(true);
  }

  @Test
  public void node_with_non_generic_name_is_not_generic() throws Exception {
    given(typeNode = new TypeNode("MyType", LOCATION));
    when(() -> typeNode.isGeneric());
    thenReturned(false);
  }

  @Test
  public void type_node_core_type_is_that_node() throws Exception {
    given(typeNode = new TypeNode("MyType", LOCATION));
    when(() -> typeNode.coreType());
    thenReturned(typeNode);
  }
}
