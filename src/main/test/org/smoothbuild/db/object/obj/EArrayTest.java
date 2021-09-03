package org.smoothbuild.db.object.obj;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.obj.expr.EArray;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class EArrayTest extends TestingContext {
  @Test
  public void spec_of_earray_is_earray_spec() {
    assertThat(eArrayExpr(list()).spec())
        .isEqualTo(eArraySpec());
  }

  @Test
  public void elements_returns_elements() {
    ImmutableList<Const> elements = list(constExpr(intVal(2)));
    assertThat(eArrayExpr(elements).elements())
        .isEqualTo(elements);
  }

  @Test
  public void earrays_with_equal_values_are_equal() {
    List<Const> elements = list(constExpr(intVal(2))) ;
    assertThat(eArrayExpr(elements))
        .isEqualTo(eArrayExpr(elements));
  }

  @Test
  public void earrays_with_different_elements_are_not_equal() {
    assertThat(eArrayExpr(list(constExpr(intVal(1)))))
        .isNotEqualTo(eArrayExpr(list(constExpr(intVal(2)))));
  }

  @Test
  public void hash_of_earrays_with_equal_elements_is_the_same() {
    ImmutableList<Const> elements = list(constExpr(intVal(1)));
    assertThat(eArrayExpr(elements).hash())
        .isEqualTo(eArrayExpr(elements).hash());
  }

  @Test
  public void hash_of_earrays_with_different_elements_is_not_the_same() {
    assertThat(eArrayExpr(list(constExpr(intVal(1)))).hash())
        .isNotEqualTo(eArrayExpr(list(constExpr(intVal(2)))).hash());
  }

  @Test
  public void hash_code_of_earrays_with_equal_elements_is_the_same() {
    ImmutableList<Const> elements = list(constExpr(intVal(1)));
    assertThat(eArrayExpr(elements).hashCode())
        .isEqualTo(eArrayExpr(elements).hashCode());
  }

  @Test
  public void hash_code_of_earrays_with_different_elements_is_not_the_same() {
    assertThat(eArrayExpr(list(constExpr(intVal(1)))).hashCode())
        .isNotEqualTo(eArrayExpr(list(constExpr(intVal(2)))).hashCode());
  }

  @Test
  public void earray_can_be_read_back_by_hash() {
    EArray array = eArrayExpr(list(constExpr(intVal(1))));
    assertThat(objectDbOther().get(array.hash()))
        .isEqualTo(array);
  }

  @Test
  public void earray_read_back_by_hash_has_same_elements() {
    ImmutableList<Const> elements = list(constExpr(intVal(1)));
    EArray array = eArrayExpr(elements);
    assertThat(((EArray) objectDbOther().get(array.hash())).elements())
        .isEqualTo(elements);
  }

  @Test
  public void to_string() {
    EArray array = eArrayExpr(list(constExpr(intVal(1))));
    assertThat(array.toString())
        .isEqualTo("EArray(???):" + array.hash());
  }
}
