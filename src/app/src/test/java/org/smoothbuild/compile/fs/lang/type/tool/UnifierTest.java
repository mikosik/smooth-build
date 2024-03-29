package org.smoothbuild.compile.fs.lang.type.tool;

import static com.google.common.collect.Collections2.permutations;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.compile.fs.lang.type.tool.AssertStructuresAreEqual.assertStructuresAreEqual;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.compile.fs.lang.type.FieldSetTS;
import org.smoothbuild.compile.fs.lang.type.TempVarS;
import org.smoothbuild.compile.fs.lang.type.TypeFS;
import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.compile.fs.lang.type.VarS;
import org.smoothbuild.testing.TestContext;

import com.google.common.collect.ImmutableList;

public class UnifierTest extends TestContext {
  private final Unifier unifier = new Unifier();

  @Nested
  class _structure_of {
    @Test
    public void array() {
      var a = unifier.newTempVar();
      var arrayTS = arrayTS(a);
      var a2 = new TempVarS("1");
      assertThat(unifier.structureOf(arrayTS))
          .isEqualTo(arrayTS(a2));
    }

    @Test
    public void function() {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      var funcTS = funcTS(list(a, b), a);
      var a2 = new TempVarS("2");
      var b2 = new TempVarS("3");
      assertThat(unifier.structureOf(funcTS))
          .isEqualTo(funcTS(list(a2, b2), a2));
    }

    @Test
    public void tuple() {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      var tupleTS = tupleTS(a, b, a);
      var a2 = new TempVarS("2");
      var b2 = new TempVarS("3");
      assertThat(unifier.structureOf(tupleTS))
          .isEqualTo(tupleTS(a2, b2, a2));
    }

    @Test
    public void interface_() {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      var interfaceTS = interfaceTS(a, b, a);
      var a2 = new TempVarS("2");
      var b2 = new TempVarS("3");
      assertThat(unifier.structureOf(interfaceTS))
          .isEqualTo(interfaceTS(a2, b2, a2));
    }

    @Test
    public void struct() {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      var structTS = structTS(a, b, a);
      var a2 = new TempVarS("2");
      var b2 = new TempVarS("3");
      assertThat(unifier.structureOf(structTS))
          .isEqualTo(structTS(a2, b2, a2));
    }
  }

  @Nested
  class _single_unify_call {
    @Nested
    class _temp_vs_temp {
      @Test
      public void temp_vs_itself() throws UnifierExc {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        assertUnifyInfers(
            unifier,
            a,
            a,
            a,
            a);
      }

      @Test
      public void temp_vs_other_temp() throws UnifierExc {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        unifier.add(new EqualityConstraint(a, b));
        assertThat(unifier.resolve(a))
            .isEqualTo(unifier.resolve(b));
      }

      @Test
      public void temp_vs_other_temp_vs_yet_another_temp() throws UnifierExc {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var c = unifier.newTempVar();
        unifier.add(new EqualityConstraint(a, b));
        unifier.add(new EqualityConstraint(b, c));
        assertThat(unifier.resolve(a))
            .isEqualTo(unifier.resolve(c));
      }

      @ParameterizedTest
      @MethodSource("org.smoothbuild.testing.TestContext#compositeTypeSFactories")
      public void temp_vs_composed_with_that_temp_fails(Function<TypeS, TypeS> composedFactory)
          throws UnifierExc {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        assertUnifyFails(a, composedFactory.apply(a));
      }

      @ParameterizedTest
      @MethodSource("org.smoothbuild.testing.TestContext#compositeTypeSFactories")
      public void temp_vs_composed_with_different_temp_succeeds(
          Function<TypeS, TypeS> composedFactory) throws UnifierExc {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        assertUnifyInfers(
            unifier,
            a,
            composedFactory.apply(b),
            a,
            composedFactory.apply(b));
      }

      @ParameterizedTest
      @MethodSource("org.smoothbuild.testing.TestContext#compositeTypeSFactories")
      public void composed_with_temp_vs_same_composed_with_other_temp_succeeds(
          Function<TypeS, TypeS> composedFactory) throws UnifierExc {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        assertUnifyInfersEquality(
            unifier,
            composedFactory.apply(a),
            composedFactory.apply(b),
            a,
            b);
      }
    }

    @Nested
    class _temp_vs_non_temp {
      @ParameterizedTest
      @MethodSource("org.smoothbuild.testing.TestContext#typesToTest")
      public void temp_vs_concrete_type(TypeS type) throws UnifierExc {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        assertUnifyInfers(
            unifier,
            a,
            type,
            a,
            type);
      }

