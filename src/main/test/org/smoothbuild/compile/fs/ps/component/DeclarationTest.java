package org.smoothbuild.compile.fs.ps.component;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.testing.TestingModLoader.err;
import static org.smoothbuild.testing.type.TestedTSF.TESTED_TYPES;
import static org.smoothbuild.util.Strings.unlines;
import static org.smoothbuild.util.collect.NList.nlist;

import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.type.TestedTS;
import org.smoothbuild.util.collect.NList;

public class DeclarationTest extends TestContext {
  @Nested
  class _members {
    @Nested
    class _annotation {
      @Test
      public void with_unknown_name_causes_error() {
        module("""
            @UnknownAnnotation("value")
            Int myFunc() = 3;
            """)
            .loadsWithError(1, "Unknown annotation `UnknownAnnotation`.");
      }
    }

    @Nested
    class _struct {
      @Test
      public void declaring_empty_struct_is_allowed() {
        module("MyStruct()")
            .loadsWithSuccess();
      }

      @Test
      public void declaring_non_empty_struct_is_allowed() {
        var code = """
            MyStruct(
              String fieldA,
              String fieldB
            )
            """;
        module(code)
            .loadsWithSuccess();
      }

      @Nested
      class _name {
        @Test
        public void that_is_normal_name() {
          module("MyStruct()")
              .loadsWithSuccess();
        }

        @Test
        public void that_is_illegal_fails() {
          module("MyStruct^()")
              .loadsWithError(1, """
            token recognition error at: '^'
            MyStruct^()
                    ^""");
        }

        @Test
        public void that_starts_with_small_letter_fails() {
          module("myStruct()")
              .loadsWithError(1, "`myStruct` is illegal struct name. "
                  + "Struct name must start with uppercase letter.");
        }

        @Test
        public void that_is_single_capital_letter_fails() {
          module("A()")
              .loadsWithError(1, "`A` is illegal struct name. "
                  + "All-uppercase names are reserved for type variables in generic types.");
        }

        @Test
        public void that_is_underscore_fails() {
          module("_()")
              .loadsWithError(1, "`_` is illegal struct name. `_` is reserved for future use.");
        }

        @Test
        public void that_is_multiple_capital_letters_fails() {
          module("ABC()")
              .loadsWithError(1, "`ABC` is illegal struct name. "
                  + "All-uppercase names are reserved for type variables in generic types.");
        }
      }

      @Nested
      class _field {
        @Nested
        class _type {
          @ParameterizedTest
          @ArgumentsSource(TestedTypes.class)
          public void can_be_monotype(TestedTS testedT) {
            module(unlines(
                testedT.typeDeclarationsAsString(),
                "MyStruct(",
                "  " + testedT.name() + " field,",
                ")"))
                .loadsWithSuccess();
          }

          @Test
          public void cannot_be_polytype() {
            var code = """
                MyStruct(
                 (B)->A field
                )
                """;
            module(code)
                .loadsWithError(
                    2, "Field type cannot be polymorphic. Found field `field` with type `(B)->A`.");
          }

          @Test
          public void cannot_be_polytype_regression_test() {
            // Verify that illegal field type does not cause error during processing of code that
            // references field's struct.
            var code = """
                MyStruct(
                  (B)->A field
                )
                @Native("impl")
                MyStruct myFunction();
                """;
            module(code)
                .loadsWithError(
                    2, "Field type cannot be polymorphic. Found field `field` with type `(B)->A`.");
          }

          @Test
          public void cannot_be_polytype_array() {
            var code = """
                MyStruct(
                  [A] field
                )
                """;
            module(code)
                .loadsWithError(
                    2, "Field type cannot be polymorphic. Found field `field` with type `[A]`.");
          }

          @Test
          public void cannot_be_type_which_encloses_it() {
            var code = """
                MyStruct(
                  MyStruct field
                )
                """;
            module(code)
                .loadsWithError("""
                  Type hierarchy contains cycle:
                  myBuild.smooth:2: MyStruct ~> MyStruct""");
          }

          @Test
          public void cannot_be_array_type_which_elem_type_encloses_it() {
            var code = """
                MyStruct(
                  String firstField,
                  [MyStruct] field
                )
                """;
            module(code)
                .loadsWithError("""
                  Type hierarchy contains cycle:
                  myBuild.smooth:3: MyStruct ~> MyStruct""");
          }

          @Test
          public void cannot_declare_func_which_result_type_encloses_it() {
            var code = """
                MyStruct(
                  ()->MyStruct field
                )
                """;
            module(code)
                .loadsWithError("""
                  Type hierarchy contains cycle:
                  myBuild.smooth:2: MyStruct ~> MyStruct""");
          }

