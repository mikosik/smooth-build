package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContextImpl;

public class DecodeSelectWrongEvaluationSpecExceptionTest extends TestingContextImpl {
  @Test
  public void message() {
    var exception = new DecodeSelectWrongEvaluationSpecException(
        Hash.of(13), selectSpec(intSpec()), strSpec());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `SELECT:Int` object"
            + " at b1197c208248d0f7ffb3e322d5ec187441dc1b26."
            + " Its index points to item with `String` spec while this expression defines"
            + " its evaluation spec as `Int`.");
  }
}
