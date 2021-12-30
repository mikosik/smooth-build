package org.smoothbuild.bytecode.type.exc;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.type.base.CatKindB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.testing.TestingContext;

import com.google.common.truth.Truth;

public class DecodeCatWrongSeqSizeExcTest extends TestingContext {
  @Test
  public void message() {
    var exception = new DecodeCatWrongSeqSizeExc(Hash.of(123), CatKindB.INT, "node-path", 7, 2);
    Truth.assertThat(exception.getMessage())
        .isEqualTo("Cannot decode INT type at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Cannot decode its node at `node-path` path in Merkle tree. "
            + "Node is a sequence with wrong size. Expected 7 but was 2.");
  }
}