      @Nested
      class _temp_as_component {
        @ParameterizedTest
        @MethodSource("org.smoothbuild.testing.TestContext#compositeTypeSFactories")
        public void composed_with_temp_vs_same_composed_with_base_type_instead_temp(
            Function<TypeS, TypeS> factory) throws UnifierExc {
          var unifier = new Unifier();
          var var = unifier.newTempVar();
          var baseType = intTS();
          assertUnifyInfers(
              unifier,
              factory.apply(var),
              factory.apply(baseType),
              var,
              baseType);
        }
      }
    }

    @Nested
    class _non_temp_vs_non_temp_var {
      @ParameterizedTest
      @MethodSource("typesToTest")
      public void base_vs_itself_succeeds(TypeS type) throws UnifierExc {
        unifier.add(new EqualityConstraint(type, type));
      }

      @Test
      public void base_vs_different_base_fails() throws UnifierExc {
        assertUnifyFails(intTS(), blobTS());
      }

      @ParameterizedTest
      @MethodSource("typesToTest")
      public void base_vs_var_fails() throws UnifierExc {
        assertUnifyFails(varB(), blobTS());
      }

      @ParameterizedTest
      @MethodSource("typesToTest")
      public void base_vs_array_fails(TypeS type) throws UnifierExc {
        assertUnifyFails(type, arrayTS(type));
      }

      @ParameterizedTest
      @MethodSource("typesToTest")
      public void base_vs_tuple_fails(TypeS type) throws UnifierExc {
        assertUnifyFails(type, tupleTS(type));
      }

      @ParameterizedTest
      @MethodSource("typesToTest")
      public void base_vs_function_fails(TypeS type) throws UnifierExc {
        assertUnifyFails(type, funcTS(type));
        assertUnifyFails(type, funcTS(type, type));
      }

      @ParameterizedTest
      @MethodSource("typesToTest")
      public void base_vs_struct_fails(TypeS type) throws UnifierExc {
        assertUnifyFails(type, structTS("MyFunc"));
        assertUnifyFails(type, structTS("MyFunc", intTS()));
      }

      @Test
      public void var_vs_itself_succeeds() throws UnifierExc {
        unifier.add(new EqualityConstraint(varA(), varA()));
      }

      @Test
      public void var_vs_array_fails() throws UnifierExc {
        assertUnifyFails(
            varA(),
            arrayTS(varA()));
      }

      @Test
      public void var_vs_tuple_fails() throws UnifierExc {
        assertUnifyFails(
            varA(),
            tupleTS(varA()));
      }

      @Test
      public void var_vs_function_fails() throws UnifierExc {
        assertUnifyFails(
            varA(),
            funcTS(varA()));
      }

      @Test
      public void var_vs_struct_fails() throws UnifierExc {
        assertUnifyFails(
            varA(),
            structTS("MyStruct", varA()));
      }

      @Test
      public void var_vs_interface_fails() throws UnifierExc {
        assertUnifyFails(
            varA(),
            interfaceTS(varA()));
      }

      @Test
      public void array_vs_itself_succeeds() throws UnifierExc {
        unifier.add(new EqualityConstraint(
            arrayTS(intTS()),
            arrayTS(intTS())));
      }

      @Test
      public void array_with_interface_vs_array_with_compatible_interface_succeeds()
          throws UnifierExc {
        unifier.add(new EqualityConstraint(
            arrayTS(interfaceTS(sigS(intTS(), "myField"))),
            arrayTS(interfaceTS(sigS(stringTS(), "otherField")))));
      }

      @Test
      public void array2_vs_itself_succeeds() throws UnifierExc {
        unifier.add(new EqualityConstraint(
            arrayTS(arrayTS(intTS())),
            arrayTS(arrayTS(intTS()))));
      }

      @Test
      public void array_vs_array_with_different_element_fails() throws UnifierExc {
        assertUnifyFails(arrayTS(intTS()), arrayTS(blobTS()));
      }

      @Test
      public void array_vs_tuple_fails() throws UnifierExc {
        assertUnifyFails(arrayTS(intTS()), tupleTS(intTS()));
      }

      @Test
      public void array_vs_function_fails() throws UnifierExc {
        assertUnifyFails(arrayTS(intTS()), funcTS(intTS()));
      }

      @Test
      public void array_vs_struct_fails() throws UnifierExc {
        assertUnifyFails(arrayTS(intTS()), structTS("MyStruct", intTS()));
      }

      @Test
      public void array_vs_interface_fails() throws UnifierExc {
        assertUnifyFails(arrayTS(intTS()), interfaceTS(intTS()));
      }

      @Test
      public void tuple_vs_itself_succeeds() throws UnifierExc {
        unifier.add(new EqualityConstraint(
            tupleTS(intTS(), blobTS()),
            tupleTS(intTS(), blobTS())));
      }

      @Test
      public void tuple_with_interface_vs_tuple_with_compatible_interface_succeeds()
          throws UnifierExc {
        unifier.add(new EqualityConstraint(
            tupleTS(interfaceTS(sigS(intTS(), "myField"))),
            tupleTS(interfaceTS(sigS(stringTS(), "otherField")))));
      }

