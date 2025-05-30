package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick.BSubExprs;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BPickTest extends VmTestContext {
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
    assertThat(pick.toString())
        .isEqualTo(
            """
        BPick(
          hash = 2a3ee0490047c831fed1b6bade199fc3c9f79d10b33e705a945a5eb171b0b385
          evaluationType = Int
          pickable = BArray(
            hash = 0bb233bbb42989f27846b6a121ff2570f12136aeababcf0d6fe1c57195bcc2a9
            type = [Int]
            elements = [
              BInt(
                hash = d6781a8034402f1bb1369df5042c4cc9d4d726044ba4ae8eb55efce43bad6ec5
                type = Int
                value = 17
              )
            ]
          )
          index = BInt(
            hash = 7188b43d5debd8d65201a289a38515321a8419bc78b29e75675211deff8b08ba
            type = Int
            value = 0
          )
        )""");
  }
}
