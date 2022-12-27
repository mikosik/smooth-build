package org.smoothbuild.vm.bytecode.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeSelectWrongEvalTypeExc;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public class DecodeSelectWrongEvalTypeExcTest extends TestContext {
  @Test
  public void message() {
    var exception = new DecodeSelectWrongEvalTypeExc(
        Hash.of(13), selectCB(intTB()), stringTB());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode `SELECT:Int` object"
            + " at b1197c208248d0f7ffb3e322d5ec187441dc1b26."
            + " Its index points to item with `String` type while this expression defines"
            + " its evaluation type as `Int`.");
  }
}