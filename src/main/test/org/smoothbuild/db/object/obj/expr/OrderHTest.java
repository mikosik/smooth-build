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
  public void type_of_empty_array_is_inferred_correctly() {
    assertThat(orderH(list()).cat())
        .isEqualTo(orderCH(nothingTH()));
  }

  @Test
  public void type_of_array_is_inferred_correctly() {
    assertThat(orderH(list(intH(3))).cat())
        .isEqualTo(orderCH(intTH()));
  }

  @Test
  public void creating_array_with_elems_with_different_evaluation_type_causes_exception() {
    assertCall(() -> orderH(list(intH(3), stringH("abc"))).cat())
        .throwsException(new IllegalArgumentException("Element evaluation types are not equal "
            + intTH().name() + " != " + stringTH().name() + "."));
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
