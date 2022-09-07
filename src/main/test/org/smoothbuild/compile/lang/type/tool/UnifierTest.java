package org.smoothbuild.compile.lang.type.tool;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.compile.lang.type.BaseTS;
import org.smoothbuild.compile.lang.type.TypeFS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;
import org.smoothbuild.testing.TestContext;

import com.google.common.collect.ImmutableList;

public class UnifierTest extends TestContext {
  private final Unifier unifier = new Unifier();

  @Nested
  class _single_unify_call {
    @Nested
    class _var_vs_var {
      @Test
      public void unify_a_with_itself() throws UnifierExc {
        assertUnifyInfers(
            varA(),
            varA(),
            varA(),
            varA());
      }

      @Test
      public void unify_a_with_b() throws UnifierExc {
        assertUnifyInfersEquality(
            varA(),
            varB(),
            varA(),
            varB());
      }

      @Test
      public void unify_a_with_b_unified_with_c() throws UnifierExc {
        unifier.unify(varA(), varB());
        unifier.unify(varB(), varC());
        assertThat(unifier.resolve(varA()))
            .isEqualTo(unifier.resolve(varC()));
      }
    }

    @Nested
    class _var_vs_non_var {
      @ParameterizedTest
      @MethodSource("baseTypes")
      public void unify_var_and_base(BaseTS baseTS) throws UnifierExc {
        assertUnifyInfers(
            varA(),
            baseTS,
            varA(),
            baseTS);
      }

      @Test
      public void unify_var_and_array_of_base() throws UnifierExc {
        assertUnifyInfers(
            varA(),
            arrayTS(intTS()),
            varA(),
            arrayTS(intTS()));
      }

      @Test
      public void unify_var_and_array_of_var() throws UnifierExc {
        assertUnifyInfers(
            varA(),
            arrayTS(varB()),
            varA(),
            arrayTS(varB()));
      }

      @Test
      public void unify_var_and_func_of_base() throws UnifierExc {
        assertUnifyInfers(
            varA(),
            funcTS(intTS(), blobTS()),
            varA(),
            funcTS(intTS(), blobTS()));
      }

      @Test
      public void unify_var_and_func_of_vars() throws UnifierExc {
        assertUnifyInfers(
            varA(),
            funcTS(varB(), varC()),
            varA(),
            funcTS(varB(), varC()));
      }

      public static ImmutableList<BaseTS> baseTypes() {
        return TypeFS.baseTs();
      }
    }

    @Nested
    class _non_var_vs_non_var {
      @Nested
      class _monomorphic {
        @ParameterizedTest
        @MethodSource("baseTypes")
        public void unify_equal_base_types(BaseTS baseTS) throws UnifierExc {
          unifier.unify(baseTS, baseTS);
        }

        @Test
        public void unify_equal_array_types() throws UnifierExc {
          unifier.unify(
              arrayTS(intTS()),
              arrayTS(intTS()));
        }

        @Test
        public void unify_equal_array2_types() throws UnifierExc {
          unifier.unify(
              arrayTS(arrayTS(intTS())),
              arrayTS(arrayTS(intTS())));
        }

        @Test
        public void unify_equal_func_types() throws UnifierExc {
          unifier.unify(
              funcTS(intTS(), blobTS()),
              funcTS(intTS(), blobTS()));
        }

        @Test
        public void unify_equal_func_types_with_res_being_func() throws UnifierExc {
          unifier.unify(
              funcTS(funcTS(intTS())),
              funcTS(funcTS(intTS())));
        }

        @Test
        public void unify_equal_func_types_with_param_being_func() throws UnifierExc {
          unifier.unify(
              funcTS(blobTS(), funcTS(intTS())),
              funcTS(blobTS(), funcTS(intTS())));
        }

        @Test
        public void unify_non_equal_base_types_fails() {
          assertUnifyFails(intTS(), blobTS());
        }

        @Test
        public void unify_non_equal_array_types_fails() {
          assertUnifyFails(arrayTS(intTS()), arrayTS(blobTS()));
        }

        @Test
        public void unify_non_equal_func_types_that_differs_with_res_fails() {
          assertUnifyFails(funcTS(intTS()), funcTS(blobTS()));
        }

