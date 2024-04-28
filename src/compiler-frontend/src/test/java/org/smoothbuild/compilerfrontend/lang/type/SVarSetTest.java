package org.smoothbuild.compilerfrontend.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.varA;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.varB;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.varC;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SVarSetTest {
  @Nested
  class _filter {
    @Test
    void filter_empty() {
      var varSet = varSetS();
      assertThat(varSet.filter(v -> !v.equals(varC()))).isEqualTo(varSetS());
    }

    @Test
    void filter() {
      var varSet = varSetS(varB(), varC(), varA());
      assertThat(varSet.filter(v -> !v.equals(varC()))).isEqualTo(varSetS(varA(), varB()));
    }
  }

  @Nested
  class _withAddedAll {
    @Test
    void empty_set_is_not_changed() {
      var varSet = varSetS(varA());
      assertThat(varSet.withAddedAll(varSetS())).isEqualTo(varSetS(varA()));
    }

    @Test
    void itself() {
      var varSet = varSetS(varA());
      assertThat(varSet.withAddedAll(varSet)).isEqualTo(varSetS(varA()));
    }

    @Test
    void other_varset() {
      var varSet = varSetS(varA());
      assertThat(varSet.withAddedAll(varSetS(varB()))).isEqualTo(varSetS(varA(), varB()));
    }
  }

  @Nested
  class withRemovedAll {
    @Test
    void empty_set() {
      var varSet = varSetS(varA());
      assertThat(varSet.withRemovedAll(varSetS())).isEqualTo(varSetS(varA()));
    }

    @Test
    void other_non_overlapping_set() {
      var varSet = varSetS(varA());
      assertThat(varSet.withRemovedAll(varSetS(varB()))).isEqualTo(varSetS(varA()));
    }

    @Test
    void other_overlapping_set() {
      var varSet = varSetS(varA(), varB(), varC());
      assertThat(varSet.withRemovedAll(varSetS(varB()))).isEqualTo(varSetS(varA(), varC()));
    }
  }

  @Test
  void to_list() {
    var varSet = varSetS(varB(), varC(), varA());
    assertThat(varSet.toList()).isEqualTo(list(varA(), varB(), varC()));
  }

  @Test
  void equals_and_hash_code() {
    new EqualsTester()
        .addEqualityGroup(varSetS(), varSetS())
        .addEqualityGroup(varSetS(varA()), varSetS(varA()))
        .addEqualityGroup(varSetS(varA(), varB()), varSetS(varA(), varB()))
        .addEqualityGroup(
            varSetS(varC(), varA(), varB()),
            varSetS(varA(), varB(), varC()),
            varSetS(varC(), varA(), varB()))
        .testEquals();
  }

  @Test
  void to_string() {
    var varSet = varSetS(varC(), varA(), varB());
    assertThat(varSet.toString()).isEqualTo("<A,B,C>");
  }
}
