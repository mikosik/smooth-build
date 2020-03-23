package org.smoothbuild.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Location.location;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.nio.file.Paths;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

public class NodeTest {
  @Test
  public void set_attribute_can_be_retrieved() {
    Node node = new Node(location(Paths.get("script.smooth"), 1));
    node.set(String.class, "abc");
    assertThat(node.get(String.class))
        .isEqualTo("abc");
  }

  @Test
  public void setting_null_is_allowed() {
    Node node = new Node(unknownLocation());
    node.set(String.class, null);
    assertThat(node.get(String.class))
        .isNull();
  }

  @Test
  public void getting_nonexistent_attribute_fails() {
    Node node = new Node(unknownLocation());
    assertCall(() -> node.get(String.class))
        .throwsException(NoSuchElementException.class);
  }
}
