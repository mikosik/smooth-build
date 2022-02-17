package org.smoothbuild.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.define.Loc.internal;

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
}