          @Test
          public void cannot_declare_func_which_param_type_encloses_it() {
            var code = """
                MyStruct(
                  (MyStruct)->Blob field
                )
                """;
            module(code)
                .loadsWithError("""
                  Type hierarchy contains cycle:
                  myBuild.smooth:2: MyStruct ~> MyStruct""");
          }
        }

        @Nested
        class _name {
          @Test
          public void that_is_legal() {
            module("""
             MyStruct(
               String field
             )
             """)
                .loadsWithSuccess();
          }

          @Test
          public void that_is_illegal_fails() {
            module("""
             MyStruct(
               String field^
             )
             """)
                .loadsWithError(2, """
              token recognition error at: '^'
                String field^
                            ^""");
          }

          @Test
          public void that_starts_with_large_letter_fails() {
            module("""
             MyStruct(
               String Field
             )
             """)
                .loadsWithError(2,
                    "`Field` is illegal identifier name. Identifiers should start with lowercase.");
          }

          @Test
          public void that_is_single_large_letter_fails() {
            module("""
             MyStruct(
               String A
             )
             """)
                .loadsWithError(2,
                    "`A` is illegal identifier name. Identifiers should start with lowercase.");
          }

          @Test
          public void that_is_single_underscore_fails() {
            module("""
             MyStruct(
               String _
             )
             """)
                .loadsWithError(2,
                    "`_` is illegal identifier name. `_` is reserved for future use.");
          }
        }

        @Nested
        class _default_value {
          @Test
          public void is_illegal() {
            module("""
             MyStruct(
               Int myField = 7
             )
             """)
                .loadsWithError(2, "Struct field `myField` has default value. "
                    + "Only function parameters can have default value.");
          }
        }
      }

      @Nested
      class _field_list {
        @Test
        public void can_have_trailing_comma() {
          module(structDeclaration("String field,"))
              .loadsWithSuccess()
              .containsType(TestContext.structTS("MyStruct", NList.nlist(sigS(stringTS(), "field"))));
        }

        @Test
        public void cannot_have_only_comma() {
          module(structDeclaration(","))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_leading_comma() {
          module(structDeclaration(", String field"))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_two_trailing_commas() {
          module(structDeclaration("String field,,"))
              .loadsWithProblems();
        }

        private String structDeclaration(String string) {
          return """
              MyStruct( PLACEHOLDER )
              """.replace("PLACEHOLDER", string);
        }
      }
    }

    @Nested
    class _value {
      @Nested
      class _type {
        @Test
        public void can_be_omitted() {
          module("""
            myValue = "abc";
            """)
              .loadsWithSuccess();
        }

        @ParameterizedTest
        @ArgumentsSource(TestedTypes.class)
        public void can_be_monotype(TestedTS type) {
          module(unlines(
              "@Native(\"Impl.met\")",
              type.name() + " myFunc();",
              type.name() + " myValue = myFunc();",
              type.typeDeclarationsAsString()))
              .loadsWithSuccess();
        }

        @Test
        public void can_be_polytype() {
          var code = """
              [A] myValue = [];
          """;
          module(code)
              .loadsWithSuccess();
        }

        @Test
        public void can_be_polytype_assigned_from_func() {
          var code = """
              A myId(A a) = a;
              (A)->A myValue = myId;
          """;
          module(code)
              .loadsWithSuccess();
        }

        @Test
        public void can_be_monotype_function_assigned_from_polytype_func() {
          var code = """
              @Native("Impl.met")
              A myId(A param);
              (Int)->Int myValue = myId;
          """;
          module(code)
              .loadsWithSuccess();
        }
      }

      @Nested
      class _name {
        @Test
        public void that_is_legal() {
          module("""
             myValue = "abc";
             """)
              .loadsWithSuccess();
        }

        @Test
        public void that_is_illegal_fails() {
          module("""
             myValue^ = "abc";
             """)
              .loadsWithError(1, """
            token recognition error at: '^'
            myValue^ = "abc";
                   ^""");
        }

        @Test
        public void that_starts_with_large_letter_fails() {
          module("""
             MyValue = "abc";
             """)
              .loadsWithError(1,
                  "`MyValue` is illegal identifier name. Identifiers should start with lowercase.");
        }

        @Test
        public void that_is_single_large_letter_fails() {
          module("""
             A = "abc";
             """)
              .loadsWithError(1,
                  "`A` is illegal identifier name. Identifiers should start with lowercase.");
        }

        @Test
        public void that_is_single_underscore_fails() {
          module("""
             _ = "abc";
             """)
              .loadsWithError(1, "`_` is illegal identifier name. `_` is reserved for future use.");
        }
      }

      @Test
      public void without_body_fails() {
        module("""
            String result;
            """)
            .loadsWithError(1, "Value cannot have empty body.");
      }

