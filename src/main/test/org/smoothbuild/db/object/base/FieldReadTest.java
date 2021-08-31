package org.smoothbuild.db.object.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class FieldReadTest extends TestingContext {
  @Test
  public void spec_of_field_read_expr_is_field_read_expr() {
    assertThat(fieldReadExpr(constExpr(emptyTupleVal()), intVal(123)).spec())
        .isEqualTo(fieldReadSpec());
  }

  @Test
  public void tuple_returns_tuple_expr() {
    Const tuple = constExpr(emptyTupleVal());
    assertThat(fieldReadExpr(tuple, intVal(123)).tuple())
        .isEqualTo(tuple);
  }

  @Test
  public void index_returns_index_value() {
    Int index = intVal(123);
    assertThat(fieldReadExpr(constExpr(emptyTupleVal()), index).index())
        .isEqualTo(index);
  }

  @Test
  public void field_read_with_equal_values_are_equal() {
    assertThat(fieldReadExpr(constExpr(emptyTupleVal()), intVal(123)))
        .isEqualTo(fieldReadExpr(constExpr(emptyTupleVal()), intVal(123)));
  }

  @Test
  public void field_read_with_different_tuples_are_not_equal() {
    assertThat(fieldReadExpr(constExpr(emptyTupleVal()), intVal(123)))
        .isNotEqualTo(fieldReadExpr(constExpr(tupleWithStrVal()), intVal(123)));

  }

  @Test
  public void field_read_with_different_indexes_are_not_equal() {
    assertThat(fieldReadExpr(constExpr(emptyTupleVal()), intVal(123)))
        .isNotEqualTo(fieldReadExpr(constExpr(emptyTupleVal()), intVal(333)));
  }

  @Test
  public void hash_of_field_reads_with_equal_values_is_the_same() {
    assertThat(fieldReadExpr(constExpr(emptyTupleVal()), intVal(123)).hash())
        .isEqualTo(fieldReadExpr(constExpr(emptyTupleVal()), intVal(123)).hash());
  }

  @Test
  public void hash_of_field_reads_with_different_tuples_is_not_the_same() {
    assertThat(fieldReadExpr(constExpr(emptyTupleVal()), intVal(123)).hash())
        .isNotEqualTo(fieldReadExpr(constExpr(tupleWithStrVal()), intVal(123)).hash());
  }

  @Test
  public void hash_of_field_reads_with_different_indexes_is_not_the_same() {
    assertThat(fieldReadExpr(constExpr(emptyTupleVal()), intVal(123)).hash())
        .isNotEqualTo(fieldReadExpr(constExpr(tupleWithStrVal()), intVal(333)).hash());
  }

  @Test
  public void hash_code_of_field_reads_with_equal_values_is_the_same() {
    assertThat(fieldReadExpr(constExpr(emptyTupleVal()), intVal(123)).hashCode())
        .isEqualTo(fieldReadExpr(constExpr(emptyTupleVal()), intVal(123)).hashCode());
  }

  @Test
  public void hash_code_of_field_reads_with_different_tuples_is_not_the_same() {
    assertThat(fieldReadExpr(constExpr(emptyTupleVal()), intVal(123)).hashCode())
        .isNotEqualTo(fieldReadExpr(constExpr(tupleWithStrVal()), intVal(123)).hashCode());
  }

  @Test
  public void hash_code_of_field_reads_with_different_indexes_is_not_the_same() {
    assertThat(fieldReadExpr(constExpr(emptyTupleVal()), intVal(123)).hashCode())
        .isNotEqualTo(fieldReadExpr(constExpr(tupleWithStrVal()), intVal(333)).hashCode());
  }

  @Test
  public void field_read_can_be_read_back_by_hash() {
    FieldRead fieldRead = fieldReadExpr(constExpr(emptyTupleVal()), intVal(123));
    assertThat(objectDbOther().get(fieldRead.hash()))
        .isEqualTo(fieldRead);
  }

  @Test
  public void field_read_read_back_by_hash_has_same_tuple() {
    Const tuple = constExpr(tupleWithStrVal());
    FieldRead fieldRead = fieldReadExpr(tuple, intVal(123));
    assertThat(((FieldRead) objectDbOther().get(fieldRead.hash())).tuple())
        .isEqualTo(tuple);
  }

  @Test
  public void field_read_read_back_by_hash_has_same_index() {
    Int index = intVal(123);
    FieldRead fieldRead = fieldReadExpr(constExpr(tupleWithStrVal()), index);
    assertThat(((FieldRead) objectDbOther().get(fieldRead.hash())).index())
        .isEqualTo(index);
  }

  @Test
  public void to_string() {
    FieldRead fieldRead = fieldReadExpr(constExpr(tupleWithStrVal()), intVal(123));
    assertThat(fieldRead.toString())
        .isEqualTo("FieldRead(???):" + fieldRead.hash());
  }
}
