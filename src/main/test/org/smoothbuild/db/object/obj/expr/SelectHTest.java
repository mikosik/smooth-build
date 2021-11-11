package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.expr.SelectH.SelectData;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.testing.TestingContext;

public class SelectHTest extends TestingContext {
  @Test
  public void type_of_select_is_inferred_correctly() {
    TupleH tuple = animalH("rabbit", 7);
    assertThat(selectH(constH(tuple), intH(1)).type())
        .isEqualTo(selectHT(intHT()));
  }

  @Test
  public void creating_select_with_non_tuple_expr_causes_exception() {
    assertCall(() -> selectH(intHE(3), intH(2)).type())
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void creating_select_with_too_great_index_causes_exception() {
    TupleH tuple = animalH("rabbit", 7);
    assertCall(() -> selectH(constH(tuple), intH(2)).type())
        .throwsException(new IndexOutOfBoundsException("index (2) must be less than size (2)"));
  }

  @Test
  public void creating_select_with_index_lower_than_zero_causes_exception() {
    TupleH tuple = animalH("rabbit", 7);
    assertCall(() -> selectH(constH(tuple), intH(-1)).type())
        .throwsException(new IndexOutOfBoundsException("index (-1) must not be negative"));
  }

  @Test
  public void data_returns_tuple_and_index() {
    TupleH tuple = tupleH(tupleHT(), list(intH(7)));
    ConstH expr = constH(tuple);
    IntH index = intH(0);
    assertThat(selectH(expr, index).data())
        .isEqualTo(new SelectData(expr, index));
  }

  @Test
  public void select_with_equal_components_are_equal() {
    TupleH tuple = animalH("rabbit", 7);
    assertThat(selectH(constH(tuple), intH(0)))
        .isEqualTo(selectH(constH(tuple), intH(0)));
  }

  @Test
  public void select_with_different_tuples_are_not_equal() {
    TupleH tuple1 = animalH("rabbit", 7);
    TupleH tuple2 = animalH("cat", 7);
    assertThat(selectH(constH(tuple1), intH(0)))
        .isNotEqualTo(selectH(constH(tuple2), intH(0)));
  }

  @Test
  public void select_with_different_indexes_are_not_equal() {
    TupleH tuple = animalH("rabbit", 7);
    assertThat(selectH(constH(tuple), intH(0)))
        .isNotEqualTo(selectH(constH(tuple), intH(1)));
  }

  @Test
  public void hash_of_selects_with_equal_components_is_the_same() {
    TupleH tuple = animalH("rabbit", 7);
    assertThat(selectH(constH(tuple), intH(0)).hash())
        .isEqualTo(selectH(constH(tuple), intH(0)).hash());
  }

  @Test
  public void hash_of_selects_with_different_tuples_is_not_the_same() {
    TupleH tuple1 = animalH("rabbit", 7);
    TupleH tuple2 = animalH("cat", 7);
    assertThat(selectH(constH(tuple1), intH(0)).hash())
        .isNotEqualTo(selectH(constH(tuple2), intH(0)).hash());
  }

  @Test
  public void hash_of_selects_with_different_indexes_is_not_the_same() {
    TupleH tuple = animalH("rabbit", 7);
    assertThat(selectH(constH(tuple), intH(0)).hash())
        .isNotEqualTo(selectH(constH(tuple), intH(1)).hash());
  }

  @Test
  public void hash_code_of_selects_with_equal_components_is_the_same() {
    TupleH tuple = animalH("rabbit", 7);
    assertThat(selectH(constH(tuple), intH(1)).hashCode())
        .isEqualTo(selectH(constH(tuple), intH(1)).hashCode());
  }

  @Test
  public void hash_code_of_selects_with_different_tuples_is_not_the_same() {
    TupleH tuple1 = animalH("rabbit", 7);
    TupleH tuple2 = animalH("cat", 7);
    assertThat(selectH(constH(tuple1), intH(0)).hashCode())
        .isNotEqualTo(selectH(constH(tuple2), intH(0)).hashCode());
  }

  @Test
  public void hash_code_of_selects_with_different_indexes_is_not_the_same() {
    TupleH tuple = animalH("rabbit", 7);
    assertThat(selectH(constH(tuple), intH(0)).hashCode())
        .isNotEqualTo(selectH(constH(tuple), intH(1)).hashCode());
  }

  @Test
  public void select_can_be_read_back_by_hash() {
    TupleH tuple = animalH("rabbit", 7);
    SelectH select = selectH(constH(tuple), intH(0));
    assertThat(objectHDbOther().get(select.hash()))
        .isEqualTo(select);
  }

  @Test
  public void select_read_back_by_hash_has_same_data() {
    ConstH tupleExpr = constH(animalH());
    IntH index = intH(0);
    SelectH select = selectH(tupleExpr, index);
    assertThat(((SelectH) objectHDbOther().get(select.hash())).data())
        .isEqualTo(new SelectData(tupleExpr, index));
  }

  @Test
  public void to_string() {
    SelectH select = selectH(constH(animalH()), intH(0));
    assertThat(select.toString())
        .isEqualTo("Select(???)@" + select.hash());
  }
}
