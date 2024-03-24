package org.smoothbuild.virtualmachine.bytecode.expr.oper;

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
    var order = orderB(intTB());
    assertThat(order.kind()).isEqualTo(orderCB(intTB()));
  }

  @Test
  public void creating_order_with_elemT_different_than_required_causes_exception()
      throws Exception {
    assertCall(() -> orderB(intTB(), stringB("abc")).kind())
        .throwsException(new IllegalArgumentException("Illegal elem type. Expected "
            + intTB().q() + " but element at index 0 has type " + stringTB().q() + "."));
  }

  @Test
  public void elemT_can_be_equal_elementT_specified_in_kind() throws Exception {
    orderB(arrayTB(intTB()), arrayB(intB(3)));
  }

  @Test
  public void elements_returns_elements() throws Exception {
    assertThat(orderB(intB(2)).elements()).isEqualTo(list(intB(2)));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BOrder> {
    @Override
    protected java.util.List<BOrder> equalExprs() throws BytecodeException {
      return list(orderB(intB(1), intB(2)), orderB(intB(1), intB(2)));
    }

    @Override
    protected java.util.List<BOrder> nonEqualExprs() throws BytecodeException {
      return list(
          orderB(intTB()),
          orderB(stringTB()),
          orderB(intB(1)),
          orderB(intB(2)),
          orderB(intB(1), intB(2)),
          orderB(intB(1), intB(3)));
    }
  }

  @Test
  public void array_can_be_read_back_by_hash() throws Exception {
    var order = orderB(intB(1));
    assertThat(exprDbOther().get(order.hash())).isEqualTo(order);
  }

  @Test
  public void array_read_back_by_hash_has_same_elementss() throws Exception {
    var order = orderB(intB(1));
    assertThat(((BOrder) exprDbOther().get(order.hash())).elements()).isEqualTo(list(intB(1)));
  }

  @Test
  public void to_string() throws Exception {
    var order = orderB(intB(1));
    assertThat(order.toString()).isEqualTo("ORDER:[Int](???)@" + order.hash());
  }
}
