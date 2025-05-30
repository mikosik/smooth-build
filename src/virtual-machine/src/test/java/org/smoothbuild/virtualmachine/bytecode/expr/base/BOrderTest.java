package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BOrderTest extends VmTestContext {
  @Test
  void kind_returns_kind() throws Exception {
    var order = bOrder(bIntType());
    assertThat(order.kind()).isEqualTo(bOrderKind(bIntType()));
  }

  @Test
  void creating_order_with_elemT_different_than_required_causes_exception() {
    assertCall(() -> bOrder(bIntType(), bString("abc")).kind())
        .throwsException(new IllegalArgumentException(
            "`element0.evaluationType()` should be `Int` but is `String`."));
  }

  @Test
  void elemT_can_be_equal_elementT_specified_in_kind() throws Exception {
    bOrder(bIntArrayType(), bArray(bInt(3)));
  }

  @Test
  void elements_returns_elements() throws Exception {
    assertThat(bOrder(bInt(2)).elements()).isEqualTo(list(bInt(2)));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BOrder> {
    @Override
    protected List<BOrder> equalExprs() throws BytecodeException {
      return list(bOrder(bInt(1), bInt(2)), bOrder(bInt(1), bInt(2)));
    }

    @Override
    protected List<BOrder> nonEqualExprs() throws BytecodeException {
      return list(
          bOrder(bIntType()),
          bOrder(bStringType()),
          bOrder(bInt(1)),
          bOrder(bInt(2)),
          bOrder(bInt(1), bInt(2)),
          bOrder(bInt(1), bInt(3)));
    }
  }

  @Test
  void array_can_be_read_back_by_hash() throws Exception {
    var order = bOrder(bInt(1));
    assertThat(exprDbOther().get(order.hash())).isEqualTo(order);
  }

  @Test
  void array_read_back_by_hash_has_same_elementss() throws Exception {
    var order = bOrder(bInt(1));
    assertThat(((BOrder) exprDbOther().get(order.hash())).elements()).isEqualTo(list(bInt(1)));
  }

  @Test
  void to_string() throws Exception {
    var order = bOrder(bInt(1));
    assertThat(order.toString())
        .isEqualTo(
            """
        BOrder(
          hash = 32525892ab4d75f2b1f23293d34118c444fa06fe837ee9efaa2072032c879054
          evaluationType = [Int]
          elements = [
            BInt(
              hash = b4f5acf1123d217b7c40c9b5f694b31bf83c07bd40b24fe42cadb0e458f4ab45
              type = Int
              value = 1
            )
          ]
        )""");
  }
}
