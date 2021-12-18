package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.val.IntB;
import org.smoothbuild.db.object.obj.val.TupleB;
import org.smoothbuild.testing.TestingContext;

public class SelectBTest extends TestingContext {
  @Test
  public void type_of_select_is_inferred_correctly() {
    TupleB tuple = animalB("rabbit", 7);
    assertThat(selectB(tuple, intB(1)).cat())
        .isEqualTo(selectCB(intTB()));
  }

  @Test
  public void creating_select_with_non_tuple_expr_causes_exception() {
    assertCall(() -> selectB(intB(3), intB(2)).cat())
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void creating_select_with_too_great_index_causes_exception() {
    TupleB tuple = animalB("rabbit", 7);
    assertCall(() -> selectB(tuple, intB(2)).cat())
        .throwsException(new IndexOutOfBoundsException("index (2) must be less than size (2)"));
  }

  @Test
  public void creating_select_with_index_lower_than_zero_causes_exception() {
    TupleB tuple = animalB("rabbit", 7);
    assertCall(() -> selectB(tuple, intB(-1)).cat())
        .throwsException(new IndexOutOfBoundsException("index (-1) must not be negative"));
  }

  @Test
  public void data_returns_tuple_and_index() {
    TupleB selectable = tupleB(tupleTB(), list(intB(7)));
    IntB index = intB(0);
    assertThat(selectB(selectable, index).data())
        .isEqualTo(new SelectB.Data(selectable, index));
  }

  @Test
  public void select_with_equal_components_are_equal() {
    TupleB tuple = animalB("rabbit", 7);
    assertThat(selectB(tuple, intB(0)))
        .isEqualTo(selectB(tuple, intB(0)));
  }

  @Test
  public void select_with_different_tuples_are_not_equal() {
    TupleB tuple1 = animalB("rabbit", 7);
    TupleB tuple2 = animalB("cat", 7);
    assertThat(selectB(tuple1, intB(0)))
        .isNotEqualTo(selectB(tuple2, intB(0)));
  }

  @Test
  public void select_with_different_indexes_are_not_equal() {
    TupleB tuple = animalB("rabbit", 7);
    assertThat(selectB(tuple, intB(0)))
        .isNotEqualTo(selectB(tuple, intB(1)));
  }

  @Test
  public void hash_of_selects_with_equal_components_is_the_same() {
    TupleB tuple = animalB("rabbit", 7);
    assertThat(selectB(tuple, intB(0)).hash())
        .isEqualTo(selectB(tuple, intB(0)).hash());
  }

  @Test
  public void hash_of_selects_with_different_tuples_is_not_the_same() {
    TupleB tuple1 = animalB("rabbit", 7);
    TupleB tuple2 = animalB("cat", 7);
    assertThat(selectB(tuple1, intB(0)).hash())
        .isNotEqualTo(selectB(tuple2, intB(0)).hash());
  }

  @Test
  public void hash_of_selects_with_different_indexes_is_not_the_same() {
    TupleB tuple = animalB("rabbit", 7);
    assertThat(selectB(tuple, intB(0)).hash())
        .isNotEqualTo(selectB(tuple, intB(1)).hash());
  }

  @Test
  public void hash_code_of_selects_with_equal_components_is_the_same() {
    TupleB tuple = animalB("rabbit", 7);
    assertThat(selectB(tuple, intB(1)).hashCode())
        .isEqualTo(selectB(tuple, intB(1)).hashCode());
  }

  @Test
  public void hash_code_of_selects_with_different_tuples_is_not_the_same() {
    TupleB tuple1 = animalB("rabbit", 7);
    TupleB tuple2 = animalB("cat", 7);
    assertThat(selectB(tuple1, intB(0)).hashCode())
        .isNotEqualTo(selectB(tuple2, intB(0)).hashCode());
  }

  @Test
  public void hash_code_of_selects_with_different_indexes_is_not_the_same() {
    TupleB tuple = animalB("rabbit", 7);
    assertThat(selectB(tuple, intB(0)).hashCode())
        .isNotEqualTo(selectB(tuple, intB(1)).hashCode());
  }

  @Test
  public void select_can_be_read_back_by_hash() {
    TupleB tuple = animalB("rabbit", 7);
    SelectB select = selectB(tuple, intB(0));
    assertThat(byteDbOther().get(select.hash()))
        .isEqualTo(select);
  }

  @Test
  public void select_read_back_by_hash_has_same_data() {
    var selectable = animalB();
    var index = intB(0);
    var select = selectB(selectable, index);
    assertThat(((SelectB) byteDbOther().get(select.hash())).data())
        .isEqualTo(new SelectB.Data(selectable, index));
  }

  @Test
  public void to_string() {
    SelectB select = selectB(animalB(), intB(0));
    assertThat(select.toString())
        .isEqualTo("Select:String(???)@" + select.hash());
  }
}
