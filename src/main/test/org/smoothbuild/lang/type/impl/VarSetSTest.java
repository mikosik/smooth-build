package org.smoothbuild.lang.type.impl;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.type.impl.VarSetS.varSetS;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

import com.google.common.testing.EqualsTester;

public class VarSetSTest extends TestingContext {
  @Test
  public void as_list() {
    var varSet = varSetS(varS("B"), varS("C"), varS("A"));
    assertThat(varSet.asList())
        .isEqualTo(list(varS("A"), varS("B"), varS("C")));
  }

  @Test
  public void equals_and_hash_code() {
    new EqualsTester()
        .addEqualityGroup(
            varSetS(),
            varSetS())
        .addEqualityGroup(
            varSetS(varS("A")),
            varSetS(varS("A")))
        .addEqualityGroup(
            varSetS(varS("A"), varS("B")),
            varSetS(varS("A"), varS("B")))
        .addEqualityGroup(
            varSetS(varS("C"), varS("A"), varS("B")),
            varSetS(varS("A"), varS("B"), varS("C")),
            varSetS(varS("C"), varS("A"), varS("B")))
        .testEquals();
  }

  @Test
  public void to_string() {
    var varSet = varSetS(varS("C"), varS("A"), varS("B"));
    assertThat(varSet.toString())
        .isEqualTo("<A,B,C>");
  }
}
