package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationSpecOfComponentException;
import org.smoothbuild.testing.TestingContextImpl;

public class DecodeExprWrongEvaluationSpecOfComponentExceptionTest extends TestingContextImpl {
  @Test
  public void message() {
    var exception = new DecodeExprWrongEvaluationSpecOfComponentException(
        Hash.of(13), selectSpec(intSpec()), "tuple", intSpec(), stringSpec());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `SELECT:Int` "
            + "object at b1197c208248d0f7ffb3e322d5ec187441dc1b26. "
            + "Its `tuple` component evaluation spec is `String` while expected `Int`.");
  }
}
