package org.smoothbuild.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.define.Loc.internal;
import static org.smoothbuild.testing.TestingContext.loc;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class FuncTNTest {
  @Nested
  class _is_polytype {
    @Test
    public void func_with_var_in_result_type() {
      TypeN func = new FuncTN(var(), list(), internal());
      assertThat(func.isPolytype())
          .isTrue();
    }

    @Test
    public void func_with_var_in_param_type() {
      TypeN func = new FuncTN(
          normalType(), list(var()), internal());
      assertThat(func.isPolytype())
          .isTrue();
    }

    @Test
    public void func_without_vars() {
      TypeN func = new FuncTN(
          normalType(), list(normalType()), internal());
      assertThat(func.isPolytype())
          .isFalse();
    }

    @Test
    public void polytype_func_node_with_depth_2_in_result_type() {
      TypeN inner = new FuncTN(var(), list(normalType()), internal());
      TypeN func = new FuncTN(inner, list(normalType()), internal());
      assertThat(func.isPolytype())
          .isTrue();
    }

    @Test
    public void polytype_func_node_with_depth_2_in_param_type() {
      TypeN inner = new FuncTN(var(), list(normalType()), internal());
      TypeN func = new FuncTN(normalType(), list(inner), internal());
      assertThat(func.isPolytype())
          .isTrue();
    }

    @Test
    public void non_polytype_func_node_with_depth_2() {
      TypeN inner = new FuncTN(
          normalType(), list(normalType()), internal());
      TypeN func = new FuncTN(inner, list(inner), internal());
      assertThat(func.isPolytype())
          .isFalse();
    }
  }

  private static TypeN normalType() {
    return new TypeN("MyType", internal());
  }

  private static TypeN var() {
    return var("A");
  }

  private static TypeN var(String name) {
    return new TypeN(name, loc());
  }
}
