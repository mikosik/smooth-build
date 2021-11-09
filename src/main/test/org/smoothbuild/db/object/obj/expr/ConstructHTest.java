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
    assertThat(construct(list()).type())
        .isEqualTo(constructOT(list()));
  }

  @Test
  public void type_of_construct_is_inferred_correctly() {
    assertThat(construct(list(intExpr(3))).type())
        .isEqualTo(constructOT(list(intOT())));
  }

  @Test
  public void items_returns_items() {
    ImmutableList<ExprH> items = list(intExpr(1), stringExpr("abc"));
    assertThat(construct(items).items())
        .isEqualTo(items);
  }

  @Test
  public void construct_with_equal_items_are_equal() {
    ImmutableList<ExprH> items = list(intExpr(1), stringExpr("abc"));
    assertThat(construct(items))
        .isEqualTo(construct(items));
  }

  @Test
  public void construct_with_different_items_are_not_equal() {
    assertThat(construct(list(intExpr(1))))
        .isNotEqualTo(construct(list(intExpr(2))));
  }

  @Test
  public void hash_of_construct_with_equal_items_is_the_same() {
    ImmutableList<ExprH> items = list(intExpr(1));
    assertThat(construct(items).hash())
        .isEqualTo(construct(items).hash());
  }

  @Test
  public void hash_of_construct_with_different_items_is_not_the_same() {
    assertThat(construct(list(intExpr(1))).hash())
        .isNotEqualTo(construct(list(intExpr(2))).hash());
  }

  @Test
  public void hash_code_of_construct_with_equal_items_is_the_same() {
    ImmutableList<ExprH> items = list(intExpr(1));
    assertThat(construct(items).hashCode())
        .isEqualTo(construct(items).hashCode());
  }

  @Test
  public void hash_code_of_construct_with_different_items_is_not_the_same() {
    assertThat(construct(list(intExpr(1))).hashCode())
        .isNotEqualTo(construct(list(intExpr(2))).hashCode());
  }

  @Test
  public void construct_can_be_read_back_by_hash() {
    ConstructH expr = construct(list(intExpr(1)));
    assertThat(objectDbOther().get(expr.hash()))
        .isEqualTo(expr);
  }

  @Test
  public void construct_read_back_by_hash_has_same_items() {
    ImmutableList<ExprH> items = list(intExpr(), stringExpr());
    ConstructH expr = construct(items);
    assertThat(((ConstructH) objectDbOther().get(expr.hash())).items())
        .isEqualTo(items);
  }

  @Test
  public void to_string() {
    ConstructH expr = construct(list(intExpr(1)));
    assertThat(expr.toString())
        .isEqualTo("CONSTRUCT(???)@" + expr.hash());
  }
}
