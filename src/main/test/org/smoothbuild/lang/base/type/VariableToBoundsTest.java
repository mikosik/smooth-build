package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.Bounds.oneSideBound;
import static org.smoothbuild.lang.base.type.Side.LOWER;
import static org.smoothbuild.lang.base.type.Side.UPPER;
import static org.smoothbuild.lang.base.type.TestingTypes.A;
import static org.smoothbuild.lang.base.type.TestingTypes.B;
import static org.smoothbuild.lang.base.type.TestingTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class VariableToBoundsTest {
  @Nested
  class _addBounds {
    @Test
    public void bounds_for_the_same_type_variable_are_merged() {
      VariableToBounds variableToBounds = VariableToBounds.empty()
          .addBounds(A, oneSideBound(UPPER, BOOL))
          .addBounds(A, oneSideBound(LOWER, STRING));
      assertThat(variableToBounds.boundsMap().get(A))
          .isEqualTo(new Bounds(STRING, BOOL));
    }

    @Test
    public void bounds_for_different_type_variables_are_not_merged() {
      VariableToBounds variableToBounds = VariableToBounds.empty()
          .addBounds(A, oneSideBound(UPPER, BOOL))
          .addBounds(B, oneSideBound(LOWER, STRING));
      assertThat(variableToBounds.boundsMap().get(A)).isEqualTo(oneSideBound(UPPER, BOOL));
      assertThat(variableToBounds.boundsMap().get(B)).isEqualTo(oneSideBound(LOWER, STRING));
    }
  }

  @Nested
  class _mergeWith {
    @Test
    public void bounds_for_the_same_type_variable_are_merged() {
      VariableToBounds toMerge = VariableToBounds.empty()
          .addBounds(A, oneSideBound(LOWER, STRING));
      VariableToBounds variableToBounds = VariableToBounds.empty()
          .addBounds(A, oneSideBound(UPPER, BOOL))
          .mergeWith(toMerge);
      assertThat(variableToBounds.boundsMap().get(A))
          .isEqualTo(new Bounds(STRING, BOOL));
    }

    @Test
    public void bounds_for_different_type_variables_are_not_merged() {
      VariableToBounds toMerge = VariableToBounds.empty()
          .addBounds(B, oneSideBound(LOWER, STRING));
      VariableToBounds variableToBounds = VariableToBounds.empty()
          .addBounds(A, oneSideBound(UPPER, BOOL))
          .mergeWith(toMerge);
      assertThat(variableToBounds.boundsMap().get(A)).isEqualTo(oneSideBound(UPPER, BOOL));
      assertThat(variableToBounds.boundsMap().get(B)).isEqualTo(oneSideBound(LOWER, STRING));
    }
  }

  @Test
  public void constraints_object_is_immutable() {
    VariableToBounds empty = VariableToBounds.empty();
    empty.addBounds(A, oneSideBound(UPPER, BOOL));
    assertThat(empty)
        .isEqualTo(VariableToBounds.empty());
  }

  @Test
  public void equality() {
    new EqualsTester()
        .addEqualityGroup(VariableToBounds.empty())
        .addEqualityGroup(VariableToBounds.empty().addBounds(A, oneSideBound(LOWER, STRING)))
        .addEqualityGroup(VariableToBounds.empty().addBounds(A, oneSideBound(UPPER, STRING)))
        .addEqualityGroup(VariableToBounds.empty().addBounds(A, oneSideBound(LOWER, BOOL)))
        .addEqualityGroup(
            VariableToBounds.empty().addBounds(B, oneSideBound(LOWER, STRING)),
            VariableToBounds.empty().addBounds(B, oneSideBound(LOWER, STRING)));
  }
}