      @Test
      public void tuple2_vs_itself_succeeds() throws UnifierExc {
        unifier.add(new EqualityConstraint(
            tupleTS(tupleTS(intTS(), blobTS())),
            tupleTS(tupleTS(intTS(), blobTS()))));
      }

      @Test
      public void tuple_vs_tuple_with_different_elements_fails() throws UnifierExc {
        assertUnifyFails(tupleTS(intTS(), blobTS()), tupleTS(intTS(), stringTS()));
      }

      @Test
      public void tuple_vs_tuple_with_one_element_missing_fails() throws UnifierExc {
        assertUnifyFails(tupleTS(intTS(), blobTS()), tupleTS(intTS()));
      }

      @Test
      public void tuple_vs_function_fails() throws UnifierExc {
        assertUnifyFails(tupleTS(intTS()), funcTS(intTS()));
      }

      @Test
      public void tuple_vs_struct_fails() throws UnifierExc {
        assertUnifyFails(tupleTS(intTS()), structTS("MyStruct", intTS()));
      }

      @Test
      public void tuple_vs_interface_fails() throws UnifierExc {
        assertUnifyFails(tupleTS(intTS()), interfaceTS(intTS()));
      }

      @Test
      public void function_vs_itself_succeeds() throws UnifierExc {
        unifier.add(new EqualityConstraint(
            funcTS(blobTS(), intTS()),
            funcTS(blobTS(), intTS())));
      }

      @Test
      public void function_with_interface_vs_function_with_compatible_interface_succeeds()
          throws UnifierExc {
        unifier.add(new EqualityConstraint(
            funcTS(interfaceTS(sigS(intTS(), "myField"))),
            funcTS(interfaceTS(sigS(stringTS(), "otherField")))));
      }

      @Test
      public void function_with_result_being_function_vs_itself_succeeds() throws UnifierExc {
        unifier.add(new EqualityConstraint(
            funcTS(funcTS(intTS())),
            funcTS(funcTS(intTS()))));
      }

      @Test
      public void function_with_parameter_being_function_vs_itself_succeeds() throws UnifierExc {
        unifier.add(new EqualityConstraint(
            funcTS(funcTS(intTS()), blobTS()),
            funcTS(funcTS(intTS()), blobTS())));
      }

      @Test
      public void function_vs_function_with_different_result_type_fails() throws UnifierExc {
        assertUnifyFails(funcTS(intTS()), funcTS(blobTS()));
      }

      @Test
      public void function_vs_function_with_different_parameter_fails() throws UnifierExc {
        assertUnifyFails(funcTS(blobTS(), intTS()), funcTS(stringTS(), intTS()));
      }

      @Test
      public void function_vs_function_with_one_parameter_missing_fails() throws UnifierExc {
        assertUnifyFails(funcTS(blobTS(), intTS()), funcTS(intTS()));
      }

      @Test
      public void function_vs_struct_fails() throws UnifierExc {
        assertUnifyFails(funcTS(intTS()), structTS("MyStruct", intTS()));
      }

      @Test
      public void function_vs_interface_fails() throws UnifierExc {
        assertUnifyFails(funcTS(intTS()), interfaceTS(intTS()));
      }

      @Test
      public void struct_vs_itself_succeeds() throws UnifierExc {
        unifier.add(new EqualityConstraint(
            structTS("MyStruct", intTS(), blobTS()),
            structTS("MyStruct", intTS(), blobTS())));
      }

      @Test
      public void struct_with_interface_vs_struct_with_compatible_interface_succeeds()
          throws UnifierExc {
        unifier.add(new EqualityConstraint(
            structTS(interfaceTS(sigS(intTS(), "myField"))),
            structTS(interfaceTS(sigS(stringTS(), "otherField")))));
      }

      @Test
      public void struct_vs_itself_with_fields_reordered_fails() throws UnifierExc {
        assertUnifyFails(
            structTS("MyStruct", sigS(intTS(), "myIntField"), sigS(blobTS(), "myBlobField")),
            structTS("MyStruct", sigS(blobTS(), "myBlobField"), sigS(intTS(), "myIntField")));
      }

      @Test
      public void struct_vs_struct_with_different_name_fails() throws UnifierExc {
        assertUnifyFails(
            structTS("MyStruct", intTS(), blobTS()),
            structTS("MyStruct2", intTS(), blobTS()));
      }

      @Test
      public void struct_vs_struct_with_different_field_type_fails() throws UnifierExc {
        assertUnifyFails(
            structTS("MyStruct", intTS(), blobTS()),
            structTS("MyStruct", intTS(), stringTS()));
      }

      @Test
      public void struct_vs_struct_with_different_field_name_fails() throws UnifierExc {
        assertUnifyFails(
            structTS("MyStruct", sigS(intTS(), "myField")),
            structTS("MyStruct", sigS(intTS(), "otherField")));
      }

