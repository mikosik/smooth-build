package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.Bounds.oneSideBound;
import static org.smoothbuild.lang.base.type.BoundsMap.merge;
import static org.smoothbuild.lang.base.type.TestingTypes.A;
import static org.smoothbuild.lang.base.type.TestingTypes.B;
import static org.smoothbuild.lang.base.type.TestingTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.Types.lower;
import static org.smoothbuild.lang.base.type.Types.upper;
import static org.smoothbuild.lang.base.type.constraint.TestingBoundsMap.bm;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class BoundsMapTest {
  @Test
  public void bounds_for_the_same_type_variable_are_merged() {
    BoundsMap bv1 = bm(A, lower(), STRING);
    BoundsMap bv2 = bm(A, upper(), BOOL);

    BoundsMap merged = merge(list(bv2, bv1));
    assertThat(merged.map().get(A).bounds())
        .isEqualTo(new Bounds(STRING, BOOL));
  }

  @Test
  public void bounds_for_different_type_variables_are_not_merged() {
    BoundsMap bv1 = bm(B, lower(), STRING);
    BoundsMap bv2 = bm(A, upper(), BOOL);

    BoundsMap merged = merge(list(bv1, bv2));
    assertThat(merged.map().get(A).bounds())
        .isEqualTo(oneSideBound(upper(), BOOL));
    assertThat(merged.map().get(B).bounds())
        .isEqualTo(oneSideBound(lower(), STRING));
  }

  @Test
  public void two_bounded_variables_are_merged_even_when_vars_were_added_in_different_order() {
    BoundsMap bv1 = bm(A, lower(), STRING, B, lower(), STRING);
    BoundsMap bv2 = bm(B, upper(), BOOL, A, upper(), BOOL);

    BoundsMap merged = merge(list(bv1, bv2));
    assertThat(merged.map().get(A).bounds())
        .isEqualTo(new Bounds(STRING, BOOL));
    assertThat(merged.map().get(B).bounds())
        .isEqualTo(new Bounds(STRING, BOOL));
  }

  @Test
  public void bounded_variables_object_is_immutable() {
    BoundsMap empty = bm();
    empty.mergeWith(list(new Bounded(A, oneSideBound(upper(), BOOL))));
    assertThat(empty)
        .isEqualTo(bm());
  }

  @Test
  public void equality() {
    new EqualsTester()
        .addEqualityGroup(bm())
        .addEqualityGroup(bm(A, lower(), STRING))
        .addEqualityGroup(bm(A, upper(), STRING))
        .addEqualityGroup(bm(A, lower(), BOOL))
        .addEqualityGroup(
            bm(B, lower(), STRING), bm(B, lower(), STRING));
  }
}
