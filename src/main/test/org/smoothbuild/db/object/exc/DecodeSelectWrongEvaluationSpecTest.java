package org.smoothbuild.db.object.exc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

public class DecodeSelectWrongEvaluationSpecTest extends TestingContext {
  @Test
  public void message() {
    var exception = new DecodeSelectWrongEvaluationSpec(
        Hash.of(13), fieldReadSpec(intSpec()), strSpec());
    assertThat(exception.getMessage())
        .isEqualTo("Cannot decode FIELD_READ:INT object"
            + " at b1197c208248d0f7ffb3e322d5ec187441dc1b26."
            + " Its index points to item with STRING spec while this expression defines"
            + " its evaluation spec as INT.");
  }
}