      @Test
      public void struct_vs_matching_interface_succeeds() throws UnifierExc {
        unifier.add(new EqualityConstraint(
            structTS("MyStruct", sigS(intTS(), "myField")),
            interfaceTS(sigS(intTS(), "myField"))));
      }

      @Test
      public void struct_vs_matching_interface_with_changed_field_type_fails() throws UnifierExc {
        assertUnifyFails(
            structTS("MyStruct", sigS(intTS(), "myField")),
            interfaceTS(sigS(blobTS(), "myField")));
      }

      @Test
      public void struct_vs_matching_interface_with_changed_field_name_fails() throws UnifierExc {
        assertUnifyFails(
            structTS("MyStruct", sigS(intTS(), "myField")),
            interfaceTS(sigS(intTS(), "otherField")));
      }

      @Test
      public void interface_vs_itself_succeeds() throws UnifierExc {
        unifier.add(new EqualityConstraint(
            interfaceTS(intTS(), blobTS()),
            interfaceTS(intTS(), blobTS())));
      }

      @Test
      public void interface_vs_interface_with_different_field_type_fails() throws UnifierExc {
        assertUnifyFails(
            interfaceTS(sigS(intTS(), "myField")),
            interfaceTS(sigS(blobTS(), "myField")));
      }

      @Test
      public void interface_vs_interface_with_different_field_name_succeeds() throws UnifierExc {
        TypeS type1 = interfaceTS(sigS(intTS(), "myField"));
        TypeS type2 = interfaceTS(sigS(intTS(), "otherField"));
        assertUnifyThroughTempImpl(type1, type2,
            interfaceTS(sigS(intTS(), "otherField"), sigS(intTS(), "myField")));
      }

      public static ImmutableList<TypeS> typesToTest() {
        return concat(TypeFS.baseTs(), new VarS("A"));
      }
    }
  }

  @Nested
  class _merging_field_sets {
    @Nested
    class _legal_field_set_merges {
      @ParameterizedTest
      @ArgumentsSource(LegalFieldSetMergesProvider.class)
      public void array_element(FieldSetTS type1, FieldSetTS type2, TypeS expected)
          throws UnifierExc {
        assertUnifyThroughTempImpl(
            arrayTS(type1),
            arrayTS(type2),
            arrayTS(expected));
      }

      @ParameterizedTest
      @ArgumentsSource(LegalFieldSetMergesProvider.class)
      public void tuple_element(FieldSetTS type1, FieldSetTS type2, TypeS expected)
          throws UnifierExc {
        assertUnifyThroughTempImpl(
            tupleTS(type1),
            tupleTS(type2),
            tupleTS(expected));
      }

      @ParameterizedTest
      @ArgumentsSource(LegalFieldSetMergesProvider.class)
      public void func_result(FieldSetTS type1, FieldSetTS type2, TypeS expected)
          throws UnifierExc {
        assertUnifyThroughTempImpl(
            funcTS(type1),
            funcTS(type2),
            funcTS(expected));
      }

      @ParameterizedTest
      @ArgumentsSource(LegalFieldSetMergesProvider.class)
      public void func_param(FieldSetTS type1, FieldSetTS type2, TypeS expected)
          throws UnifierExc {
        assertUnifyThroughTempImpl(
            funcTS(type1, intTS()),
            funcTS(type2, intTS()),
            funcTS(expected, intTS()));
      }

      @ParameterizedTest
      @ArgumentsSource(LegalFieldSetMergesProvider.class)
      public void struct_field(FieldSetTS type1, FieldSetTS type2, TypeS expected)
          throws UnifierExc {
        assertUnifyThroughTempImpl(
            structTS(type1),
            structTS(type2),
            structTS(expected));
      }

      @ParameterizedTest
      @ArgumentsSource(LegalFieldSetMergesProvider.class)
      public void interface_field(FieldSetTS type1, FieldSetTS type2, TypeS expected)
          throws UnifierExc {
        assertUnifyThroughTempImpl(
            interfaceTS(type1),
            interfaceTS(type2),
            interfaceTS(expected));
      }
    }

    @Nested
    class _illegal_field_set_merges {
      @ParameterizedTest
      @ArgumentsSource(IllegalFieldSetMergesProvider.class)
      public void array_element(FieldSetTS type1, FieldSetTS type2) throws UnifierExc {
        assertUnifyFails(
            arrayTS(type1),
            arrayTS(type2)
        );
      }

      @ParameterizedTest
      @ArgumentsSource(IllegalFieldSetMergesProvider.class)
      public void tuple_element(FieldSetTS type1, FieldSetTS type2) throws UnifierExc {
        assertUnifyFails(
            tupleTS(type1),
            tupleTS(type2)
        );
      }

