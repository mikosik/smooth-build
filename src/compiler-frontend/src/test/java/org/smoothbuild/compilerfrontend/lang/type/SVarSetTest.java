package org.smoothbuild.compilerfrontend.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.sVarSet;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SVarSetTest extends FrontendCompilerTestContext {
  @Nested
  class _filter {
    @Test
    void filter_empty() {
      var varSet = sVarSet();
      assertThat(varSet.filter(v -> !v.equals(varC()))).isEqualTo(sVarSet());
    }

    @Test
    void filter() {
      var varSet = sVarSet(varB(), varC(), varA());
      assertThat(varSet.filter(v -> !v.equals(varC()))).isEqualTo(sVarSet(varA(), varB()));
    }
  }

  @Nested
  class _withAddedAll {
    @Test
    void empty_set_is_not_changed() {
      var varSet = sVarSet(varA());
      assertThat(varSet.addAll(sVarSet())).isEqualTo(sVarSet(varA()));
    }

    @Test
    void itself() {
      var varSet = sVarSet(varA());
      assertThat(varSet.addAll(varSet)).isEqualTo(sVarSet(varA()));
    }

    @Test
    void other_varset() {
      var varSet = sVarSet(varA());
      assertThat(varSet.addAll(sVarSet(varB()))).isEqualTo(sVarSet(varA(), varB()));
    }
  }

  @Nested
  class withRemovedAll {
    @Test
    void empty_set() {
      var varSet = sVarSet(varA());
      assertThat(varSet.removeAll(set())).isEqualTo(sVarSet(varA()));
    }

    @Test
    void other_non_overlapping_set() {
      var varSet = sVarSet(varA());
      assertThat(varSet.removeAll(set(varB()))).isEqualTo(sVarSet(varA()));
    }

    @Test
    void other_overlapping_set() {
      var varSet = sVarSet(varA(), varB(), varC());
      assertThat(varSet.removeAll(set(varB()))).isEqualTo(sVarSet(varA(), varC()));
    }
  }

  @Test
  void to_list() {
    var varSet = sVarSet(varB(), varC(), varA());
    assertThat(varSet.toList()).isEqualTo(list(varA(), varB(), varC()));
  }

  @Test
  void equals_and_hash_code() {
    new EqualsTester()
        .addEqualityGroup(sVarSet(), sVarSet())
        .addEqualityGroup(sVarSet(varA()), sVarSet(varA()))
        .addEqualityGroup(sVarSet(varA(), varB()), sVarSet(varA(), varB()))
        .addEqualityGroup(
            sVarSet(varC(), varA(), varB()),
            sVarSet(varA(), varB(), varC()),
            sVarSet(varC(), varA(), varB()))
        .testEquals();
  }

  @Test
  void to_string() {
    var varSet = sVarSet(varC(), varA(), varB());
    assertThat(varSet.toString()).isEqualTo("<A,B,C>");
  }
}
