package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

public class DecodeExprWrongEvaluationSpecOfComponentExceptionTest extends TestingContext {
  @Test
  public void message() {
    var exception = new DecodeExprWrongEvaluationSpecOfComponentException(
        Hash.of(13), selectSpec(intSpec()), "rec", intSpec(), strSpec());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `SELECT:Int` "
            + "object at b1197c208248d0f7ffb3e322d5ec187441dc1b26. "
            + "Its `rec` component evaluation spec is `String` while expected `Int`.");
  }
}
