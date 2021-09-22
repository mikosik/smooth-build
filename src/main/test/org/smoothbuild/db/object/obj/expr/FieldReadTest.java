package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.expr.FieldRead.SelectData;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.testing.TestingContext;

public class FieldReadTest extends TestingContext {
  @Test
  public void spec_of_field_read_is_inferred_correctly() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    assertThat(fieldReadExpr(constExpr(rec), intVal(1)).spec())
        .isEqualTo(fieldReadSpec(intSpec()));
  }

  @Test
  public void creating_field_read_with_non_rec_expr_causes_exception() {
    assertCall(() -> fieldReadExpr(intExpr(3), intVal(2)).spec())
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void creating_field_read_with_too_great_index_causes_exception() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    assertCall(() -> fieldReadExpr(constExpr(rec), intVal(2)).spec())
        .throwsException(new IndexOutOfBoundsException("index (2) must be less than size (2)"));
  }

  @Test
  public void creating_field_read_with_index_lower_than_zero_causes_exception() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    assertCall(() -> fieldReadExpr(constExpr(rec), intVal(-1)).spec())
        .throwsException(new IndexOutOfBoundsException("index (-1) must not be negative"));
  }

  @Test
  public void data_returns_rec_and_index() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    Const expr = constExpr(rec);
    Int index = intVal(0);
    assertThat(fieldReadExpr(expr, index).data())
        .isEqualTo(new SelectData(expr, index));
  }

  @Test
  public void field_read_with_equal_components_are_equal() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    assertThat(fieldReadExpr(constExpr(rec), intVal(0)))
        .isEqualTo(fieldReadExpr(constExpr(rec), intVal(0)));
  }

  @Test
  public void field_read_with_different_recs_are_not_equal() {
    Rec rec1 = recVal(list(strVal("abc"), intVal(7)));
    Rec rec2 = recVal(list(strVal("def"), intVal(7)));
    assertThat(fieldReadExpr(constExpr(rec1), intVal(0)))
        .isNotEqualTo(fieldReadExpr(constExpr(rec2), intVal(0)));
  }

  @Test
  public void field_read_with_different_indexes_are_not_equal() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    assertThat(fieldReadExpr(constExpr(rec), intVal(0)))
        .isNotEqualTo(fieldReadExpr(constExpr(rec), intVal(1)));
  }

  @Test
  public void hash_of_field_reads_with_equal_components_is_the_same() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    assertThat(fieldReadExpr(constExpr(rec), intVal(0)).hash())
        .isEqualTo(fieldReadExpr(constExpr(rec), intVal(0)).hash());
  }

  @Test
  public void hash_of_field_reads_with_different_recs_is_not_the_same() {
    Rec rec1 = recVal(list(strVal("abc"), intVal(7)));
    Rec rec2 = recVal(list(strVal("def"), intVal(7)));
    assertThat(fieldReadExpr(constExpr(rec1), intVal(0)).hash())
        .isNotEqualTo(fieldReadExpr(constExpr(rec2), intVal(0)).hash());
  }

  @Test
  public void hash_of_field_reads_with_different_indexes_is_not_the_same() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    assertThat(fieldReadExpr(constExpr(rec), intVal(0)).hash())
        .isNotEqualTo(fieldReadExpr(constExpr(rec), intVal(1)).hash());
  }

  @Test
  public void hash_code_of_field_reads_with_equal_components_is_the_same() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    assertThat(fieldReadExpr(constExpr(rec), intVal(1)).hashCode())
        .isEqualTo(fieldReadExpr(constExpr(rec), intVal(1)).hashCode());
  }

  @Test
  public void hash_code_of_field_reads_with_different_recs_is_not_the_same() {
    Rec rec1 = recVal(list(strVal("abc"), intVal(7)));
    Rec rec2 = recVal(list(strVal("def"), intVal(7)));
    assertThat(fieldReadExpr(constExpr(rec1), intVal(0)).hashCode())
        .isNotEqualTo(fieldReadExpr(constExpr(rec2), intVal(0)).hashCode());
  }

  @Test
  public void hash_code_of_field_reads_with_different_indexes_is_not_the_same() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    assertThat(fieldReadExpr(constExpr(rec), intVal(0)).hashCode())
        .isNotEqualTo(fieldReadExpr(constExpr(rec), intVal(1)).hashCode());
  }

  @Test
  public void field_read_can_be_read_back_by_hash() {
    Rec rec = recVal(list(strVal("abc"), intVal(7)));
    FieldRead fieldRead = fieldReadExpr(constExpr(rec), intVal(0));
    assertThat(objectDbOther().get(fieldRead.hash()))
        .isEqualTo(fieldRead);
  }

  @Test
  public void field_read_read_back_by_hash_has_same_data() {
    Const rec = constExpr(recWithStrVal());
    Int index = intVal(0);
    FieldRead fieldRead = fieldReadExpr(rec, index);
    assertThat(((FieldRead) objectDbOther().get(fieldRead.hash())).data())
        .isEqualTo(new SelectData(rec, index));
  }

  @Test
  public void to_string() {
    FieldRead fieldRead = fieldReadExpr(constExpr(recWithStrVal()), intVal(0));
    assertThat(fieldRead.toString())
        .isEqualTo("FieldRead(???):" + fieldRead.hash());
  }
}
