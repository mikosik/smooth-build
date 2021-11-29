package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class ConstructHTest extends TestingContext {
  @Test
  public void type_of_empty_construct_is_inferred_correctly() {
    assertThat(constructH(list()).spec())
        .isEqualTo(constructHT(list()));
  }

  @Test
  public void type_of_construct_is_inferred_correctly() {
    assertThat(constructH(list(intH(3))).spec())
        .isEqualTo(constructHT(list(intHT())));
  }

  @Test
  public void items_returns_items() {
    ImmutableList<ObjectH> items = list(intH(1), stringH("abc"));
    assertThat(constructH(items).items())
        .isEqualTo(items);
  }

  @Test
  public void construct_with_equal_items_are_equal() {
    ImmutableList<ObjectH> items = list(intH(1), stringH("abc"));
    assertThat(constructH(items))
        .isEqualTo(constructH(items));
  }

  @Test
  public void construct_with_different_items_are_not_equal() {
    assertThat(constructH(list(intH(1))))
        .isNotEqualTo(constructH(list(intH(2))));
  }

  @Test
  public void hash_of_construct_with_equal_items_is_the_same() {
    ImmutableList<ObjectH> items = list(intH(1));
    assertThat(constructH(items).hash())
        .isEqualTo(constructH(items).hash());
  }

  @Test
  public void hash_of_construct_with_different_items_is_not_the_same() {
    assertThat(constructH(list(intH(1))).hash())
        .isNotEqualTo(constructH(list(intH(2))).hash());
  }

  @Test
  public void hash_code_of_construct_with_equal_items_is_the_same() {
    ImmutableList<ObjectH> items = list(intH(1));
    assertThat(constructH(items).hashCode())
        .isEqualTo(constructH(items).hashCode());
  }

  @Test
  public void hash_code_of_construct_with_different_items_is_not_the_same() {
    assertThat(constructH(list(intH(1))).hashCode())
        .isNotEqualTo(constructH(list(intH(2))).hashCode());
  }

  @Test
  public void construct_can_be_read_back_by_hash() {
    ConstructH expr = constructH(list(intH(1)));
    assertThat(objectHDbOther().get(expr.hash()))
        .isEqualTo(expr);
  }

  @Test
  public void construct_read_back_by_hash_has_same_items() {
    ImmutableList<ObjectH> items = list(intH(), stringH());
    ConstructH expr = constructH(items);
    assertThat(((ConstructH) objectHDbOther().get(expr.hash())).items())
        .isEqualTo(items);
  }

  @Test
  public void to_string() {
    ConstructH expr = constructH(list(intH(1)));
    assertThat(expr.toString())
        .isEqualTo("CONSTRUCT(???)@" + expr.hash());
  }
}
