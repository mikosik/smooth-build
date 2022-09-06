package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.ExprBTestCase;
import org.smoothbuild.testing.TestContext;

public class OrderBTest extends TestContext {
  @Test
  public void cat_returns_category() {
    var orderH = orderB(intTB());
    assertThat(orderH.cat())
        .isEqualTo(orderCB(intTB()));
  }

  @Test
  public void creating_order_with_elemT_different_than_required_causes_exception() {
    assertCall(() -> orderB(intTB(), stringB("abc")).cat())
        .throwsException(new IllegalArgumentException("Illegal elem type. Expected " + intTB().q()
            + " but element at index 0 has type " + stringTB().q() + "."));
  }

  @Test
  public void elemT_can_be_equal_elemT_specified_in_category() {
    orderB(arrayTB(intTB()), arrayB(intB(3)));
  }

  @Test
  public void elems_returns_elems() {
    assertThat(orderB(intB(2)).elems())
        .isEqualTo(list(intB(2)));
  }

  @Nested
  class _equals_hash_hashcode extends ExprBTestCase<OrderB> {
    @Override
    protected List<OrderB> equalExprs() {
      return list(
          orderB(intB(1), intB(2)),
          orderB(intB(1), intB(2))
      );
    }

    @Override
    protected List<OrderB> nonEqualExprs() {
      return list(
          orderB(intTB()),
          orderB(stringTB()),
          orderB(intB(1)),
          orderB(intB(2)),
          orderB(intB(1), intB(2)),
          orderB(intB(1), intB(3))
      );
    }
  }

  @Test
  public void array_can_be_read_back_by_hash() {
    OrderB array = orderB(intB(1));
    assertThat(bytecodeDbOther().get(array.hash()))
        .isEqualTo(array);
  }

  @Test
  public void array_read_back_by_hash_has_same_elems() {
    OrderB array = orderB(intB(1));
    assertThat(((OrderB) bytecodeDbOther().get(array.hash())).elems())
        .isEqualTo(list(intB(1)));
  }

  @Test
  public void to_string() {
    OrderB array = orderB(intB(1));
    assertThat(array.toString())
        .isEqualTo("Order:[Int](???)@" + array.hash());
  }
}