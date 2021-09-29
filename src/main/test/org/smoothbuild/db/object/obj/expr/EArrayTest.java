package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class EArrayTest extends TestingContext {
  @Test
  public void spec_of_empty_earray_is_inferred_correctly() {
    assertThat(eArrayExpr(list()).spec())
        .isEqualTo(eArraySpec(nothingSpec()));
  }

  @Test
  public void spec_of_earray_is_inferred_correctly() {
    assertThat(eArrayExpr(list(intExpr(3))).spec())
        .isEqualTo(eArraySpec(intSpec()));
  }

  @Test
  public void creating_earray_with_elements_with_different_evaluation_spec_causes_exception() {
    assertCall(() -> eArrayExpr(list(intExpr(3), strExpr("abc"))).spec())
        .throwsException(new IllegalArgumentException("Element evaluation specs are not equal "
            + intSpec().name() + " != " + strSpec().name() + "."));
  }

  @Test
  public void elements_returns_elements() {
    ImmutableList<Const> elements = list(intExpr(2));
    assertThat(eArrayExpr(elements).elements())
        .isEqualTo(elements);
  }

  @Test
  public void earrays_with_equal_elements_are_equal() {
    List<Const> elements = list(intExpr(2)) ;
    assertThat(eArrayExpr(elements))
        .isEqualTo(eArrayExpr(elements));
  }

  @Test
  public void earrays_with_different_elements_are_not_equal() {
    assertThat(eArrayExpr(list(intExpr(1))))
        .isNotEqualTo(eArrayExpr(list(intExpr(2))));
  }

  @Test
  public void hash_of_earrays_with_equal_elements_is_the_same() {
    ImmutableList<Const> elements = list(intExpr(1));
    assertThat(eArrayExpr(elements).hash())
        .isEqualTo(eArrayExpr(elements).hash());
  }

  @Test
  public void hash_of_earrays_with_different_elements_is_not_the_same() {
    assertThat(eArrayExpr(list(intExpr(1))).hash())
        .isNotEqualTo(eArrayExpr(list(intExpr(2))).hash());
  }

  @Test
  public void hash_code_of_earrays_with_equal_elements_is_the_same() {
    ImmutableList<Const> elements = list(intExpr(1));
    assertThat(eArrayExpr(elements).hashCode())
        .isEqualTo(eArrayExpr(elements).hashCode());
  }

  @Test
  public void hash_code_of_earrays_with_different_elements_is_not_the_same() {
    assertThat(eArrayExpr(list(intExpr(1))).hashCode())
        .isNotEqualTo(eArrayExpr(list(intExpr(2))).hashCode());
  }

  @Test
  public void earray_can_be_read_back_by_hash() {
    EArray array = eArrayExpr(list(intExpr(1)));
    assertThat(objectDbOther().get(array.hash()))
        .isEqualTo(array);
  }

  @Test
  public void earray_read_back_by_hash_has_same_elements() {
    ImmutableList<Const> elements = list(intExpr(1));
    EArray array = eArrayExpr(elements);
    assertThat(((EArray) objectDbOther().get(array.hash())).elements())
        .isEqualTo(elements);
  }

  @Test
  public void to_string() {
    EArray array = eArrayExpr(list(intExpr(1)));
    assertThat(array.toString())
        .isEqualTo("EArray(???):" + array.hash());
  }
}
