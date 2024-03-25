package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class DecodeSelectWrongEvaluationTypeExceptionTest extends TestingVirtualMachine {
  @Test
  public void message() throws Exception {
    var exception = new DecodeSelectWrongEvaluationTypeException(
        Hash.of(13), bSelectKind(bIntType()), bStringType());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `SELECT` expression"
            + " at 43c66c260828c9839f26474151db105481ff92f5e01377f75389d4ce3d2dd574."
            + " Its index points to item with `String` type while this expression defines"
            + " its evaluation type as `Int`.");
  }
}
