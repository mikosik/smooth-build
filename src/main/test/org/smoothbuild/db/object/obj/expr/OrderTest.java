package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContextImpl;

import com.google.common.collect.ImmutableList;

public class OrderTest extends TestingContextImpl {
  @Test
  public void spec_of_empty_array_is_inferred_correctly() {
    assertThat(orderExpr(list()).spec())
        .isEqualTo(orderSpec(nothingSpec()));
  }

  @Test
  public void spec_of_array_is_inferred_correctly() {
    assertThat(orderExpr(list(intExpr(3))).spec())
        .isEqualTo(orderSpec(intSpec()));
  }

  @Test
  public void creating_array_with_elements_with_different_evaluation_spec_causes_exception() {
    assertCall(() -> orderExpr(list(intExpr(3), strExpr("abc"))).spec())
        .throwsException(new IllegalArgumentException("Element evaluation specs are not equal "
            + intSpec().name() + " != " + strSpec().name() + "."));
  }

  @Test
  public void elements_returns_elements() {
    ImmutableList<Const> elements = list(intExpr(2));
    assertThat(orderExpr(elements).elements())
        .isEqualTo(elements);
  }

  @Test
  public void arrays_with_equal_elements_are_equal() {
    List<Const> elements = list(intExpr(2)) ;
    assertThat(orderExpr(elements))
        .isEqualTo(orderExpr(elements));
  }

  @Test
  public void arrays_with_different_elements_are_not_equal() {
    assertThat(orderExpr(list(intExpr(1))))
        .isNotEqualTo(orderExpr(list(intExpr(2))));
  }

  @Test
  public void hash_of_arrays_with_equal_elements_is_the_same() {
    ImmutableList<Const> elements = list(intExpr(1));
    assertThat(orderExpr(elements).hash())
        .isEqualTo(orderExpr(elements).hash());
  }

  @Test
  public void hash_of_arrays_with_different_elements_is_not_the_same() {
    assertThat(orderExpr(list(intExpr(1))).hash())
        .isNotEqualTo(orderExpr(list(intExpr(2))).hash());
  }

  @Test
  public void hash_code_of_arrays_with_equal_elements_is_the_same() {
    ImmutableList<Const> elements = list(intExpr(1));
    assertThat(orderExpr(elements).hashCode())
        .isEqualTo(orderExpr(elements).hashCode());
  }

  @Test
  public void hash_code_of_arrays_with_different_elements_is_not_the_same() {
    assertThat(orderExpr(list(intExpr(1))).hashCode())
        .isNotEqualTo(orderExpr(list(intExpr(2))).hashCode());
  }

  @Test
  public void array_can_be_read_back_by_hash() {
    Order array = orderExpr(list(intExpr(1)));
    assertThat(objectDbOther().get(array.hash()))
        .isEqualTo(array);
  }

  @Test
  public void array_read_back_by_hash_has_same_elements() {
    ImmutableList<Const> elements = list(intExpr(1));
    Order array = orderExpr(elements);
    assertThat(((Order) objectDbOther().get(array.hash())).elements())
        .isEqualTo(elements);
  }

  @Test
  public void to_string() {
    Order array = orderExpr(list(intExpr(1)));
    assertThat(array.toString())
        .isEqualTo("Order(???)@" + array.hash());
  }
}
