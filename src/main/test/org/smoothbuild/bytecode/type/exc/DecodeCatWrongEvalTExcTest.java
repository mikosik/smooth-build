package org.smoothbuild.bytecode.type.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.type.base.CatKindB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.testing.TestingContext;

public class DecodeCatWrongEvalTExcTest extends TestingContext {
  @Test
  public void message() {
    var exc = new DecodeCatWrongEvalTExc(Hash.of(33), CatKindB.COMBINE, arrayTB(oVarTB("A")));
    assertThat(exc.getMessage())
        .isEqualTo("Cannot decode COMBINE type at 2db3ed483064ec0fac807c34b54e934e5201d658. "
            + "Cannot decode its node at `data` path in Merkle tree. It is equal to `[A]` but "
            + "evalT should not contain open-vars.");
  }
}
