package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvalTypeOfComponentException;
import org.smoothbuild.testing.TestingContext;

public class DecodeExprWrongEvalTypeOfComponentExceptionTest extends TestingContext {
  @Test
  public void message() {
    var exception = new DecodeExprWrongEvalTypeOfComponentException(
        Hash.of(13), selectHT(intHT()), "tuple", intHT(), stringHT());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `SELECT:Int` "
            + "object at b1197c208248d0f7ffb3e322d5ec187441dc1b26. "
            + "Its `tuple` component evaluation type is `String` while expected `Int`.");
  }
}
