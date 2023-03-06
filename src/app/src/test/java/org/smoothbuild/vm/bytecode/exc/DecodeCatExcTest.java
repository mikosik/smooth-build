package org.smoothbuild.vm.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.exc.DecodeCatExc;

public class DecodeCatExcTest {
  @Test
  public void message() {
    var exception = new DecodeCatExc(Hash.of(123), new RuntimeException());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode category at 1959893f68220459cbd800396e1eae7bfc382e97.");
  }
}
