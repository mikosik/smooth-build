package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class RecExprTest extends TestingContext {
  @Test
  public void spec_of_empty_erecord_is_inferred_correctly() {
    assertThat(eRecExpr(list()).spec())
        .isEqualTo(recExprSpec(list()));
  }

  @Test
  public void spec_of_erecord_is_inferred_correctly() {
    assertThat(eRecExpr(list(intExpr(3))).spec())
        .isEqualTo(recExprSpec(list(intSpec())));
  }

  @Test
  public void items_returns_items() {
    var items = list(intExpr(1), strExpr("abc"));
    assertThat(eRecExpr(items).items())
        .isEqualTo(items);
  }

  @Test
  public void erecords_with_equal_items_are_equal() {
    var items = list(intExpr(1), strExpr("abc"));
    assertThat(eRecExpr(items))
        .isEqualTo(eRecExpr(items));
  }

  @Test
  public void erecords_with_different_items_are_not_equal() {
    assertThat(eRecExpr(list(intExpr(1))))
        .isNotEqualTo(eRecExpr(list(intExpr(2))));
  }

  @Test
  public void hash_of_erecords_with_equal_items_is_the_same() {
    var items = list(intExpr(1));
    assertThat(eRecExpr(items).hash())
        .isEqualTo(eRecExpr(items).hash());
  }

  @Test
  public void hash_of_erecords_with_different_items_is_not_the_same() {
    assertThat(eRecExpr(list(intExpr(1))).hash())
        .isNotEqualTo(eRecExpr(list(intExpr(2))).hash());
  }

  @Test
  public void hash_code_of_erecords_with_equal_items_is_the_same() {
    var items = list(intExpr(1));
    assertThat(eRecExpr(items).hashCode())
        .isEqualTo(eRecExpr(items).hashCode());
  }

  @Test
  public void hash_code_of_erecords_with_different_items_is_not_the_same() {
    assertThat(eRecExpr(list(intExpr(1))).hashCode())
        .isNotEqualTo(eRecExpr(list(intExpr(2))).hashCode());
  }

  @Test
  public void erecord_can_be_read_back_by_hash() {
    RecExpr record = eRecExpr(list(intExpr(1)));
    assertThat(objectDbOther().get(record.hash()))
        .isEqualTo(record);
  }

  @Test
  public void erecord_read_back_by_hash_has_same_items() {
    var items = list(intExpr(), strExpr());
    RecExpr record = eRecExpr(items);
    assertThat(((RecExpr) objectDbOther().get(record.hash())).items())
        .isEqualTo(items);
  }

  @Test
  public void to_string() {
    RecExpr record = eRecExpr(list(intExpr(1)));
    assertThat(record.toString())
        .isEqualTo("RecExpr(???)@" + record.hash());
  }
}