      @Test
      public void with_bytecode_ann_and_body_fails() {
        module("""
            @Bytecode("implementation")
            String result = "abc";
            """)
            .loadsWithError(2, "Value with @Bytecode annotation cannot have body.");
      }

      @Test
      public void with_bytecode_ann_and_without_res_type_fails() {
        module("""
            @Bytecode("implementation")
            result;
            """)
            .loadsWithError(2, "Value `result` with @Bytecode annotation must declare type.");
      }

      @Test
      public void with_native_ann_and_body_fails() {
        module("""
            @Native("implementation")
            String result = "abc";
            """)
            .loadsWithError(1, "Value cannot have @Native annotation.");
      }

      @Test
      public void with_native_ann_and_without_body_fails() {
        module("""
            @Native("implementation")
            String result;
            """)
            .loadsWithError(1, "Value cannot have @Native annotation.");
      }

      @Test
      public void with_native_impure_ann_and_body_fails() {
        module("""
            @NativeImpure("implementation")
            String result = "abc";
            """)
            .loadsWithError(1, "Value cannot have @NativeImpure annotation.");
      }

      @Test
      public void with_native_impure_ann_and_without_body_fails() {
        module("""
            @NativeImpure("implementation")
            String result;
            """)
            .loadsWithError(1, "Value cannot have @NativeImpure annotation.");
      }

      @Test
      public void with_unknown_ann_and_body_fails() {
        module("""
            @Unknown("implementation")
            String result = "abc";
            """)
            .loadsWithError(1, "Unknown annotation `Unknown`.");
      }

      @Test
      public void with_unknown_ann_and_without_body_fails() {
        module("""
            @Unknown("implementation")
            String result;
            """)
            .loadsWithError(1, "Unknown annotation `Unknown`.");
      }
    }

    @Nested
    class _func {
      @Test
      public void without_body_fails() {
        module("""
          String myFunc();
          """)
            .loadsWithError(1, "Function body is missing.");
      }

      @Test
      public void with_unknown_ann_with_body_fails() {
        module("""
          @Unknown("abc")
          String myFunc() = "abc";
          """)
            .loadsWithError(1, "Unknown annotation `Unknown`.");
      }

      @Test
      public void with_unknown_ann_without_body_fails() {
        module("""
          @Unknown("abc")
          String myFunc();
          """)
            .loadsWithError(1, "Unknown annotation `Unknown`.");
      }

      @Test
      public void with_native_ann_and_with_body_fails() {
        module("""
          @Native("Impl.met")
          String myFunc() = "abc";
          """)
            .loadsWithError(2, "Function `myFunc` with @Native annotation cannot have body.");
      }

      @Test
      public void with_native_ann_without_declared_result_type_fails() {
        module("""
          @Native("Impl.met")
          myFunc();
          """)
            .loadsWithError(2,
                "Function `myFunc` with @Native annotation must declare result type.");
      }

      @Test
      public void with_bytecode_ann_and_with_body_fails() {
        module("""
          @Bytecode("Impl.met")
          String myFunc() = "abc";
          """)
            .loadsWithError(2, "Function `myFunc` with @Bytecode annotation cannot have body.");
      }

      @Test
      public void with_bytecode_ann_and_without_declared_result_type_fails() {
        module("""
          @Bytecode("Impl.met")
          myFunc();
          """)
            .loadsWithError(2,
                "Function `myFunc` with @Bytecode annotation must declare result type.");
      }

      @Nested
      class _result {
        @Nested
        class _type {
          @Test
          public void result_type_can_be_omitted() {
            module("""
            myFunc() = "abc";
            """)
                .loadsWithSuccess();
          }

          @ParameterizedTest
          @ArgumentsSource(TestedTypes.class)
          public void can_be_monotype(TestedTS type) {
            module(unlines(
                "@Native(\"impl\")",
                type.name() + " myFunc();",
                type.declarationsAsString()))
                .loadsWithSuccess();
          }

          @Test
          public void can_contain_var_not_present_in_any_param_type() {
            var code = """
                @Native("Impl.met")
                A myFunc(B b, C c);
                """;
            module(code)
                .loadsWithSuccess();
          }

          @Test
          public void can_contain_var_that_is_present_in_some_param_type() {
            var code = """
                @Native("Impl.met")
                A myFunc(A a);
                """;
            module(code)
                .loadsWithSuccess();
          }
        }
      }

      @Nested
      class _name {
        @Test
        public void that_is_legal() {
          module("""
             myFunc() = "abc";
             """)
              .loadsWithSuccess();
        }

        @Test
        public void that_is_illegal_fails() {
          module("""
             myFunc^() = "abc";
             """)
              .loadsWithError(1, """
            token recognition error at: '^'
            myFunc^() = "abc";
                  ^""");
        }

