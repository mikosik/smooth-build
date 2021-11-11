package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class OrderHTest extends TestingContext {
  @Test
  public void type_of_empty_array_is_inferred_correctly() {
    assertThat(orderH(list()).type())
        .isEqualTo(orderHT(nothingHT()));
  }

  @Test
  public void type_of_array_is_inferred_correctly() {
    assertThat(orderH(list(intHE(3))).type())
        .isEqualTo(orderHT(intHT()));
  }

  @Test
  public void creating_array_with_elements_with_different_evaluation_type_causes_exception() {
    assertCall(() -> orderH(list(intHE(3), stringHE("abc"))).type())
        .throwsException(new IllegalArgumentException("Element evaluation types are not equal "
            + intHT().name() + " != " + stringHT().name() + "."));
  }

  @Test
  public void elements_returns_elements() {
    ImmutableList<ExprH> elements = list(intHE(2));
    assertThat(orderH(elements).elements())
        .isEqualTo(elements);
  }

  @Test
  public void arrays_with_equal_elements_are_equal() {
    ImmutableList<ExprH> elements = list(intHE(2)) ;
    assertThat(orderH(elements))
        .isEqualTo(orderH(elements));
  }

  @Test
  public void arrays_with_different_elements_are_not_equal() {
    assertThat(orderH(list(intHE(1))))
        .isNotEqualTo(orderH(list(intHE(2))));
  }

  @Test
  public void hash_of_arrays_with_equal_elements_is_the_same() {
    ImmutableList<ExprH> elements = list(intHE(1));
    assertThat(orderH(elements).hash())
        .isEqualTo(orderH(elements).hash());
  }

  @Test
  public void hash_of_arrays_with_different_elements_is_not_the_same() {
    assertThat(orderH(list(intHE(1))).hash())
        .isNotEqualTo(orderH(list(intHE(2))).hash());
  }

  @Test
  public void hash_code_of_arrays_with_equal_elements_is_the_same() {
    ImmutableList<ExprH> elements = list(intHE(1));
    assertThat(orderH(elements).hashCode())
        .isEqualTo(orderH(elements).hashCode());
  }

  @Test
  public void hash_code_of_arrays_with_different_elements_is_not_the_same() {
    assertThat(orderH(list(intHE(1))).hashCode())
        .isNotEqualTo(orderH(list(intHE(2))).hashCode());
  }

  @Test
  public void array_can_be_read_back_by_hash() {
    OrderH array = orderH(list(intHE(1)));
    assertThat(objectHDbOther().get(array.hash()))
        .isEqualTo(array);
  }

  @Test
  public void array_read_back_by_hash_has_same_elements() {
    ImmutableList<ExprH> elements = list(intHE(1));
    OrderH array = orderH(elements);
    assertThat(((OrderH) objectHDbOther().get(array.hash())).elements())
        .isEqualTo(elements);
  }

  @Test
  public void to_string() {
    OrderH array = orderH(list(intHE(1)));
    assertThat(array.toString())
        .isEqualTo("Order(???)@" + array.hash());
  }
}
