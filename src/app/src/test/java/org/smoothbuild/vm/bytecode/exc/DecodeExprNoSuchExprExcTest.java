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
        .isEqualTo("Cannot decode object at 1959893f68220459cbd800396e1eae7bfc382e97. "
            + "Cannot find it in object db.");
  }
}