      @ParameterizedTest
      @ArgumentsSource(IllegalFieldSetMergesProvider.class)
      public void func_result(FieldSetTS type1, FieldSetTS type2) throws UnifierExc {
        assertUnifyFails(
            funcTS(type1),
            funcTS(type2)
        );
      }

      @ParameterizedTest
      @ArgumentsSource(IllegalFieldSetMergesProvider.class)
      public void func_param(FieldSetTS type1, FieldSetTS type2) throws UnifierExc {
        assertUnifyFails(
            funcTS(type1, intTS()),
            funcTS(type2, intTS())
        );
      }

      @ParameterizedTest
      @ArgumentsSource(IllegalFieldSetMergesProvider.class)
      public void struct_field(FieldSetTS type1, FieldSetTS type2) throws UnifierExc {
        assertUnifyFails(
            structTS(type1),
            structTS(type2)
        );
      }

      @ParameterizedTest
      @ArgumentsSource(IllegalFieldSetMergesProvider.class)
      public void interface_field(FieldSetTS type1, FieldSetTS type2) throws UnifierExc {
        assertUnifyFails(
            interfaceTS(type1),
            interfaceTS(type2)
        );
      }
    }
  }

  private static class LegalFieldSetMergesProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
          // struct vs struct
          arguments(
              structTS("MyStruct", sigS(intTS(), "myField")),
              structTS("MyStruct", sigS(intTS(), "myField")),
              structTS("MyStruct", sigS(intTS(), "myField"))),

          // interface vs interface
          arguments(
              interfaceTS(sigS(intTS(), "myField")),
              interfaceTS(sigS(intTS(), "myField")),
              interfaceTS(sigS(intTS(), "myField"))),
          arguments(
              interfaceTS(sigS(intTS(), "myField")),
              interfaceTS(),
              interfaceTS(sigS(intTS(), "myField"))),
          arguments(
              interfaceTS(sigS(intTS(), "myField")),
              interfaceTS(sigS(intTS(), "otherField")),
              interfaceTS(sigS(intTS(), "otherField"), sigS(intTS(), "myField"))),

