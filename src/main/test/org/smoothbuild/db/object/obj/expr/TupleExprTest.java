package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContextImpl;

public class TupleExprTest extends TestingContextImpl {
  @Test
  public void spec_of_empty_tuple_expr_is_inferred_correctly() {
    assertThat(tupleExpr(list()).spec())
        .isEqualTo(tupleExprSpec(list()));
  }

  @Test
  public void spec_of_tuple_expr_is_inferred_correctly() {
    assertThat(tupleExpr(list(intExpr(3))).spec())
        .isEqualTo(tupleExprSpec(list(intSpec())));
  }

  @Test
  public void items_returns_items() {
    var items = list(intExpr(1), strExpr("abc"));
    assertThat(tupleExpr(items).items())
        .isEqualTo(items);
  }

  @Test
  public void tuple_expr_with_equal_items_are_equal() {
    var items = list(intExpr(1), strExpr("abc"));
    assertThat(tupleExpr(items))
        .isEqualTo(tupleExpr(items));
  }

  @Test
  public void tuple_expr_with_different_items_are_not_equal() {
    assertThat(tupleExpr(list(intExpr(1))))
        .isNotEqualTo(tupleExpr(list(intExpr(2))));
  }

  @Test
  public void hash_of_tuple_expr_with_equal_items_is_the_same() {
    var items = list(intExpr(1));
    assertThat(tupleExpr(items).hash())
        .isEqualTo(tupleExpr(items).hash());
  }

  @Test
  public void hash_of_tuple_expr_with_different_items_is_not_the_same() {
    assertThat(tupleExpr(list(intExpr(1))).hash())
        .isNotEqualTo(tupleExpr(list(intExpr(2))).hash());
  }

  @Test
  public void hash_code_of_tuple_expr_with_equal_items_is_the_same() {
    var items = list(intExpr(1));
    assertThat(tupleExpr(items).hashCode())
        .isEqualTo(tupleExpr(items).hashCode());
  }

  @Test
  public void hash_code_of_tuple_expr_with_different_items_is_not_the_same() {
    assertThat(tupleExpr(list(intExpr(1))).hashCode())
        .isNotEqualTo(tupleExpr(list(intExpr(2))).hashCode());
  }

  @Test
  public void tuple_expr_can_be_read_back_by_hash() {
    TupleExpr expr = tupleExpr(list(intExpr(1)));
    assertThat(objectDbOther().get(expr.hash()))
        .isEqualTo(expr);
  }

  @Test
  public void tuple_expr_read_back_by_hash_has_same_items() {
    var items = list(intExpr(), strExpr());
    TupleExpr expr = tupleExpr(items);
    assertThat(((TupleExpr) objectDbOther().get(expr.hash())).items())
        .isEqualTo(items);
  }

  @Test
  public void to_string() {
    TupleExpr expr = tupleExpr(list(intExpr(1)));
    assertThat(expr.toString())
        .isEqualTo("TupleExpr(???)@" + expr.hash());
  }
}
