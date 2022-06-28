package org.smoothbuild.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.TestingContext.loc;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TypePTest {
  @Nested
  class _is_polytype {
    @Test
    public void node_with_type_var_name_is_polytype() {
      TypeP typeP = new TypeP("B", loc());
      assertThat(typeP.isPolytype())
          .isTrue();
    }

    @Test
    public void node_with_non_type_var_name_is_not_polytype() {
      TypeP typeP = new TypeP("MyType", loc());
      assertThat(typeP.isPolytype())
          .isFalse();
    }
  }
}
