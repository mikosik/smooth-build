package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.expr.Select.SelectData;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.testing.TestingContextImpl;

public class SelectTest extends TestingContextImpl {
  @Test
  public void spec_of_select_is_inferred_correctly() {
    Struc_ struct = animalVal("rabbit", 7);
    assertThat(selectExpr(constExpr(struct), intVal(1)).spec())
        .isEqualTo(selectSpec(intSpec()));
  }

  @Test
  public void creating_select_with_non_struct_expr_causes_exception() {
    assertCall(() -> selectExpr(intExpr(3), intVal(2)).spec())
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void creating_select_with_too_great_index_causes_exception() {
    Struc_ struct = animalVal("rabbit", 7);
    assertCall(() -> selectExpr(constExpr(struct), intVal(2)).spec())
        .throwsException(new IndexOutOfBoundsException("index (2) must be less than size (2)"));
  }

  @Test
  public void creating_select_with_index_lower_than_zero_causes_exception() {
    Struc_ struct = animalVal("rabbit", 7);
    assertCall(() -> selectExpr(constExpr(struct), intVal(-1)).spec())
        .throwsException(new IndexOutOfBoundsException("index (-1) must not be negative"));
  }

  @Test
  public void data_returns_struct_and_index() {
    Struc_ struct = structVal(structSpec(), list(intVal(7)));
    Const expr = constExpr(struct);
    Int index = intVal(0);
    assertThat(selectExpr(expr, index).data())
        .isEqualTo(new SelectData(expr, index));
  }

  @Test
  public void select_with_equal_components_are_equal() {
    Struc_ struct = animalVal("rabbit", 7);
    assertThat(selectExpr(constExpr(struct), intVal(0)))
        .isEqualTo(selectExpr(constExpr(struct), intVal(0)));
  }

  @Test
  public void select_with_different_structs_are_not_equal() {
    Struc_ struct1 = animalVal("rabbit", 7);
    Struc_ struct2 = animalVal("cat", 7);
    assertThat(selectExpr(constExpr(struct1), intVal(0)))
        .isNotEqualTo(selectExpr(constExpr(struct2), intVal(0)));
  }

  @Test
  public void select_with_different_indexes_are_not_equal() {
    Struc_ struct = animalVal("rabbit", 7);
    assertThat(selectExpr(constExpr(struct), intVal(0)))
        .isNotEqualTo(selectExpr(constExpr(struct), intVal(1)));
  }

  @Test
  public void hash_of_selects_with_equal_components_is_the_same() {
    Struc_ struct = animalVal("rabbit", 7);
    assertThat(selectExpr(constExpr(struct), intVal(0)).hash())
        .isEqualTo(selectExpr(constExpr(struct), intVal(0)).hash());
  }

  @Test
  public void hash_of_selects_with_different_structs_is_not_the_same() {
    Struc_ struct1 = animalVal("rabbit", 7);
    Struc_ struct2 = animalVal("cat", 7);
    assertThat(selectExpr(constExpr(struct1), intVal(0)).hash())
        .isNotEqualTo(selectExpr(constExpr(struct2), intVal(0)).hash());
  }

  @Test
  public void hash_of_selects_with_different_indexes_is_not_the_same() {
    Struc_ struct = animalVal("rabbit", 7);
    assertThat(selectExpr(constExpr(struct), intVal(0)).hash())
        .isNotEqualTo(selectExpr(constExpr(struct), intVal(1)).hash());
  }

  @Test
  public void hash_code_of_selects_with_equal_components_is_the_same() {
    Struc_ struct = animalVal("rabbit", 7);
    assertThat(selectExpr(constExpr(struct), intVal(1)).hashCode())
        .isEqualTo(selectExpr(constExpr(struct), intVal(1)).hashCode());
  }

  @Test
  public void hash_code_of_selects_with_different_structs_is_not_the_same() {
    Struc_ struct1 = animalVal("rabbit", 7);
    Struc_ struct2 = animalVal("cat", 7);
    assertThat(selectExpr(constExpr(struct1), intVal(0)).hashCode())
        .isNotEqualTo(selectExpr(constExpr(struct2), intVal(0)).hashCode());
  }

  @Test
  public void hash_code_of_selects_with_different_indexes_is_not_the_same() {
    Struc_ struct = animalVal("rabbit", 7);
    assertThat(selectExpr(constExpr(struct), intVal(0)).hashCode())
        .isNotEqualTo(selectExpr(constExpr(struct), intVal(1)).hashCode());
  }

  @Test
  public void select_can_be_read_back_by_hash() {
    Struc_ struct = animalVal("rabbit", 7);
    Select select = selectExpr(constExpr(struct), intVal(0));
    assertThat(objectDbOther().get(select.hash()))
        .isEqualTo(select);
  }

  @Test
  public void select_read_back_by_hash_has_same_data() {
    Const structExpr = constExpr(animalVal());
    Int index = intVal(0);
    Select select = selectExpr(structExpr, index);
    assertThat(((Select) objectDbOther().get(select.hash())).data())
        .isEqualTo(new SelectData(structExpr, index));
  }

  @Test
  public void to_string() {
    Select select = selectExpr(constExpr(animalVal()), intVal(0));
    assertThat(select.toString())
        .isEqualTo("Select(???)@" + select.hash());
  }
}
