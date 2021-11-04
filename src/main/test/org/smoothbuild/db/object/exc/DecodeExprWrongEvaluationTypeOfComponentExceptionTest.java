package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationTypeOfComponentException;
import org.smoothbuild.testing.TestingContextImpl;

public class DecodeExprWrongEvaluationTypeOfComponentExceptionTest extends TestingContextImpl {
  @Test
  public void message() {
    var exception = new DecodeExprWrongEvaluationTypeOfComponentException(
        Hash.of(13), selectOT(intOT()), "tuple", intOT(), stringOT());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `SELECT:Int` "
            + "object at b1197c208248d0f7ffb3e322d5ec187441dc1b26. "
            + "Its `tuple` component evaluation type is `String` while expected `Int`.");
  }
}
