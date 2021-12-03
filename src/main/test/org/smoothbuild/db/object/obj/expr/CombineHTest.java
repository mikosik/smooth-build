package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class CombineHTest extends TestingContext {
  @Test
  public void type_of_empty_combine_is_inferred_correctly() {
    assertThat(combineH(list()).cat())
        .isEqualTo(combineCH(list()));
  }

  @Test
  public void type_of_combine_is_inferred_correctly() {
    assertThat(combineH(list(intH(3))).cat())
        .isEqualTo(combineCH(list(intTH())));
  }

  @Test
  public void items_returns_items() {
    ImmutableList<ObjH> items = list(intH(1), stringH("abc"));
    assertThat(combineH(items).items())
        .isEqualTo(items);
  }

  @Test
  public void combine_with_equal_items_are_equal() {
    ImmutableList<ObjH> items = list(intH(1), stringH("abc"));
    assertThat(combineH(items))
        .isEqualTo(combineH(items));
  }

  @Test
  public void combine_with_different_items_are_not_equal() {
    assertThat(combineH(list(intH(1))))
        .isNotEqualTo(combineH(list(intH(2))));
  }

  @Test
  public void hash_of_combine_with_equal_items_is_the_same() {
    ImmutableList<ObjH> items = list(intH(1));
    assertThat(combineH(items).hash())
        .isEqualTo(combineH(items).hash());
  }

  @Test
  public void hash_of_combine_with_different_items_is_not_the_same() {
    assertThat(combineH(list(intH(1))).hash())
        .isNotEqualTo(combineH(list(intH(2))).hash());
  }

  @Test
  public void hash_code_of_combine_with_equal_items_is_the_same() {
    ImmutableList<ObjH> items = list(intH(1));
    assertThat(combineH(items).hashCode())
        .isEqualTo(combineH(items).hashCode());
  }

  @Test
  public void hash_code_of_combine_with_different_items_is_not_the_same() {
    assertThat(combineH(list(intH(1))).hashCode())
        .isNotEqualTo(combineH(list(intH(2))).hashCode());
  }

  @Test
  public void combine_can_be_read_back_by_hash() {
    CombineH expr = combineH(list(intH(1)));
    assertThat(objDbOther().get(expr.hash()))
        .isEqualTo(expr);
  }

  @Test
  public void combine_read_back_by_hash_has_same_items() {
    ImmutableList<ObjH> items = list(intH(), stringH());
    CombineH expr = combineH(items);
    assertThat(((CombineH) objDbOther().get(expr.hash())).items())
        .isEqualTo(items);
  }

  @Test
  public void to_string() {
    CombineH expr = combineH(list(intH(1)));
    assertThat(expr.toString())
        .isEqualTo("Combine:{Int}(???)@" + expr.hash());
  }
}