        @Test
        public void unify_non_equal_func_types_that_differs_with_param_fails() {
          assertUnifyFails(funcTS(intTS(), blobTS()), funcTS(intTS(), stringTS()));
        }

        @Test
        public void unify_non_equal_func_types_that_differs_with_param_count_fails() {
          assertUnifyFails(funcTS(intTS(), blobTS()), funcTS(intTS()));
        }

        @ParameterizedTest
        @MethodSource("baseTypes")
        public void unify_base_and_array_fails(BaseTS base) {
          assertUnifyFails(base, arrayTS(base));
        }

        @ParameterizedTest
        @MethodSource("baseTypes")
        public void unify_base_and_func_fails(BaseTS base) {
          assertUnifyFails(base, funcTS(base));
          assertUnifyFails(base, funcTS(base, base));
        }

        @Test
        public void unify_array_and_func_fails() {
          assertUnifyFails(arrayTS(intTS()), funcTS(intTS()));
        }

        public static ImmutableList<BaseTS> baseTypes() {
          return TypeFS.baseTs();
        }
      }

      @Nested
      class _with_vars {
        @ParameterizedTest
        @MethodSource("baseTypes")
        public void unify_array_of_a_with_array_of_base(BaseTS base) throws UnifierExc {
          assertUnifyInfers(
              arrayTS(varA()),
              arrayTS(base),
              varA(),
              base);
        }

        @Test
        public void unify_array_of_a_with_array_of_b() throws UnifierExc {
          assertUnifyInfersEquality(
              arrayTS(varA()),
              arrayTS(varB()),
              varA(),
              varB());
        }

        @ParameterizedTest
        @MethodSource("baseTypes")
        public void unify_func_with_res_a_with_func_with_res_base(BaseTS base)
            throws UnifierExc {
          assertUnifyInfers(
              funcTS(varA()),
              funcTS(base),
              varA(),
              base);
        }

        @Test
        public void unify_func_with_res_a_with_func_with_res_b() throws UnifierExc {
          assertUnifyInfersEquality(
              funcTS(varA()),
              funcTS(varB()),
              varA(),
              varB());
        }

        @ParameterizedTest
        @MethodSource("baseTypes")
        public void unify_func_with_param_a_with_func_with_param_base(BaseTS base)
            throws UnifierExc {
          assertUnifyInfers(
              funcTS(intTS(), varA()),
              funcTS(intTS(), base),
              varA(),
              base);
        }

        @Test
        public void unify_func_with_param_a_with_func_with_param_b() throws UnifierExc {
          assertUnifyInfersEquality(
              funcTS(intTS(), varA()),
              funcTS(intTS(), varB()),
              varA(),
              varB());
        }

        public static ImmutableList<BaseTS> baseTypes() {
          return TypeFS.baseTs();
        }
      }
    }
  }

  @Nested
  class _cycles {
    @Test
    public void one_elem_cycle_through_array_elem() {
      assertUnifyFails(varA(), arrayTS(varA()));
    }

    @Test
    public void two_elem_cycle_through_array_elem() throws UnifierExc {
      unifier.unify(varA(), arrayTS(varB()));
      assertUnifyFails(varB(), arrayTS(varA()));
    }

    @Test
    public void one_elem_cycle_through_func_res() {
      assertUnifyFails(varA(), funcTS(varA()));
    }

    @Test
    public void two_elem_cycle_through_func_res() throws UnifierExc {
      unifier.unify(varA(), funcTS(varB()));
      assertUnifyFails(varB(), funcTS(varA()));
    }

    @Test
    public void one_elem_cycle_through_func_param() {
      assertUnifyFails(varA(), funcTS(intTS(), varA()));
    }

    @Test
    public void two_elem_cycle_through_func_param() throws UnifierExc {
      unifier.unify(varA(), funcTS(intTS(), varB()));
      assertUnifyFails(varB(), funcTS(intTS(), varA()));
    }

    @Test
    public void regression_test() throws UnifierExc {
      // Cycle detection algorithm had a bug which is detected by this test.
      unifier.unify(varA(), arrayTS(varB()));
      unifier.unify(varC(), funcTS(varA(), varB()));
    }
  }

