package org.smoothbuild.vm.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.vm.bytecode.expr.exc.DecodeExprRootException.cannotReadRootException;
import static org.smoothbuild.vm.bytecode.expr.exc.DecodeExprRootException.wrongSizeOfRootSeqException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public class DecodeExprRootExceptionTest extends TestContext {
  @Test
  public void cannot_read_root_exception() {
    var exception = cannotReadRootException(Hash.of(123), new RuntimeException());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode object at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Cannot decode root.");
  }

  @Test
  public void wrong_size_of_root_exception() {
    var exception = wrongSizeOfRootSeqException(Hash.of(123), 3);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode object at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Its root points to hash sequence with 3 elems when it should point to "
            + "sequence with 1 or 2 elems.");
  }
}
