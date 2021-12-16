package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class OrderHTest extends TestingContext {
  @Test
  public void cat_returns_category() {
    var orderH = orderH(intTH(), list());
    assertThat(orderH.cat())
        .isEqualTo(orderCH(intTH()));
  }

  @Test
  public void creating_order_with_elemT_different_than_required_causes_exception() {
    assertCall(() -> orderH(intTH(), list(stringH("abc"))).cat())
        .throwsException(new IllegalArgumentException("Illegal elem type. Expected " + intTH().q()
            + " but element at index 0 has type " + stringTH().q() + "."));
  }

  @Test
  public void elemT_can_be_equal_elemT_specified_in_category() {
    orderH(arrayTH(intTH()), list(arrayH(intH(3))));
  }

  @Test
  public void elemT_can_be_equal_polytype_elemT_specified_in_category() {
    var varA = varTH("A");
    orderH(arrayTH(varA), list(arrayH(varA)));
  }

  @Test
  public void elemT_can_be_subtype_of_elemT_specified_in_category() {
    orderH(arrayTH(intTH()), list(arrayH(nothingTH())));
  }

  @Test
  public void elems_returns_elems() {
    ImmutableList<ObjH> elems = list(intH(2));
    assertThat(orderH(elems).elems())
        .isEqualTo(elems);
  }

  @Test
  public void arrays_with_equal_elems_are_equal() {
    ImmutableList<ObjH> elems = list(intH(2)) ;
    assertThat(orderH(elems))
        .isEqualTo(orderH(elems));
  }

  @Test
  public void arrays_with_different_elems_are_not_equal() {
    assertThat(orderH(list(intH(1))))
        .isNotEqualTo(orderH(list(intH(2))));
  }

  @Test
  public void hash_of_arrays_with_equal_elems_is_the_same() {
    ImmutableList<ObjH> elems = list(intH(1));
    assertThat(orderH(elems).hash())
        .isEqualTo(orderH(elems).hash());
  }

  @Test
  public void hash_of_arrays_with_different_elems_is_not_the_same() {
    assertThat(orderH(list(intH(1))).hash())
        .isNotEqualTo(orderH(list(intH(2))).hash());
  }

  @Test
  public void hash_code_of_arrays_with_equal_elems_is_the_same() {
    ImmutableList<ObjH> elems = list(intH(1));
    assertThat(orderH(elems).hashCode())
        .isEqualTo(orderH(elems).hashCode());
  }

  @Test
  public void hash_code_of_arrays_with_different_elems_is_not_the_same() {
    assertThat(orderH(list(intH(1))).hashCode())
        .isNotEqualTo(orderH(list(intH(2))).hashCode());
  }

  @Test
  public void array_can_be_read_back_by_hash() {
    OrderH array = orderH(list(intH(1)));
    assertThat(objDbOther().get(array.hash()))
        .isEqualTo(array);
  }

  @Test
  public void array_read_back_by_hash_has_same_elems() {
    ImmutableList<ObjH> elems = list(intH(1));
    OrderH array = orderH(elems);
    assertThat(((OrderH) objDbOther().get(array.hash())).elems())
        .isEqualTo(elems);
  }

  @Test
  public void to_string() {
    OrderH array = orderH(list(intH(1)));
    assertThat(array.toString())
        .isEqualTo("Order:[Int](???)@" + array.hash());
  }
}
