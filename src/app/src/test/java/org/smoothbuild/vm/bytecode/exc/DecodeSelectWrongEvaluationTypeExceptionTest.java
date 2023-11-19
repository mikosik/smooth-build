package org.smoothbuild.vm.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeSelectWrongEvaluationTypeException;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public class DecodeSelectWrongEvaluationTypeExceptionTest extends TestContext {
  @Test
  public void message() {
    var exception = new DecodeSelectWrongEvaluationTypeException(
        Hash.of(13), selectCB(intTB()), stringTB());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `SELECT:Int` object"
            + " at 43c66c260828c9839f26474151db105481ff92f5e01377f75389d4ce3d2dd574."
            + " Its index points to item with `String` type while this expression defines"
            + " its evaluation type as `Int`.");
  }
}
