package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.exc.DecodeSelectIndexOutOfBoundsException;
import org.smoothbuild.testing.TestingContextImpl;

public class DecodeSelectIndexOutOfBoundsExceptionTest extends TestingContextImpl {
  @Test
  public void message() {
    var exception = new DecodeSelectIndexOutOfBoundsException(
        Hash.of(13), selectOT(intOT()), 13, 10);
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `SELECT:Int` object "
            + "at b1197c208248d0f7ffb3e322d5ec187441dc1b26. "
            + "Its index component is 13 while TUPLE size is 10.");
  }
}
