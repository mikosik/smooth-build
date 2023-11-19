package org.smoothbuild.vm.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeSelectIndexOutOfBoundsException;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public class DecodeSelectIndexOutOfBoundsExceptionTest extends TestContext {
  @Test
  public void message() {
    var exception =
        new DecodeSelectIndexOutOfBoundsException(Hash.of(13), selectCB(intTB()), 13, 10);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `SELECT:Int` object "
            + "at 43c66c260828c9839f26474151db105481ff92f5e01377f75389d4ce3d2dd574. "
            + "Its index component is 13 while TUPLE size is 10.");
  }
}