        @Test
        public void that_starts_with_large_letter_fails() {
          module("""
             MyFunc() = "abc";
             """)
              .loadsWithError(1,
                  "`MyFunc` is illegal identifier name. Identifiers should start with lowercase.");
        }

        @Test
        public void that_is_single_large_letter_fails() {
          module("""
             A() = "abc";
             """)
              .loadsWithError(1,
                  "`A` is illegal identifier name. Identifiers should start with lowercase.");
        }

        @Test
        public void that_is_single_underscore_fails() {
          module("""
             _() = "abc";
             """)
              .loadsWithError(1, "`_` is illegal identifier name. `_` is reserved for future use.");
        }
      }

      @Nested
      class _param {
        @Nested
        class _type {
          @ParameterizedTest
          @ArgumentsSource(TestedTypes.class)
          public void can_be_monotype(TestedTS type) {
            module(unlines(
                "@Native(\"Impl.met\")",
                "String myFunc(" + type.name() + " param);",
                type.typeDeclarationsAsString()))
                .loadsWithSuccess();
          }

          @Test
          public void can_be_polytype() {
            var code = """
                @Native("Impl.met")
                String myFunc((A)->A f);
                """;
            module(code)
                .loadsWithSuccess();
          }

          @Test
          public void can_be_single_var_polytype() {
            var code = """
                @Native("Impl.met")
                String myFunc((A)->Int f);
                """;
            module(code)
                .loadsWithSuccess();
          }
        }

        @Nested
        class _name {
          @Test
          public void that_is_legal() {
            module("""
             String myFunc(String name) = "abc";
             """)
                .loadsWithSuccess();
          }

          @Test
          public void that_is_illegal_fails() {
            module("""
             String myFunc(String name^);
             """)
                .loadsWithError(1, """
              token recognition error at: '^'
              String myFunc(String name^);
                                       ^""");
          }

          @Test
          public void that_starts_with_large_letter_fails() {
            module("""
             Int myFunc(Int Name) = 7;
             """)
                .loadsWithError(1,
                    "`Name` is illegal identifier name. Identifiers should start with lowercase.");
          }

          @Test
          public void that_is_single_large_letter_fails() {
            module("""
             Int myFunc(Int A) = 7;
             """)
                .loadsWithError(1,
                    "`A` is illegal identifier name. Identifiers should start with lowercase.");
          }

          @Test
          public void that_is_single_underscore_fails() {
            module("""
             Int myFunc(Int _) = 7;
             """)
                .loadsWithError(
                    1, "`_` is illegal identifier name. `_` is reserved for future use.");
          }
        }

        @Test
        public void default_param_before_non_default_is_allowed() {
          var code = """
              @Native("Impl.met")
              String myFunc(
                String default = "value",
                String nonDefault);
              """;
          var myFuncParams = nlist(
              itemS(3, stringTS(), "default", valueS(3, "myFunc:default", stringS(3, "value"))),
              itemS(4, stringTS(), "nonDefault"));
          var ann = nativeAnnotationS(1, stringS(1, "Impl.met"));
          var myFunc = annotatedFuncS(2, ann, stringTS(), "myFunc", myFuncParams);
          module(code)
              .loadsWithSuccess()
              .containsEvaluable(myFunc);
        }

        @Test
        public void param_with_vars_can_have_default_value() {
          module("""
              A myFunc(A value = "abc") = value;
              """)
              .loadsWithSuccess();
        }

        @Test
        public void default_arg_gets_converted_to_param_type() {
          module("""
              [String] myFunc(String param1, [String] param2 = []) = param2;
              [String] result = myFunc("abc");
              """)
              .loadsWithSuccess();
        }
      }

      @Nested
      class _param_list {
        @Test
        public void can_have_trailing_comma() {
          module(funcDeclaration("String param1,"))
              .loadsWithSuccess()
              .containsEvaluable(
                  funcS(1, stringTS(), "myFunc", nlist(itemS(1, stringTS(), "param1")),
                      stringS(1, "abc")));
        }

        @Test
        public void cannot_have_only_comma() {
          module(funcDeclaration(","))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_leading_comma() {
          module(funcDeclaration(",String string"))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_two_trailing_commas() {
          module(funcDeclaration("String string,,"))
              .loadsWithProblems();
        }

        private String funcDeclaration(String string) {
          return """
              String myFunc(PLACEHOLDER) = "abc";
              """.replace("PLACEHOLDER", string);
        }
      }
    }
  }

  @Nested
  class _expressions {
    @Nested
    class _call {
      @Nested
      class _func {
        @Test
        public void passing_more_positional_args_than_params_causes_error() {
          String code = """
              myIdentity(String param) = param;
              result = myIdentity("abc", "def");
              """;
          module(code)
              .loadsWithError(2, "Illegal call.");
        }

