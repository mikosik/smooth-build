package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.expr.Select.SelectData;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.testing.TestingContext;

public class SelectTest extends TestingContext {
  @Test
  public void type_of_select_is_inferred_correctly() {
    Tuple tuple = animal("rabbit", 7);
    assertThat(select(const_(tuple), int_(1)).type())
        .isEqualTo(selectOT(intOT()));
  }

  @Test
  public void creating_select_with_non_tuple_expr_causes_exception() {
    assertCall(() -> select(intExpr(3), int_(2)).type())
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void creating_select_with_too_great_index_causes_exception() {
    Tuple tuple = animal("rabbit", 7);
    assertCall(() -> select(const_(tuple), int_(2)).type())
        .throwsException(new IndexOutOfBoundsException("index (2) must be less than size (2)"));
  }

  @Test
  public void creating_select_with_index_lower_than_zero_causes_exception() {
    Tuple tuple = animal("rabbit", 7);
    assertCall(() -> select(const_(tuple), int_(-1)).type())
        .throwsException(new IndexOutOfBoundsException("index (-1) must not be negative"));
  }

  @Test
  public void data_returns_tuple_and_index() {
    Tuple tuple = tuple(tupleOT(), list(int_(7)));
    Const expr = const_(tuple);
    Int index = int_(0);
    assertThat(select(expr, index).data())
        .isEqualTo(new SelectData(expr, index));
  }

  @Test
  public void select_with_equal_components_are_equal() {
    Tuple tuple = animal("rabbit", 7);
    assertThat(select(const_(tuple), int_(0)))
        .isEqualTo(select(const_(tuple), int_(0)));
  }

  @Test
  public void select_with_different_tuples_are_not_equal() {
    Tuple tuple1 = animal("rabbit", 7);
    Tuple tuple2 = animal("cat", 7);
    assertThat(select(const_(tuple1), int_(0)))
        .isNotEqualTo(select(const_(tuple2), int_(0)));
  }

  @Test
  public void select_with_different_indexes_are_not_equal() {
    Tuple tuple = animal("rabbit", 7);
    assertThat(select(const_(tuple), int_(0)))
        .isNotEqualTo(select(const_(tuple), int_(1)));
  }

  @Test
  public void hash_of_selects_with_equal_components_is_the_same() {
    Tuple tuple = animal("rabbit", 7);
    assertThat(select(const_(tuple), int_(0)).hash())
        .isEqualTo(select(const_(tuple), int_(0)).hash());
  }

  @Test
  public void hash_of_selects_with_different_tuples_is_not_the_same() {
    Tuple tuple1 = animal("rabbit", 7);
    Tuple tuple2 = animal("cat", 7);
    assertThat(select(const_(tuple1), int_(0)).hash())
        .isNotEqualTo(select(const_(tuple2), int_(0)).hash());
  }

  @Test
  public void hash_of_selects_with_different_indexes_is_not_the_same() {
    Tuple tuple = animal("rabbit", 7);
    assertThat(select(const_(tuple), int_(0)).hash())
        .isNotEqualTo(select(const_(tuple), int_(1)).hash());
  }

  @Test
  public void hash_code_of_selects_with_equal_components_is_the_same() {
    Tuple tuple = animal("rabbit", 7);
    assertThat(select(const_(tuple), int_(1)).hashCode())
        .isEqualTo(select(const_(tuple), int_(1)).hashCode());
  }

  @Test
  public void hash_code_of_selects_with_different_tuples_is_not_the_same() {
    Tuple tuple1 = animal("rabbit", 7);
    Tuple tuple2 = animal("cat", 7);
    assertThat(select(const_(tuple1), int_(0)).hashCode())
        .isNotEqualTo(select(const_(tuple2), int_(0)).hashCode());
  }

  @Test
  public void hash_code_of_selects_with_different_indexes_is_not_the_same() {
    Tuple tuple = animal("rabbit", 7);
    assertThat(select(const_(tuple), int_(0)).hashCode())
        .isNotEqualTo(select(const_(tuple), int_(1)).hashCode());
  }

  @Test
  public void select_can_be_read_back_by_hash() {
    Tuple tuple = animal("rabbit", 7);
    Select select = select(const_(tuple), int_(0));
    assertThat(objectDbOther().get(select.hash()))
        .isEqualTo(select);
  }

  @Test
  public void select_read_back_by_hash_has_same_data() {
    Const tupleExpr = const_(animal());
    Int index = int_(0);
    Select select = select(tupleExpr, index);
    assertThat(((Select) objectDbOther().get(select.hash())).data())
        .isEqualTo(new SelectData(tupleExpr, index));
  }

  @Test
  public void to_string() {
    Select select = select(const_(animal()), int_(0));
    assertThat(select.toString())
        .isEqualTo("Select(???)@" + select.hash());
  }
}
