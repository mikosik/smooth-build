package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BOrderTest extends TestingVirtualMachine {
  @Test
  public void kind_returns_kind() throws Exception {
    var order = bOrder(bIntType());
    assertThat(order.kind()).isEqualTo(bOrderKind(bIntType()));
  }

  @Test
  public void creating_order_with_elemT_different_than_required_causes_exception()
      throws Exception {
    assertCall(() -> bOrder(bIntType(), bString("abc")).kind())
        .throwsException(new IllegalArgumentException(
            "Illegal elem type. Expected " + bIntType().q() + " but element at index 0 has type "
                + bStringType().q() + "."));
  }

  @Test
  public void elemT_can_be_equal_elementT_specified_in_kind() throws Exception {
    bOrder(bArrayType(bIntType()), bArray(bInt(3)));
  }

  @Test
  public void elements_returns_elements() throws Exception {
    assertThat(bOrder(bInt(2)).elements()).isEqualTo(list(bInt(2)));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BOrder> {
    @Override
    protected java.util.List<BOrder> equalExprs() throws BytecodeException {
      return list(bOrder(bInt(1), bInt(2)), bOrder(bInt(1), bInt(2)));
    }

    @Override
    protected java.util.List<BOrder> nonEqualExprs() throws BytecodeException {
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
  public void array_can_be_read_back_by_hash() throws Exception {
    var order = bOrder(bInt(1));
    assertThat(exprDbOther().get(order.hash())).isEqualTo(order);
  }

  @Test
  public void array_read_back_by_hash_has_same_elementss() throws Exception {
    var order = bOrder(bInt(1));
    assertThat(((BOrder) exprDbOther().get(order.hash())).elements()).isEqualTo(list(bInt(1)));
  }

  @Test
  public void to_string() throws Exception {
    var order = bOrder(bInt(1));
    assertThat(order.toString()).isEqualTo("ORDER:[Int](???)@" + order.hash());
  }
}
