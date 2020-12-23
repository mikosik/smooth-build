package org.smoothbuild.lang.base.type.constraint;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.TestingTypes.A;
import static org.smoothbuild.lang.base.type.TestingTypes.B;
import static org.smoothbuild.lang.base.type.TestingTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.constraint.Bounds.oneSideBound;
import static org.smoothbuild.lang.base.type.constraint.Side.LOWER;
import static org.smoothbuild.lang.base.type.constraint.Side.UPPER;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class ConstraintsTest {
  @Nested
  class _addBounds {
    @Test
    public void bounds_for_the_same_type_variable_are_merged() {
      Constraints constraints = Constraints.empty()
          .addBounds(A, oneSideBound(UPPER, BOOL))
          .addBounds(A, oneSideBound(LOWER, STRING));
      assertThat(constraints.boundsMap().get(A))
          .isEqualTo(new Bounds(STRING, BOOL));
    }

    @Test
    public void bounds_for_different_type_variables_are_not_merged() {
      Constraints constraints = Constraints.empty()
          .addBounds(A, oneSideBound(UPPER, BOOL))
          .addBounds(B, oneSideBound(LOWER, STRING));
      assertThat(constraints.boundsMap().get(A)).isEqualTo(oneSideBound(UPPER, BOOL));
      assertThat(constraints.boundsMap().get(B)).isEqualTo(oneSideBound(LOWER, STRING));
    }
  }

  @Nested
  class _mergeWith {
    @Test
    public void bounds_for_the_same_type_variable_are_merged() {
      Constraints toMerge = Constraints.empty()
          .addBounds(A, oneSideBound(LOWER, STRING));
      Constraints constraints = Constraints.empty()
          .addBounds(A, oneSideBound(UPPER, BOOL))
          .mergeWith(toMerge);
      assertThat(constraints.boundsMap().get(A))
          .isEqualTo(new Bounds(STRING, BOOL));
    }

    @Test
    public void bounds_for_different_type_variables_are_not_merged() {
      Constraints toMerge = Constraints.empty()
          .addBounds(B, oneSideBound(LOWER, STRING));
      Constraints constraints = Constraints.empty()
          .addBounds(A, oneSideBound(UPPER, BOOL))
          .mergeWith(toMerge);
      assertThat(constraints.boundsMap().get(A)).isEqualTo(oneSideBound(UPPER, BOOL));
      assertThat(constraints.boundsMap().get(B)).isEqualTo(oneSideBound(LOWER, STRING));
    }
  }

  @Test
  public void constraints_object_is_immutable() {
    Constraints empty = Constraints.empty();
    empty.addBounds(A, oneSideBound(UPPER, BOOL));
    assertThat(empty)
        .isEqualTo(Constraints.empty());
  }

  @Test
  public void equality() {
    new EqualsTester()
        .addEqualityGroup(Constraints.empty())
        .addEqualityGroup(Constraints.empty().addBounds(A, oneSideBound(LOWER, STRING)))
        .addEqualityGroup(Constraints.empty().addBounds(A, oneSideBound(UPPER, STRING)))
        .addEqualityGroup(Constraints.empty().addBounds(A, oneSideBound(LOWER, BOOL)))
        .addEqualityGroup(
            Constraints.empty().addBounds(B, oneSideBound(LOWER, STRING)),
            Constraints.empty().addBounds(B, oneSideBound(LOWER, STRING)));
  }
}
