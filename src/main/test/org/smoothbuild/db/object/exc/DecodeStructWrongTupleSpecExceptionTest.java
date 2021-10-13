package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

public class DecodeStructWrongTupleSpecExceptionTest extends TestingContext {
  @Test
  public void message() {
    var exception = new DecodeStructWrongTupleSpecException(
        Hash.of(13), structSpec(recSpec(list(intSpec())), list("field")), recSpec(list(strSpec())));
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode {INT field} object "
            + "at b1197c208248d0f7ffb3e322d5ec187441dc1b26. "
            + "Its fields should match {INT} spec while their spec is {STRING}.");
  }
}
