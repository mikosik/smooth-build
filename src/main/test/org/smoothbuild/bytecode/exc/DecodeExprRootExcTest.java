package org.smoothbuild.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.exc.DecodeExprRootExc;
import org.smoothbuild.db.Hash;
import org.smoothbuild.testing.TestContext;

public class DecodeExprRootExcTest extends TestContext {
  @Test
  public void cannot_read_root_exception() {
    var exception = DecodeExprRootExc.cannotReadRootException(Hash.of(123), new RuntimeException());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Cannot decode root.");
  }

  @Test
  public void wrong_size_of_root_exception() {
    var exception = DecodeExprRootExc.wrongSizeOfRootSeqException(Hash.of(123), 3);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Its root points to hash sequence with 3 elems when it should point to "
            + "sequence with 2 elems.");
  }
}
