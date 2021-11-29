package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.exc.DecodeSelectWrongEvalTypeException;
import org.smoothbuild.testing.TestingContext;

public class DecodeSelectWrongEvalTypeExceptionTest extends TestingContext {
  @Test
  public void message() {
    var exception = new DecodeSelectWrongEvalTypeException(
        Hash.of(13), selectHT(intHT()), stringHT());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `SELECT:Int` object"
            + " at b1197c208248d0f7ffb3e322d5ec187441dc1b26."
            + " Its index points to item with `String` type while this expression defines"
            + " its evaluation type as `Int`.");
  }
}