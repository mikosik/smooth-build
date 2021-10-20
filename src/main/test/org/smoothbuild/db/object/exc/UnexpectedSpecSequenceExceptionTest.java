package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.db.object.spec.base.SpecKind.INT;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.exc.UnexpectedSpecSequenceException;
import org.smoothbuild.testing.TestingContextImpl;

public class UnexpectedSpecSequenceExceptionTest extends TestingContextImpl {
  @Test
  public void message() {
    var exception = new UnexpectedSpecSequenceException(Hash.of(123), INT, "node-path", 7, 2);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode INT spec at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Cannot decode its node at `node-path` path in Merkle tree. "
            + "Node is a sequence with wrong size. Expected 7 but was 2.");
  }
}
