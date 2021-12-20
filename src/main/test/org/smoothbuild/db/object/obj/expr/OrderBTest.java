package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
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
    orderB(arrayTB(varA), list(arrayB(varA)));
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

  @Test
  public void arrays_with_equal_elems_are_equal() {
    ImmutableList<ObjB> elems = list(intB(2)) ;
    assertThat(orderB(elems))
        .isEqualTo(orderB(elems));
  }

  @Test
  public void arrays_with_different_elems_are_not_equal() {
    assertThat(orderB(list(intB(1))))
        .isNotEqualTo(orderB(list(intB(2))));
  }

  @Test
  public void hash_of_arrays_with_equal_elems_is_the_same() {
    ImmutableList<ObjB> elems = list(intB(1));
    assertThat(orderB(elems).hash())
        .isEqualTo(orderB(elems).hash());
  }

  @Test
  public void hash_of_arrays_with_different_elems_is_not_the_same() {
    assertThat(orderB(list(intB(1))).hash())
        .isNotEqualTo(orderB(list(intB(2))).hash());
  }

  @Test
  public void hash_code_of_arrays_with_equal_elems_is_the_same() {
    ImmutableList<ObjB> elems = list(intB(1));
    assertThat(orderB(elems).hashCode())
        .isEqualTo(orderB(elems).hashCode());
  }

  @Test
  public void hash_code_of_arrays_with_different_elems_is_not_the_same() {
    assertThat(orderB(list(intB(1))).hashCode())
        .isNotEqualTo(orderB(list(intB(2))).hashCode());
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
