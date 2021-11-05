package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class OrderTest extends TestingContext {
  @Test
  public void type_of_empty_array_is_inferred_correctly() {
    assertThat(order(list()).type())
        .isEqualTo(orderOT(nothingOT()));
  }

  @Test
  public void type_of_array_is_inferred_correctly() {
    assertThat(order(list(intExpr(3))).type())
        .isEqualTo(orderOT(intOT()));
  }

  @Test
  public void creating_array_with_elements_with_different_evaluation_type_causes_exception() {
    assertCall(() -> order(list(intExpr(3), stringExpr("abc"))).type())
        .throwsException(new IllegalArgumentException("Element evaluation types are not equal "
            + intOT().name() + " != " + stringOT().name() + "."));
  }

  @Test
  public void elements_returns_elements() {
    ImmutableList<Expr> elements = list(intExpr(2));
    assertThat(order(elements).elements())
        .isEqualTo(elements);
  }

  @Test
  public void arrays_with_equal_elements_are_equal() {
    ImmutableList<Expr> elements = list(intExpr(2)) ;
    assertThat(order(elements))
        .isEqualTo(order(elements));
  }

  @Test
  public void arrays_with_different_elements_are_not_equal() {
    assertThat(order(list(intExpr(1))))
        .isNotEqualTo(order(list(intExpr(2))));
  }

  @Test
  public void hash_of_arrays_with_equal_elements_is_the_same() {
    ImmutableList<Expr> elements = list(intExpr(1));
    assertThat(order(elements).hash())
        .isEqualTo(order(elements).hash());
  }

  @Test
  public void hash_of_arrays_with_different_elements_is_not_the_same() {
    assertThat(order(list(intExpr(1))).hash())
        .isNotEqualTo(order(list(intExpr(2))).hash());
  }

  @Test
  public void hash_code_of_arrays_with_equal_elements_is_the_same() {
    ImmutableList<Expr> elements = list(intExpr(1));
    assertThat(order(elements).hashCode())
        .isEqualTo(order(elements).hashCode());
  }

  @Test
  public void hash_code_of_arrays_with_different_elements_is_not_the_same() {
    assertThat(order(list(intExpr(1))).hashCode())
        .isNotEqualTo(order(list(intExpr(2))).hashCode());
  }

  @Test
  public void array_can_be_read_back_by_hash() {
    Order array = order(list(intExpr(1)));
    assertThat(objectDbOther().get(array.hash()))
        .isEqualTo(array);
  }

  @Test
  public void array_read_back_by_hash_has_same_elements() {
    ImmutableList<Expr> elements = list(intExpr(1));
    Order array = order(elements);
    assertThat(((Order) objectDbOther().get(array.hash())).elements())
        .isEqualTo(elements);
  }

  @Test
  public void to_string() {
    Order array = order(list(intExpr(1)));
    assertThat(array.toString())
        .isEqualTo("Order(???)@" + array.hash());
  }
}
