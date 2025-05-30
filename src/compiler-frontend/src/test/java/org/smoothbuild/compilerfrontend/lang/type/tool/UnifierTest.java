package org.smoothbuild.compilerfrontend.lang.type.tool;

import static com.google.common.collect.Collections2.permutations;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.compilerfrontend.lang.name.Name.typeName;
import static org.smoothbuild.compilerfrontend.lang.type.STypeVar.flexibleTypeVar;

import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.dagger.FrontendCompilerTestContext;
import org.smoothbuild.compilerfrontend.lang.type.SInterfaceType;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;
import org.smoothbuild.compilerfrontend.lang.type.STypes;

public class UnifierTest extends FrontendCompilerTestContext {
  private final Unifier unifier = new Unifier();

  @Nested
  class _single_unify_call {
    @Nested
    class _temp_vs_temp {
      @Test
      void temp_vs_itself() throws UnifierException {
        var unifier = new Unifier();
        var a = unifier.newFlexibleTypeVar();
        assertUnifyInfers(unifier, a, a, a, a);
      }

      @Test
      void temp_vs_other_temp() throws UnifierException {
        var unifier = new Unifier();
        var a = unifier.newFlexibleTypeVar();
        var b = unifier.newFlexibleTypeVar();
        unifier.add(new Constraint(a, b));
        assertThat(unifier.resolve(a)).isEqualTo(unifier.resolve(b));
      }

      @Test
      void temp_vs_other_temp_vs_yet_another_temp() throws UnifierException {
        var unifier = new Unifier();
        var a = unifier.newFlexibleTypeVar();
        var b = unifier.newFlexibleTypeVar();
        var c = unifier.newFlexibleTypeVar();
        unifier.add(new Constraint(a, b));
        unifier.add(new Constraint(b, c));
        assertThat(unifier.resolve(a)).isEqualTo(unifier.resolve(c));
      }

