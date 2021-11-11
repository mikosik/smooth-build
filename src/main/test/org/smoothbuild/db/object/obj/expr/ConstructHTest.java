package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class ConstructHTest extends TestingContext {
  @Test
  public void type_of_empty_construct_is_inferred_correctly() {
    assertThat(constructH(list()).type())
        .isEqualTo(constructHT(list()));
  }

  @Test
  public void type_of_construct_is_inferred_correctly() {
    assertThat(constructH(list(intHE(3))).type())
        .isEqualTo(constructHT(list(intHT())));
  }

  @Test
  public void items_returns_items() {
    ImmutableList<ExprH> items = list(intHE(1), stringHE("abc"));
    assertThat(constructH(items).items())
        .isEqualTo(items);
  }

  @Test
  public void construct_with_equal_items_are_equal() {
    ImmutableList<ExprH> items = list(intHE(1), stringHE("abc"));
    assertThat(constructH(items))
        .isEqualTo(constructH(items));
  }

  @Test
  public void construct_with_different_items_are_not_equal() {
    assertThat(constructH(list(intHE(1))))
        .isNotEqualTo(constructH(list(intHE(2))));
  }

  @Test
  public void hash_of_construct_with_equal_items_is_the_same() {
    ImmutableList<ExprH> items = list(intHE(1));
    assertThat(constructH(items).hash())
        .isEqualTo(constructH(items).hash());
  }

  @Test
  public void hash_of_construct_with_different_items_is_not_the_same() {
    assertThat(constructH(list(intHE(1))).hash())
        .isNotEqualTo(constructH(list(intHE(2))).hash());
  }

  @Test
  public void hash_code_of_construct_with_equal_items_is_the_same() {
    ImmutableList<ExprH> items = list(intHE(1));
    assertThat(constructH(items).hashCode())
        .isEqualTo(constructH(items).hashCode());
  }

  @Test
  public void hash_code_of_construct_with_different_items_is_not_the_same() {
    assertThat(constructH(list(intHE(1))).hashCode())
        .isNotEqualTo(constructH(list(intHE(2))).hashCode());
  }

  @Test
  public void construct_can_be_read_back_by_hash() {
    ConstructH expr = constructH(list(intHE(1)));
    assertThat(objectHDbOther().get(expr.hash()))
        .isEqualTo(expr);
  }

  @Test
  public void construct_read_back_by_hash_has_same_items() {
    ImmutableList<ExprH> items = list(intHE(), stringHE());
    ConstructH expr = constructH(items);
    assertThat(((ConstructH) objectHDbOther().get(expr.hash())).items())
        .isEqualTo(items);
  }

  @Test
  public void to_string() {
    ConstructH expr = constructH(list(intHE(1)));
    assertThat(expr.toString())
        .isEqualTo("CONSTRUCT(???)@" + expr.hash());
  }
}
