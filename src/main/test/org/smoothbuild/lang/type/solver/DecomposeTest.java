package org.smoothbuild.lang.type.solver;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.smoothbuild.lang.type.ConstrS.constrS;
import static org.smoothbuild.lang.type.solver.Decompose.decompose;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.type.ConstrS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.testing.type.TestingTS;

@TestInstance(PER_CLASS)
public class DecomposeTest extends TestingTS {
  @TestInstance(PER_CLASS)
  @Nested
  class _decomposable_to_empty_set extends TestingTS {
    @TestInstance(PER_CLASS)
    @Nested
    class _type_vs_itself extends TestingTS {
      @ParameterizedTest
      @MethodSource("elementaryTypes")
      public void elementary_type_vs_itself(TypeS type) throws Exception {
        assertThat(decompose(constrS(type, type)))
            .isEmpty();
      }

      @Test
      public void var_vs_itself() throws Exception {
        var varA = var("A");
        assertThat(decompose(constrS(varA, varA)))
            .isEmpty();
      }

      @Test
      public void nothing_vs_itself() throws Exception {
        assertThat(decompose(constrS(nothing(), nothing())))
            .isEmpty();
      }

      @Test
      public void any_vs_itself() throws Exception {
        assertThat(decompose(constrS(any(), any())))
            .isEmpty();
      }

      @Test
      public void array_vs_array_of_same_base_type() throws Exception {
        var array = array(blob());
        assertThat(decompose(constrS(array, array)))
            .isEmpty();
      }

      @Test
      public void func_vs_func_with_same_paramTs_and_resT() throws Exception {
        var func = func(blob(), list(bool(), string()));
        assertThat(decompose(constrS(func, func)))
            .isEmpty();
      }
    }

    @TestInstance(PER_CLASS)
    @Nested
    class _nothing_vs_sth extends TestingTS {
      @ParameterizedTest
      @MethodSource("elementaryTypes")
      public void noting_vs_elementary_type(TypeS type) throws Exception {
        assertThat(decompose(constrS(nothing(), type)))
            .isEmpty();
      }

      @Test
      public void noting_vs_var() throws Exception {
        assertThat(decompose(constrS(nothing(), varA())))
            .isEmpty();
      }

      @Test
      public void noting_vs_array() throws Exception {
        assertThat(decompose(constrS(nothing(), array(blob()))))
            .isEmpty();
      }

      @Test
      public void noting_vs_any() throws Exception {
        assertThat(decompose(constrS(nothing(), any())))
            .isEmpty();
      }

      @Test
      public void nothing_vs_func() throws Exception {
        assertThat(decompose(constrS(nothing(), func(blob(), list()))))
            .isEmpty();
      }
    }

    @TestInstance(PER_CLASS)
    @Nested
    class _sth_vs_any extends TestingTS {
      @ParameterizedTest
      @MethodSource("elementaryTypes")
      public void elementary_type_vs_any(TypeS type) throws Exception {
        assertThat(decompose(constrS(type, any())))
            .isEmpty();
      }

      @Test
      public void var_vs_any() throws Exception {
        assertThat(decompose(constrS(varA(), any())))
            .isEmpty();
      }

      @Test
      public void array_vs_any() throws Exception {
        assertThat(decompose(constrS(array(blob()), any())))
            .isEmpty();
      }

      @Test
      public void func_vs_any() throws Exception {
        assertThat(decompose(constrS(func(blob(), list()), any())))
            .isEmpty();
      }
    }
  }

  @Nested
  class _unsolvable {
    @Test
    public void two_constructed_types() {
      assertDecomposeFails(constrS(bool(), string()));
    }

    @Test
    public void two_different_structs() {
      assertDecomposeFails(constrS(
          struct("MyStruct1", nList()),
          struct("MyStruct2", nList())));
    }

    @Test
    public void any_vs_nothing() {
      assertDecomposeFails(constrS(any(), nothing()));
    }
  }

  @Nested
  class _array {
    @Test
    public void vs_array() throws Exception {
      assertThat(decompose(constrS(array(blob()), array(varA()))))
          .containsExactly(constrS(blob(), varA()));
    }

    @Test
    public void vs_base_type() {
      assertDecomposeFails(constrS(array(blob()), bool()));
    }
  }

  @Nested
  class _func {
    @Test
    public void vs_func() throws Exception {
      assertThat(decompose(constrS(func(blob(), list(int_())), func(varA(), list(varB())))))
          .containsExactly(
              constrS(blob(), varA()),
              constrS(varB(), int_()));
    }

    @Test
    public void vs_base_type() {
      assertDecomposeFails(constrS(func(blob(), list()), bool()));
    }
  }

  @Nested
  class _join {
    @Test
    public void vs_var() throws Exception {
      assertThat(decompose(constrS(join(blob(), int_()), varA())))
          .containsExactly(
              constrS(blob(), varA()),
              constrS(int_(), varA()));
    }

    @Test
    public void join_with_one_element_unsolvable_against_upper_side() {
      assertDecomposeFailsWithReason(
          constrS(join(blob(), int_()), blob()),
          constrS(int_(), blob()));
    }

    @Test
    public void on_right_side() {
      assertDecomposeFails(constrS(varA(), join(blob(), int_())));
    }
  }

  @Nested
  class _meet {
    @Test
    public void vs_var() throws Exception {
      assertThat(decompose(constrS(varA(), meet(blob(), int_()))))
          .containsExactly(constrS(varA(), blob()), constrS(varA(), int_()));
    }

    @Test
    public void meet_with_one_element_unsolvable_against_lower_side() {
      assertDecomposeFailsWithReason(
          constrS(blob(), meet(blob(), int_())),
          constrS(blob(), int_()));
    }

    @Test
    public void on_left_side() {
      assertDecomposeFails(constrS(meet(blob(), int_()), varA()));
    }
  }

  private void assertDecomposeFails(ConstrS constr) {
    assertDecomposeFailsWithReason(constr, constr);
  }

  private void assertDecomposeFailsWithReason(ConstrS constr, ConstrS reason) {
    assertCall(() -> decompose(constr))
        .throwsException(new ConstrDecomposeExc(reason));
  }
}
