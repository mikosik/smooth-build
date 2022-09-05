package org.smoothbuild.bytecode.type.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.CatKindB;
import org.smoothbuild.testing.TestContext;

public class DecodeCatWrongSeqSizeExcTest extends TestContext {
  @Test
  public void message() {
    var exception = new DecodeCatWrongSeqSizeExc(Hash.of(123), CatKindB.INT, "node-path", 7, 2);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode INT category at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Cannot decode its node at `node-path` path in Merkle tree. "
            + "Node is a sequence with wrong size. Expected 7 but was 2.");
  }
}
