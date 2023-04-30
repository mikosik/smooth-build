package org.smoothbuild.vm.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprNoSuchExprExc;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public class DecodeExprNoSuchExprExcTest {
  @Test
  public void message() {
    var exception = new DecodeExprNoSuchExprExc(Hash.of(123));
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode object at "
            + "a5dcf5b8418dfafec16079148ec90cf81dfc6276c1cce220017c782ecb7d7aea. "
            + "Cannot find it in object db.");
  }
}
