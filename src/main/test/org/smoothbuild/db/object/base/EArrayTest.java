package org.smoothbuild.db.object.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class EArrayTest extends TestingContext {
  @Test
  public void spec_of_earray_is_earray() {
    assertThat(eArray(list()).spec())
        .isEqualTo(eArrayS());
  }

  @Test
  public void elements_returns_elements() {
    ImmutableList<Const> elements = list(constE(intV(2)));
    assertThat(eArray(elements).elements())
        .isEqualTo(elements);
  }

  @Test
  public void earrays_with_equal_values_are_equal() {
    List<Const> elements = list(constE(intV(2))) ;
    assertThat(eArray(elements))
        .isEqualTo(eArray(elements));
  }

  @Test
  public void earrays_with_different_elements_are_not_equal() {
    assertThat(eArray(list(constE(intV(1)))))
        .isNotEqualTo(eArray(list(constE(intV(2)))));
  }

  @Test
  public void hash_of_earrays_with_equal_elements_is_the_same() {
    ImmutableList<Const> elements = list(constE(intV(1)));
    assertThat(eArray(elements).hash())
        .isEqualTo(eArray(elements).hash());
  }

  @Test
  public void hash_of_earrays_with_different_elements_is_not_the_same() {
    assertThat(eArray(list(constE(intV(1)))).hash())
        .isNotEqualTo(eArray(list(constE(intV(2)))).hash());
  }

  @Test
  public void hash_code_of_earrays_with_equal_elements_is_the_same() {
    ImmutableList<Const> elements = list(constE(intV(1)));
    assertThat(eArray(elements).hashCode())
        .isEqualTo(eArray(elements).hashCode());
  }

  @Test
  public void hash_code_of_earrays_with_different_elements_is_not_the_same() {
    assertThat(eArray(list(constE(intV(1)))).hashCode())
        .isNotEqualTo(eArray(list(constE(intV(2)))).hashCode());
  }

  @Test
  public void earray_can_be_read_back_by_hash() {
    EArray array = eArray(list(constE(intV(1))));
    assertThat(objectDbOther().get(array.hash()))
        .isEqualTo(array);
  }

  @Test
  public void earray_read_back_by_hash_has_same_elements() {
    ImmutableList<Const> elements = list(constE(intV(1)));
    EArray array = eArray(elements);
    assertThat(((EArray) objectDbOther().get(array.hash())).elements())
        .isEqualTo(elements);
  }

  @Test
  public void to_string() {
    EArray array = eArray(list(constE(intV(1))));
    assertThat(array.toString())
        .isEqualTo("EArray(???):" + array.hash());
  }
}
