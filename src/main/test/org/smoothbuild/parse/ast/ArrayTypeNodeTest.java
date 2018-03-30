package org.smoothbuild.parse.ast;

import static org.smoothbuild.lang.message.Location.location;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.nio.file.Paths;

import org.junit.Test;
import org.smoothbuild.lang.message.Location;

public class ArrayTypeNodeTest {
  private static final Location LOCATION = location(Paths.get("file.txt"), 3);
  private TypeNode typeNode;

  @Test
  public void array_node_with_generic_element_name_is_generic() throws Exception {
    given(typeNode = new ArrayTypeNode(new TypeNode("b", LOCATION), LOCATION));
    when(() -> typeNode.isGeneric());
    thenReturned(true);
  }

  @Test
  public void array_node_with_non_generic_element_name_is_not_generic() throws Exception {
    given(typeNode = new ArrayTypeNode(new TypeNode("MyType", LOCATION), LOCATION));
    when(() -> typeNode.isGeneric());
    thenReturned(false);
  }

  @Test
  public void array_node_of_depth_2_with_generic_element_name_is_generic() throws Exception {
    given(typeNode = new ArrayTypeNode(
        new ArrayTypeNode(
            new TypeNode("b", LOCATION),
            LOCATION),
        LOCATION));
    when(() -> typeNode.isGeneric());
    thenReturned(true);
  }

  @Test
  public void array_node_of_depth_2_with_non_generic_element_name_is_not_generic()
      throws Exception {
    given(typeNode = new ArrayTypeNode(
        new ArrayTypeNode(
            new TypeNode("MyType", LOCATION),
            LOCATION),
        LOCATION));
    when(() -> typeNode.isGeneric());
    thenReturned(false);
  }
}