  @Nested
  class _complex_cases {
    @Nested
    class _transitive_cases {
      @Test
      public void unify_a_with_b_unified_with_concrete_type() throws UnifierExc {
        unifier.unify(varA(), varB());
        unifier.unify(varB(), intTS());
        assertThat(unifier.resolve(varA()))
            .isEqualTo(intTS());
      }

      @Test
      public void unify_a_with_array_b_unified_with_c() throws UnifierExc {
        unifier.unify(varA(), arrayTS(varB()));
        unifier.unify(arrayTS(varB()), varC());
        assertThat(unifier.resolve(varA()))
            .isEqualTo(unifier.resolve(varC()));
      }

      @Test
      public void unify_array_a_with_b_unified_with_array_c() throws UnifierExc {
        unifier.unify(arrayTS(varA()), varB());
        unifier.unify(varB(), arrayTS(varC()));
        assertThat(unifier.resolve(varA()))
            .isEqualTo(unifier.resolve(varC()));
      }
    }

    @Nested
    class _join_separate_unified_groups {
      @Test
      public void join_array_of_x_with_array_of_y() throws UnifierExc {
        unifier.unify(varA(), arrayTS(varX()));
        unifier.unify(varB(), arrayTS(varY()));
        unifier.unify(varA(), varB());
        assertThat(unifier.resolve(varX()))
            .isEqualTo(unifier.resolve(varY()));
      }

      @Test
      public void join_array_of_x_with_array_of_int() throws UnifierExc {
        unifier.unify(varA(), arrayTS(varX()));
        unifier.unify(varB(), arrayTS(intTS()));
        unifier.unify(varA(), varB());
        assertThat(unifier.resolve(varX()))
            .isEqualTo(intTS());
      }

      @Test
      public void join_array_of_int_with_array_of_blob_fails() throws UnifierExc {
        unifier.unify(varA(), arrayTS(intTS()));
        unifier.unify(varB(), arrayTS(blobTS()));
        assertCall(() -> unifier.unify(varA(), varB()))
            .throwsException(UnifierExc.class);
      }

      @Test
      public void join_func_with_param_x_with_func_with_param_y() throws UnifierExc {
        unifier.unify(varA(), funcTS(stringTS(), varX()));
        unifier.unify(varB(), funcTS(stringTS(), varY()));
        unifier.unify(varA(), varB());
        assertThat(unifier.resolve(varX()))
            .isEqualTo(unifier.resolve(varY()));
      }

      @Test
      public void join_func_with_param_x_with_func_with_param_int() throws UnifierExc {
        unifier.unify(varA(), funcTS(stringTS(), varX()));
        unifier.unify(varB(), funcTS(stringTS(), intTS()));
        unifier.unify(varA(), varB());
        assertThat(unifier.resolve(varX()))
            .isEqualTo(intTS());
      }

      @Test
      public void join_func_with_param_int_with_func_with_param_blob_fails() throws UnifierExc {
        unifier.unify(varA(), funcTS(stringTS(), intTS()));
        unifier.unify(varB(), funcTS(stringTS(), blobTS()));
        assertCall(() -> unifier.unify(varA(), varB()))
            .throwsException(UnifierExc.class);
      }

      @Test
      public void join_func_with_res_x_with_func_with_res_y() throws UnifierExc {
        unifier.unify(varA(), funcTS(varX()));
        unifier.unify(varB(), funcTS(varY()));
        unifier.unify(varA(), varB());
        assertThat(unifier.resolve(varX()))
            .isEqualTo(unifier.resolve(varY()));
      }

      @Test
      public void join_func_with_res_x_with_func_with_res_int() throws UnifierExc {
        unifier.unify(varA(), funcTS(varX()));
        unifier.unify(varB(), funcTS(intTS()));
        unifier.unify(varA(), varB());
        assertThat(unifier.resolve(varX()))
            .isEqualTo(intTS());
      }

      @Test
      public void join_func_with_res_int_with_func_with_res_blob_fails() throws UnifierExc {
        unifier.unify(varA(), funcTS(intTS()));
        unifier.unify(varB(), funcTS(blobTS()));
        assertCall(() -> unifier.unify(varA(), varB()))
            .throwsException(UnifierExc.class);
      }

