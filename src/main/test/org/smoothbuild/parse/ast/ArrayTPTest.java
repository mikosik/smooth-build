package org.smoothbuild.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Loc.internal;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ArrayTPTest {
  @Nested
  class is_polytype {
    @Test
    public void polytype_array_node() {
      TypeP typeP = new ArrayTP(new TypeP("B", internal()), internal());
      assertThat(typeP.isPolytype())
          .isTrue();
    }

    @Test
    public void non_polytype_array_node() {
      TypeP typeP = new ArrayTP(new TypeP("MyType", internal()),
          internal());
      assertThat(typeP.isPolytype())
          .isFalse();
    }

    @Test
    public void polytype_array_node_with_depth_2() {
      TypeP typeP = new ArrayTP(
          new ArrayTP(
              new TypeP("B", internal()),
              internal()),
          internal());
      assertThat(typeP.isPolytype())
          .isTrue();
    }

    @Test
    public void non_polytype_array_node_with_depth_2() {
      var typeN = new ArrayTP(new ArrayTP(new TypeP("MyType", internal()), internal()), internal());
      assertThat(typeN.isPolytype())
          .isFalse();
    }
  }
}
