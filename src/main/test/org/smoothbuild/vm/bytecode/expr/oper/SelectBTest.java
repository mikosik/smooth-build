package org.smoothbuild.vm.bytecode.expr.oper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.ExprBTestCase;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;

import com.google.common.truth.Truth;

public class SelectBTest extends TestContext {
  @Test
  public void creating_select_with_non_tuple_expr_causes_exception() {
    assertCall(() -> selectB(intB(3), intB(2)))
        .throwsException(new IllegalArgumentException(
            "selectable.evalT() should be TupleTB but is `Int`."));
  }

  @Test
  public void creating_select_with_too_great_index_causes_exception() {
    TupleB tuple = animalB("rabbit", 7);
    assertCall(() -> selectB(tuple, intB(2)).category())
        .throwsException(new IndexOutOfBoundsException("index (2) must be less than size (2)"));
  }

  @Test
  public void creating_select_with_index_lower_than_zero_causes_exception() {
    TupleB tuple = animalB("rabbit", 7);
    assertCall(() -> selectB(tuple, intB(-1)).category())
        .throwsException(new IndexOutOfBoundsException("index (-1) must not be negative"));
  }

  @Test
  public void data_returns_tuple_and_index() {
    var selectable = tupleB(intB(7));
    var index = intB(0);
    Truth.assertThat(selectB(selectable, index).dataSeq())
        .isEqualTo(list(selectable, index));
  }

  @Nested
  class _equals_hash_hashcode extends ExprBTestCase<SelectB> {
    @Override
    protected List<SelectB> equalExprs() {
      return list(
          selectB(tupleB(intB(7), stringB("abc")), intB(0)),
          selectB(tupleB(intB(7), stringB("abc")), intB(0))
      );
    }

    @Override
    protected List<SelectB> nonEqualExprs() {
      return list(
          selectB(tupleB(intB(1)), intB(0)),
          selectB(tupleB(intB(2)), intB(0)),
          selectB(tupleB(intB(2), intB(2)), intB(0)),
          selectB(tupleB(intB(2), intB(2)), intB(1)),
          selectB(tupleB(intB(2), intB(7)), intB(0)),
          selectB(tupleB(intB(7), intB(2)), intB(0))
      );
    }
  }

  @Test
  public void select_can_be_read_back_by_hash() {
    TupleB tuple = animalB("rabbit", 7);
    SelectB select = selectB(tuple, intB(0));
    Truth.assertThat(bytecodeDbOther().get(select.hash()))
        .isEqualTo(select);
  }

  @Test
  public void select_read_back_by_hash_has_same_data() {
    var selectable = animalB();
    var index = intB(0);
    var select = selectB(selectable, index);
    assertThat(((SelectB) bytecodeDbOther().get(select.hash())).dataSeq())
        .isEqualTo(list(selectable, index));
  }

  @Test
  public void to_string() {
    SelectB select = selectB(animalB(), intB(0));
    assertThat(select.toString())
        .isEqualTo("SELECT:String(???)@" + select.hash());
  }
}