      @ParameterizedTest
      @MethodSource(
          "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
      public void temp_vs_composed_with_that_temp_fails(Function<SType, SType> composedFactory)
          throws UnifierException {
        var unifier = new Unifier();
        var a = unifier.newFlexibleTypeVar();
        assertUnifyFails(a, composedFactory.apply(a));
      }

      @ParameterizedTest
      @MethodSource(
          "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
      public void temp_vs_composed_with_different_temp_succeeds(
          Function<SType, SType> composedFactory) throws UnifierException {
        var unifier = new Unifier();
        var a = unifier.newFlexibleTypeVar();
        var b = unifier.newFlexibleTypeVar();
        assertUnifyInfers(unifier, a, composedFactory.apply(b), a, composedFactory.apply(b));
      }

      @ParameterizedTest
      @MethodSource(
          "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
      public void composed_with_temp_vs_same_composed_with_other_temp_succeeds(
          Function<SType, SType> composedFactory) throws UnifierException {
        var unifier = new Unifier();
        var a = unifier.newFlexibleTypeVar();
        var b = unifier.newFlexibleTypeVar();
        assertUnifyInfersEquality(
            unifier, composedFactory.apply(a), composedFactory.apply(b), a, b);
      }
    }

    @Nested
    class _temp_vs_non_temp {
      @ParameterizedTest
      @MethodSource("org.smoothbuild.compilerfrontend.testing.TestingSExpression#typesToTest")
      public void temp_vs_concrete_type(SType type) throws UnifierException {
        var unifier = new Unifier();
        var a = unifier.newFlexibleTypeVar();
        assertUnifyInfers(unifier, a, type, a, type);
      }

      @Nested
      class _temp_as_component {
        @ParameterizedTest
        @MethodSource(
            "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
        public void composed_with_temp_vs_same_composed_with_base_type_instead_temp(
            Function<SType, SType> factory) throws UnifierException {
          var unifier = new Unifier();
          var var = unifier.newFlexibleTypeVar();
          var baseType = sIntType();
          assertUnifyInfers(unifier, factory.apply(var), factory.apply(baseType), var, baseType);
        }
      }
    }

    @Nested
    class _non_temp_vs_non_temp_var {
      @ParameterizedTest
      @MethodSource("typesToTest")
      public void base_vs_itself_succeeds(SType type) throws UnifierException {
        unifier.add(new Constraint(type, type));
      }

      @Test
      void base_vs_different_base_fails() throws UnifierException {
        assertUnifyFails(sIntType(), sBlobType());
      }

      @ParameterizedTest
      @MethodSource("typesToTest")
      public void base_vs_var_fails() throws UnifierException {
        assertUnifyFails(varB(), sBlobType());
      }

      @ParameterizedTest
      @MethodSource("typesToTest")
      public void base_vs_array_fails(SType type) throws UnifierException {
        assertUnifyFails(type, sArrayType(type));
      }

      @ParameterizedTest
      @MethodSource("typesToTest")
      public void base_vs_tuple_fails(SType type) throws UnifierException {
        assertUnifyFails(type, sTupleType(type));
      }

      @ParameterizedTest
      @MethodSource("typesToTest")
      public void base_vs_function_fails(SType type) throws UnifierException {
        assertUnifyFails(type, sFuncType(type));
        assertUnifyFails(type, sFuncType(type, type));
      }

      @ParameterizedTest
      @MethodSource("typesToTest")
      public void base_vs_struct_fails(SType type) throws UnifierException {
        assertUnifyFails(type, sStructType("MyFunc"));
        assertUnifyFails(type, sStructType("MyFunc", sIntType()));
      }

      @Test
      void var_vs_itself_succeeds() throws UnifierException {
        unifier.add(new Constraint(varA(), varA()));
      }

      @Test
      void var_vs_array_fails() throws UnifierException {
        assertUnifyFails(varA(), sVarAArrayT());
      }

      @Test
      void var_vs_tuple_fails() throws UnifierException {
        assertUnifyFails(varA(), sTupleType(varA()));
      }

      @Test
      void var_vs_function_fails() throws UnifierException {
        assertUnifyFails(varA(), sVarAFuncType());
      }

      @Test
      void var_vs_struct_fails() throws UnifierException {
        assertUnifyFails(varA(), sStructType("MyStruct", varA()));
      }

      @Test
      void var_vs_interface_fails() throws UnifierException {
        assertUnifyFails(varA(), sInterfaceType(varA()));
      }

      @Test
      void array_vs_itself_succeeds() throws UnifierException {
        unifier.add(new Constraint(sIntArrayT(), sIntArrayT()));
      }

      @Test
      void array_with_interface_vs_array_with_compatible_interface_succeeds()
          throws UnifierException {
        unifier.add(new Constraint(
            sArrayType(sInterfaceType(sSig(sIntType(), "myField"))),
            sArrayType(sInterfaceType(sSig(sStringType(), "otherField")))));
      }

      @Test
      void array2_vs_itself_succeeds() throws UnifierException {
        unifier.add(new Constraint(sArrayType(sIntArrayT()), sArrayType(sIntArrayT())));
      }

      @Test
      void array_vs_array_with_different_element_fails() throws UnifierException {
        assertUnifyFails(sIntArrayT(), sBlobArrayT());
      }

      @Test
      void array_vs_tuple_fails() throws UnifierException {
        assertUnifyFails(sIntArrayT(), sTupleType(sIntType()));
      }

      @Test
      void array_vs_function_fails() throws UnifierException {
        assertUnifyFails(sIntArrayT(), sIntFuncType());
      }

      @Test
      void array_vs_struct_fails() throws UnifierException {
        assertUnifyFails(sIntArrayT(), sStructType("MyStruct", sIntType()));
      }

      @Test
      void array_vs_interface_fails() throws UnifierException {
        assertUnifyFails(sIntArrayT(), sInterfaceType(sIntType()));
      }

      @Test
      void tuple_vs_itself_succeeds() throws UnifierException {
        unifier.add(new Constraint(
            sTupleType(sIntType(), sBlobType()), sTupleType(sIntType(), sBlobType())));
      }

      @Test
      void tuple_with_interface_vs_tuple_with_compatible_interface_succeeds()
          throws UnifierException {
        unifier.add(new Constraint(
            sTupleType(sInterfaceType(sSig(sIntType(), "myField"))),
            sTupleType(sInterfaceType(sSig(sStringType(), "otherField")))));
      }

      @Test
      void tuple2_vs_itself_succeeds() throws UnifierException {
        unifier.add(new Constraint(
            sTupleType(sTupleType(sIntType(), sBlobType())),
            sTupleType(sTupleType(sIntType(), sBlobType()))));
      }

      @Test
      void tuple_vs_tuple_with_different_elements_fails() throws UnifierException {
        assertUnifyFails(
            sTupleType(sIntType(), sBlobType()), sTupleType(sIntType(), sStringType()));
      }

      @Test
      void tuple_vs_tuple_with_one_element_missing_fails() throws UnifierException {
        assertUnifyFails(sTupleType(sIntType(), sBlobType()), sTupleType(sIntType()));
      }

      @Test
      void tuple_vs_function_fails() throws UnifierException {
        assertUnifyFails(sTupleType(sIntType()), sIntFuncType());
      }

      @Test
      void tuple_vs_struct_fails() throws UnifierException {
        assertUnifyFails(sTupleType(sIntType()), sStructType("MyStruct", sIntType()));
      }

      @Test
      void tuple_vs_interface_fails() throws UnifierException {
        assertUnifyFails(sTupleType(sIntType()), sInterfaceType(sIntType()));
      }

      @Test
      void function_vs_itself_succeeds() throws UnifierException {
        unifier.add(
            new Constraint(sFuncType(sBlobType(), sIntType()), sFuncType(sBlobType(), sIntType())));
      }

      @Test
      void function_with_interface_vs_function_with_compatible_interface_succeeds()
          throws UnifierException {
        unifier.add(new Constraint(
            sFuncType(sInterfaceType(sSig(sIntType(), "myField"))),
            sFuncType(sInterfaceType(sSig(sStringType(), "otherField")))));
      }

      @Test
      void function_with_result_being_function_vs_itself_succeeds() throws UnifierException {
        unifier.add(new Constraint(sFuncType(sIntFuncType()), sFuncType(sIntFuncType())));
      }

      @Test
      void function_with_parameter_being_function_vs_itself_succeeds() throws UnifierException {
        unifier.add(new Constraint(
            sFuncType(sIntFuncType(), sBlobType()), sFuncType(sIntFuncType(), sBlobType())));
      }

      @Test
      void function_vs_function_with_different_result_type_fails() throws UnifierException {
        assertUnifyFails(sIntFuncType(), sBlobFuncType());
      }

      @Test
      void function_vs_function_with_different_parameter_fails() throws UnifierException {
        assertUnifyFails(sFuncType(sBlobType(), sIntType()), sFuncType(sStringType(), sIntType()));
      }

      @Test
      void function_vs_function_with_one_parameter_missing_fails() throws UnifierException {
        assertUnifyFails(sFuncType(sBlobType(), sIntType()), sIntFuncType());
      }

      @Test
      void function_vs_struct_fails() throws UnifierException {
        assertUnifyFails(sIntFuncType(), sStructType("MyStruct", sIntType()));
      }

      @Test
      void function_vs_interface_fails() throws UnifierException {
        assertUnifyFails(sIntFuncType(), sInterfaceType(sIntType()));
      }

      @Test
      void struct_vs_itself_succeeds() throws UnifierException {
        unifier.add(new Constraint(
            sStructType("MyStruct", sIntType(), sBlobType()),
            sStructType("MyStruct", sIntType(), sBlobType())));
      }

      @Test
      void struct_with_interface_vs_struct_with_compatible_interface_succeeds()
          throws UnifierException {
        unifier.add(new Constraint(
            sStructType(sInterfaceType(sSig(sIntType(), "myField"))),
            sStructType(sInterfaceType(sSig(sStringType(), "otherField")))));
      }

      @Test
      void struct_vs_itself_with_fields_reordered_fails() throws UnifierException {
        assertUnifyFails(
            sStructType(
                "MyStruct", sSig(sIntType(), "myIntField"), sSig(sBlobType(), "myBlobField")),
            sStructType(
                "MyStruct", sSig(sBlobType(), "myBlobField"), sSig(sIntType(), "myIntField")));
      }

      @Test
      void struct_vs_struct_with_different_name_fails() throws UnifierException {
        assertUnifyFails(
            sStructType("MyStruct", sIntType(), sBlobType()),
            sStructType("MyStruct2", sIntType(), sBlobType()));
      }

      @Test
      void struct_vs_struct_with_different_field_type_fails() throws UnifierException {
        assertUnifyFails(
            sStructType("MyStruct", sIntType(), sBlobType()),
            sStructType("MyStruct", sIntType(), sStringType()));
      }

      @Test
      void struct_vs_struct_with_different_field_name_fails() throws UnifierException {
        assertUnifyFails(
            sStructType("MyStruct", sSig(sIntType(), "myField")),
            sStructType("MyStruct", sSig(sIntType(), "otherField")));
      }

      @Test
      void struct_vs_matching_interface_succeeds() throws UnifierException {
        unifier.add(new Constraint(
            sStructType("MyStruct", sSig(sIntType(), "myField")),
            sInterfaceType(sSig(sIntType(), "myField"))));
      }

      @Test
      void struct_vs_matching_interface_with_changed_field_type_fails() throws UnifierException {
        assertUnifyFails(
            sStructType("MyStruct", sSig(sIntType(), "myField")),
            sInterfaceType(sSig(sBlobType(), "myField")));
      }

      @Test
      void struct_vs_matching_interface_with_changed_field_name_fails() throws UnifierException {
        assertUnifyFails(
            sStructType("MyStruct", sSig(sIntType(), "myField")),
            sInterfaceType(sSig(sIntType(), "otherField")));
      }

      @Test
      void interface_vs_itself_succeeds() throws UnifierException {
        unifier.add(new Constraint(
            sInterfaceType(sIntType(), sBlobType()), sInterfaceType(sIntType(), sBlobType())));
      }

      @Test
      void interface_vs_interface_with_different_field_type_fails() throws UnifierException {
        assertUnifyFails(
            sInterfaceType(sSig(sIntType(), "myField")),
            sInterfaceType(sSig(sBlobType(), "myField")));
      }

      @Test
      void interface_vs_interface_with_different_field_name_succeeds() throws UnifierException {
        SType type1 = sInterfaceType(sSig(sIntType(), "myField"));
        SType type2 = sInterfaceType(sSig(sIntType(), "otherField"));
        assertUnifyThroughTempImpl(
            type1,
            type2,
            sInterfaceType(sSig(sIntType(), "otherField"), sSig(sIntType(), "myField")));
      }

      public static List<SType> typesToTest() {
        return List.<SType>list().addAll(STypes.baseTypes()).add(new STypeVar(typeName("A")));
      }
    }
  }

  @Nested
  class _merging_field_sets {
    @Nested
    class _legal_field_set_merges {
      @ParameterizedTest
      @MethodSource("legalFieldSetMerges")
      public void array_element(SInterfaceType type1, SInterfaceType type2, SType expected)
          throws UnifierException {
        assertUnifyThroughTempImpl(sArrayType(type1), sArrayType(type2), sArrayType(expected));
      }

      @ParameterizedTest
      @MethodSource("legalFieldSetMerges")
      public void tuple_element(SInterfaceType type1, SInterfaceType type2, SType expected)
          throws UnifierException {
        assertUnifyThroughTempImpl(sTupleType(type1), sTupleType(type2), sTupleType(expected));
      }

      @ParameterizedTest
      @MethodSource("legalFieldSetMerges")
      public void func_result(SInterfaceType type1, SInterfaceType type2, SType expected)
          throws UnifierException {
        assertUnifyThroughTempImpl(sFuncType(type1), sFuncType(type2), sFuncType(expected));
      }

      @ParameterizedTest
      @MethodSource("legalFieldSetMerges")
      public void func_param(SInterfaceType type1, SInterfaceType type2, SType expected)
          throws UnifierException {
        assertUnifyThroughTempImpl(
            sFuncType(type1, sIntType()),
            sFuncType(type2, sIntType()),
            sFuncType(expected, sIntType()));
      }

      @ParameterizedTest
      @MethodSource("legalFieldSetMerges")
      public void struct_field(SInterfaceType type1, SInterfaceType type2, SType expected)
          throws UnifierException {
        assertUnifyThroughTempImpl(sStructType(type1), sStructType(type2), sStructType(expected));
      }

      @ParameterizedTest
      @MethodSource("legalFieldSetMerges")
      public void interface_field(SInterfaceType type1, SInterfaceType type2, SType expected)
          throws UnifierException {
        assertUnifyThroughTempImpl(
            sInterfaceType(type1), sInterfaceType(type2), sInterfaceType(expected));
      }

      public static List<Arguments> legalFieldSetMerges() {
        return new UnifierTest().legalFieldSetMerges();
      }
    }

    @Nested
    class _illegal_field_set_merges {
      @ParameterizedTest
      @MethodSource("illegalFieldSetMerges")
      public void array_element(SInterfaceType type1, SInterfaceType type2)
          throws UnifierException {
        assertUnifyFails(sArrayType(type1), sArrayType(type2));
      }

      @ParameterizedTest
      @MethodSource("illegalFieldSetMerges")
      public void tuple_element(SInterfaceType type1, SInterfaceType type2)
          throws UnifierException {
        assertUnifyFails(sTupleType(type1), sTupleType(type2));
      }

      @ParameterizedTest
      @MethodSource("illegalFieldSetMerges")
      public void func_result(SInterfaceType type1, SInterfaceType type2) throws UnifierException {
        assertUnifyFails(sFuncType(type1), sFuncType(type2));
      }

      @ParameterizedTest
      @MethodSource("illegalFieldSetMerges")
      public void func_param(SInterfaceType type1, SInterfaceType type2) throws UnifierException {
        assertUnifyFails(sFuncType(type1, sIntType()), sFuncType(type2, sIntType()));
      }

      @ParameterizedTest
      @MethodSource("illegalFieldSetMerges")
      public void struct_field(SInterfaceType type1, SInterfaceType type2) throws UnifierException {
        assertUnifyFails(sStructType(type1), sStructType(type2));
      }

      @ParameterizedTest
      @MethodSource("illegalFieldSetMerges")
      public void interface_field(SInterfaceType type1, SInterfaceType type2)
          throws UnifierException {
        assertUnifyFails(sInterfaceType(type1), sInterfaceType(type2));
      }

      public static List<Arguments> illegalFieldSetMerges() {
        return new UnifierTest().illegalFieldSetMerges();
      }
    }
  }

  public List<Arguments> legalFieldSetMerges() {
    return list(
        // struct vs struct
        arguments(
            sStructType("MyStruct", sSig(sIntType(), "myField")),
            sStructType("MyStruct", sSig(sIntType(), "myField")),
            sStructType("MyStruct", sSig(sIntType(), "myField"))),

        // interface vs interface
        arguments(
            sInterfaceType(sSig(sIntType(), "myField")),
            sInterfaceType(sSig(sIntType(), "myField")),
            sInterfaceType(sSig(sIntType(), "myField"))),
        arguments(
            sInterfaceType(sSig(sIntType(), "myField")),
            sInterfaceType(),
            sInterfaceType(sSig(sIntType(), "myField"))),
        arguments(
            sInterfaceType(sSig(sIntType(), "myField")),
            sInterfaceType(sSig(sIntType(), "otherField")),
            sInterfaceType(sSig(sIntType(), "otherField"), sSig(sIntType(), "myField"))),

        // struct vs interface
        arguments(
            sStructType("MyStruct", sSig(sIntType(), "myField")),
            sInterfaceType(sSig(sIntType(), "myField")),
            sStructType("MyStruct", sSig(sIntType(), "myField"))),
        arguments(
            sStructType("MyStruct", sSig(sIntType(), "myField")),
            sInterfaceType(),
            sStructType("MyStruct", sSig(sIntType(), "myField"))));
  }

  public List<Arguments> illegalFieldSetMerges() {
    return list(
        // struct vs struct
        arguments(
            sStructType("MyStruct", sSig(sIntType(), "myField")),
            sStructType("OtherStruct", sSig(sIntType(), "myField"))),
        arguments(
            sStructType("MyStruct", sSig(sIntType(), "myField")),
            sStructType("MyStruct", sSig(sBlobType(), "myField"))),
        arguments(
            sStructType("MyStruct", sSig(sIntType(), "myField")),
            sStructType("MyStruct", sSig(sIntType(), "otherField"))),
        arguments(sStructType("MyStruct", sSig(sIntType(), "myField")), sStructType("MyStruct")),

        // interface vs interface
        arguments(
            sInterfaceType(sSig(sIntType(), "myField")),
            sInterfaceType(sSig(sBlobType(), "myField"))),

        // struct vs interface
        arguments(
            sStructType("MyStruct", sSig(sIntType(), "myField")),
            sInterfaceType(sSig(sBlobType(), "myField"))),
        arguments(
            sStructType("MyStruct", sSig(sIntType(), "myField")),
            sInterfaceType(sSig(sIntType(), "otherField"))),
        arguments(
            sStructType("MyStruct", sSig(sIntType(), "myField")),
            sInterfaceType(sSig(sIntType(), "myField"), sSig(sIntType(), "otherField"))));
  }

  @Nested
  class _cycles {
    @ParameterizedTest
    @MethodSource(
        "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
    public void one_element_cycle_through_composed(Function<SType, SType> composedFactory)
        throws UnifierException {
      var a = unifier.newFlexibleTypeVar();
      var constraints = list(new Constraint(a, composedFactory.apply(a)));
      assertExceptionThrownForLastConstraintForEachPermutation(constraints);
    }

    @ParameterizedTest
    @MethodSource(
        "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
    public void two_elements_cycle_through_composed(Function<SType, SType> composedFactory)
        throws UnifierException {
      var a = unifier.newFlexibleTypeVar();
      var b = unifier.newFlexibleTypeVar();
      var constraints = list(
          new Constraint(a, composedFactory.apply(b)), new Constraint(b, composedFactory.apply(a)));
      assertExceptionThrownForLastConstraintForEachPermutation(constraints);
    }

    @ParameterizedTest
    @MethodSource(
        "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
    public void three_elements_cycle_through_composed(Function<SType, SType> composedFactory)
        throws UnifierException {
      var a = unifier.newFlexibleTypeVar();
      var b = unifier.newFlexibleTypeVar();
      var c = unifier.newFlexibleTypeVar();
      var constraints = list(
          new Constraint(a, composedFactory.apply(b)),
          new Constraint(b, composedFactory.apply(c)),
          new Constraint(c, composedFactory.apply(a)));
      assertExceptionThrownForLastConstraintForEachPermutation(constraints);
    }

    @Test
    void regression_test() throws UnifierException {
      // Cycle detection algorithm had a bug which is detected by this test.
      var a = unifier.newFlexibleTypeVar();
      var b = unifier.newFlexibleTypeVar();
      var c = unifier.newFlexibleTypeVar();
      var constraints = list(new Constraint(a, sArrayType(b)), new Constraint(c, sFuncType(b, a)));
      assertConstraintsAreSolvableForEachPermutation(constraints);
    }
  }

  @Nested
  class _complex_cases {
    @Nested
    class _transitive_cases {
      @Test
      void unify_a_vs_b_vs_concrete_type() throws UnifierException {
        var a = unifier.newFlexibleTypeVar();
        var b = unifier.newFlexibleTypeVar();
        var constraints = list(new Constraint(a, b), new Constraint(b, sIntType()));
        assertResolvedAreEqualForEachPermutation(constraints, a, sIntType());
      }

      @Test
      void unify_a_vs_array_b_vs_c() throws UnifierException {
        var a = unifier.newFlexibleTypeVar();
        var b = unifier.newFlexibleTypeVar();
        var c = unifier.newFlexibleTypeVar();
        var constraints = list(new Constraint(a, sArrayType(b)), new Constraint(sArrayType(b), c));
        assertResolvedAreEqualForEachPermutation(constraints, a, c);
      }

      @Test
      void unify_array_a_vs_b_vs_array_c() throws UnifierException {
        var a = unifier.newFlexibleTypeVar();
        var b = unifier.newFlexibleTypeVar();
        var c = unifier.newFlexibleTypeVar();
        var constraints = list(new Constraint(sArrayType(a), b), new Constraint(b, sArrayType(c)));
        assertResolvedAreEqualForEachPermutation(constraints, a, c);
      }
    }

    @Nested
    class _join_separate_unified_groups {
      @ParameterizedTest
      @MethodSource(
          "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
      public void join_composed_of_temp_vs_composed_of_different_temp(
          Function<SType, SType> composedFactory) throws UnifierException {
        var a = unifier.newFlexibleTypeVar();
        var b = unifier.newFlexibleTypeVar();
        var x = unifier.newFlexibleTypeVar();
        var y = unifier.newFlexibleTypeVar();
        var constraints = list(
            new Constraint(a, composedFactory.apply(x)),
            new Constraint(b, composedFactory.apply(y)),
            new Constraint(a, b));
        assertResolvedAreEqualForEachPermutation(constraints, x, y);
      }

      @ParameterizedTest
      @MethodSource(
          "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
      public void join_composed_of_temp_vs_composed_of_int(Function<SType, SType> composedFactory)
          throws UnifierException {
        var a = unifier.newFlexibleTypeVar();
        var b = unifier.newFlexibleTypeVar();
        var x = unifier.newFlexibleTypeVar();
        var constraints = list(
            new Constraint(a, composedFactory.apply(x)),
            new Constraint(b, composedFactory.apply(sIntType())),
            new Constraint(a, b));
        assertResolvedAreEqualForEachPermutation(constraints, x, sIntType());
      }

      @ParameterizedTest
      @MethodSource(
          "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
      public void join_composed_of_int_vs_composed_of_blob_fails(
          Function<SType, SType> composedFactory) throws UnifierException {
        var a = unifier.newFlexibleTypeVar();
        var b = unifier.newFlexibleTypeVar();
        var constraints = list(
            new Constraint(a, composedFactory.apply(sIntType())),
            new Constraint(b, composedFactory.apply(sBlobType())),
            new Constraint(a, b));
        assertExceptionThrownForLastConstraintForEachPermutation(constraints);
      }

      @Test
      void join_func_vs_func_with_different_param_count_fails() throws UnifierException {
        var a = unifier.newFlexibleTypeVar();
        var b = unifier.newFlexibleTypeVar();
        var constraints = list(
            new Constraint(a, sFuncType(sIntType(), sIntType())),
            new Constraint(b, sIntFuncType()),
            new Constraint(a, b));
        assertExceptionThrownForLastConstraintForEachPermutation(constraints);
      }

      @Test
      void join_array_vs_tuple_fails() throws UnifierException {
        var a = unifier.newFlexibleTypeVar();
        var b = unifier.newFlexibleTypeVar();
        unifier.add(new Constraint(a, sIntArrayT()));
        unifier.add(new Constraint(b, sTupleType(sIntType())));
        assertCall(() -> unifier.add(new Constraint(a, b))).throwsException(UnifierException.class);
      }

      @Test
      void join_array_vs_func_fails() throws UnifierException {
        var a = unifier.newFlexibleTypeVar();
        var b = unifier.newFlexibleTypeVar();
        unifier.add(new Constraint(a, sIntArrayT()));
        unifier.add(new Constraint(b, sIntFuncType()));
        assertCall(() -> unifier.add(new Constraint(a, b))).throwsException(UnifierException.class);
      }

      @Test
      void join_tuple_vs_func_fails() throws UnifierException {
        var a = unifier.newFlexibleTypeVar();
        var b = unifier.newFlexibleTypeVar();
        unifier.add(new Constraint(a, sTupleType(sIntType())));
        unifier.add(new Constraint(b, sIntFuncType()));
        assertCall(() -> unifier.add(new Constraint(a, b))).throwsException(UnifierException.class);
      }
    }
  }

  @Nested
  class _temporary_vars {
    @Test
    void non_temporary_var_has_priority_over_temporary() throws UnifierException {
      STypeVar a = unifier.newFlexibleTypeVar();
      STypeVar b = unifier.newFlexibleTypeVar();
      STypeVar x = varX();
      var constraints = list(new Constraint(a, b), new Constraint(b, x));
      assertResolvedAreEqualForEachPermutation(constraints, a, x);
      assertResolvedAreEqualForEachPermutation(constraints, b, x);
      assertResolvedAreEqualForEachPermutation(constraints, x, x);
    }

    @Test
    void resolve_returns_temporary_var_when_no_normal_var_is_unified() {
      var a = unifier.newFlexibleTypeVar();
      assertThat(unifier.resolve(a)).isEqualTo(a);
    }
  }

  @Nested
  class _resolve {
    @Test
    void unknown_flexible_var_cannot_be_resolved() {
      assertCall(() -> unifier.resolve(flexibleTypeVar(1)))
          .throwsException(new IllegalStateException("Unknown flexible type var `T~1`."));
    }

    @ParameterizedTest
    @MethodSource(
        "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
    public void composed_type(Function<SType, SType> composedFactory) throws UnifierException {
      var a = unifier.newFlexibleTypeVar();
      var b = unifier.newFlexibleTypeVar();
      var constraints =
          list(new Constraint(a, sIntType()), new Constraint(b, composedFactory.apply(a)));
      assertResolvedAreEqualForEachPermutation(constraints, b, composedFactory.apply(sIntType()));
    }
  }

  private void assertUnifyInfers(
      Unifier unifier, SType type1, SType type2, SType unresolved, SType expected)
      throws UnifierException {
    unifier.add(new Constraint(type1, type2));
    assertThat(unifier.resolve(unresolved)).isEqualTo(expected);
  }

  private void assertUnifyInfersEquality(
      Unifier unifier, SType type1, SType type2, SType unresolved1, SType unresolved2)
      throws UnifierException {
    unifier.add(new Constraint(type1, type2));
    assertThat(unifier.resolve(unresolved1)).isEqualTo(unifier.resolve(unresolved2));
  }

  private void assertUnifyThroughTempImpl(SType type1, SType type2, SType expected)
      throws UnifierException {
    var a = unifier.newFlexibleTypeVar();
    var constraints = list(new Constraint(a, type1), new Constraint(a, type2));
    assertResolvedAreEqualForEachPermutation(constraints, a, expected);
  }

  private void assertUnifyFails(SType type1, SType type2) throws UnifierException {
    assertExceptionThrownForLastConstraintForEachPermutation(list(new Constraint(type1, type2)));
  }

  private static void assertResolvedAreEqualForEachPermutation(
      List<Constraint> constraints, SType type, SType expected) throws UnifierException {
    assertForEachPermutation(
        constraints, unifier -> assertResolvedAreEquals(unifier, type, expected));
  }

  private static void assertResolvedAreEquals(Unifier unifier, SType type, SType expected) {
    assertThat(unifier.resolve(type)).isEqualTo(unifier.resolve(expected));
  }

  private static void assertConstraintsAreSolvableForEachPermutation(List<Constraint> constraints)
      throws UnifierException {
    assertForEachPermutation(constraints, (unifier) -> {});
  }

  private static void assertForEachPermutation(
      List<Constraint> constraints, Consumer<Unifier> assertion) throws UnifierException {
    var permutations = permutations(constraints.asJdkList());
    for (java.util.List<Constraint> permutation : permutations) {
      assertPermutation(listOfAll(permutation), assertion);
    }
  }

  private static void assertPermutation(List<Constraint> permutation, Consumer<Unifier> assertion)
      throws UnifierException {
    var unifier = new Unifier();
    for (int i = 0; i < 4; i++) {
      unifier.newFlexibleTypeVar();
    }
    for (var constraint : permutation) {
      unifier.add(constraint);
    }
    assertion.accept(unifier);
  }

  private static void assertExceptionThrownForLastConstraintForEachPermutation(
      List<Constraint> constraints) throws UnifierException {
    var permutations = permutations(constraints.asJdkList());
    for (java.util.List<Constraint> permutation : permutations) {
      assertExceptionThrownForPermutation(listOfAll(permutation));
    }
  }

  private static void assertExceptionThrownForPermutation(List<Constraint> permutation)
      throws UnifierException {
    var unifier = new Unifier();
    for (int i = 0; i < 4; i++) {
      unifier.newFlexibleTypeVar();
    }
    for (int i = 0; i < permutation.size() - 1; i++) {
      unifier.add(permutation.get(i));
    }
    assertCall(() -> unifier.add(permutation.get(permutation.size() - 1)))
        .throwsException(UnifierException.class);
  }
}
