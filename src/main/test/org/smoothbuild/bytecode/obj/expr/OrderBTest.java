package org.smoothbuild.bytecode.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.ObjBTestCase;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.collect.Lists;

public class OrderBTest extends TestingContext {
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
  public void elemT_can_be_equal_polytype_elemT_specified_in_category() {
    var varA = cVarTB("A");
    orderB(varA, paramRefB(varA, 0));
  }

  @Test
  public void elemT_can_be_subtype_of_elemT_specified_in_category() {
    var elem = arrayB(nothingTB());
    var orderB = orderB(arrayTB(intTB()), elem);
    assertThat(orderB.elems().get(0))
        .isEqualTo(elem);
  }

  @Test
  public void creating_order_with_resT_having_open_vars_causes_exc() {
    var a = oVarTB("A");
    assertCall(() -> orderB(a))
        .throwsException(new IllegalArgumentException("evalT must not have open vars"));
  }

  @Test
  public void elems_returns_elems() {
    assertThat(orderB(intB(2)).elems())
        .isEqualTo(list(intB(2)));
  }

  @Nested
  class _equals_hash_hashcode extends ObjBTestCase<OrderB> {
    @Override
    protected List<OrderB> equalValues() {
      return list(
          orderB(intB(1), intB(2)),
          orderB(intB(1), intB(2))
      );
    }

    @Override
    protected List<OrderB> nonEqualValues() {
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
    assertThat(objDbOther().get(array.hash()))
        .isEqualTo(array);
  }

  @Test
  public void array_read_back_by_hash_has_same_elems() {
    OrderB array = orderB(intB(1));
    assertThat(((OrderB) objDbOther().get(array.hash())).elems())
        .isEqualTo(Lists.<ObjB>list(intB(1)));
  }

  @Test
  public void to_string() {
    OrderB array = orderB(intB(1));
    assertThat(array.toString())
        .isEqualTo("Order:[Int](???)@" + array.hash());
  }
}
