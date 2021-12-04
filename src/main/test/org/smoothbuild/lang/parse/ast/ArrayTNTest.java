package org.smoothbuild.lang.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.define.Loc.internal;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ArrayTNTest {
  @Nested
  class is_polytype {
    @Test
    public void polytype_array_node() {
      TypeN typeN = new ArrayTN(new TypeN("B", internal()), internal());
      assertThat(typeN.isPolytype())
          .isTrue();
    }

    @Test
    public void non_polytype_array_node() {
      TypeN typeN = new ArrayTN(new TypeN("MyType", internal()),
          internal());
      assertThat(typeN.isPolytype())
          .isFalse();
    }

    @Test
    public void polytype_array_node_with_depth_2() {
      TypeN typeN = new ArrayTN(
          new ArrayTN(
              new TypeN("B", internal()),
              internal()),
          internal());
      assertThat(typeN.isPolytype())
          .isTrue();
    }

    @Test
    public void non_polytype_array_node_with_depth_2() {
      var typeN = new ArrayTN(new ArrayTN(new TypeN("MyType", internal()), internal()), internal());
      assertThat(typeN.isPolytype())
          .isFalse();
    }
  }

  @Nested
  class _vars_used_once {
    @Test
    public void array_node_which_elem_is_a_var() {
      TypeN elemTN = new TypeN("A", internal());
      TypeN typeN = new ArrayTN(elemTN, internal());
      assertThat(typeN.varsUsedOnce())
          .containsExactly("A");
    }

    @Test
    public void array_node_which_elem_is_not_a_var() {
      TypeN elemTN = new TypeN("MyType", internal());
      TypeN typeN = new ArrayTN(elemTN, internal());
      assertThat(typeN.varsUsedOnce())
          .isEmpty();
    }

    @Test
    public void array_of_array_which_elem_is_a_var() {
      TypeN elemTN = new TypeN("A", internal());
      TypeN typeN = new ArrayTN(new ArrayTN(elemTN, internal()), internal());
      assertThat(typeN.varsUsedOnce())
          .containsExactly("A");
    }

    @Test
    public void array_of_array_which_elem_is_not_a_var() {
      TypeN elemTN = new TypeN("MyType", internal());
      TypeN typeN = new ArrayTN(new ArrayTN(elemTN, internal()), internal());
      assertThat(typeN.varsUsedOnce())
          .isEmpty();
    }
  }
}