      @Test
      public void join_func_with_func_with_different_param_count_fails() throws UnifierExc {
        unifier.unify(varA(), funcTS(intTS()));
        unifier.unify(varB(), funcTS(intTS(), intTS()));
        assertCall(() -> unifier.unify(varA(), varB()))
            .throwsException(UnifierExc.class);
      }

      @Test
      public void join_array_with_func_fails() throws UnifierExc {
        unifier.unify(varA(), arrayTS(intTS()));
        unifier.unify(varB(), funcTS(intTS()));
        assertCall(() -> unifier.unify(varA(), varB()))
            .throwsException(UnifierExc.class);
      }
    }
  }

  @Nested
  class _prefixed_vars {
    @Test
    public void non_prefixed_var_has_priority_over_prefixed() throws UnifierExc {
      VarS a = varA().prefixed("p");
      VarS b = varB().prefixed("p");
      VarS x = varX();

      unifier.unify(a, b);
      unifier.unify(b, x);

      assertThat(unifier.resolve(a))
          .isEqualTo(x);
      assertThat(unifier.resolve(b))
          .isEqualTo(x);
      assertThat(unifier.resolve(x))
          .isEqualTo(x);
    }

    @Test
    public void when_no_non_prefixed_var_is_unified_then_prefixed_is_returned()
        throws UnifierExc {
      VarS a = varA().prefixed("p");
      VarS b = varB().prefixed("p");
      assertUnifyInfersEquality(a, b, a, b);
    }
  }

  @Nested
  class _resolve {
    @Test
    public void unknown_var_cannot_be_resolved() {
      assertCall(() -> unifier.resolve(varA()))
          .throwsException(new IllegalStateException("Unknown variable A."));
    }

    @Test
    public void added_var_can_be_resolved() {
      unifier.addVar(varA());
      assertThat(unifier.resolve(varA()))
          .isEqualTo(varA());
    }

    @Test
    public void added_var_can_be_resolved_inside_compound_type() {
      unifier.addVar(varA());
      assertThat(unifier.resolve(arrayTS(varA())))
          .isEqualTo(arrayTS(varA()));
    }

    @Test
    public void func_type() throws UnifierExc {
      unifier.unify(varA(), intTS());
      unifier.unify(varB(), boolTS());
      assertThat(unifier.resolve(funcTS(varA(), varB())))
          .isEqualTo(funcTS(intTS(), boolTS()));
    }

    @Test
    public void array_type() throws UnifierExc {
      unifier.unify(varA(), intTS());
      assertThat(unifier.resolve(arrayTS(varA())))
          .isEqualTo(arrayTS(intTS()));
    }
  }

  private void assertUnifyInfersEquality(TypeS type1, TypeS type2, VarS var1, VarS var2)
      throws UnifierExc {
    assertUnifyInfersEqualityImpl(type1, type2, var1, var2);
    assertUnifyInfersEqualityImpl(type2, type1, var1, var2);
  }

  private void assertUnifyInfersEqualityImpl(TypeS type1, TypeS type2, VarS var1, VarS var2)
      throws UnifierExc {
    Unifier unifier = new Unifier();
    unifier.unify(type1, type2);
    assertThat(unifier.resolve(var1))
        .isEqualTo(unifier.resolve(var2));
  }

  private void assertUnifyInfers(TypeS type1, TypeS type2, VarS var, TypeS expected)
      throws UnifierExc {
    assertUnifyInfersImpl(type1, type2, var, expected);
    assertUnifyInfersImpl(type2, type1, var, expected);
  }

  private static void assertUnifyInfersImpl(TypeS type1, TypeS type2, VarS var, TypeS expected)
      throws UnifierExc {
    Unifier unifier = new Unifier();
    unifier.unify(type1, type2);
    assertThat(unifier.resolve(var))
        .isEqualTo(expected);
  }

  private void assertUnifyFails(TypeS type1, TypeS type2) {
    assertCall(() -> unifier.unify(type1, type2))
        .throwsException(UnifierExc.class);
    assertCall(() -> unifier.unify(type2, type1))
        .throwsException(UnifierExc.class);
  }
}