        @Test
        public void passing_less_positional_args_than_params_causes_error() {
          String code = """
              returnFirst(String param1, String param2) = param1;
              result = returnFirst("abc");
              """;
          module(code)
              .loadsWithError(2, "Parameter `param2` must be specified.");
        }

        @Test
        public void passing_less_positional_args_than_params_causes_error_version_without_name() {
          String code = """
              returnFirst(String param1, String param2) = param1;
              funcValue = returnFirst;
              result = funcValue("abc");
              """;
          module(code)
              .loadsWithError(3, "Illegal call.");
//              .loadsWithError(3, "Too few parameters. Expected 2 found 1.");
        }

        @Test
        public void named_arg_which_doesnt_exist_causes_error() {
          String code = """
              myIdentity(String param) = param;
              result = myIdentity(wrongName="abc");
              """;
          module(code)
              .loadsWithError(2, "Unknown parameter `wrongName`.");
        }

        @Test
        public void named_args_can_be_passed_in_the_same_order_as_params() {
          String code = """
              returnFirst(String param1, String param2) = param1;
              result = returnFirst(param1="abc", param2="def");
              """;
          module(code)
              .loadsWithSuccess();
        }

        @Test
        public void named_args_can_be_passed_in_different_order_than_params() {
          String code = """
              returnFirst(String param1, String param2) = param1;
              result = returnFirst(param2="def", param1="abc");
              """;
          module(code)
              .loadsWithSuccess();
        }

        @Test
        public void all_named_args_must_come_after_positional() {
          String code = """
              returnFirst(String param1, String param2) = param1;
              result = returnFirst(param2="def", "abc");
              """;
          module(code)
              .loadsWithError(2, "Positional arguments must be placed before named arguments.");
        }

        @Test
        public void assigning_arg_by_name_twice_causes_error() {
          String code = """
              myIdentity(String param) = param;
              result = myIdentity(param="abc", param="abc");
              """;
          module(code)
              .loadsWithError(2, "`param` is already assigned.");
        }

        @Test
        public void assigning_by_name_arg_that_is_assigned_by_position_causes_error() {
          String code = """
              myIdentity(String param) = param;
              result = myIdentity("abc", param="abc");
              """;
          module(code)
              .loadsWithError(2, "`param` is already assigned.");
        }

        @Test
        public void param_with_default_value_can_be_assigned_positionally() {
          String code = """
              myIdentity(String param1="abc", String param2="def") = param1;
              result = myIdentity("abc", "def");
              """;
          module(code)
              .loadsWithSuccess();
        }

        @Test
        public void param_with_default_value_can_be_assigned_by_name() {
          String code = """
            myIdentity(String param1="abc", String param2="def") = param1;
            result = myIdentity(param1="abc", param2="def");
            """;
          module(code)
              .loadsWithSuccess();
        }

        @Test
        public void anonymous_function_parameter_names_are_always_stripped() {
          String code = """
            myFunc(String param) = param;
            valueReferencingFunc = myFunc;
            result = ((Int int) -> int)(param=7);
            """;
          module(code)
              .loadsWithError(3, "Unknown parameter `param`.");
        }

        @Test
        public void named_function_parameter_names_are_stripped_during_assignment() {
          String code = """
            myFunc(String param) = param;
            valueReferencingFunc = myFunc;
            result = valueReferencingFunc(param="abc");
            """;
          module(code)
              .loadsWithError(3, "Unknown parameter `param`.");
        }

        @Test
        public void named_function_parameter_default_values_are_stripped_during_assignment() {
          String code = """
            myFunc(String param = "abc") = param;
            valueReferencingFunc = myFunc;
            result = valueReferencingFunc();
            """;
          module(code)
              .loadsWithError(3, "Illegal call.");
        }
      }

      @Nested
      class _ctor {
        @Test
        public void creating_empty_struct_instance_is_allowed() {
          var code = """
              MyStruct()
              result = MyStruct();
              """;
          module(code)
              .loadsWithSuccess();
        }

        @Test
        public void creating_non_empty_struct_is_allowed() {
          var code = """
              MyStruct(
                String field,
              )
              result = MyStruct("abc");
              """;
          module(code)
              .loadsWithSuccess();
        }

        @Test
        public void calling_ctor_without_all_params_causes_error() {
          var code = """
              MyStruct(
                String field,
              )
              result = MyStruct();
              """;
          module(code)
              .loadsWithError(4, "Parameter `field` must be specified.");
        }
      }

      @Nested
      class _arg_list {
        @Test
        public void can_have_trailing_comma() {
          module(funcCall("7,"))
              .loadsWithSuccess()
              .containsEvaluable(valueS(2, intTS(), "result",
                  TestContext.callS(2, TestContext.monoizeS(2, intIdFuncS()), intS(2, 7))));
        }

