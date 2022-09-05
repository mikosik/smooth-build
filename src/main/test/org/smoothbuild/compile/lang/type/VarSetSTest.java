package org.smoothbuild.compile.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

import com.google.common.testing.EqualsTester;

public class VarSetSTest extends TestContext {
  @Nested
  class _filter {
    @Test
    public void filter_empty() {
      var varSet = VarSetS.varSetS();
      assertThat(varSet.filter(v -> !v.equals(varC())))
          .isEqualTo(VarSetS.varSetS());
    }

    @Test
    public void filter() {
      var varSet = VarSetS.varSetS(varB(), varC(), varA());
      assertThat(varSet.filter(v -> !v.equals(varC())))
          .isEqualTo(VarSetS.varSetS(varA(), varB()));
    }
  }

  @Test
  public void as_list() {
    var varSet = VarSetS.varSetS(varB(), varC(), varA());
    assertThat(varSet.asList())
        .isEqualTo(list(varA(), varB(), varC()));
  }

  @Test
  public void equals_and_hash_code() {
    new EqualsTester()
        .addEqualityGroup(
            VarSetS.varSetS(),
            VarSetS.varSetS())
        .addEqualityGroup(
            VarSetS.varSetS(varA()),
            VarSetS.varSetS(varA()))
        .addEqualityGroup(
            VarSetS.varSetS(varA(), varB()),
            VarSetS.varSetS(varA(), varB()))
        .addEqualityGroup(
            VarSetS.varSetS(varC(), varA(), varB()),
            VarSetS.varSetS(varA(), varB(), varC()),
            VarSetS.varSetS(varC(), varA(), varB()))
        .testEquals();
  }

  @Test
  public void to_string() {
    var varSet = VarSetS.varSetS(varC(), varA(), varB());
    assertThat(varSet.toString())
        .isEqualTo("<A,B,C>");
  }
}
