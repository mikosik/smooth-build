package org.smoothbuild.compilerfrontend.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.compilerfrontend.lang.type.STypeVarSet.sTypeVarSet;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class STypeVarSetTest extends FrontendCompilerTestContext {
  @Nested
  class _filter {
    @Test
    void filter_empty() {
      var varSet = sTypeVarSet();
      assertThat(varSet.filter(v -> !v.equals(varC()))).isEqualTo(sTypeVarSet());
    }

    @Test
    void filter() {
      var varSet = sTypeVarSet(varB(), varC(), varA());
      assertThat(varSet.filter(v -> !v.equals(varC()))).isEqualTo(sTypeVarSet(varA(), varB()));
    }
  }

  @Nested
  class _withAddedAll {
    @Test
    void empty_set_is_not_changed() {
      var varSet = sTypeVarSet(varA());
      assertThat(varSet.addAll(sTypeVarSet())).isEqualTo(sTypeVarSet(varA()));
    }

    @Test
    void itself() {
      var varSet = sTypeVarSet(varA());
      assertThat(varSet.addAll(varSet)).isEqualTo(sTypeVarSet(varA()));
    }

    @Test
    void other_varset() {
      var varSet = sTypeVarSet(varA());
      assertThat(varSet.addAll(sTypeVarSet(varB()))).isEqualTo(sTypeVarSet(varA(), varB()));
    }
  }

  @Nested
  class withRemovedAll {
    @Test
    void empty_set() {
      var varSet = sTypeVarSet(varA());
      assertThat(varSet.removeAll(set())).isEqualTo(sTypeVarSet(varA()));
    }

    @Test
    void other_non_overlapping_set() {
      var varSet = sTypeVarSet(varA());
      assertThat(varSet.removeAll(set(varB()))).isEqualTo(sTypeVarSet(varA()));
    }

    @Test
    void other_overlapping_set() {
      var varSet = sTypeVarSet(varA(), varB(), varC());
      assertThat(varSet.removeAll(set(varB()))).isEqualTo(sTypeVarSet(varA(), varC()));
    }
  }

  @Test
  void to_list() {
    var varSet = sTypeVarSet(varB(), varC(), varA());
    assertThat(varSet.toList()).isEqualTo(list(varA(), varB(), varC()));
  }

  @Test
  void equals_and_hash_code() {
    new EqualsTester()
        .addEqualityGroup(sTypeVarSet(), sTypeVarSet())
        .addEqualityGroup(sTypeVarSet(varA()), sTypeVarSet(varA()))
        .addEqualityGroup(sTypeVarSet(varA(), varB()), sTypeVarSet(varA(), varB()))
        .addEqualityGroup(
            sTypeVarSet(varC(), varA(), varB()),
            sTypeVarSet(varA(), varB(), varC()),
            sTypeVarSet(varC(), varA(), varB()))
        .testEquals();
  }

  @Test
  void to_string() {
    var varSet = sTypeVarSet(varC(), varA(), varB());
    assertThat(varSet.toString()).isEqualTo("<A,B,C>");
  }
}