          // struct vs interface
          arguments(
              structTS("MyStruct", sigS(intTS(), "myField")),
              interfaceTS(sigS(intTS(), "myField")),
              structTS("MyStruct", sigS(intTS(), "myField"))),
          arguments(
              structTS("MyStruct", sigS(intTS(), "myField")),
              interfaceTS(),
              structTS("MyStruct", sigS(intTS(), "myField")))
      );
    }
  }

  private static class IllegalFieldSetMergesProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
          // struct vs struct
          arguments(
              structTS("MyStruct", sigS(intTS(), "myField")),
              structTS("OtherStruct", sigS(intTS(), "myField"))),
          arguments(
              structTS("MyStruct", sigS(intTS(), "myField")),
              structTS("MyStruct", sigS(blobTS(), "myField"))),
          arguments(
              structTS("MyStruct", sigS(intTS(), "myField")),
              structTS("MyStruct", sigS(intTS(), "otherField"))),
          arguments(
              structTS("MyStruct", sigS(intTS(), "myField")),
              structTS("MyStruct")),


          // interface vs interface
          arguments(
              interfaceTS(sigS(intTS(), "myField")),
              interfaceTS(sigS(blobTS(), "myField"))),

          // struct vs interface
          arguments(
              structTS("MyStruct", sigS(intTS(), "myField")),
              interfaceTS(sigS(blobTS(), "myField"))),
          arguments(
              structTS("MyStruct", sigS(intTS(), "myField")),
              interfaceTS(sigS(intTS(), "otherField"))),
          arguments(
              structTS("MyStruct", sigS(intTS(), "myField")),
              interfaceTS(sigS(intTS(), "myField"), sigS(intTS(), "otherField")))
      );
    }
  }

  @Nested
  class _cycles {
    @ParameterizedTest
    @MethodSource("org.smoothbuild.testing.TestContext#compositeTypeSFactories")
    public void one_element_cycle_through_composed(Function<TypeS, TypeS> composedFactory)
        throws UnifierExc {
      var a = unifier.newTempVar();
      var constraints = list(
          new EqualityConstraint(a, composedFactory.apply(a))
      );
      assertExceptionThrownForLastConstraintForEachPermutation(constraints);
    }

    @ParameterizedTest
    @MethodSource("org.smoothbuild.testing.TestContext#compositeTypeSFactories")
    public void two_elements_cycle_through_composed(Function<TypeS, TypeS> composedFactory)
        throws UnifierExc {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      var constraints = list(
          new EqualityConstraint(a, composedFactory.apply(b)),
          new EqualityConstraint(b, composedFactory.apply(a)));
      assertExceptionThrownForLastConstraintForEachPermutation(constraints);
    }

    @ParameterizedTest
    @MethodSource("org.smoothbuild.testing.TestContext#compositeTypeSFactories")
    public void three_elements_cycle_through_composed(Function<TypeS, TypeS> composedFactory)
        throws UnifierExc {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      var c = unifier.newTempVar();
      var constraints = list(
          new EqualityConstraint(a, composedFactory.apply(b)),
          new EqualityConstraint(b, composedFactory.apply(c)),
          new EqualityConstraint(c, composedFactory.apply(a))
      );
      assertExceptionThrownForLastConstraintForEachPermutation(constraints);
    }

    @Test
    public void regression_test() throws UnifierExc {
      // Cycle detection algorithm had a bug which is detected by this test.
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      var c = unifier.newTempVar();
      var constraints = list(
          new EqualityConstraint(a, arrayTS(b)),
          new EqualityConstraint(c, funcTS(b, a))
      );
      assertConstraintsAreSolvableForEachPermutation(constraints);
    }
  }

  @Nested
  class _complex_cases {
    @Nested
    class _transitive_cases {
      @Test
      public void unify_a_vs_b_vs_concrete_type() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var constraints = list(
            new EqualityConstraint(a, b),
            new EqualityConstraint(b, intTS()));
        assertResolvedAreEqualForEachPermutation(constraints, a, intTS());
      }

      @Test
      public void unify_a_vs_array_b_vs_c() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var c = unifier.newTempVar();
        var constraints = list(
            new EqualityConstraint(a, arrayTS(b)),
            new EqualityConstraint(arrayTS(b), c)
        );
        assertResolvedAreEqualForEachPermutation(constraints, a, c);
      }

      @Test
      public void unify_array_a_vs_b_vs_array_c() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var c = unifier.newTempVar();
        var constraints = list(
            new EqualityConstraint(arrayTS(a), b),
            new EqualityConstraint(b, arrayTS(c))
        );
        assertResolvedAreEqualForEachPermutation(constraints, a, c);
      }
    }

    @Nested
    class _join_separate_unified_groups {
      @ParameterizedTest
      @MethodSource("org.smoothbuild.testing.TestContext#compositeTypeSFactories")
      public void join_composed_of_temp_vs_composed_of_different_temp(
          Function<TypeS, TypeS> composedFactory) throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var x = unifier.newTempVar();
        var y = unifier.newTempVar();
        var constraints = list(
            new EqualityConstraint(a, composedFactory.apply(x)),
            new EqualityConstraint(b, composedFactory.apply(y)),
            new EqualityConstraint(a, b)
        );
        assertResolvedAreEqualForEachPermutation(constraints, x, y);
      }

      @ParameterizedTest
      @MethodSource("org.smoothbuild.testing.TestContext#compositeTypeSFactories")
      public void join_composed_of_temp_vs_composed_of_int(Function<TypeS, TypeS> composedFactory)
          throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var x = unifier.newTempVar();
        var constraints = list(
            new EqualityConstraint(a, composedFactory.apply(x)),
            new EqualityConstraint(b, composedFactory.apply(intTS())),
            new EqualityConstraint(a, b)
        );
        assertResolvedAreEqualForEachPermutation(constraints, x, intTS());
      }

      @ParameterizedTest
      @MethodSource("org.smoothbuild.testing.TestContext#compositeTypeSFactories")
      public void join_composed_of_int_vs_composed_of_blob_fails(
          Function<TypeS, TypeS> composedFactory) throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var constraints = list(
            new EqualityConstraint(a, composedFactory.apply(intTS())),
            new EqualityConstraint(b, composedFactory.apply(blobTS())),
            new EqualityConstraint(a, b)
        );
        assertExceptionThrownForLastConstraintForEachPermutation(constraints);
      }

      @Test
      public void join_func_vs_func_with_different_param_count_fails() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var constraints = list(
            new EqualityConstraint(a, funcTS(intTS(), intTS())),
            new EqualityConstraint(b, funcTS(intTS())),
            new EqualityConstraint(a, b)
        );
        assertExceptionThrownForLastConstraintForEachPermutation(constraints);
      }

      @Test
      public void join_array_vs_tuple_fails() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        unifier.add(new EqualityConstraint(a, arrayTS(intTS())));
        unifier.add(new EqualityConstraint(b, tupleTS(intTS())));
        assertCall(() -> unifier.add(new EqualityConstraint(a, b)))
            .throwsException(UnifierExc.class);
      }

      @Test
      public void join_array_vs_func_fails() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        unifier.add(new EqualityConstraint(a, arrayTS(intTS())));
        unifier.add(new EqualityConstraint(b, funcTS(intTS())));
        assertCall(() -> unifier.add(new EqualityConstraint(a, b)))
            .throwsException(UnifierExc.class);
      }

      @Test
      public void join_tuple_vs_func_fails() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        unifier.add(new EqualityConstraint(a, tupleTS(intTS())));
        unifier.add(new EqualityConstraint(b, funcTS(intTS())));
        assertCall(() -> unifier.add(new EqualityConstraint(a, b)))
            .throwsException(UnifierExc.class);
      }
    }
  }

  @Nested
  class _temporary_vars {
    @Test
    public void resolve_unknown_temp_var_causes_exception() {
      assertCall(() -> unifier.resolve(tempVarA()))
          .throwsException(new IllegalStateException("Unknown temp var `1`."));
    }

    @Test
    public void non_temporary_var_has_priority_over_temporary() throws UnifierExc {
      VarS a = unifier.newTempVar();
      VarS b = unifier.newTempVar();
      VarS x = varX();
      var constraints = list(
          new EqualityConstraint(a, b),
          new EqualityConstraint(b, x)
      );
      assertResolvedAreEqualForEachPermutation(constraints, a, x);
      assertResolvedAreEqualForEachPermutation(constraints, b, x);
      assertResolvedAreEqualForEachPermutation(constraints, x, x);
    }

    @Test
    public void resolve_returns_temporary_var_when_no_normal_var_is_unified() {
      var a = unifier.newTempVar();
      assertThat(unifier.resolve(a))
          .isEqualTo(a);
    }
  }

  @Nested
  class _resolve {
    @Test
    public void unknown_temp_var_cannot_be_resolved() {
      assertCall(() -> unifier.resolve(tempVarA()))
          .throwsException(new IllegalStateException("Unknown temp var `1`."));
    }

    @ParameterizedTest
    @MethodSource("org.smoothbuild.testing.TestContext#compositeTypeSFactories")
    public void composed_type(Function<TypeS, TypeS> composedFactory) throws UnifierExc {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      var constraints = list(
          new EqualityConstraint(a, intTS()),
          new EqualityConstraint(b, composedFactory.apply(a))
      );
      assertResolvedAreEqualForEachPermutation(constraints, b, composedFactory.apply(intTS()));
    }
  }

  @Nested
  class _schema_instantiation {
    @ParameterizedTest
    @MethodSource("org.smoothbuild.testing.TestContext#typesToTest")
    public void concrete_type_instantiated_to_itself_succeeds(TypeS type) throws UnifierExc {
      var schema = unifier.newTempVar();
      var instantiation = unifier.newTempVar();
      var constraints = list(
          new EqualityConstraint(schema, type),
          new EqualityConstraint(instantiation, type),
          new InstantiationConstraint(instantiation, schema)
      );
      assertConstraintsAreSolvableForEachPermutation(constraints);
    }

    @ParameterizedTest
    @MethodSource("org.smoothbuild.testing.TestContext#typesToTest")
    public void concrete_type_instantiated_to_different_type_fails(TypeS type) throws UnifierExc {
      var differentType = type.equals(intTS()) ? blobTS() : intTS();
      var schema = unifier.newTempVar();
      var instantiation = unifier.newTempVar();
      var constraints = list(
          new EqualityConstraint(schema, type),
          new EqualityConstraint(instantiation, differentType),
          new InstantiationConstraint(instantiation, schema)
      );
      assertExceptionThrownForLastConstraintForEachPermutation(constraints);
    }

    @ParameterizedTest
    @MethodSource("org.smoothbuild.testing.TestContext#typesToTest")
    public void concrete_type_instantiated_to_x_infers_x(TypeS type) throws UnifierExc {
      var x = unifier.newTempVar();
      var schema = unifier.newTempVar();
      var instantiation = unifier.newTempVar();
      var constraints = list(
          new EqualityConstraint(schema, type),
          new EqualityConstraint(instantiation, x),
          new InstantiationConstraint(instantiation, schema)
      );
      assertResolvedAreEqualForEachPermutation(constraints, x, type);
    }

    @ParameterizedTest
    @MethodSource("org.smoothbuild.testing.TestContext#compositeTypeSFactories")
    public void concrete_composed_type_instantiated_to_composed_with_x_infers_x(
        Function<TypeS, TypeS> factory) throws UnifierExc {
      var x = unifier.newTempVar();
      var schema = unifier.newTempVar();
      var instantiation = unifier.newTempVar();
      var constrainst = list(
          new EqualityConstraint(schema, factory.apply(intTS())),
          new EqualityConstraint(instantiation, factory.apply(x)),
          new InstantiationConstraint(instantiation, schema)
      );
      assertResolvedAreEqualForEachPermutation(constrainst, x, intTS());
    }

    @ParameterizedTest
    @MethodSource("org.smoothbuild.testing.TestContext#compositeTypeSFactories")
    public void composed_with_temp_instantiated_to_x_infers_x_structure_from_composed(
        Function<TypeS, TypeS> factory) throws UnifierExc {
      var temp = unifier.newTempVar();
      var schema = unifier.newTempVar();
      var instantiation = unifier.newTempVar();
      var constraints = list(
          new EqualityConstraint(schema, factory.apply(temp)),
          new InstantiationConstraint(instantiation, schema)
      );
      assertResolvedStructuresAreEqualForEachPermutation(
          constraints, instantiation, factory.apply(temp));
    }

    @ParameterizedTest
    @MethodSource("org.smoothbuild.testing.TestContext#compositeTypeSFactories")
    public void composed_with_temp_instantiated_many_times_to_same_composed_with_different_concrete_types_succeeds(
        Function<TypeS, TypeS> factory) throws UnifierExc {
      var temp = unifier.newTempVar();
      var schema = unifier.newTempVar();
      var instantiation1 = unifier.newTempVar();
      var instantiation2 = unifier.newTempVar();
      var constraints = list(
          new EqualityConstraint(schema, factory.apply(temp)),
          new EqualityConstraint(instantiation1, factory.apply(intTS())),
          new EqualityConstraint(instantiation2, factory.apply(blobTS())),
          new InstantiationConstraint(instantiation1, schema),
          new InstantiationConstraint(instantiation2, schema)
      );
      assertConstraintsAreSolvableForEachPermutation(constraints);
    }
  }

  private void assertUnifyInfers(Unifier unifier, TypeS type1, TypeS type2,
      TypeS unresolved, TypeS expected) throws UnifierExc {
    unifier.add(new EqualityConstraint(type1, type2));
    assertThat(unifier.resolve(unresolved))
        .isEqualTo(expected);
  }

  private void assertUnifyInfersEquality(Unifier unifier, TypeS type1, TypeS type2,
      TypeS unresolved1, TypeS unresolved2) throws UnifierExc {
    unifier.add(new EqualityConstraint(type1, type2));
    assertThat(unifier.resolve(unresolved1))
        .isEqualTo(unifier.resolve(unresolved2));
  }

  private void assertUnifyThroughTempImpl(TypeS type1, TypeS type2, TypeS expected)
      throws UnifierExc {
    var a = unifier.newTempVar();
    var constraints = list(
        new EqualityConstraint(a, type1),
        new EqualityConstraint(a, type2));
    assertResolvedAreEqualForEachPermutation(constraints, a, expected);
  }

  private void assertUnifyFails(TypeS type1, TypeS type2) throws UnifierExc {
    assertExceptionThrownForLastConstraintForEachPermutation(list(new EqualityConstraint(type1, type2)));
  }

  private static void assertResolvedStructuresAreEqualForEachPermutation(
      List<? extends Constraint> constraints, TypeS type, TypeS expected)
      throws UnifierExc {
    assertForEachPermutation(
        constraints, unifier -> assertResolvedStructuresAreEqual(unifier, type, expected));
  }

  private static void assertResolvedStructuresAreEqual(
      Unifier unifier, TypeS type1, TypeS type2) {
    assertStructuresAreEqual(unifier.structureOf(type1), unifier.structureOf(type2));
  }

  private static void assertResolvedAreEqualForEachPermutation(
      List<? extends Constraint> constraints, TypeS type, TypeS expected)
      throws UnifierExc {
    assertForEachPermutation(
        constraints, unifier -> assertResolvedAreEquals(unifier, type, expected));
  }

  private static void assertResolvedAreEquals(Unifier unifier, TypeS type, TypeS expected) {
    assertThat(unifier.resolve(type))
        .isEqualTo(unifier.resolve(expected));
  }

  private static void assertConstraintsAreSolvableForEachPermutation(
      List<? extends Constraint> constraints) throws UnifierExc {
    assertForEachPermutation(constraints, (unifier) -> {});
  }

  private static void assertForEachPermutation(
      List<? extends Constraint> constraints, Consumer<Unifier> assertion) throws UnifierExc {
    var permutations = permutations(constraints);
    for (List<? extends Constraint> permutation : permutations) {
      assertPermutation(permutation, assertion);
    }
  }

  private static void assertPermutation(
      List<? extends Constraint> permutation, Consumer<Unifier> assertion)
      throws UnifierExc {
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
      List<? extends Constraint> constraints) throws UnifierExc {
    var permutations = permutations(constraints);
    for (List<? extends Constraint> permutation : permutations) {
      assertExceptionThrownForPermutation(permutation);
    }
  }

  private static void assertExceptionThrownForPermutation(List<? extends Constraint> permutation) throws UnifierExc {
    var unifier = new Unifier();
    for (int i = 0; i < 4; i++) {
      unifier.newTempVar();
    }
    for (int i = 0; i < permutation.size() - 1; i++) {
      unifier.add(permutation.get(i));
    }
    assertCall(() -> unifier.add(permutation.get(permutation.size() - 1)))
        .throwsException(UnifierExc.class);
  }
}
