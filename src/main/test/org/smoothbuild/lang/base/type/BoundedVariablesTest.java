package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.BoundedVariables.merge;
import static org.smoothbuild.lang.base.type.Bounds.oneSideBound;
import static org.smoothbuild.lang.base.type.Side.LOWER;
import static org.smoothbuild.lang.base.type.Side.UPPER;
import static org.smoothbuild.lang.base.type.TestingTypes.A;
import static org.smoothbuild.lang.base.type.TestingTypes.B;
import static org.smoothbuild.lang.base.type.TestingTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class BoundedVariablesTest {
  @Nested
  class _addBounds {
    @Test
    public void bounds_for_the_same_type_variable_are_merged() {
      BoundedVariables boundedVariables = BoundedVariables.empty()
          .addBounds(A, oneSideBound(UPPER, BOOL))
          .addBounds(A, oneSideBound(LOWER, STRING));
      assertThat(boundedVariables.boundsMap().get(A))
          .isEqualTo(new Bounds(STRING, BOOL));
    }

    @Test
    public void bounds_for_different_type_variables_are_not_merged() {
      BoundedVariables boundedVariables = BoundedVariables.empty()
          .addBounds(A, oneSideBound(UPPER, BOOL))
          .addBounds(B, oneSideBound(LOWER, STRING));
      assertThat(boundedVariables.boundsMap().get(A)).isEqualTo(oneSideBound(UPPER, BOOL));
      assertThat(boundedVariables.boundsMap().get(B)).isEqualTo(oneSideBound(LOWER, STRING));
    }
  }

  @Nested
  class _mergeWith {
    @Test
    public void bounds_for_the_same_type_variable_are_merged() {
      BoundedVariables toMerge = BoundedVariables.empty()
          .addBounds(A, oneSideBound(LOWER, STRING));
      BoundedVariables boundedVariables = BoundedVariables.empty()
          .addBounds(A, oneSideBound(UPPER, BOOL))
          .mergeWith(toMerge);
      assertThat(boundedVariables.boundsMap().get(A))
          .isEqualTo(new Bounds(STRING, BOOL));
    }

    @Test
    public void bounds_for_different_type_variables_are_not_merged() {
      BoundedVariables toMerge = BoundedVariables.empty()
          .addBounds(B, oneSideBound(LOWER, STRING));
      BoundedVariables boundedVariables = BoundedVariables.empty()
          .addBounds(A, oneSideBound(UPPER, BOOL))
          .mergeWith(toMerge);
      assertThat(boundedVariables.boundsMap().get(A)).isEqualTo(oneSideBound(UPPER, BOOL));
      assertThat(boundedVariables.boundsMap().get(B)).isEqualTo(oneSideBound(LOWER, STRING));
    }

    @Test
    public void mergeWith_returns_other_instance_when_this_is_empty() {
      BoundedVariables boundedVariables = BoundedVariables.empty();
      BoundedVariables toMerge = BoundedVariables.empty()
          .addBounds(B, oneSideBound(LOWER, STRING));
      assertThat(boundedVariables.mergeWith(toMerge))
          .isSameInstanceAs(toMerge);
    }

    @Test
    public void mergeWith_returns_this_instance_when_other_is_empty() {
      BoundedVariables boundedVariables = BoundedVariables.empty()
          .addBounds(B, oneSideBound(LOWER, STRING));
      BoundedVariables toMerge = BoundedVariables.empty();
      assertThat(boundedVariables.mergeWith(toMerge))
          .isSameInstanceAs(boundedVariables);
    }
  }

  @Nested
  class _merge_list_elements {
    @Test
    public void bounds_for_the_same_type_variable_are_merged() {
      BoundedVariables toMerge = BoundedVariables.empty()
          .addBounds(A, oneSideBound(LOWER, STRING));
      BoundedVariables boundedVariables = BoundedVariables.empty()
          .addBounds(A, oneSideBound(UPPER, BOOL));
      assertThat(merge(list(toMerge, boundedVariables)))
          .isEqualTo(BoundedVariables.empty().addBounds(A, new Bounds(STRING, BOOL)));
    }

    @Test
    public void bounds_for_different_type_variables_are_not_merged() {
      BoundedVariables toMerge = BoundedVariables.empty()
          .addBounds(B, oneSideBound(LOWER, STRING));
      BoundedVariables boundedVariables = BoundedVariables.empty()
          .addBounds(A, oneSideBound(UPPER, BOOL));
      BoundedVariables actual = merge(list(toMerge, boundedVariables));
      assertThat(actual.boundsMap().get(A)).isEqualTo(oneSideBound(UPPER, BOOL));
      assertThat(actual.boundsMap().get(B)).isEqualTo(oneSideBound(LOWER, STRING));
    }

    @Test
    public void mergeWith_returns_other_instance_when_this_is_empty() {
      BoundedVariables boundedVariables = BoundedVariables.empty();
      BoundedVariables toMerge = BoundedVariables.empty()
          .addBounds(B, oneSideBound(LOWER, STRING));
      assertThat(merge(list(toMerge, boundedVariables)))
          .isSameInstanceAs(toMerge);
    }

    @Test
    public void mergeWith_returns_this_instance_when_other_is_empty() {
      BoundedVariables boundedVariables = BoundedVariables.empty()
          .addBounds(B, oneSideBound(LOWER, STRING));
      BoundedVariables toMerge = BoundedVariables.empty();
      assertThat(merge(list(toMerge, boundedVariables)))
          .isSameInstanceAs(boundedVariables);
    }
  }

  @Nested
  class _merge_two_list_elements {
    @Test
    public void bounds_for_the_same_type_variable_are_merged() {
      BoundedVariables toMerge = BoundedVariables.empty()
          .addBounds(A, oneSideBound(LOWER, STRING));
      BoundedVariables boundedVariables = BoundedVariables.empty()
          .addBounds(A, oneSideBound(UPPER, BOOL));
      assertThat(merge(list(toMerge), list(boundedVariables)))
          .isEqualTo(BoundedVariables.empty().addBounds(A, new Bounds(STRING, BOOL)));
    }

    @Test
    public void bounds_for_different_type_variables_are_not_merged() {
      BoundedVariables toMerge = BoundedVariables.empty()
          .addBounds(B, oneSideBound(LOWER, STRING));
      BoundedVariables boundedVariables = BoundedVariables.empty()
          .addBounds(A, oneSideBound(UPPER, BOOL));
      BoundedVariables actual = merge(list(toMerge), list(boundedVariables));
      assertThat(actual.boundsMap().get(A)).isEqualTo(oneSideBound(UPPER, BOOL));
      assertThat(actual.boundsMap().get(B)).isEqualTo(oneSideBound(LOWER, STRING));
    }

    @Test
    public void mergeWith_returns_other_instance_when_this_is_empty() {
      BoundedVariables boundedVariables = BoundedVariables.empty();
      BoundedVariables toMerge = BoundedVariables.empty()
          .addBounds(B, oneSideBound(LOWER, STRING));
      assertThat(merge(list(toMerge), list(boundedVariables)))
          .isSameInstanceAs(toMerge);
    }

    @Test
    public void mergeWith_returns_this_instance_when_other_is_empty() {
      BoundedVariables boundedVariables = BoundedVariables.empty()
          .addBounds(B, oneSideBound(LOWER, STRING));
      BoundedVariables toMerge = BoundedVariables.empty();
      assertThat(merge(list(toMerge), list(boundedVariables)))
          .isSameInstanceAs(boundedVariables);
    }
  }

  @Test
  public void constraints_object_is_immutable() {
    BoundedVariables empty = BoundedVariables.empty();
    empty.addBounds(A, oneSideBound(UPPER, BOOL));
    assertThat(empty)
        .isEqualTo(BoundedVariables.empty());
  }

  @Test
  public void equality() {
    new EqualsTester()
        .addEqualityGroup(BoundedVariables.empty())
        .addEqualityGroup(BoundedVariables.empty().addBounds(A, oneSideBound(LOWER, STRING)))
        .addEqualityGroup(BoundedVariables.empty().addBounds(A, oneSideBound(UPPER, STRING)))
        .addEqualityGroup(BoundedVariables.empty().addBounds(A, oneSideBound(LOWER, BOOL)))
        .addEqualityGroup(
            BoundedVariables.empty().addBounds(B, oneSideBound(LOWER, STRING)),
            BoundedVariables.empty().addBounds(B, oneSideBound(LOWER, STRING)));
  }
}
