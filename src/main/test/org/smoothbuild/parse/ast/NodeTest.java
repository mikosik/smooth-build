package org.smoothbuild.parse.ast;

import static org.smoothbuild.lang.base.Location.location;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.nio.file.Paths;
import java.util.NoSuchElementException;

import org.junit.Test;

public class NodeTest {
  private Node node;

  @Test
  public void set_attribute_can_be_retrieved() {
    given(node = new Node(location(Paths.get("script.smooth"), 1)));
    when(() -> node.set(String.class, "abc"));
    thenEqual(node.get(String.class), "abc");
  }

  @Test
  public void setting_null_is_allowed() throws Exception {
    given(node = new Node(location(Paths.get("script.smooth"), 1)));
    given(node).set(String.class, null);
    when(() -> node.get(String.class));
    thenReturned(null);
  }

  @Test
  public void getting_nonexistent_attribute_fails() throws Exception {
    given(node = new Node(location(Paths.get("script.smooth"), 1)));
    when(() -> node.get(String.class));
    thenThrown(NoSuchElementException.class);
  }
}
