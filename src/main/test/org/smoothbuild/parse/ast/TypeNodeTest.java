package org.smoothbuild.parse.ast;

import static org.smoothbuild.lang.message.Location.location;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.nio.file.Paths;

import org.junit.Test;
import org.smoothbuild.lang.message.Location;

public class TypeNodeTest {
  private static final Location LOCATION = location(Paths.get("file.txt"), 3);
  private TypeNode typeNode;

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
}
