package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.ExprBTestCase;
import org.smoothbuild.testing.TestContext;

public class PickBTest extends TestContext {
  @Test
  public void creating_pick_with_non_array_expr_as_pickable_causes_exception() {
    assertCall(() -> pickB(intB(3), intB(2)))
        .throwsException(new IllegalArgumentException(
            "pickable.evalT() should be ArrayTB but is `Int`."));
  }

  @Test
  public void creating_pick_with_non_int_expr_as_index_causes_exception() {
    assertCall(() -> pickB(arrayB(boolTB()), stringB()))
        .throwsException(new IllegalArgumentException(
            "index.evalT() should be IntTB but is `String`."));
  }

  @Test
  public void data_returns_array_and_index() {
    var pickable = arrayB(intB(7));
    var index = intB(0);
    assertThat(pickB(pickable, index).dataSeq())
        .isEqualTo(list(pickable, index));
  }

  @Nested
  class _equals_hash_hashcode extends ExprBTestCase<PickB> {
    @Override
    protected List<PickB> equalExprs() {
      return list(
          pickB(arrayB(intB(7), intB(9)), intB(0)),
          pickB(arrayB(intB(7), intB(9)), intB(0))
      );
    }

    @Override
    protected List<PickB> nonEqualExprs() {
      return list(
          pickB(arrayB(intB(1)), intB(0)),
          pickB(arrayB(intB(2)), intB(0)),
          pickB(arrayB(intB(2), intB(2)), intB(0)),
          pickB(arrayB(intB(2), intB(2)), intB(1)),
          pickB(arrayB(intB(2), intB(7)), intB(0)),
          pickB(arrayB(intB(7), intB(2)), intB(0))
      );
    }
  }

  @Test
  public void pick_can_be_read_back_by_hash() {
    var pick = pickB(arrayB(intB(7)), intB(0));
    assertThat(bytecodeDbOther().get(pick.hash()))
        .isEqualTo(pick);
  }

  @Test
  public void pick_read_back_by_hash_has_same_data() {
    var array = arrayB(intB(17), intB(18));
    var index = intB(0);
    var pick = pickB(array, index);
    assertThat(((PickB) bytecodeDbOther().get(pick.hash())).dataSeq())
        .isEqualTo(list(array, index));
  }

  @Test
  public void to_string() {
    var pick = pickB(arrayB(intB(17)), intB(0));
    assertThat(pick.toString())
        .isEqualTo("Pick:Int(???)@" + pick.hash());
  }
}
