package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.ObjBTestCase;
import org.smoothbuild.db.object.obj.base.ObjB;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class OrderBTest extends TestingContext {
  @Test
  public void cat_returns_category() {
    var orderH = orderB(intTB(), list());
    assertThat(orderH.cat())
        .isEqualTo(orderCB(intTB()));
  }

  @Test
  public void creating_order_with_elemT_different_than_required_causes_exception() {
    assertCall(() -> orderB(intTB(), list(stringB("abc"))).cat())
        .throwsException(new IllegalArgumentException("Illegal elem type. Expected " + intTB().q()
            + " but element at index 0 has type " + stringTB().q() + "."));
  }

  @Test
  public void elemT_can_be_equal_elemT_specified_in_category() {
    orderB(arrayTB(intTB()), list(arrayB(intB(3))));
  }

  @Test
  public void elemT_can_be_equal_polytype_elemT_specified_in_category() {
    var varA = varTB("A");
    orderB(varA, list(paramRefB(varA, 0)));
  }

  @Test
  public void elemT_can_be_subtype_of_elemT_specified_in_category() {
    var elem = arrayB(nothingTB());
    var orderB = orderB(arrayTB(intTB()), list(elem));
    assertThat(orderB.elems().get(0))
        .isEqualTo(elem);
  }

  @Test
  public void elems_returns_elems() {
    ImmutableList<ObjB> elems = list(intB(2));
    assertThat(orderB(elems).elems())
        .isEqualTo(elems);
  }

  @Nested
  class _equals_hash_hashcode extends ObjBTestCase<OrderB> {
    @Override
    protected List<OrderB> equalValues() {
      return list(
          orderB(list(intB(1), intB(2))),
          orderB(list(intB(1), intB(2)))
      );
    }

    @Override
    protected List<OrderB> nonEqualValues() {
      return list(
          orderB(intTB(), list()),
          orderB(stringTB(), list()),
          orderB(list(intB(1))),
          orderB(list(intB(2))),
          orderB(list(intB(1), intB(2))),
          orderB(list(intB(1), intB(3)))
      );
    }
  }

  @Test
  public void array_can_be_read_back_by_hash() {
    OrderB array = orderB(list(intB(1)));
    assertThat(byteDbOther().get(array.hash()))
        .isEqualTo(array);
  }

  @Test
  public void array_read_back_by_hash_has_same_elems() {
    ImmutableList<ObjB> elems = list(intB(1));
    OrderB array = orderB(elems);
    assertThat(((OrderB) byteDbOther().get(array.hash())).elems())
        .isEqualTo(elems);
  }

  @Test
  public void to_string() {
    OrderB array = orderB(list(intB(1)));
    assertThat(array.toString())
        .isEqualTo("Order:[Int](???)@" + array.hash());
  }
}
