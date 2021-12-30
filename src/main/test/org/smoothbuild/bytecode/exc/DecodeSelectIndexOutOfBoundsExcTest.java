package org.smoothbuild.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.exc.DecodeSelectIndexOutOfBoundsExc;
import org.smoothbuild.db.Hash;
import org.smoothbuild.testing.TestingContext;

public class DecodeSelectIndexOutOfBoundsExcTest extends TestingContext {
  @Test
  public void message() {
    var exception = new DecodeSelectIndexOutOfBoundsExc(
        Hash.of(13), selectCB(intTB()), 13, 10);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `Select:Int` object "
            + "at b1197c208248d0f7ffb3e322d5ec187441dc1b26. "
            + "Its index component is 13 while TUPLE size is 10.");
  }
}
