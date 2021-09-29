package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.expr.Select.SelectData;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.testing.TestingContext;

public class SelectTest extends TestingContext {
  @Test
  public void spec_of_select_is_inferred_correctly() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    assertThat(selectExpr(constExpr(rec), intVal(1)).spec())
        .isEqualTo(selectSpec(intSpec()));
  }

  @Test
  public void creating_select_with_non_rec_expr_causes_exception() {
    assertCall(() -> selectExpr(intExpr(3), intVal(2)).spec())
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void creating_select_with_too_great_index_causes_exception() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    assertCall(() -> selectExpr(constExpr(rec), intVal(2)).spec())
        .throwsException(new IndexOutOfBoundsException("index (2) must be less than size (2)"));
  }

  @Test
  public void creating_select_with_index_lower_than_zero_causes_exception() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    assertCall(() -> selectExpr(constExpr(rec), intVal(-1)).spec())
        .throwsException(new IndexOutOfBoundsException("index (-1) must not be negative"));
  }

  @Test
  public void data_returns_rec_and_index() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    Const expr = constExpr(rec);
    Int index = intVal(0);
    assertThat(selectExpr(expr, index).data())
        .isEqualTo(new SelectData(expr, index));
  }

  @Test
  public void select_with_equal_components_are_equal() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    assertThat(selectExpr(constExpr(rec), intVal(0)))
        .isEqualTo(selectExpr(constExpr(rec), intVal(0)));
  }

  @Test
  public void select_with_different_recs_are_not_equal() {
    Rec rec1 = recVal(list(strVal("abc"), intVal(7)));
    Rec rec2 = recVal(list(strVal("def"), intVal(7)));
    assertThat(selectExpr(constExpr(rec1), intVal(0)))
        .isNotEqualTo(selectExpr(constExpr(rec2), intVal(0)));
  }

  @Test
  public void select_with_different_indexes_are_not_equal() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    assertThat(selectExpr(constExpr(rec), intVal(0)))
        .isNotEqualTo(selectExpr(constExpr(rec), intVal(1)));
  }

  @Test
  public void hash_of_selects_with_equal_components_is_the_same() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    assertThat(selectExpr(constExpr(rec), intVal(0)).hash())
        .isEqualTo(selectExpr(constExpr(rec), intVal(0)).hash());
  }

  @Test
  public void hash_of_selects_with_different_recs_is_not_the_same() {
    Rec rec1 = recVal(list(strVal("abc"), intVal(7)));
    Rec rec2 = recVal(list(strVal("def"), intVal(7)));
    assertThat(selectExpr(constExpr(rec1), intVal(0)).hash())
        .isNotEqualTo(selectExpr(constExpr(rec2), intVal(0)).hash());
  }

  @Test
  public void hash_of_selects_with_different_indexes_is_not_the_same() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    assertThat(selectExpr(constExpr(rec), intVal(0)).hash())
        .isNotEqualTo(selectExpr(constExpr(rec), intVal(1)).hash());
  }

  @Test
  public void hash_code_of_selects_with_equal_components_is_the_same() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    assertThat(selectExpr(constExpr(rec), intVal(1)).hashCode())
        .isEqualTo(selectExpr(constExpr(rec), intVal(1)).hashCode());
  }

  @Test
  public void hash_code_of_selects_with_different_recs_is_not_the_same() {
    Rec rec1 = recVal(list(strVal("abc"), intVal(7)));
    Rec rec2 = recVal(list(strVal("def"), intVal(7)));
    assertThat(selectExpr(constExpr(rec1), intVal(0)).hashCode())
        .isNotEqualTo(selectExpr(constExpr(rec2), intVal(0)).hashCode());
  }

  @Test
  public void hash_code_of_selects_with_different_indexes_is_not_the_same() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    assertThat(selectExpr(constExpr(rec), intVal(0)).hashCode())
        .isNotEqualTo(selectExpr(constExpr(rec), intVal(1)).hashCode());
  }

  @Test
  public void select_can_be_read_back_by_hash() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    Select select = selectExpr(constExpr(rec), intVal(0));
    assertThat(objectDbOther().get(select.hash()))
        .isEqualTo(select);
  }

  @Test
  public void select_read_back_by_hash_has_same_data() {
    Const rec = constExpr(recWithStrVal());
    Int index = intVal(0);
    Select select = selectExpr(rec, index);
    assertThat(((Select) objectDbOther().get(select.hash())).data())
        .isEqualTo(new SelectData(rec, index));
  }

  @Test
  public void to_string() {
    Select select = selectExpr(constExpr(recWithStrVal()), intVal(0));
    assertThat(select.toString())
        .isEqualTo("Select(???)@" + select.hash());
  }
}
