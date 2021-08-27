package org.smoothbuild.db.object.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class FieldReadTest extends TestingContext {
  @Test
  public void spec_of_field_read_expr_is_field_read_expr() {
    assertThat(fieldReadE(constE(emptyTupleV()), intV(123)).spec())
        .isEqualTo(fieldReadS());
  }

  @Test
  public void tuple_returns_tuple_expr() {
    Const tuple = constE(emptyTupleV());
    assertThat(fieldReadE(tuple, intV(123)).tuple())
        .isEqualTo(tuple);
  }

  @Test
  public void index_returns_index_value() {
    Int index = intV(123);
    assertThat(fieldReadE(constE(emptyTupleV()), index).index())
        .isEqualTo(index);
  }

  @Test
  public void field_read_with_equal_values_are_equal() {
    assertThat(fieldReadE(constE(emptyTupleV()), intV(123)))
        .isEqualTo(fieldReadE(constE(emptyTupleV()), intV(123)));
  }

  @Test
  public void field_read_with_different_tuples_are_not_equal() {
    assertThat(fieldReadE(constE(emptyTupleV()), intV(123)))
        .isNotEqualTo(fieldReadE(constE(tupleWithStrV()), intV(123)));

  }

  @Test
  public void field_read_with_different_indexes_are_not_equal() {
    assertThat(fieldReadE(constE(emptyTupleV()), intV(123)))
        .isNotEqualTo(fieldReadE(constE(emptyTupleV()), intV(333)));
  }

  @Test
  public void hash_of_field_reads_with_equal_values_is_the_same() {
    assertThat(fieldReadE(constE(emptyTupleV()), intV(123)).hash())
        .isEqualTo(fieldReadE(constE(emptyTupleV()), intV(123)).hash());
  }

  @Test
  public void hash_of_field_reads_with_different_tuples_is_not_the_same() {
    assertThat(fieldReadE(constE(emptyTupleV()), intV(123)).hash())
        .isNotEqualTo(fieldReadE(constE(tupleWithStrV()), intV(123)).hash());
  }

  @Test
  public void hash_of_field_reads_with_different_indexes_is_not_the_same() {
    assertThat(fieldReadE(constE(emptyTupleV()), intV(123)).hash())
        .isNotEqualTo(fieldReadE(constE(tupleWithStrV()), intV(333)).hash());
  }

  @Test
  public void hash_code_of_field_reads_with_equal_values_is_the_same() {
    assertThat(fieldReadE(constE(emptyTupleV()), intV(123)).hashCode())
        .isEqualTo(fieldReadE(constE(emptyTupleV()), intV(123)).hashCode());
  }

  @Test
  public void hash_code_of_field_reads_with_different_tuples_is_not_the_same() {
    assertThat(fieldReadE(constE(emptyTupleV()), intV(123)).hashCode())
        .isNotEqualTo(fieldReadE(constE(tupleWithStrV()), intV(123)).hashCode());
  }

  @Test
  public void hash_code_of_field_reads_with_different_indexes_is_not_the_same() {
    assertThat(fieldReadE(constE(emptyTupleV()), intV(123)).hashCode())
        .isNotEqualTo(fieldReadE(constE(tupleWithStrV()), intV(333)).hashCode());
  }

  @Test
  public void field_read_can_be_read_back_by_hash() {
    FieldRead fieldRead = fieldReadE(constE(emptyTupleV()), intV(123));
    assertThat(objectDbOther().get(fieldRead.hash()))
        .isEqualTo(fieldRead);
  }

  @Test
  public void field_read_read_back_by_hash_has_same_tuple() {
    Const tuple = constE(tupleWithStrV());
    FieldRead fieldRead = fieldReadE(tuple, intV(123));
    assertThat(((FieldRead) objectDbOther().get(fieldRead.hash())).tuple())
        .isEqualTo(tuple);
  }

  @Test
  public void field_read_read_back_by_hash_has_same_index() {
    Int index = intV(123);
    FieldRead fieldRead = fieldReadE(constE(tupleWithStrV()), index);
    assertThat(((FieldRead) objectDbOther().get(fieldRead.hash())).index())
        .isEqualTo(index);
  }

  @Test
  public void to_string() {
    FieldRead fieldRead = fieldReadE(constE(tupleWithStrV()), intV(123));
    assertThat(fieldRead.toString())
        .isEqualTo("FieldRead(???):" + fieldRead.hash());
  }
}
