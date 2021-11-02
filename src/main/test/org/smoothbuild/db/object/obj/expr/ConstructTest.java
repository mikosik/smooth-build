package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContextImpl;

public class ConstructTest extends TestingContextImpl {
  @Test
  public void spec_of_empty_construct_is_inferred_correctly() {
    assertThat(constructExpr(list()).spec())
        .isEqualTo(constructSpec(list()));
  }

  @Test
  public void spec_of_construct_is_inferred_correctly() {
    assertThat(constructExpr(list(intExpr(3))).spec())
        .isEqualTo(constructSpec(list(intSpec())));
  }

  @Test
  public void items_returns_items() {
    var items = list(intExpr(1), strExpr("abc"));
    assertThat(constructExpr(items).items())
        .isEqualTo(items);
  }

  @Test
  public void construct_with_equal_items_are_equal() {
    var items = list(intExpr(1), strExpr("abc"));
    assertThat(constructExpr(items))
        .isEqualTo(constructExpr(items));
  }

  @Test
  public void construct_with_different_items_are_not_equal() {
    assertThat(constructExpr(list(intExpr(1))))
        .isNotEqualTo(constructExpr(list(intExpr(2))));
  }

  @Test
  public void hash_of_construct_with_equal_items_is_the_same() {
    var items = list(intExpr(1));
    assertThat(constructExpr(items).hash())
        .isEqualTo(constructExpr(items).hash());
  }

  @Test
  public void hash_of_construct_with_different_items_is_not_the_same() {
    assertThat(constructExpr(list(intExpr(1))).hash())
        .isNotEqualTo(constructExpr(list(intExpr(2))).hash());
  }

  @Test
  public void hash_code_of_construct_with_equal_items_is_the_same() {
    var items = list(intExpr(1));
    assertThat(constructExpr(items).hashCode())
        .isEqualTo(constructExpr(items).hashCode());
  }

  @Test
  public void hash_code_of_construct_with_different_items_is_not_the_same() {
    assertThat(constructExpr(list(intExpr(1))).hashCode())
        .isNotEqualTo(constructExpr(list(intExpr(2))).hashCode());
  }

  @Test
  public void construct_can_be_read_back_by_hash() {
    Construct expr = constructExpr(list(intExpr(1)));
    assertThat(objectDbOther().get(expr.hash()))
        .isEqualTo(expr);
  }

  @Test
  public void construct_read_back_by_hash_has_same_items() {
    var items = list(intExpr(), strExpr());
    Construct expr = constructExpr(items);
    assertThat(((Construct) objectDbOther().get(expr.hash())).items())
        .isEqualTo(items);
  }

  @Test
  public void to_string() {
    Construct expr = constructExpr(list(intExpr(1)));
    assertThat(expr.toString())
        .isEqualTo("CONSTRUCT(???)@" + expr.hash());
  }
}
