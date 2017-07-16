package org.smoothbuild.parse;

import static org.smoothbuild.lang.message.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.NoSuchElementException;

import org.junit.Test;
import org.smoothbuild.parse.ast.Node;

public class NodeTest {
  private Node node;

  @Test
  public void set_attribute_can_be_retrieved() {
    given(node = new Node(codeLocation(1)));
    when(() -> node.set(String.class, "abc"));
    thenEqual(node.get(String.class), "abc");
  }

  @Test
  public void getting_nonexistent_attribute_fails() throws Exception {
    given(node = new Node(codeLocation(1)));
    when(() -> node.get(String.class));
    thenThrown(NoSuchElementException.class);
  }

  @Test
  public void has_return_false_for_nonexistent_attribute() throws Exception {
    given(node = new Node(codeLocation(1)));
    when(() -> node.has(String.class));
    thenReturned(false);
  }

  @Test
  public void has_returns_true_for_existing_attribute() throws Exception {
    given(node = new Node(codeLocation(1)));
    given(node).set(String.class, "abc");
    when(() -> node.has(String.class));
    thenReturned(true);
  }
}
