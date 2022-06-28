package org.smoothbuild.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Loc.internal;
import static org.smoothbuild.testing.TestingContext.loc;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class FuncTPTest {
  @Nested
  class _is_polytype {
    @Test
    public void func_with_var_in_result_type() {
      TypeP func = new FuncTP(var(), list(), internal());
      assertThat(func.isPolytype())
          .isTrue();
    }

    @Test
    public void func_with_var_in_param_type() {
      TypeP func = new FuncTP(
          normalType(), list(var()), internal());
      assertThat(func.isPolytype())
          .isTrue();
    }

    @Test
    public void func_without_vars() {
      TypeP func = new FuncTP(
          normalType(), list(normalType()), internal());
      assertThat(func.isPolytype())
          .isFalse();
    }

    @Test
    public void polytype_func_node_with_depth_2_in_result_type() {
      TypeP inner = new FuncTP(var(), list(normalType()), internal());
      TypeP func = new FuncTP(inner, list(normalType()), internal());
      assertThat(func.isPolytype())
          .isTrue();
    }

    @Test
    public void polytype_func_node_with_depth_2_in_param_type() {
      TypeP inner = new FuncTP(var(), list(normalType()), internal());
      TypeP func = new FuncTP(normalType(), list(inner), internal());
      assertThat(func.isPolytype())
          .isTrue();
    }

    @Test
    public void non_polytype_func_node_with_depth_2() {
      TypeP inner = new FuncTP(
          normalType(), list(normalType()), internal());
      TypeP func = new FuncTP(inner, list(inner), internal());
      assertThat(func.isPolytype())
          .isFalse();
    }
  }

  private static TypeP normalType() {
    return new TypeP("MyType", internal());
  }

  private static TypeP var() {
    return var("A");
  }

  private static TypeP var(String name) {
    return new TypeP(name, loc());
  }
}
