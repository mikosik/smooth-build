package org.smoothbuild.compile.fs.lang.type.tool;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.concat;

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
import org.smoothbuild.compile.fs.lang.type.TypeFS;
import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.compile.fs.lang.type.VarS;
import org.smoothbuild.testing.TestContext;

import com.google.common.collect.ImmutableList;

public class UnifierTest extends TestContext {
  private final Unifier unifier = new Unifier();

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
        unifier.unify(a, b);
        assertThat(unifier.resolve(a))
            .isEqualTo(unifier.resolve(b));
      }

      @Test
      public void temp_vs_other_temp_vs_yet_another_temp() throws UnifierExc {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var c = unifier.newTempVar();
        unifier.unify(a, b);
        unifier.unify(b, c);
        assertThat(unifier.resolve(a))
            .isEqualTo(unifier.resolve(c));
      }

      @Test
      public void temp_vs_array_of_temps() throws UnifierExc {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        assertUnifyInfers(
            unifier,
            a,
            arrayTS(b),
            a,
            arrayTS(b));
      }

      @Test
      public void temp_vs_tuple_of_temp() throws UnifierExc {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        assertUnifyInfers(
            unifier,
            a,
            tupleTS(b),
            a,
            tupleTS(b));
      }

      @Test
      public void temp_vs_function_of_temp() throws UnifierExc {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        assertUnifyInfers(
            unifier,
            a,
            funcTS(b, intTS()),
            a,
            funcTS(b, intTS()));
      }

      @Test
      public void temp_vs_struct_of_temp() throws UnifierExc {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        assertUnifyInfers(
            unifier,
            a,
            structTS("MyStruct", b),
            a,
            structTS("MyStruct", b));
      }

      @Test
      public void temp_vs_interface_of_temp() throws UnifierExc {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        assertUnifyInfers(
            unifier,
            a,
            interfaceTS(b),
            a,
            interfaceTS(b));
      }

      @Test
      public void array_of_temps_vs_array_of_different_temps() throws UnifierExc {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        assertUnifyInfersEquality(
            unifier,
            arrayTS(a),
            arrayTS(b),
            a,
            b);
      }

      @Test
      public void tuple_with_temp_vs_tuple_with_other_temp() throws UnifierExc {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        assertUnifyInfersEquality(
            unifier,
            tupleTS(a),
            tupleTS(b),
            a,
            b
        );
      }

      @Test
      public void func_with_res_temp_vs_func_with_res_other_temp() throws UnifierExc {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        assertUnifyInfersEquality(
            unifier,
            funcTS(a),
            funcTS(b),
            a,
            b);
      }

      @Test
      public void func_with_param_temp_vs_func_with_param_other_temp() throws UnifierExc {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        assertUnifyInfersEquality(
            unifier,
            funcTS(a, intTS()),
            funcTS(b, intTS()),
            a,
            b);
      }

      @Test
      public void struct_with_field_temp_vs_struct_with_field_other_temp() throws UnifierExc {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        assertUnifyInfersEquality(
            unifier,
            structTS("MyStruct", a, intTS()),
            structTS("MyStruct", b, intTS()),
            a,
            b);
      }

      @Test
      public void interface_with_field_temp_vs_interface_with_field_other_temp() throws UnifierExc {
        var unifier = new Unifier();
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        assertUnifyInfersEquality(
            unifier,
            interfaceTS(a, intTS()),
            interfaceTS(b, intTS()),
            a,
            b);
      }
    }

    @Nested
    class _temp_vs_non_temp {
      @Nested
      class _temp_as_root {
        @ParameterizedTest
        @MethodSource("typesToTest")
        public void temp_and_base(TypeS type) throws UnifierExc {
          var unifier = new Unifier();
          var a = unifier.newTempVar();
          assertUnifyInfers(
              unifier,
              a,
              type,
              a,
              type);
        }

        @ParameterizedTest
        @MethodSource("typesToTest")
        public void temp_and_array(TypeS type) throws UnifierExc {
          var unifier = new Unifier();
          var a = unifier.newTempVar();
          assertUnifyInfers(
              unifier,
              a,
              arrayTS(type),
              a,
              arrayTS(type));
        }

        @ParameterizedTest
        @MethodSource("typesToTest")
        public void temp_and_tuple(TypeS type) throws UnifierExc {
          var unifier = new Unifier();
          var a = unifier.newTempVar();
          assertUnifyInfers(
              unifier,
              a,
              tupleTS(type),
              a,
              tupleTS(type));
        }

        @ParameterizedTest
        @MethodSource("typesToTest")
        public void temp_and_func_with_res(TypeS type) throws UnifierExc {
          var unifier = new Unifier();
          var a = unifier.newTempVar();
          assertUnifyInfers(
              unifier,
              a,
              funcTS(type),
              a,
              funcTS(type));
        }

        @ParameterizedTest
        @MethodSource("typesToTest")
        public void temp_and_func_with_param(TypeS type) throws UnifierExc {
          var unifier = new Unifier();
          var a = unifier.newTempVar();
          assertUnifyInfers(
              unifier,
              a,
              funcTS(type, intTS()),
              a,
              funcTS(type, intTS()));
        }

        @ParameterizedTest
        @MethodSource("typesToTest")
        public void temp_and_struct_with_field(TypeS type) throws UnifierExc {
          var unifier = new Unifier();
          var a = unifier.newTempVar();
          assertUnifyInfers(
              unifier,
              a,
              structTS("MyStruct", type, intTS()),
              a,
              structTS("MyStruct", type, intTS()));
        }

        @ParameterizedTest
        @MethodSource("typesToTest")
        public void temp_and_interface_with_field(TypeS type) throws UnifierExc {
          var unifier = new Unifier();
          var a = unifier.newTempVar();
          assertUnifyInfers(
              unifier,
              a,
              interfaceTS(type, intTS()),
              a,
              interfaceTS(type, intTS()));
        }

        public static ImmutableList<TypeS> typesToTest() {
          return concat(TypeFS.baseTs(), new VarS("A"));
        }
      }

      @Nested
      class _temp_as_component {
        @ParameterizedTest
        @MethodSource("typesToTest")
        public void unify_array_of_temp_a_with_array_of_base(TypeS type) throws UnifierExc {
          var unifier = new Unifier();
          var var = unifier.newTempVar();
          assertUnifyInfers(
              unifier,
              arrayTS(var),
              arrayTS(type),
              var,
              type);
        }

        @ParameterizedTest
        @MethodSource("typesToTest")
        public void unify_tuple_of_temp_with_tuple_of_base(TypeS type) throws UnifierExc {
          var unifier = new Unifier();
          var var = unifier.newTempVar();
          assertUnifyInfers(
              unifier,
              tupleTS(var),
              tupleTS(type),
              var,
              type);
        }

        @ParameterizedTest
        @MethodSource("typesToTest")
        public void unify_func_with_res_temp_with_func_with_res_base(TypeS type) throws UnifierExc {
          var unifier = new Unifier();
          var var = unifier.newTempVar();
          assertUnifyInfers(
              unifier,
              funcTS(var),
              funcTS(type),
              var,
              type);
        }

        @ParameterizedTest
        @MethodSource("typesToTest")
        public void unify_func_with_param_temp_with_func_with_param_base(TypeS type)
            throws UnifierExc {
          var unifier = new Unifier();
          var var = unifier.newTempVar();
          assertUnifyInfers(
              unifier,
              funcTS(var, intTS()),
              funcTS(type, intTS()),
              var,
              type);
        }

        @ParameterizedTest
        @MethodSource("typesToTest")
        public void unify_struct_with_field_temp_with_struct_with_field_base(TypeS type)
            throws UnifierExc {
          var unifier = new Unifier();
          var var = unifier.newTempVar();
          assertUnifyInfers(
              unifier,
              structTS("MyStruct", var, intTS()),
              structTS("MyStruct", type, intTS()),
              var,
              type);
        }

        @ParameterizedTest
        @MethodSource("typesToTest")
        public void unify_interface_with_field_temp_with_interface_with_field_base(TypeS type)
            throws UnifierExc {
          var unifier = new Unifier();
          var var = unifier.newTempVar();
          assertUnifyInfers(
              unifier,
              interfaceTS(var, intTS()),
              interfaceTS(type, intTS()),
              var,
              type);
        }

        public static ImmutableList<TypeS> typesToTest() {
          return concat(TypeFS.baseTs(), new VarS("A"));
        }
      }
    }

    @Nested
    class _non_temp_vs_non_temp_var {
      @ParameterizedTest
      @MethodSource("typesToTest")
      public void base_vs_itself_succeeds(TypeS type) throws UnifierExc {
        unifier.unify(type, type);
      }

      @Test
      public void base_vs_different_base_fails() {
        assertUnifyFails(intTS(), blobTS());
      }

      @ParameterizedTest
      @MethodSource("typesToTest")
      public void base_vs_var_fails() {
        assertUnifyFails(varB(), blobTS());
      }

      @ParameterizedTest
      @MethodSource("typesToTest")
      public void base_vs_array_fails(TypeS type) {
        assertUnifyFails(type, arrayTS(type));
      }

      @ParameterizedTest
      @MethodSource("typesToTest")
      public void base_vs_tuple_fails(TypeS type) {
        assertUnifyFails(type, tupleTS(type));
      }

      @ParameterizedTest
      @MethodSource("typesToTest")
      public void base_vs_function_fails(TypeS type) {
        assertUnifyFails(type, funcTS(type));
        assertUnifyFails(type, funcTS(type, type));
      }

      @ParameterizedTest
      @MethodSource("typesToTest")
      public void base_vs_struct_fails(TypeS type) {
        assertUnifyFails(type, structTS("MyFunc"));
        assertUnifyFails(type, structTS("MyFunc", intTS()));
      }

      @Test
      public void var_vs_itself_succeeds() throws UnifierExc {
        unifier.unify(
            varA(),
            varA());
      }

      @Test
      public void var_vs_array_fails() {
        assertUnifyFails(
            varA(),
            arrayTS(varA()));
      }

      @Test
      public void var_vs_tuple_fails() {
        assertUnifyFails(
            varA(),
            tupleTS(varA()));
      }

      @Test
      public void var_vs_function_fails() {
        assertUnifyFails(
            varA(),
            funcTS(varA()));
      }

      @Test
      public void var_vs_struct_fails() {
        assertUnifyFails(
            varA(),
            structTS("MyStruct", varA()));
      }

      @Test
      public void var_vs_interface_fails() {
        assertUnifyFails(
            varA(),
            interfaceTS(varA()));
      }

      @Test
      public void array_vs_itself_succeeds() throws UnifierExc {
        unifier.unify(
            arrayTS(intTS()),
            arrayTS(intTS()));
      }

      @Test
      public void array_with_interface_vs_array_with_compatible_interface_succeeds()
          throws UnifierExc {
        unifier.unify(
            arrayTS(interfaceTS(sigS(intTS(), "myField"))),
            arrayTS(interfaceTS(sigS(stringTS(), "otherField"))));
      }

      @Test
      public void array2_vs_itself_succeeds() throws UnifierExc {
        unifier.unify(
            arrayTS(arrayTS(intTS())),
            arrayTS(arrayTS(intTS())));
      }

      @Test
      public void array_vs_array_with_different_element_fails() {
        assertUnifyFails(arrayTS(intTS()), arrayTS(blobTS()));
      }

      @Test
      public void array_vs_tuple_fails() {
        assertUnifyFails(arrayTS(intTS()), tupleTS(intTS()));
      }

      @Test
      public void array_vs_function_fails() {
        assertUnifyFails(arrayTS(intTS()), funcTS(intTS()));
      }

      @Test
      public void array_vs_struct_fails() {
        assertUnifyFails(arrayTS(intTS()), structTS("MyStruct", intTS()));
      }

      @Test
      public void array_vs_interface_fails() {
        assertUnifyFails(arrayTS(intTS()), interfaceTS(intTS()));
      }

      @Test
      public void tuple_vs_itself_succeeds() throws UnifierExc {
        unifier.unify(
            tupleTS(intTS(), blobTS()),
            tupleTS(intTS(), blobTS()));
      }

      @Test
      public void tuple_with_interface_vs_tuple_with_compatible_interface_succeeds()
          throws UnifierExc {
        unifier.unify(
            tupleTS(interfaceTS(sigS(intTS(), "myField"))),
            tupleTS(interfaceTS(sigS(stringTS(), "otherField"))));
      }

      @Test
      public void tuple2_vs_itself_succeeds() throws UnifierExc {
        unifier.unify(
            tupleTS(tupleTS(intTS(), blobTS())),
            tupleTS(tupleTS(intTS(), blobTS())));
      }

      @Test
      public void tuple_vs_tuple_with_different_elements_fails() {
        assertUnifyFails(tupleTS(intTS(), blobTS()), tupleTS(intTS(), stringTS()));
      }

      @Test
      public void tuple_vs_tuple_with_one_element_missing_fails() {
        assertUnifyFails(tupleTS(intTS(), blobTS()), tupleTS(intTS()));
      }

      @Test
      public void tuple_vs_function_fails() {
        assertUnifyFails(tupleTS(intTS()), funcTS(intTS()));
      }

      @Test
      public void tuple_vs_struct_fails() {
        assertUnifyFails(tupleTS(intTS()), structTS("MyStruct", intTS()));
      }

      @Test
      public void tuple_vs_interface_fails() {
        assertUnifyFails(tupleTS(intTS()), interfaceTS(intTS()));
      }

      @Test
      public void function_vs_itself_succeeds() throws UnifierExc {
        unifier.unify(
            funcTS(blobTS(), intTS()),
            funcTS(blobTS(), intTS()));
      }

      @Test
      public void function_with_interface_vs_function_with_compatible_interface_succeeds()
          throws UnifierExc {
        unifier.unify(
            funcTS(interfaceTS(sigS(intTS(), "myField"))),
            funcTS(interfaceTS(sigS(stringTS(), "otherField"))));
      }

      @Test
      public void function_with_result_being_function_vs_itself_succeeds() throws UnifierExc {
        unifier.unify(
            funcTS(funcTS(intTS())),
            funcTS(funcTS(intTS())));
      }

      @Test
      public void function_with_parameter_being_function_vs_itself_succeeds() throws UnifierExc {
        unifier.unify(
            funcTS(funcTS(intTS()), blobTS()),
            funcTS(funcTS(intTS()), blobTS()));
      }

      @Test
      public void function_vs_function_with_different_result_type_fails() {
        assertUnifyFails(funcTS(intTS()), funcTS(blobTS()));
      }

      @Test
      public void function_vs_function_with_different_parameter_fails() {
        assertUnifyFails(funcTS(blobTS(), intTS()), funcTS(stringTS(), intTS()));
      }

      @Test
      public void function_vs_function_with_one_parameter_missing_fails() {
        assertUnifyFails(funcTS(blobTS(), intTS()), funcTS(intTS()));
      }

      @Test
      public void function_vs_struct_fails() {
        assertUnifyFails(funcTS(intTS()), structTS("MyStruct", intTS()));
      }

      @Test
      public void function_vs_interface_fails() {
        assertUnifyFails(funcTS(intTS()), interfaceTS(intTS()));
      }

      @Test
      public void struct_vs_itself_succeeds() throws UnifierExc {
        unifier.unify(
            structTS("MyStruct", intTS(), blobTS()),
            structTS("MyStruct", intTS(), blobTS()));
      }

      @Test
      public void struct_with_interface_vs_struct_with_compatible_interface_succeeds()
          throws UnifierExc {
        unifier.unify(
            structTS(interfaceTS(sigS(intTS(), "myField"))),
            structTS(interfaceTS(sigS(stringTS(), "otherField"))));
      }

      @Test
      public void struct_vs_itself_with_fields_reordered_fails() {
        assertUnifyFails(
            structTS("MyStruct", sigS(intTS(), "myIntField"), sigS(blobTS(), "myBlobField")),
            structTS("MyStruct", sigS(blobTS(), "myBlobField"), sigS(intTS(), "myIntField")));
      }

      @Test
      public void struct_vs_struct_with_different_name_fails() {
        assertUnifyFails(
            structTS("MyStruct", intTS(), blobTS()),
            structTS("MyStruct2", intTS(), blobTS()));
      }

      @Test
      public void struct_vs_struct_with_different_field_type_fails() {
        assertUnifyFails(
            structTS("MyStruct", intTS(), blobTS()),
            structTS("MyStruct", intTS(), stringTS()));
      }

      @Test
      public void struct_vs_struct_with_different_field_name_fails() {
        assertUnifyFails(
            structTS("MyStruct", sigS(intTS(), "myField")),
            structTS("MyStruct", sigS(intTS(), "otherField")));
      }

      @Test
      public void struct_vs_matching_interface_succeeds() throws UnifierExc {
        unifier.unify(
            structTS("MyStruct", sigS(intTS(), "myField")),
            interfaceTS(sigS(intTS(), "myField")));
      }

      @Test
      public void struct_vs_matching_interface_with_changed_field_type_fails() {
        assertUnifyFails(
            structTS("MyStruct", sigS(intTS(), "myField")),
            interfaceTS(sigS(blobTS(), "myField")));
      }

      @Test
      public void struct_vs_matching_interface_with_changed_field_name_fails() {
        assertUnifyFails(
            structTS("MyStruct", sigS(intTS(), "myField")),
            interfaceTS(sigS(intTS(), "otherField")));
      }

      @Test
      public void interface_vs_itself_succeeds() throws UnifierExc {
        unifier.unify(
            interfaceTS(intTS(), blobTS()),
            interfaceTS(intTS(), blobTS()));
      }

      @Test
      public void interface_vs_interface_with_different_field_type_fails() {
        assertUnifyFails(
            interfaceTS(sigS(intTS(), "myField")),
            interfaceTS(sigS(blobTS(), "myField")));
      }

      @Test
      public void interface_vs_interface_with_different_field_name_succeeds() throws UnifierExc {
        assertUnifyThroughTemp(
            interfaceTS(sigS(intTS(), "myField")),
            interfaceTS(sigS(intTS(), "otherField")),
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
        assertUnifyThroughTemp(
            arrayTS(type1),
            arrayTS(type2),
            arrayTS(expected)
        );
      }

      @ParameterizedTest
      @ArgumentsSource(LegalFieldSetMergesProvider.class)
      public void tuple_element(FieldSetTS type1, FieldSetTS type2, TypeS expected)
          throws UnifierExc {
        assertUnifyThroughTemp(
            tupleTS(type1),
            tupleTS(type2),
            tupleTS(expected)
        );
      }

      @ParameterizedTest
      @ArgumentsSource(LegalFieldSetMergesProvider.class)
      public void func_result(FieldSetTS type1, FieldSetTS type2, TypeS expected)
          throws UnifierExc {
        assertUnifyThroughTemp(
            funcTS(type1),
            funcTS(type2),
            funcTS(expected)
        );
      }

      @ParameterizedTest
      @ArgumentsSource(LegalFieldSetMergesProvider.class)
      public void func_param(FieldSetTS type1, FieldSetTS type2, TypeS expected)
          throws UnifierExc {
        assertUnifyThroughTemp(
            funcTS(type1, intTS()),
            funcTS(type2, intTS()),
            funcTS(expected, intTS())
        );
      }

      @ParameterizedTest
      @ArgumentsSource(LegalFieldSetMergesProvider.class)
      public void struct_field(FieldSetTS type1, FieldSetTS type2, TypeS expected)
          throws UnifierExc {
        assertUnifyThroughTemp(
            structTS(type1),
            structTS(type2),
            structTS(expected)
        );
      }

      @ParameterizedTest
      @ArgumentsSource(LegalFieldSetMergesProvider.class)
      public void interface_field(FieldSetTS type1, FieldSetTS type2, TypeS expected)
          throws UnifierExc {
        assertUnifyThroughTemp(
            interfaceTS(type1),
            interfaceTS(type2),
            interfaceTS(expected)
        );
      }
    }

    @Nested
    class _illegal_field_set_merges {
      @ParameterizedTest
      @ArgumentsSource(IllegalFieldSetMergesProvider.class)
      public void array_element(FieldSetTS type1, FieldSetTS type2) {
        assertUnifyFails(
            arrayTS(type1),
            arrayTS(type2)
        );
      }

      @ParameterizedTest
      @ArgumentsSource(IllegalFieldSetMergesProvider.class)
      public void tuple_element(FieldSetTS type1, FieldSetTS type2) {
        assertUnifyFails(
            tupleTS(type1),
            tupleTS(type2)
        );
      }

      @ParameterizedTest
      @ArgumentsSource(IllegalFieldSetMergesProvider.class)
      public void func_result(FieldSetTS type1, FieldSetTS type2) {
        assertUnifyFails(
            funcTS(type1),
            funcTS(type2)
        );
      }

      @ParameterizedTest
      @ArgumentsSource(IllegalFieldSetMergesProvider.class)
      public void func_param(FieldSetTS type1, FieldSetTS type2) {
        assertUnifyFails(
            funcTS(type1, intTS()),
            funcTS(type2, intTS())
        );
      }

      @ParameterizedTest
      @ArgumentsSource(IllegalFieldSetMergesProvider.class)
      public void struct_field(FieldSetTS type1, FieldSetTS type2) {
        assertUnifyFails(
            structTS(type1),
            structTS(type2)
        );
      }

      @ParameterizedTest
      @ArgumentsSource(IllegalFieldSetMergesProvider.class)
      public void interface_field(FieldSetTS type1, FieldSetTS type2) {
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
    @Test
    public void one_elem_cycle_through_array_elem() {
      assertUnifyFails(varA(), arrayTS(varA()));
    }

    @Test
    public void two_elem_cycle_through_array_elem() throws UnifierExc {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      unifier.unify(a, arrayTS(b));
      assertUnifyFails(b, arrayTS(a));
    }

    @Test
    public void one_elem_cycle_through_tuple_elem() {
      assertUnifyFails(varA(), tupleTS(varA()));
    }

    @Test
    public void two_elem_cycle_through_tuple_elem() throws UnifierExc {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      unifier.unify(a, tupleTS(b));
      assertUnifyFails(b, tupleTS(a));
    }

    @Test
    public void one_elem_cycle_through_func_res() {
      assertUnifyFails(varA(), funcTS(varA()));
    }

    @Test
    public void two_elem_cycle_through_func_res() throws UnifierExc {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      unifier.unify(a, funcTS(b));
      assertUnifyFails(b, funcTS(a));
    }

    @Test
    public void one_elem_cycle_through_func_param() {
      assertUnifyFails(varA(), funcTS(varA(), intTS()));
    }

    @Test
    public void two_elem_cycle_through_func_param() throws UnifierExc {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      unifier.unify(a, funcTS(b, intTS()));
      assertUnifyFails(b, funcTS(a, intTS()));
    }

    @Test
    public void regression_test() throws UnifierExc {
      // Cycle detection algorithm had a bug which is detected by this test.
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      var c = unifier.newTempVar();
      unifier.unify(a, arrayTS(b));
      unifier.unify(c, funcTS(b, a));
    }
  }

  @Nested
  class _complex_cases {
    @Nested
    class _transitive_cases {
      @Test
      public void unify_a_with_b_unified_with_concrete_type() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        unifier.unify(a, b);
        unifier.unify(b, intTS());
        assertThat(unifier.resolve(a))
            .isEqualTo(intTS());
      }

      @Test
      public void unify_a_with_array_b_unified_with_c() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var c = unifier.newTempVar();
        unifier.unify(a, arrayTS(b));
        unifier.unify(arrayTS(b), c);
        assertThat(unifier.resolve(a))
            .isEqualTo(unifier.resolve(c));
      }

      @Test
      public void unify_array_a_with_b_unified_with_array_c() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var c = unifier.newTempVar();
        unifier.unify(arrayTS(a), b);
        unifier.unify(b, arrayTS(c));
        assertThat(unifier.resolve(a))
            .isEqualTo(unifier.resolve(c));
      }
    }

    @Nested
    class _join_separate_unified_groups {
      @Test
      public void join_array_of_x_with_array_of_y() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var x = unifier.newTempVar();
        var y = unifier.newTempVar();
        unifier.unify(a, arrayTS(x));
        unifier.unify(b, arrayTS(y));
        unifier.unify(a, b);
        assertThat(unifier.resolve(x))
            .isEqualTo(unifier.resolve(y));
      }

      @Test
      public void join_array_of_x_with_array_of_int() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var x = unifier.newTempVar();
        unifier.unify(a, arrayTS(x));
        unifier.unify(b, arrayTS(intTS()));
        unifier.unify(a, b);
        assertThat(unifier.resolve(x))
            .isEqualTo(intTS());
      }

      @Test
      public void join_array_of_int_with_array_of_blob_fails() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        unifier.unify(a, arrayTS(intTS()));
        unifier.unify(b, arrayTS(blobTS()));
        assertCall(() -> unifier.unify(a, b))
            .throwsException(UnifierExc.class);
      }

      @Test
      public void join_tuple_of_x_with_tuple_of_y() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var x = unifier.newTempVar();
        var y = unifier.newTempVar();
        unifier.unify(a, tupleTS(x));
        unifier.unify(b, tupleTS(y));
        unifier.unify(a, b);
        assertThat(unifier.resolve(x))
            .isEqualTo(unifier.resolve(y));
      }

      @Test
      public void join_tuple_of_x_with_tuple_of_int() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var x = unifier.newTempVar();
        unifier.unify(a, tupleTS(x));
        unifier.unify(b, tupleTS(intTS()));
        unifier.unify(a, b);
        assertThat(unifier.resolve(x))
            .isEqualTo(intTS());
      }

      @Test
      public void join_tuple_of_int_with_tuple_of_blob_fails() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        unifier.unify(a, tupleTS(intTS()));
        unifier.unify(b, tupleTS(blobTS()));
        assertCall(() -> unifier.unify(a, b))
            .throwsException(UnifierExc.class);
      }

      @Test
      public void join_func_with_res_x_with_func_with_res_y() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var x = unifier.newTempVar();
        var y = unifier.newTempVar();
        unifier.unify(a, funcTS(x));
        unifier.unify(b, funcTS(y));
        unifier.unify(a, b);
        assertThat(unifier.resolve(x))
            .isEqualTo(unifier.resolve(y));
      }

      @Test
      public void join_func_with_res_x_with_func_with_res_int() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var x = unifier.newTempVar();
        unifier.unify(a, funcTS(x));
        unifier.unify(b, funcTS(intTS()));
        unifier.unify(a, b);
        assertThat(unifier.resolve(x))
            .isEqualTo(intTS());
      }

      @Test
      public void join_func_with_res_int_with_func_with_res_blob_fails() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        unifier.unify(a, funcTS(intTS()));
        unifier.unify(b, funcTS(blobTS()));
        assertCall(() -> unifier.unify(a, b))
            .throwsException(UnifierExc.class);
      }

      @Test
      public void join_func_with_param_x_with_func_with_param_y() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var x = unifier.newTempVar();
        var y = unifier.newTempVar();
        unifier.unify(a, funcTS(x, intTS()));
        unifier.unify(b, funcTS(y, intTS()));
        unifier.unify(a, b);
        assertThat(unifier.resolve(x))
            .isEqualTo(unifier.resolve(y));
      }

      @Test
      public void join_func_with_param_x_with_func_with_param_int() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        var x = unifier.newTempVar();
        unifier.unify(a, funcTS(x, intTS()));
        unifier.unify(b, funcTS(intTS(), intTS()));
        unifier.unify(a, b);
        assertThat(unifier.resolve(x))
            .isEqualTo(intTS());
      }

      @Test
      public void join_func_with_param_int_with_func_with_param_blob_fails() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        unifier.unify(a, funcTS(intTS(), intTS()));
        unifier.unify(b, funcTS(blobTS(), intTS()));
        assertCall(() -> unifier.unify(a, b))
            .throwsException(UnifierExc.class);
      }

      @Test
      public void join_func_with_func_with_different_param_count_fails() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        unifier.unify(a, funcTS(intTS(), intTS()));
        unifier.unify(b, funcTS(intTS()));
        assertCall(() -> unifier.unify(a, b))
            .throwsException(UnifierExc.class);
      }

      @Test
      public void join_array_with_tuple_fails() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        unifier.unify(a, arrayTS(intTS()));
        unifier.unify(b, tupleTS(intTS()));
        assertCall(() -> unifier.unify(a, b))
            .throwsException(UnifierExc.class);
      }

      @Test
      public void join_array_with_func_fails() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        unifier.unify(a, arrayTS(intTS()));
        unifier.unify(b, funcTS(intTS()));
        assertCall(() -> unifier.unify(a, b))
            .throwsException(UnifierExc.class);
      }

      @Test
      public void join_tuple_with_func_fails() throws UnifierExc {
        var a = unifier.newTempVar();
        var b = unifier.newTempVar();
        unifier.unify(a, tupleTS(intTS()));
        unifier.unify(b, funcTS(intTS()));
        assertCall(() -> unifier.unify(a, b))
            .throwsException(UnifierExc.class);

      }
    }
  }

  @Nested
  class _temporary_vars {
    @Test
    public void resolve_unknown_temp_var_causes_exception() {
      assertCall(() -> unifier.resolve(tempVarA()))
          .throwsException(new IllegalStateException("Unknown temp var `A`."));
    }

    @Test
    public void non_temporary_var_has_priority_over_temporary() throws UnifierExc {
      VarS a = unifier.newTempVar();
      VarS b = unifier.newTempVar();
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
          .throwsException(new IllegalStateException("Unknown temp var `A`."));
    }

    @Test
    public void array_type() throws UnifierExc {
      var temp = unifier.newTempVar();
      unifier.unify(temp, intTS());
      assertThat(unifier.resolve(arrayTS(temp)))
          .isEqualTo(arrayTS(intTS()));
    }

    @Test
    public void tuple_type() throws UnifierExc {
      var temp = unifier.newTempVar();
      unifier.unify(temp, intTS());
      assertThat(unifier.resolve(tupleTS(temp)))
          .isEqualTo(tupleTS(intTS()));
    }

    @Test
    public void func_type() throws UnifierExc {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      unifier.unify(a, intTS());
      unifier.unify(b, boolTS());
      assertThat(unifier.resolve(funcTS(b, a)))
          .isEqualTo(funcTS(boolTS(), intTS()));
    }

    @Test
    public void struct_type() throws UnifierExc {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      unifier.unify(a, intTS());
      unifier.unify(b, boolTS());
      assertThat(unifier.resolve(structTS(a, b)))
          .isEqualTo(structTS(intTS(), boolTS()));
    }

    @Test
    public void interface_type() throws UnifierExc {
      var a = unifier.newTempVar();
      var b = unifier.newTempVar();
      unifier.unify(a, intTS());
      unifier.unify(b, boolTS());
      assertThat(unifier.resolve(interfaceTS(a, b)))
          .isEqualTo(interfaceTS(intTS(), boolTS()));
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

  private void assertUnifyInfers(Unifier unifier, TypeS type1, TypeS type2,
      TypeS unresolved, TypeS expected) throws UnifierExc {
    unifier.unify(type1, type2);
    assertThat(unifier.resolve(unresolved))
        .isEqualTo(expected);
  }

  private void assertUnifyInfersEquality(Unifier unifier, TypeS type1, TypeS type2,
      TypeS unresolved1, TypeS unresolved2) throws UnifierExc {
    unifier.unify(type1, type2);
    assertThat(unifier.resolve(unresolved1))
        .isEqualTo(unifier.resolve(unresolved2));
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

  private void assertUnifyThroughTemp(TypeS type1, TypeS type2, TypeS expected) throws UnifierExc {
    assertUnifyThroughTempImpl(type1, type2, expected);
    assertUnifyThroughTempImpl(type2, type1, expected);
  }

  private static void assertUnifyThroughTempImpl(TypeS type1, TypeS type2, TypeS expected)
      throws UnifierExc {
    var unifier = new Unifier();
    var temp = unifier.newTempVar();
    unifier.unify(temp, type1);
    unifier.unify(temp, type2);
    assertThat(unifier.resolve(temp))
        .isEqualTo(expected);
  }

  private void assertUnifyFails(TypeS type1, TypeS type2) {
    assertCall(() -> unifier.unify(type1, type2))
        .throwsException(UnifierExc.class);
    assertCall(() -> unifier.unify(type2, type1))
        .throwsException(UnifierExc.class);
  }
}
