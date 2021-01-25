package org.smoothbuild.lang.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.TestingLocation.loc;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TypeNodeTest {
  @Nested
  class _is_polytype {
    @Test
    public void node_with_type_variable_name_is_polytype() {
      TypeNode typeNode = new TypeNode("B", loc());
      assertThat(typeNode.isPolytype())
          .isTrue();
    }

    @Test
    public void node_with_non_type_variable_name_is_not_polytype() {
      TypeNode typeNode = new TypeNode("MyType", loc());
      assertThat(typeNode.isPolytype())
          .isFalse();
    }
  }

  @Nested
  class _variables {
    @Test
    public void type_node_which_is_a_variable() {
      TypeNode typeNode = new TypeNode("A", loc());
      assertThat(typeNode.variablesUsedOnce())
          .containsExactly("A");
    }
    @Test
    public void type_node_which_is_not_a_variable() {
      TypeNode typeNode = new TypeNode("MyType", loc());
      assertThat(typeNode.variablesUsedOnce())
          .isEmpty();
    }
  }
}
