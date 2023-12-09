package org.smoothbuild.vm.bytecode.expr.oper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.AbstractExprBTestSuite;

public class SelectBTest extends TestContext {
  @Test
  public void creating_select_with_non_tuple_expr_causes_exception() {
    assertCall(() -> selectB(intB(3), intB(2)))
        .throwsException(new IllegalArgumentException(
            "selectable.evaluationT() should be TupleTB but is `Int`."));
  }

  @Test
  public void creating_select_with_too_great_index_causes_exception() {
    var tupleB = animalB("rabbit", 7);
    assertCall(() -> selectB(tupleB, intB(2)).category())
        .throwsException(new IndexOutOfBoundsException("index (2) must be less than size (2)"));
  }

  @Test
  public void creating_select_with_index_lower_than_zero_causes_exception() {
    var tupleB = animalB("rabbit", 7);
    assertCall(() -> selectB(tupleB, intB(-1)).category())
        .throwsException(new IndexOutOfBoundsException("index (-1) must not be negative"));
  }

  @Test
  public void sub_expressions_contains_tuple_and_index() {
    var selectable = tupleB(intB(7));
    var index = intB(0);
    assertThat(selectB(selectable, index).subExprs())
        .isEqualTo(new SelectSubExprsB(selectable, index));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<SelectB> {
    @Override
    protected List<SelectB> equalExprs() {
      return list(
          selectB(tupleB(intB(7), stringB("abc")), intB(0)),
          selectB(tupleB(intB(7), stringB("abc")), intB(0)));
    }

    @Override
    protected List<SelectB> nonEqualExprs() {
      return list(
          selectB(tupleB(intB(1)), intB(0)),
          selectB(tupleB(intB(2)), intB(0)),
          selectB(tupleB(intB(2), intB(2)), intB(0)),
          selectB(tupleB(intB(2), intB(2)), intB(1)),
          selectB(tupleB(intB(2), intB(7)), intB(0)),
          selectB(tupleB(intB(7), intB(2)), intB(0)));
    }
  }

  @Test
  public void select_can_be_read_back_by_hash() {
    var tupleB = animalB("rabbit", 7);
    var selectB = selectB(tupleB, intB(0));
    assertThat(bytecodeDbOther().get(selectB.hash())).isEqualTo(selectB);
  }

  @Test
  public void select_read_back_by_hash_has_same_sub_expressions() {
    var selectable = animalB();
    var index = intB(0);
    var select = selectB(selectable, index);
    assertThat(((SelectB) bytecodeDbOther().get(select.hash())).subExprs())
        .isEqualTo(new SelectSubExprsB(selectable, index));
  }

  @Test
  public void to_string() {
    var selectB = selectB(animalB(), intB(0));
    assertThat(selectB.toString()).isEqualTo("SELECT:String(???)@" + selectB.hash());
  }
}