        @Test
        public void cannot_have_only_comma() {
          module(funcCall(","))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_leading_comma() {
          module(funcCall(",7"))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_two_trailing_commas() {
          module(funcCall("1,,"))
              .loadsWithProblems();
        }

        private String funcCall(String string) {
          return """
              Int myIntId(Int i) = i;
              result = myIntId(PLACEHOLDER);
              """.replace("PLACEHOLDER", string);
        }
      }
    }

    @Nested
    class _anonymous_function {
      @Test
      public void with_no_params() {
        var code = """
            result = () -> 7;
            """;
        module(code)
            .loadsWithSuccess();
      }

      @Test
      public void with_one_params() {
        var code = """
            result = (Int int) -> 7;
            """;
        module(code)
            .loadsWithSuccess();
      }

      @Test
      public void with_two_params() {
        var code = """
            result = (Int int, String string) -> 7;
            """;
        module(code)
            .loadsWithSuccess();
      }

      @Test
      public void with_default_value_fails() {
        var code = """
            result = (Int int = 8) -> 7;
            """;
        module(code)
            .loadsWithError(1, "Parameter `int` of anonymous function cannot have default value.");
      }

      @Test
      public void pipe_inside_body() {
        var code = """
            result = () -> (7 > []);
            """;
        module(code)
            .loadsWithSuccess();
      }

      @Test
      public void without_body_fails() {
        var code = """
            result = () -> ;
            """;
        module(code)
            .loadsWithError(1, """
                mismatched input ';' expecting {'(', '[', NAME, INT, BLOB, STRING}
                result = () -> ;
                               ^""");
      }

      @Nested
      class _param {
        @Test
        public void with_poly_type() {
          var code = """
              result = (A a) -> 7;
              """;
          module(code)
              .loadsWithSuccess();
        }

        @Test
        public void with_default_value_fails() {
          var code = """
              result = (Int int = 7) -> 7;
              """;
          module(code)
              .loadsWithError(
                  1, "Parameter `int` of anonymous function cannot have default value.");
        }
      }

      @Nested
      class _param_list {
        @Test
        public void can_have_trailing_comma() {
          var code = """
            result = (Int x,) -> 7;
            """;
          module(code)
              .loadsWithSuccess();
        }

        @Test
        public void cannot_have_only_comma() {
          var code = """
              result = (,) -> 7;
              """;
          module(code)
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_leading_comma() {
          var code = """
              result = (,Int x) -> 7;
              """;
          module(code)
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_two_trailing_commas() {
          var code = """
              result = (Int x,,) -> 7;
              """;
          module(code)
              .loadsWithProblems();
        }
      }
    }

    @Nested
    class _order {
      @ParameterizedTest
      @ArgumentsSource(Literals.class)
      public void with_one_elem(String literal) {
        module("result = [" + literal + "];")
            .loadsWithSuccess();
      }

      @ParameterizedTest
      @ArgumentsSource(Literals.class)
      public void with_two_elems(String literal) {
        module("result = [" + literal + ", " + literal + "];")
            .loadsWithSuccess();
      }

      @ParameterizedTest
      @ArgumentsSource(Literals.class)
      public void with_array_containing_one_elem(String literal) {
        module("result = [[" + literal + "]];")
            .loadsWithSuccess();
      }

      @ParameterizedTest
      @ArgumentsSource(Literals.class)
      public void with_array_and_empty_array_elems(String literal) {
        module("result = [[" + literal + "], []];")
            .loadsWithSuccess();
      }

      @ParameterizedTest
      @ArgumentsSource(Literals.class)
      public void with_array_containing_two_elems(String literal) {
        module("result = [[" + literal + ", " + literal + "]];")
            .loadsWithSuccess();
      }

      @Nested
      class _elem_list {
        @Test
        public void can_have_trailing_comma() {
          module(arrayLiteral("0x07,"))
              .loadsWithSuccess()
              .containsEvaluable(valueS(1, arrayTS(blobTS()), "result",
                  orderS(1, blobTS(), blobS(1, 7))));
        }

        @Test
        public void cannot_have_only_comma() {
          module(arrayLiteral(","))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_leading_comma() {
          module(arrayLiteral(",0x01"))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_two_trailing_commas() {
          module(arrayLiteral("0x01,,"))
              .loadsWithProblems();
        }

        private String arrayLiteral(String string) {
          return """
              result = [PLACEHOLDER];
              """.replace("PLACEHOLDER", string);
        }
      }


      @Test
      public void error_in_first_elem_doesnt_suppress_error_in_second_elem() {
        module("""
            myFunc() = "abc";
            result = [
              myFunc(unknown1=""),
              myFunc(unknown2="")
            ];
            """)
            .loadsWith(
                err(3, "Unknown parameter `unknown1`."),
                err(4, "Unknown parameter `unknown2`.")
            );
      }
    }

