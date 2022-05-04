package org.smoothbuild.bytecode.type.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.bytecode.type.val.VarSetB.varSetB;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

import com.google.common.testing.EqualsTester;

public class VarSetBTest extends TestingContext {
  @Test
  public void as_list() {
    var varSet = varSetB(varB("B"), varB("C"), varB("A"));
    assertThat(varSet.asList())
        .isEqualTo(list(varB("A"), varB("B"), varB("C")));
  }

  @Test
  public void equals_and_hash_code() {
    new EqualsTester()
        .addEqualityGroup(
            varSetB(),
            varSetB())
        .addEqualityGroup(
            varSetB(varB("A")),
            varSetB(varB("A")))
        .addEqualityGroup(
            varSetB(varB("A"), varB("B")),
            varSetB(varB("A"), varB("B")))
        .addEqualityGroup(
            varSetB(varB("C"), varB("A"), varB("B")),
            varSetB(varB("A"), varB("B"), varB("C")),
            varSetB(varB("C"), varB("A"), varB("B")))
        .testEquals();
  }

  @Test
  public void to_string() {
    var varSet = varSetB(varB("C"), varB("A"), varB("B"));
    assertThat(varSet.toString())
        .isEqualTo("<A,B,C>");
  }
}
