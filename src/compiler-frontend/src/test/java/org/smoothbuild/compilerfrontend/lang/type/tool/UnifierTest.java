package org.smoothbuild.compilerfrontend.lang.type.tool;

import static com.google.common.collect.Collections2.permutations;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.type.SFieldSetType;
import org.smoothbuild.compilerfrontend.lang.type.STempVar;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypes;
import org.smoothbuild.compilerfrontend.lang.type.SVar;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class UnifierTest extends FrontendCompilerTestContext {
  private final Unifier unifier = new Unifier();

  @Nested
  class _structure_of {
    @Test
    void array() {
      var a = unifier.newTempVar();
      var arrayTS = sArrayType(a);
      var a2 = new STempVar("1");
      assertThat(unifier.structureOf(arrayTS)).isEqualTo(sArrayType(a2));
    }

    @Test
    void function() {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      var funcTS = sFuncType(list(a, b), a);
      var a2 = new STempVar("2");
      var b2 = new STempVar("3");
      assertThat(unifier.structureOf(funcTS)).isEqualTo(sFuncType(list(a2, b2), a2));
    }

    @Test
    void tuple() {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      var tupleTS = sTupleType(a, b, a);
      var a2 = new STempVar("2");
      var b2 = new STempVar("3");
      assertThat(unifier.structureOf(tupleTS)).isEqualTo(sTupleType(a2, b2, a2));
    }

    @Test
    void interface_() {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      var interfaceTS = sInterfaceType(a, b, a);
      var a2 = new STempVar("2");
      var b2 = new STempVar("3");
      assertThat(unifier.structureOf(interfaceTS)).isEqualTo(sInterfaceType(a2, b2, a2));
    }

    @Test
    void struct() {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      var structTS = sStructType(a, b, a);
      var a2 = new STempVar("2");
      var b2 = new STempVar("3");
      assertThat(unifier.structureOf(structTS)).isEqualTo(sStructType(a2, b2, a2));
    }
  }

  @Nested
  class _single_unify_call {
    @Nested
    class _temp_vs_temp {
      @Test
      void temp_vs_itself() throws UnifierException {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        assertUnifyInfers(unifier, a, a, a, a);
      }

      @Test
      void temp_vs_other_temp() throws UnifierException {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        unifier.add(new EqualityConstraint(a, b));
        assertThat(unifier.resolve(a)).isEqualTo(unifier.resolve(b));
      }

      @Test
      void temp_vs_other_temp_vs_yet_another_temp() throws UnifierException {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var c = unifier.newTempVar();
        unifier.add(new EqualityConstraint(a, b));
        unifier.add(new EqualityConstraint(b, c));
        assertThat(unifier.resolve(a)).isEqualTo(unifier.resolve(c));
      }

      @ParameterizedTest
      @MethodSource(
          "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
      public void temp_vs_composed_with_that_temp_fails(Function<SType, SType> composedFactory)
          throws UnifierException {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        assertUnifyFails(a, composedFactory.apply(a));
      }

      @ParameterizedTest
      @MethodSource(
          "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
      public void temp_vs_composed_with_different_temp_succeeds(
          Function<SType, SType> composedFactory) throws UnifierException {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        assertUnifyInfers(unifier, a, composedFactory.apply(b), a, composedFactory.apply(b));
      }

      @ParameterizedTest
      @MethodSource(
          "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
      public void composed_with_temp_vs_same_composed_with_other_temp_succeeds(
          Function<SType, SType> composedFactory) throws UnifierException {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
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
        var a = unifier.newTempVar();
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
          var var = unifier.newTempVar();
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
        unifier.add(new EqualityConstraint(type, type));
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
        unifier.add(new EqualityConstraint(varA(), varA()));
      }

      @Test
      void var_vs_array_fails() throws UnifierException {
        assertUnifyFails(varA(), sArrayType(varA()));
      }

      @Test
      void var_vs_tuple_fails() throws UnifierException {
        assertUnifyFails(varA(), sTupleType(varA()));
      }

      @Test
      void var_vs_function_fails() throws UnifierException {
        assertUnifyFails(varA(), sFuncType(varA()));
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
        unifier.add(new EqualityConstraint(sArrayType(sIntType()), sArrayType(sIntType())));
      }

      @Test
      void array_with_interface_vs_array_with_compatible_interface_succeeds()
          throws UnifierException {
        unifier.add(new EqualityConstraint(
            sArrayType(sInterfaceType(sSig(sIntType(), "myField"))),
            sArrayType(sInterfaceType(sSig(sStringType(), "otherField")))));
      }

      @Test
      void array2_vs_itself_succeeds() throws UnifierException {
        unifier.add(new EqualityConstraint(
            sArrayType(sArrayType(sIntType())), sArrayType(sArrayType(sIntType()))));
      }

      @Test
      void array_vs_array_with_different_element_fails() throws UnifierException {
        assertUnifyFails(sArrayType(sIntType()), sArrayType(sBlobType()));
      }

      @Test
      void array_vs_tuple_fails() throws UnifierException {
        assertUnifyFails(sArrayType(sIntType()), sTupleType(sIntType()));
      }

      @Test
      void array_vs_function_fails() throws UnifierException {
        assertUnifyFails(sArrayType(sIntType()), sFuncType(sIntType()));
      }

      @Test
      void array_vs_struct_fails() throws UnifierException {
        assertUnifyFails(sArrayType(sIntType()), sStructType("MyStruct", sIntType()));
      }

      @Test
      void array_vs_interface_fails() throws UnifierException {
        assertUnifyFails(sArrayType(sIntType()), sInterfaceType(sIntType()));
      }

      @Test
      void tuple_vs_itself_succeeds() throws UnifierException {
        unifier.add(new EqualityConstraint(
            sTupleType(sIntType(), sBlobType()), sTupleType(sIntType(), sBlobType())));
      }

      @Test
      void tuple_with_interface_vs_tuple_with_compatible_interface_succeeds()
          throws UnifierException {
        unifier.add(new EqualityConstraint(
            sTupleType(sInterfaceType(sSig(sIntType(), "myField"))),
            sTupleType(sInterfaceType(sSig(sStringType(), "otherField")))));
      }

      @Test
      void tuple2_vs_itself_succeeds() throws UnifierException {
        unifier.add(new EqualityConstraint(
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
        assertUnifyFails(sTupleType(sIntType()), sFuncType(sIntType()));
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
        unifier.add(new EqualityConstraint(
            sFuncType(sBlobType(), sIntType()), sFuncType(sBlobType(), sIntType())));
      }

      @Test
      void function_with_interface_vs_function_with_compatible_interface_succeeds()
          throws UnifierException {
        unifier.add(new EqualityConstraint(
            sFuncType(sInterfaceType(sSig(sIntType(), "myField"))),
            sFuncType(sInterfaceType(sSig(sStringType(), "otherField")))));
      }

      @Test
      void function_with_result_being_function_vs_itself_succeeds() throws UnifierException {
        unifier.add(new EqualityConstraint(
            sFuncType(sFuncType(sIntType())), sFuncType(sFuncType(sIntType()))));
      }

      @Test
      void function_with_parameter_being_function_vs_itself_succeeds() throws UnifierException {
        unifier.add(new EqualityConstraint(
            sFuncType(sFuncType(sIntType()), sBlobType()),
            sFuncType(sFuncType(sIntType()), sBlobType())));
      }

      @Test
      void function_vs_function_with_different_result_type_fails() throws UnifierException {
        assertUnifyFails(sFuncType(sIntType()), sFuncType(sBlobType()));
      }

      @Test
      void function_vs_function_with_different_parameter_fails() throws UnifierException {
        assertUnifyFails(sFuncType(sBlobType(), sIntType()), sFuncType(sStringType(), sIntType()));
      }

      @Test
      void function_vs_function_with_one_parameter_missing_fails() throws UnifierException {
        assertUnifyFails(sFuncType(sBlobType(), sIntType()), sFuncType(sIntType()));
      }

      @Test
      void function_vs_struct_fails() throws UnifierException {
        assertUnifyFails(sFuncType(sIntType()), sStructType("MyStruct", sIntType()));
      }

      @Test
      void function_vs_interface_fails() throws UnifierException {
        assertUnifyFails(sFuncType(sIntType()), sInterfaceType(sIntType()));
      }

      @Test
      void struct_vs_itself_succeeds() throws UnifierException {
        unifier.add(new EqualityConstraint(
            sStructType("MyStruct", sIntType(), sBlobType()),
            sStructType("MyStruct", sIntType(), sBlobType())));
      }

      @Test
      void struct_with_interface_vs_struct_with_compatible_interface_succeeds()
          throws UnifierException {
        unifier.add(new EqualityConstraint(
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
        unifier.add(new EqualityConstraint(
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
        unifier.add(new EqualityConstraint(
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
        return STypes.baseTypes().append(new SVar("A"));
      }
    }
  }

  @Nested
  class _merging_field_sets {
    @Nested
    class _legal_field_set_merges {
      @ParameterizedTest
      @MethodSource("legalFieldSetMerges")
      public void array_element(SFieldSetType type1, SFieldSetType type2, SType expected)
          throws UnifierException {
        assertUnifyThroughTempImpl(sArrayType(type1), sArrayType(type2), sArrayType(expected));
      }

      @ParameterizedTest
      @MethodSource("legalFieldSetMerges")
      public void tuple_element(SFieldSetType type1, SFieldSetType type2, SType expected)
          throws UnifierException {
        assertUnifyThroughTempImpl(sTupleType(type1), sTupleType(type2), sTupleType(expected));
      }

      @ParameterizedTest
      @MethodSource("legalFieldSetMerges")
      public void func_result(SFieldSetType type1, SFieldSetType type2, SType expected)
          throws UnifierException {
        assertUnifyThroughTempImpl(sFuncType(type1), sFuncType(type2), sFuncType(expected));
      }

      @ParameterizedTest
      @MethodSource("legalFieldSetMerges")
      public void func_param(SFieldSetType type1, SFieldSetType type2, SType expected)
          throws UnifierException {
        assertUnifyThroughTempImpl(
            sFuncType(type1, sIntType()),
            sFuncType(type2, sIntType()),
            sFuncType(expected, sIntType()));
      }

      @ParameterizedTest
      @MethodSource("legalFieldSetMerges")
      public void struct_field(SFieldSetType type1, SFieldSetType type2, SType expected)
          throws UnifierException {
        assertUnifyThroughTempImpl(sStructType(type1), sStructType(type2), sStructType(expected));
      }

      @ParameterizedTest
      @MethodSource("legalFieldSetMerges")
      public void interface_field(SFieldSetType type1, SFieldSetType type2, SType expected)
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
      public void array_element(SFieldSetType type1, SFieldSetType type2) throws UnifierException {
        assertUnifyFails(sArrayType(type1), sArrayType(type2));
      }

      @ParameterizedTest
      @MethodSource("illegalFieldSetMerges")
      public void tuple_element(SFieldSetType type1, SFieldSetType type2) throws UnifierException {
        assertUnifyFails(sTupleType(type1), sTupleType(type2));
      }

      @ParameterizedTest
      @MethodSource("illegalFieldSetMerges")
      public void func_result(SFieldSetType type1, SFieldSetType type2) throws UnifierException {
        assertUnifyFails(sFuncType(type1), sFuncType(type2));
      }

      @ParameterizedTest
      @MethodSource("illegalFieldSetMerges")
      public void func_param(SFieldSetType type1, SFieldSetType type2) throws UnifierException {
        assertUnifyFails(sFuncType(type1, sIntType()), sFuncType(type2, sIntType()));
      }

      @ParameterizedTest
      @MethodSource("illegalFieldSetMerges")
      public void struct_field(SFieldSetType type1, SFieldSetType type2) throws UnifierException {
        assertUnifyFails(sStructType(type1), sStructType(type2));
      }

      @ParameterizedTest
      @MethodSource("illegalFieldSetMerges")
      public void interface_field(SFieldSetType type1, SFieldSetType type2)
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
      var a = unifier.newTempVar();
      var constraints = list(new EqualityConstraint(a, composedFactory.apply(a)));
      assertExceptionThrownForLastConstraintForEachPermutation(constraints);
    }

    @ParameterizedTest
    @MethodSource(
        "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
    public void two_elements_cycle_through_composed(Function<SType, SType> composedFactory)
        throws UnifierException {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      var constraints = list(
          new EqualityConstraint(a, composedFactory.apply(b)),
          new EqualityConstraint(b, composedFactory.apply(a)));
      assertExceptionThrownForLastConstraintForEachPermutation(constraints);
    }

    @ParameterizedTest
    @MethodSource(
        "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
    public void three_elements_cycle_through_composed(Function<SType, SType> composedFactory)
        throws UnifierException {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      var c = unifier.newTempVar();
      var constraints = list(
          new EqualityConstraint(a, composedFactory.apply(b)),
          new EqualityConstraint(b, composedFactory.apply(c)),
          new EqualityConstraint(c, composedFactory.apply(a)));
      assertExceptionThrownForLastConstraintForEachPermutation(constraints);
    }

    @Test
    void regression_test() throws UnifierException {
      // Cycle detection algorithm had a bug which is detected by this test.
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      var c = unifier.newTempVar();
      var constraints = list(
          new EqualityConstraint(a, sArrayType(b)), new EqualityConstraint(c, sFuncType(b, a)));
      assertConstraintsAreSolvableForEachPermutation(constraints);
    }
  }

  @Nested
  class _complex_cases {
    @Nested
    class _transitive_cases {
      @Test
      void unify_a_vs_b_vs_concrete_type() throws UnifierException {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var constraints = list(new EqualityConstraint(a, b), new EqualityConstraint(b, sIntType()));
        assertResolvedAreEqualForEachPermutation(constraints, a, sIntType());
      }

      @Test
      void unify_a_vs_array_b_vs_c() throws UnifierException {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var c = unifier.newTempVar();
        var constraints = list(
            new EqualityConstraint(a, sArrayType(b)), new EqualityConstraint(sArrayType(b), c));
        assertResolvedAreEqualForEachPermutation(constraints, a, c);
      }

      @Test
      void unify_array_a_vs_b_vs_array_c() throws UnifierException {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var c = unifier.newTempVar();
        var constraints = list(
            new EqualityConstraint(sArrayType(a), b), new EqualityConstraint(b, sArrayType(c)));
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
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var x = unifier.newTempVar();
        var y = unifier.newTempVar();
        var constraints = list(
            new EqualityConstraint(a, composedFactory.apply(x)),
            new EqualityConstraint(b, composedFactory.apply(y)),
            new EqualityConstraint(a, b));
        assertResolvedAreEqualForEachPermutation(constraints, x, y);
      }

      @ParameterizedTest
      @MethodSource(
          "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
      public void join_composed_of_temp_vs_composed_of_int(Function<SType, SType> composedFactory)
          throws UnifierException {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var x = unifier.newTempVar();
        var constraints = list(
            new EqualityConstraint(a, composedFactory.apply(x)),
            new EqualityConstraint(b, composedFactory.apply(sIntType())),
            new EqualityConstraint(a, b));
        assertResolvedAreEqualForEachPermutation(constraints, x, sIntType());
      }

      @ParameterizedTest
      @MethodSource(
          "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
      public void join_composed_of_int_vs_composed_of_blob_fails(
          Function<SType, SType> composedFactory) throws UnifierException {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var constraints = list(
            new EqualityConstraint(a, composedFactory.apply(sIntType())),
            new EqualityConstraint(b, composedFactory.apply(sBlobType())),
            new EqualityConstraint(a, b));
        assertExceptionThrownForLastConstraintForEachPermutation(constraints);
      }

      @Test
      void join_func_vs_func_with_different_param_count_fails() throws UnifierException {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var constraints = list(
            new EqualityConstraint(a, sFuncType(sIntType(), sIntType())),
            new EqualityConstraint(b, sFuncType(sIntType())),
            new EqualityConstraint(a, b));
        assertExceptionThrownForLastConstraintForEachPermutation(constraints);
      }

      @Test
      void join_array_vs_tuple_fails() throws UnifierException {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        unifier.add(new EqualityConstraint(a, sArrayType(sIntType())));
        unifier.add(new EqualityConstraint(b, sTupleType(sIntType())));
        assertCall(() -> unifier.add(new EqualityConstraint(a, b)))
            .throwsException(UnifierException.class);
      }

      @Test
      void join_array_vs_func_fails() throws UnifierException {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        unifier.add(new EqualityConstraint(a, sArrayType(sIntType())));
        unifier.add(new EqualityConstraint(b, sFuncType(sIntType())));
        assertCall(() -> unifier.add(new EqualityConstraint(a, b)))
            .throwsException(UnifierException.class);
      }

      @Test
      void join_tuple_vs_func_fails() throws UnifierException {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        unifier.add(new EqualityConstraint(a, sTupleType(sIntType())));
        unifier.add(new EqualityConstraint(b, sFuncType(sIntType())));
        assertCall(() -> unifier.add(new EqualityConstraint(a, b)))
            .throwsException(UnifierException.class);
      }
    }
  }

  @Nested
  class _temporary_vars {
    @Test
    void resolve_unknown_temp_var_causes_exception() {
      assertCall(() -> unifier.resolve(sTempVarA()))
          .throwsException(new IllegalStateException("Unknown temp var `1`."));
    }

    @Test
    void non_temporary_var_has_priority_over_temporary() throws UnifierException {
      SVar a = unifier.newTempVar();
      SVar b = unifier.newTempVar();
      SVar x = varX();
      var constraints = list(new EqualityConstraint(a, b), new EqualityConstraint(b, x));
      assertResolvedAreEqualForEachPermutation(constraints, a, x);
      assertResolvedAreEqualForEachPermutation(constraints, b, x);
      assertResolvedAreEqualForEachPermutation(constraints, x, x);
    }

    @Test
    void resolve_returns_temporary_var_when_no_normal_var_is_unified() {
      var a = unifier.newTempVar();
      assertThat(unifier.resolve(a)).isEqualTo(a);
    }
  }

  @Nested
  class _resolve {
    @Test
    void unknown_temp_var_cannot_be_resolved() {
      assertCall(() -> unifier.resolve(sTempVarA()))
          .throwsException(new IllegalStateException("Unknown temp var `1`."));
    }

    @ParameterizedTest
    @MethodSource(
        "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
    public void composed_type(Function<SType, SType> composedFactory) throws UnifierException {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      var constraints = list(
          new EqualityConstraint(a, sIntType()),
          new EqualityConstraint(b, composedFactory.apply(a)));
      assertResolvedAreEqualForEachPermutation(constraints, b, composedFactory.apply(sIntType()));
    }
  }

  @Nested
  class _schema_instantiation {
    @ParameterizedTest
    @MethodSource("org.smoothbuild.compilerfrontend.testing.TestingSExpression#typesToTest")
    public void concrete_type_instantiated_to_itself_succeeds(SType type) throws UnifierException {
      var schema = unifier.newTempVar();
      var instantiation = unifier.newTempVar();
      var constraints = list(
          new EqualityConstraint(schema, type),
          new EqualityConstraint(instantiation, type),
          new InstantiationConstraint(instantiation, schema));
      assertConstraintsAreSolvableForEachPermutation(constraints);
    }

    @ParameterizedTest
    @MethodSource("org.smoothbuild.compilerfrontend.testing.TestingSExpression#typesToTest")
    public void concrete_type_instantiated_to_different_type_fails(SType type)
        throws UnifierException {
      var differentType = type.equals(sIntType()) ? sBlobType() : sIntType();
      var schema = unifier.newTempVar();
      var instantiation = unifier.newTempVar();
      var constraints = list(
          new EqualityConstraint(schema, type),
          new EqualityConstraint(instantiation, differentType),
          new InstantiationConstraint(instantiation, schema));
      assertExceptionThrownForLastConstraintForEachPermutation(constraints);
    }

    @ParameterizedTest
    @MethodSource("org.smoothbuild.compilerfrontend.testing.TestingSExpression#typesToTest")
    public void concrete_type_instantiated_to_x_infers_x(SType type) throws UnifierException {
      var x = unifier.newTempVar();
      var schema = unifier.newTempVar();
      var instantiation = unifier.newTempVar();
      var constraints = list(
          new EqualityConstraint(schema, type),
          new EqualityConstraint(instantiation, x),
          new InstantiationConstraint(instantiation, schema));
      assertResolvedAreEqualForEachPermutation(constraints, x, type);
    }

    @ParameterizedTest
    @MethodSource(
        "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
    public void concrete_composed_type_instantiated_to_composed_with_x_infers_x(
        Function<SType, SType> factory) throws UnifierException {
      var x = unifier.newTempVar();
      var schema = unifier.newTempVar();
      var instantiation = unifier.newTempVar();
      var constrainst = list(
          new EqualityConstraint(schema, factory.apply(sIntType())),
          new EqualityConstraint(instantiation, factory.apply(x)),
          new InstantiationConstraint(instantiation, schema));
      assertResolvedAreEqualForEachPermutation(constrainst, x, sIntType());
    }

    @ParameterizedTest
    @MethodSource(
        "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
    public void composed_with_temp_instantiated_to_x_infers_x_structure_from_composed(
        Function<SType, SType> factory) throws UnifierException {
      var temp = unifier.newTempVar();
      var schema = unifier.newTempVar();
      var instantiation = unifier.newTempVar();
      var constraints = list(
          new EqualityConstraint(schema, factory.apply(temp)),
          new InstantiationConstraint(instantiation, schema));
      assertResolvedStructuresAreEqualForEachPermutation(
          constraints, instantiation, factory.apply(temp));
    }

    @ParameterizedTest
    @MethodSource(
        "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
    public void
        composed_with_temp_instantiated_many_times_to_same_composed_with_different_concrete_types_succeeds(
            Function<SType, SType> factory) throws UnifierException {
      var temp = unifier.newTempVar();
      var schema = unifier.newTempVar();
      var instantiation1 = unifier.newTempVar();
      var instantiation2 = unifier.newTempVar();
      var constraints = list(
          new EqualityConstraint(schema, factory.apply(temp)),
          new EqualityConstraint(instantiation1, factory.apply(sIntType())),
          new EqualityConstraint(instantiation2, factory.apply(sBlobType())),
          new InstantiationConstraint(instantiation1, schema),
          new InstantiationConstraint(instantiation2, schema));
      assertConstraintsAreSolvableForEachPermutation(constraints);
    }
  }

  private void assertUnifyInfers(
      Unifier unifier, SType type1, SType type2, SType unresolved, SType expected)
      throws UnifierException {
    unifier.add(new EqualityConstraint(type1, type2));
    assertThat(unifier.resolve(unresolved)).isEqualTo(expected);
  }

  private void assertUnifyInfersEquality(
      Unifier unifier, SType type1, SType type2, SType unresolved1, SType unresolved2)
      throws UnifierException {
    unifier.add(new EqualityConstraint(type1, type2));
    assertThat(unifier.resolve(unresolved1)).isEqualTo(unifier.resolve(unresolved2));
  }

  private void assertUnifyThroughTempImpl(SType type1, SType type2, SType expected)
      throws UnifierException {
    var a = unifier.newTempVar();
    var constraints = list(new EqualityConstraint(a, type1), new EqualityConstraint(a, type2));
    assertResolvedAreEqualForEachPermutation(constraints, a, expected);
  }

  private void assertUnifyFails(SType type1, SType type2) throws UnifierException {
    assertExceptionThrownForLastConstraintForEachPermutation(
        list(new EqualityConstraint(type1, type2)));
  }

  private static void assertResolvedStructuresAreEqualForEachPermutation(
      List<? extends Constraint> constraints, SType type, SType expected) throws UnifierException {
    assertForEachPermutation(
        constraints, unifier -> assertResolvedStructuresAreEqual(unifier, type, expected));
  }

  private static void assertResolvedStructuresAreEqual(Unifier unifier, SType type1, SType type2) {
    AssertStructuresAreEqual.assertStructuresAreEqual(
        unifier.structureOf(type1), unifier.structureOf(type2));
  }

  private static void assertResolvedAreEqualForEachPermutation(
      List<? extends Constraint> constraints, SType type, SType expected) throws UnifierException {
    assertForEachPermutation(
        constraints, unifier -> assertResolvedAreEquals(unifier, type, expected));
  }

  private static void assertResolvedAreEquals(Unifier unifier, SType type, SType expected) {
    assertThat(unifier.resolve(type)).isEqualTo(unifier.resolve(expected));
  }

  private static void assertConstraintsAreSolvableForEachPermutation(
      List<? extends Constraint> constraints) throws UnifierException {
    assertForEachPermutation(constraints, (unifier) -> {});
  }

  private static void assertForEachPermutation(
      List<? extends Constraint> constraints, Consumer<Unifier> assertion) throws UnifierException {
    var permutations = permutations(constraints);
    for (java.util.List<? extends Constraint> permutation : permutations) {
      assertPermutation(listOfAll(permutation), assertion);
    }
  }

  private static void assertPermutation(
      List<? extends Constraint> permutation, Consumer<Unifier> assertion) throws UnifierException {
    var unifier = new Unifier();
    for (int i = 0; i < 4; i++) {
      unifier.newTempVar();
    }
    for (var constraint : permutation) {
      unifier.add(constraint);
    }
    assertion.accept(unifier);
  }

  private static void assertExceptionThrownForLastConstraintForEachPermutation(
      List<? extends Constraint> constraints) throws UnifierException {
    var permutations = permutations(constraints);
    for (java.util.List<? extends Constraint> permutation : permutations) {
      assertExceptionThrownForPermutation(listOfAll(permutation));
    }
  }

  private static void assertExceptionThrownForPermutation(List<? extends Constraint> permutation)
      throws UnifierException {
    var unifier = new Unifier();
    for (int i = 0; i < 4; i++) {
      unifier.newTempVar();
    }
    for (int i = 0; i < permutation.size() - 1; i++) {
      unifier.add(permutation.get(i));
    }
    assertCall(() -> unifier.add(permutation.get(permutation.size() - 1)))
        .throwsException(UnifierException.class);
  }
}