    @Nested
    class _select {
      @Test
      public void reading_field() {
        var code = """
            MyStruct(
              String field,
            )
            String result = MyStruct("abc").field;
            """;
        module(code)
            .loadsWithSuccess();
      }

      @Test
      public void reading_field_that_does_not_exist_causes_error() {
        var code = """
            MyStruct(
              String field,
            )
            result = MyStruct("abc").otherField;
            """;
        module(code)
            .loadsWithError(4, "Unknown field `otherField`.");
      }
    }

    @Nested
    class _literal {
      @Nested
      class _declaring_blob_literal {
        @ParameterizedTest
        @ValueSource(strings = {
            "0x",
            "0x12",
            "0x1234",
            "0x12345678",
            "0xabcdef",
            "0xABCDEF",
            "0xabcdefABCDEF"})
        public void is_legal(String literal) {
          module("result = " + literal + ";")
              .loadsWithSuccess();
        }

        @Nested
        class _causes_error_when {
          @Test
          public void has_only_one_digit() {
            module("result = 0x1;")
                .loadsWithError(1, "Illegal Blob literal: Digits count is odd.");
          }

          @Test
          public void has_odd_number_of_digits() {
            module("result = 0x123;")
                .loadsWithError(1, "Illegal Blob literal: Digits count is odd.");
          }

          @Test
          public void has_non_digit_character() {
            module("result = 0xGG;")
                .loadsWithError(1, """
              extraneous input 'GG' expecting ';'
              result = 0xGG;
                         ^^""");
          }
        }
      }

      @Nested
      class _declaring_int_literal {
        @ParameterizedTest
        @ValueSource(strings = {
            "0",
            "12",
            "123",
            "1234",
            "123456789",
            "-1",
            "-2",
            "-10",
            "-123456789",
        })
        public void is_legal(String literal) {
          module("result = " + literal + ";")
              .loadsWithSuccess();
        }


        @Nested
        class _causes_error_when {
          @ParameterizedTest
          @ValueSource(strings = {
              "00",
              "01",
              "001",
              "-0",
              "-00",
              "-01",
              "-001",
          })
          public void has_leading_zeros(String literal) {
            module("result = " + literal + ";")
                .loadsWithError(1, "Illegal Int literal: `" + literal + "`.");
          }

          @Test
          public void has_two_minus_signs() {
            module("result = --1;")
                .loadsWithError(1, """
                    token recognition error at: '--'
                    result = --1;
                             ^""");
          }

          @Test
          public void has_space_inside() {
            module("result = 12 3;")
                .loadsWithError(1, """
                  extraneous input '3' expecting ';'
                  result = 12 3;
                              ^""");
          }

          @Test
          public void has_space_after_minus_sign() {
            module("result = - 123;")
                .loadsWithError(1, """
                  token recognition error at: '- '
                  result = - 123;
                           ^""");
          }
        }
      }

      @Nested
      class _declaring_string_literal {
        @ParameterizedTest
        @ValueSource(strings = {
            "",
            "abc",
            "abcdefghijklmnopqrstuvwxyz",
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
            "0123456789",  // digits
            "abc←",        // unicode character
            "#",           // smooth language comment opening character
            "'",           // single quote
            "\\\\",        // escaped backslash
            "\\t",         // escaped tab
            "\\b",         // escaped backspace
            "\\n",         // escaped new line
            "\\r",         // escaped carriage return
            "\\f",         // escaped form feed
            "\\\""         // escaped double quotes
        })
        public void is_legal(String literal) {
          module("result = \"" + literal + "\";")
              .loadsWithSuccess();
        }

        @Nested
        class causes_error_when {
          @Test
          public void has_no_closing_quote() {
            module("""
             result = "abc;
             """)
                .loadsWithProblems();
          }

          @Test
          public void spans_to_next_line() {
            module("""
             result = "ab
             cd";
             """)
                .loadsWithProblems();
          }

          @Test
          public void has_illegal_escape_seq() {
            module("""
             result = "\\A";
             """)
                .loadsWithError(1, "Illegal String literal: "
                    + "Illegal escape sequence at char index = 1. "
                    + "Legal sequences are: \\t \\b \\n \\r \\f \\\" \\\\.");
          }

          @Test
          public void has_escape_seq_without_code() {
            var code = """
                result = "\\";
                """;
            module(code)
                .loadsWithError(1, "Illegal String literal: "
                    + "Missing escape code after backslash \\ at char index = 0.");
          }
        }
      }

      @Nested
      class _declaring_native_literal {
        @Nested
        class _causes_error_when {
          @Test
          public void path_has_illegal_escape_seq() {
            var module = """
                @Native("\\A")
                String myFunc();
                """;
            var error = err(1, "Illegal String literal: "
                + "Illegal escape sequence at char index = 1. "
                + "Legal sequences are: \\t \\b \\n \\r \\f \\\" \\\\.");

            module(module).loadsWith(error);
          }

