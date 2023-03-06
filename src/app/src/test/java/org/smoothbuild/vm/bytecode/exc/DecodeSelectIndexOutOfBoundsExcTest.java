package org.smoothbuild.vm.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeSelectIndexOutOfBoundsExc;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public class DecodeSelectIndexOutOfBoundsExcTest extends TestContext {
  @Test
  public void message() {
    var exception = new DecodeSelectIndexOutOfBoundsExc(
        Hash.of(13), selectCB(intTB()), 13, 10);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `SELECT:Int` object "
            + "at b1197c208248d0f7ffb3e322d5ec187441dc1b26. "
            + "Its index component is 13 while TUPLE size is 10.");
  }
}
