package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick.BSubExprs;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BPickTest extends TestingVirtualMachine {
  @Test
  void creating_pick_with_non_array_expr_as_pickable_causes_exception() {
    assertCall(() -> bPick(bInt(3), bInt(2)))
        .throwsException(new IllegalArgumentException(
            "`pickable.evaluationType()` should be `BArrayType` but is `BIntType`."));
  }

  @Test
  void creating_pick_with_non_int_expr_as_index_causes_exception() {
    assertCall(() -> bPick(bArray(bBoolType()), bString()))
        .throwsException(new IllegalArgumentException(
            "`index.evaluationType()` should be `BIntType` but is `BStringType`."));
  }

  @Test
  void data_returns_array_and_index() throws Exception {
    var pickable = bArray(bInt(7));
    var index = bInt(0);
    assertThat(bPick(pickable, index).subExprs()).isEqualTo(new BSubExprs(pickable, index));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BPick> {
    @Override
    protected List<BPick> equalExprs() throws BytecodeException {
      return list(
          bPick(bArray(bInt(7), bInt(9)), bInt(0)), bPick(bArray(bInt(7), bInt(9)), bInt(0)));
    }

    @Override
    protected List<BPick> nonEqualExprs() throws BytecodeException {
      return list(
          bPick(bArray(bInt(1)), bInt(0)),
          bPick(bArray(bInt(2)), bInt(0)),
          bPick(bArray(bInt(2), bInt(2)), bInt(0)),
          bPick(bArray(bInt(2), bInt(2)), bInt(1)),
          bPick(bArray(bInt(2), bInt(7)), bInt(0)),
          bPick(bArray(bInt(7), bInt(2)), bInt(0)));
    }
  }

  @Test
  void pick_can_be_read_back_by_hash() throws Exception {
    var pick = bPick(bArray(bInt(7)), bInt(0));
    assertThat(exprDbOther().get(pick.hash())).isEqualTo(pick);
  }

  @Test
  void pick_read_back_by_hash_has_same_sub_expressions() throws Exception {
    var array = bArray(bInt(17), bInt(18));
    var index = bInt(0);
    var pick = bPick(array, index);
    assertThat(((BPick) exprDbOther().get(pick.hash())).subExprs())
        .isEqualTo(new BSubExprs(array, index));
  }

  @Test
  void to_string() throws Exception {
    var pick = bPick(bArray(bInt(17)), bInt(0));
    assertThat(pick.toString()).isEqualTo("PICK:Int(???)@" + pick.hash());
  }
}