          @Test
          public void path_has_escape_seq_without_code() {
            var module = """
                @Native("\\")
                String myFunc();""";
            var error = err(1, "Illegal String literal:"
                + " Missing escape code after backslash \\ at char index = 0.");

            module(module).loadsWith(error);
          }
        }
      }
    }

    @Nested
    class _parens {
      @ParameterizedTest
      @ArgumentsSource(Literals.class)
      public void with_literal(String literal) {
        module("result = (" + literal + ");")
            .loadsWithSuccess();
      }

      @Test
      public void with_additional_comma() {
        module("result = (7, );")
            .loadsWithError(1, """
                extraneous input ',' expecting ')'
                result = (7, );
                           ^""");
      }
    }

    @Nested
    class _piped_value_consumption {
      @Test
      public void not_consumed_by_select() {
        var code = """
            MyStruct(
              String myField
            )
            myValue = myStruct("def");
            result = "abc" > myValue.myField;
            """;
        module(code)
            .loadsWithError(5, "Piped value is not consumed.");
      }

      @Test
      public void not_consumed_by_int_literal() {
        var code = """
            result = "abc" > 7;
            """;
        module(code)
            .loadsWithError(1, "Piped value is not consumed.");
      }

      @Test
      public void not_consumed_by_string_literal() {
        var code = """
            result = "abc" > "def";
            """;
        module(code)
            .loadsWithError(1, "Piped value is not consumed.");
      }

      @Test
      public void not_consumed_by_blob_literal() {
        var code = """
            result = "abc" > 0xAA;
            """;
        module(code)
            .loadsWithError(1, "Piped value is not consumed.");
      }

      @Test
      public void not_consumed_by_value_ref() {
        var code = """
            myValue = 7;
            result = "abc" > myValue;
            """;
        module(code)
            .loadsWithError(2, "Piped value is not consumed.");
      }

      @Test
      public void not_consumed_by_consuming_expr_inside_anonymous_function_body() {
        var code = """
            result = 7 > () -> [];
            """;
        module(code)
            .loadsWithError(1, "Piped value is not consumed.");
      }

      @Test
      public void not_consumed_by_expression_inside_parens() {
        var code = """
            result = "abc" > (7);
            """;
        module(code)
            .loadsWithError(1, "Piped value is not consumed.");
      }

      @Test
      public void not_consumed_by_first_expr_of_inner_pipe_inside_parens() {
        var code = """
            result = "abc" > (7 > []);
            """;
        module(code)
            .loadsWithError(1, "Piped value is not consumed.");
      }

      @Test
      public void consumed_by_expression_after_parens_containing_inner_pipe() {
        var code = """
            String stringId(String string) = string;
            A id(A a) = a;
            result = "abc" > (stringId > id())();
            """;
        module(code)
            .loadsWithSuccess();
      }

      @Test
      public void not_consumed_by_expression_after_parens_containing_inner_pipe() {
        var code = """
            MyStruct(
              Int myField
            )
            v = myStruct(7);
            A id(A a) = a;
            result = "abc" > (v > id()).myField;
            """;
        module(code)
            .loadsWithError(6, "Piped value is not consumed.");
      }
    }
  }

  @Nested
  class _comments {
    @Test
    public void full_line_comment() {
      module("""
           # ((( full line comment "
           result = "";
           """)
          .loadsWithSuccess();
    }

    @Test
    public void trailing_comment() {
      module("""
           result = "" ;  # comment at the end of line
           """)
          .loadsWithSuccess();
    }
  }

  private static class TestedTypes implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      return TESTED_TYPES.stream()
          .map(Arguments::of);
    }
  }
  private static class Literals implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
          arguments("[]"),
          arguments("0x01"),
          arguments("\"abc\""),
          arguments("7")
      );
    }
  }
  @Nested
  class _func_type_literal {
    @Test
    public void cannot_have_trailing_comma() {
      module(funcTDeclaration("String,"))
          .loadsWithProblems();
    }

    @Test
    public void cannot_have_only_comma() {
      module(funcTDeclaration(","))
          .loadsWithProblems();
    }

    @Test
    public void cannot_have_leading_comma() {
      module(funcTDeclaration(",String"))
          .loadsWithProblems();
    }

    @Test
    public void cannot_have_two_trailing_commas() {
      module(funcTDeclaration("String,,"))
          .loadsWithProblems();
    }

    private String funcTDeclaration(String string) {
      return """
              @Native("Impl.met")
              (PLACEHOLDER)->Blob myFunc();
              """.replace("PLACEHOLDER", string);
    }
  }
}
