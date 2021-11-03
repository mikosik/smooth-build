package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjNodeException;
import org.smoothbuild.testing.TestingContextImpl;

public class UnexpectedObjNodeExceptionTest extends TestingContextImpl {
  @Test
  public void message_with_types() {
    var exception = new UnexpectedObjNodeException(
        Hash.of(123), intSpec(), "node-path", boolSpec(), stringSpec());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Cannot decode its node at `node-path` path in Merkle tree. "
            + "Node has unexpected type. Expected `Bool` but was `String`.");
  }

  @Test
  public void message_with_index_and_types() {
    var exception = new UnexpectedObjNodeException(
        Hash.of(123), intSpec(), "node-path", 7, boolSpec(), stringSpec());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Cannot decode its node at `node-path[7]` path in Merkle tree. "
            + "Node has unexpected type. Expected `Bool` but was `String`.");
  }

  @Test
  public void message_with_classes() {
    var exception = new UnexpectedObjNodeException(
        Hash.of(123), intSpec(), "node-path", Integer.class, Double.class);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Int` object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Cannot decode its node at `node-path` path in Merkle tree. "
            + "Node has unexpected class. Expected java.lang.Integer but was java.lang.Double.");
  }
}
