package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.virtualmachine.bytecode.expr.exc.RootHashChainSizeIsWrongException.cannotReadRootException;
import static org.smoothbuild.virtualmachine.bytecode.expr.exc.RootHashChainSizeIsWrongException.wrongSizeOfRootChainException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.testing.TestingVm;

public class RootHashChainSizeIsWrongExceptionTest extends TestingVm {
  @Test
  void cannot_read_root_exception() {
    var exception = cannotReadRootException(Hash.of(123), new RuntimeException());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode expression at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Cannot decode root.");
  }

  @Test
  void wrong_size_of_root_exception() {
    var exception = wrongSizeOfRootChainException(Hash.of(123), 3);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode expression at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Its root points to hash sequence with 3 elems when it should point to "
            + "sequence with 1 or 2 elems.");
  }
}
