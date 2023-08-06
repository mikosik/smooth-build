package org.smoothbuild.virtualmachine.bytecode.expr.oper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractExprBTestSuite;
import org.smoothbuild.virtualmachine.testing.TestVirtualMachine;

public class PickBTest extends TestVirtualMachine {
  @Test
  public void creating_pick_with_non_array_expr_as_pickable_causes_exception() {
    assertCall(() -> pickB(intB(3), intB(2)))
        .throwsException(
            new IllegalArgumentException("pickable.evaluationT() should be ArrayTB but is `Int`."));
  }

  @Test
  public void creating_pick_with_non_int_expr_as_index_causes_exception() {
    assertCall(() -> pickB(arrayB(boolTB()), stringB()))
        .throwsException(
            new IllegalArgumentException("index.evaluationT() should be IntTB but is `String`."));
  }

  @Test
  public void data_returns_array_and_index() throws Exception {
    var pickable = arrayB(intB(7));
    var index = intB(0);
    assertThat(pickB(pickable, index).subExprs()).isEqualTo(new PickB.SubExprsB(pickable, index));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<PickB> {
    @Override
    protected List<PickB> equalExprs() throws BytecodeException {
      return list(
          pickB(arrayB(intB(7), intB(9)), intB(0)), pickB(arrayB(intB(7), intB(9)), intB(0)));
    }

    @Override
    protected List<PickB> nonEqualExprs() throws BytecodeException {
      return list(
          pickB(arrayB(intB(1)), intB(0)),
          pickB(arrayB(intB(2)), intB(0)),
          pickB(arrayB(intB(2), intB(2)), intB(0)),
          pickB(arrayB(intB(2), intB(2)), intB(1)),
          pickB(arrayB(intB(2), intB(7)), intB(0)),
          pickB(arrayB(intB(7), intB(2)), intB(0)));
    }
  }

  @Test
  public void pick_can_be_read_back_by_hash() throws Exception {
    var pick = pickB(arrayB(intB(7)), intB(0));
    assertThat(exprDbOther().get(pick.hash())).isEqualTo(pick);
  }

  @Test
  public void pick_read_back_by_hash_has_same_sub_expressions() throws Exception {
    var array = arrayB(intB(17), intB(18));
    var index = intB(0);
    var pick = pickB(array, index);
    assertThat(((PickB) exprDbOther().get(pick.hash())).subExprs())
        .isEqualTo(new PickB.SubExprsB(array, index));
  }

  @Test
  public void to_string() throws Exception {
    var pick = pickB(arrayB(intB(17)), intB(0));
    assertThat(pick.toString()).isEqualTo("PICK:Int(???)@" + pick.hash());
  }
}
