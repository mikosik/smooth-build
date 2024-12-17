package org.smoothbuild.compilerfrontend.acceptance;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.base.Strings.unlines;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.compilerfrontend.acceptance.Util.illegalCallMessage;
import static org.smoothbuild.compilerfrontend.lang.base.NList.nlist;
import static org.smoothbuild.compilerfrontend.testing.TestedTSF.TESTED_TYPES;

import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.compilerfrontend.testing.FrontendCompileTester;
import org.smoothbuild.compilerfrontend.testing.TestedTS;

public class DeclarationTest extends FrontendCompileTester {
  @Nested
  class _members {
    @Nested
    class _annotation {
      @Test
      void with_unknown_name_causes_error() {
        module(
                """
            @UnknownAnnotation("value")
            Int myFunc() = 3;
            """)
            .loadsWithError(1, "Unknown annotation `UnknownAnnotation`.");
      }
    }

    @Nested
    class _struct {
      @Test
      void declaring_empty_struct_is_allowed() {
        module("MyStruct{}").loadsWithSuccess();
      }

      @Test
      void declaring_non_empty_struct_is_allowed() {
        var code =
            """
            MyStruct{
              String fieldA,
              String fieldB
            }
            """;
        module(code).loadsWithSuccess();
      }

      @Nested
      class _name {
        @Test
        void that_is_normal_name() {
          module("MyStruct{}").loadsWithSuccess();
        }

        @Test
        void that_is_illegal_fails() {
          module("MyStruct^{}")
              .loadsWithError(
                  1,
                  """
            token recognition error at: '^'
            MyStruct^{}
                    ^""");
        }

        @Test
        void that_starts_with_small_letter_fails() {
          module("myStruct{}")
              .loadsWithError(1, structWithLowercaseFirstCharacterError("myStruct"));
        }

        @Test
        void that_is_single_capital_letter_fails() {
          module("A{}")
              .loadsWithError(
                  1,
                  "`A` is illegal struct name. "
                      + "All-uppercase names are reserved for type variables.");
        }

        @Test
        void that_is_single_underscore_fails() {
          module("_{}")
              .loadsWithError(1, "`_` is illegal struct name. `_` is reserved for future use.");
        }

        @Test
        void that_is_full_name_fails() {
          var code = """
              My:Struct{}
              """;
          module(code).loadsWithError(1, structNameWithIllegalCharacterError("My:Struct", ":"));
        }

        @Test
        void that_is_multiple_capital_letters_fails() {
          module("ABC{}")
              .loadsWithError(
                  1,
                  "`ABC` is illegal struct name. "
                      + "All-uppercase names are reserved for type variables.");
        }
      }

      @Nested
      class _field {
        @Nested
        class _type {
          @ParameterizedTest
          @ArgumentsSource(TestedTypes.class)
          public void can_be_monotype(TestedTS testedT) {
            var code = unlines(
                testedT.typeDeclarationsAsString(),
                "MyStruct{",
                "  " + testedT.name() + " field,",
                "}");
            module(code).loadsWithSuccess();
          }

          @Test
          void cannot_be_polytype() {
            var code =
                """
                MyStruct{
                 (B)->A field
                }
                """;
            module(code)
                .loadsWithError(
                    2, "Field type cannot be polymorphic. Found field `field` with type `(B)->A`.");
          }

          @Test
          void cannot_be_polytype_regression_test() {
            // Verify that illegal field type fails with error and further code is not checked.
            var code =
                """
                MyStruct{
                  (B)->A field
                }
                @Native("impl")
                MyStruct myFunction();
                """;
            module(code)
                .loadsWithError(
                    2, "Field type cannot be polymorphic. Found field `field` with type `(B)->A`.");
          }

          @Test
          void cannot_be_polytype_array() {
            var code =
                """
                MyStruct{
                  [A] field
                }
                """;
            module(code)
                .loadsWithError(
                    2, "Field type cannot be polymorphic. Found field `field` with type `[A]`.");
          }

          @Test
          void cannot_be_type_which_encloses_it() {
            var code =
                """
                MyStruct{
                  MyStruct field
                }
                """;
            module(code)
                .loadsWithError(
                    """
                  Type hierarchy contains cycle:
                  {t-project}/module.smooth:2: MyStruct ~> MyStruct""");
          }

          @Test
          void cannot_be_array_type_which_elem_type_encloses_it() {
            var code =
                """
                MyStruct{
                  String firstField,
                  [MyStruct] field
                }
                """;
            module(code)
                .loadsWithError(
                    """
                  Type hierarchy contains cycle:
                  {t-project}/module.smooth:3: MyStruct ~> MyStruct""");
          }

          @Test
          void cannot_declare_func_which_result_type_encloses_it() {
            var code =
                """
                MyStruct{
                  ()->MyStruct field
                }
                """;
            module(code)
                .loadsWithError(
                    """
                  Type hierarchy contains cycle:
                  {t-project}/module.smooth:2: MyStruct ~> MyStruct""");
          }

          @Test
          void cannot_declare_func_which_param_type_encloses_it() {
            var code =
                """
                MyStruct{
                  (MyStruct)->Blob field
                }
                """;
            module(code)
                .loadsWithError(
                    """
                  Type hierarchy contains cycle:
                  {t-project}/module.smooth:2: MyStruct ~> MyStruct""");
          }
        }

        @Nested
        class _name {
          @Test
          void that_is_legal() {
            module(
                    """
                MyStruct{
                  String field
                }
                """)
                .loadsWithSuccess();
          }

          @Test
          void that_is_illegal_fails() {
            module(
                    """
                MyStruct{
                  String field^
                }
                """)
                .loadsWithError(
                    2,
                    """
              token recognition error at: '^'
                String field^
                            ^""");
          }

          @Test
          void that_starts_with_large_letter_fails() {
            module(
                    """
                MyStruct{
                  String Field
                }
                """)
                .loadsWithError(2, identifierWithUppercaseFirstCharacterError("Field"));
          }

          @Test
          void that_is_single_large_letter_fails() {
            module(
                    """
                MyStruct {
                  String A
                }
                """)
                .loadsWithError(2, identifierWithUppercaseFirstCharacterError("A"));
          }

          @Test
          void that_is_single_underscore_fails() {
            var code =
                """
                MyStruct{
                  String _
                }
                """;
            module(code)
                .loadsWithError(
                    2, "`_` is illegal identifier name. `_` is reserved for future use.");
          }

          @Test
          void that_is_full_name_fails() {
            var code =
                """
                MyStruct{
                  String my:field
                }
                """;
            module(code)
                .loadsWithError(2, identifierNameWithIllegalCharacterError("my:field", ":"));
          }
        }

        @Nested
        class _default_value {
          @Test
          void is_illegal() {
            module(
                    """
                MyStruct{
                  Int myField = 7
                }
                """)
                .loadsWithError(
                    2,
                    "Struct field `myField` has default value. "
                        + "Only function parameters can have default value.");
          }
        }
      }

      @Nested
      class _field_list {
        @Test
        void can_have_trailing_comma() {
          module(structDeclaration("String field,"))
              .loadsWithSuccess()
              .containsType(sStructType("MyStruct", nlist(sSig(sStringType(), "field"))));
        }

        @Test
        void cannot_have_only_comma() {
          module(structDeclaration(",")).loadsWithProblems();
        }

        @Test
        void cannot_have_leading_comma() {
          module(structDeclaration(", String field")).loadsWithProblems();
        }

        @Test
        void cannot_have_two_trailing_commas() {
          module(structDeclaration("String field,,")).loadsWithProblems();
        }

        private String structDeclaration(String string) {
          return "MyStruct{ PLACEHOLDER }".replace("PLACEHOLDER", string);
        }
      }
    }

    @Nested
    class _value {
      @Nested
      class _type {
        @Test
        void can_be_omitted() {
          module("""
              myValue = "abc";
              """).loadsWithSuccess();
        }

        @ParameterizedTest
        @ArgumentsSource(TestedTypes.class)
        public void can_be_monotype(TestedTS type) {
          var code = unlines(
              "@Native(\"Impl.met\")",
              type.name() + " myFunc();",
              type.name() + " myValue = myFunc();",
              type.typeDeclarationsAsString());
          module(code).loadsWithSuccess();
        }

        @Test
        void can_be_polytype() {
          var code = """
              [A] myValue = [];
          """;
          module(code).loadsWithSuccess();
        }

        @Test
        void can_be_polytype_assigned_from_func() {
          var code =
              """
              A myId(A a) = a;
              (A)->A myValue = myId;
          """;
          module(code).loadsWithSuccess();
        }

        @Test
        void can_be_monotype_function_assigned_from_polytype_func() {
          var code =
              """
              @Native("Impl.met")
              A myId(A param);
              (Int)->Int myValue = myId;
          """;
          module(code).loadsWithSuccess();
        }
      }

      @Nested
      class _name {
        @Test
        void that_is_legal() {
          module("""
              myValue = "abc";
              """).loadsWithSuccess();
        }

        @Test
        void that_is_illegal_fails() {
          module("""
              myValue^ = "abc";
              """)
              .loadsWithError(
                  1,
                  """
            token recognition error at: '^'
            myValue^ = "abc";
                   ^""");
        }

        @Test
        void that_starts_with_large_letter_fails() {
          module("""
              MyValue = "abc";
              """)
              .loadsWithError(1, identifierWithUppercaseFirstCharacterError("MyValue"));
        }

        @Test
        void that_is_single_large_letter_fails() {
          module("""
              A = "abc";
              """)
              .loadsWithError(1, identifierWithUppercaseFirstCharacterError("A"));
        }

        @Test
        void that_is_single_underscore_fails() {
          module("""
              _ = "abc";
              """)
              .loadsWithError(1, "`_` is illegal identifier name. `_` is reserved for future use.");
        }

        @Test
        void that_is_full_name_fails() {
          var code = """
              my:name = "abc";
              """;
          module(code).loadsWithError(1, identifierNameWithIllegalCharacterError("my:name", ":"));
        }
      }

      @Test
      void without_body_fails() {
        module("""
            String result;
            """)
            .loadsWithError(1, "Value cannot have empty body.");
      }

      @Test
      void with_bytecode_ann_and_body_fails() {
        module(
                """
            @Bytecode("implementation")
            String result = "abc";
            """)
            .loadsWithError(2, "Value with @Bytecode annotation cannot have body.");
      }

      @Test
      void with_bytecode_ann_and_without_res_type_fails() {
        module("""
            @Bytecode("implementation")
            result;
            """)
            .loadsWithError(2, "Value `result` with @Bytecode annotation must declare type.");
      }

      @Test
      void with_native_ann_and_body_fails() {
        module(
                """
            @Native("implementation")
            String result = "abc";
            """)
            .loadsWithError(1, "Value cannot have @Native annotation.");
      }

      @Test
      void with_native_ann_and_without_body_fails() {
        module("""
            @Native("implementation")
            String result;
            """)
            .loadsWithError(1, "Value cannot have @Native annotation.");
      }

      @Test
      void with_native_impure_ann_and_body_fails() {
        module(
                """
            @NativeImpure("implementation")
            String result = "abc";
            """)
            .loadsWithError(1, "Value cannot have @NativeImpure annotation.");
      }

      @Test
      void with_native_impure_ann_and_without_body_fails() {
        module(
                """
            @NativeImpure("implementation")
            String result;
            """)
            .loadsWithError(1, "Value cannot have @NativeImpure annotation.");
      }

      @Test
      void with_unknown_ann_and_body_fails() {
        module(
                """
            @Unknown("implementation")
            String result = "abc";
            """)
            .loadsWithError(1, "Unknown annotation `Unknown`.");
      }

      @Test
      void with_unknown_ann_and_without_body_fails() {
        module(
                """
            @Unknown("implementation")
            String result;
            """)
            .loadsWithError(1, "Unknown annotation `Unknown`.");
      }
    }

    @Nested
    class _func {
      @Test
      void without_body_fails() {
        module("""
            String myFunc();
            """)
            .loadsWithError(1, "Function body is missing.");
      }

      @Test
      void with_unknown_ann_with_body_fails() {
        module("""
            @Unknown("abc")
            String myFunc() = "abc";
            """)
            .loadsWithError(1, "Unknown annotation `Unknown`.");
      }

      @Test
      void with_unknown_ann_without_body_fails() {
        module("""
            @Unknown("abc")
            String myFunc();
            """)
            .loadsWithError(1, "Unknown annotation `Unknown`.");
      }

      @Test
      void with_native_ann_and_with_body_fails() {
        module(
                """
            @Native("Impl.met")
            String myFunc() = "abc";
            """)
            .loadsWithError(2, "Function `myFunc` with @Native annotation cannot have body.");
      }

      @Test
      void with_native_ann_without_declared_result_type_fails() {
        module("""
            @Native("Impl.met")
            myFunc();
            """)
            .loadsWithError(
                2, "Function `myFunc` with @Native annotation must declare result type.");
      }

      @Test
      void with_bytecode_ann_and_with_body_fails() {
        module(
                """
            @Bytecode("Impl.met")
            String myFunc() = "abc";
            """)
            .loadsWithError(2, "Function `myFunc` with @Bytecode annotation cannot have body.");
      }

      @Test
      void with_bytecode_ann_and_without_declared_result_type_fails() {
        module("""
            @Bytecode("Impl.met")
            myFunc();
            """)
            .loadsWithError(
                2, "Function `myFunc` with @Bytecode annotation must declare result type.");
      }

      @Nested
      class _result {
        @Nested
        class _type {
          @Test
          void result_type_can_be_omitted() {
            module("""
                myFunc() = "abc";
                """).loadsWithSuccess();
          }

          @ParameterizedTest
          @ArgumentsSource(TestedTypes.class)
          public void can_be_monotype(TestedTS type) {
            var code = unlines(
                "@Native(\"impl\")", type.name() + " myFunc();", type.declarationsAsString());
            module(code).loadsWithSuccess();
          }

          @Test
          void can_contain_var_not_present_in_any_param_type() {
            var code =
                """
                @Native("Impl.met")
                A myFunc(B b, C c);
                """;
            module(code).loadsWithSuccess();
          }

          @Test
          void can_contain_var_that_is_present_in_some_param_type() {
            var code =
                """
                @Native("Impl.met")
                A myFunc(A a);
                """;
            module(code).loadsWithSuccess();
          }
        }
      }

      @Nested
      class _name {
        @Test
        void that_is_legal() {
          module("""
              myFunc() = "abc";
              """).loadsWithSuccess();
        }

        @Test
        void that_is_illegal_fails() {
          module("""
              myFunc^() = "abc";
              """)
              .loadsWithError(
                  1,
                  """
            token recognition error at: '^'
            myFunc^() = "abc";
                  ^""");
        }

        @Test
        void that_starts_with_large_letter_fails() {
          module("""
              MyFunc() = "abc";
              """)
              .loadsWithError(1, identifierWithUppercaseFirstCharacterError("MyFunc"));
        }

        @Test
        void that_is_single_large_letter_fails() {
          module("""
              A() = "abc";
              """)
              .loadsWithError(
                  1, "`A` is illegal identifier name. It must start with lowercase letter.");
        }

        @Test
        void that_is_single_underscore_fails() {
          module("""
              _() = "abc";
              """)
              .loadsWithError(1, "`_` is illegal identifier name. `_` is reserved for future use.");
        }

        @Test
        void that_is_full_name_fails() {
          var code = """
              my:func() = "abc";
              """;
          module(code).loadsWithError(1, identifierNameWithIllegalCharacterError("my:func", ":"));
        }
      }

      @Nested
      class _param {
        @Nested
        class _type {
          @ParameterizedTest
          @ArgumentsSource(TestedTypes.class)
          public void can_be_monotype(TestedTS type) {
            var code = unlines(
                "@Native(\"Impl.met\")",
                "String myFunc(" + type.name() + " param);",
                type.typeDeclarationsAsString());
            module(code).loadsWithSuccess();
          }

          @Test
          void can_be_polytype() {
            var code =
                """
                @Native("Impl.met")
                String myFunc((A)->A f);
                """;
            module(code).loadsWithSuccess();
          }

          @Test
          void can_be_single_var_polytype() {
            var code =
                """
                @Native("Impl.met")
                String myFunc((A)->Int f);
                """;
            module(code).loadsWithSuccess();
          }
        }

        @Nested
        class _name {
          @Test
          void that_is_legal() {
            module("""
                String myFunc(String name) = "abc";
                """)
                .loadsWithSuccess();
          }

          @Test
          void that_is_illegal_fails() {
            module("""
                String myFunc(String name^);
                """)
                .loadsWithError(
                    1,
                    """
              token recognition error at: '^'
              String myFunc(String name^);
                                       ^""");
          }

          @Test
          void that_starts_with_large_letter_fails() {
            module("""
                Int myFunc(Int Name) = 7;
                """)
                .loadsWithError(1, identifierWithUppercaseFirstCharacterError("Name"));
          }

          @Test
          void that_is_single_large_letter_fails() {
            module("""
                Int myFunc(Int A) = 7;
                """)
                .loadsWithError(1, identifierWithUppercaseFirstCharacterError("A"));
          }

          @Test
          void that_is_single_underscore_fails() {
            module("""
                Int myFunc(Int _) = 7;
                """)
                .loadsWithError(
                    1, "`_` is illegal identifier name. `_` is reserved for future use.");
          }

          @Test
          void that_is_full_name_fails() {
            var code = """
                Int myFunc(Int my:param) = 7;
              """;
            module(code)
                .loadsWithError(1, identifierNameWithIllegalCharacterError("my:param", ":"));
          }
        }

        @Test
        void default_param_before_non_default_is_allowed() {
          var code =
              """
              @Native("Impl.met")
              String myFunc(
                String default = "value",
                String nonDefault);
              """;
          var value = sValue(3, "myFunc:default", sString(3, "value"));
          var myFuncParams = nlist(
              sItem(3, sStringType(), "default", "myFunc:default"),
              sItem(4, sStringType(), "nonDefault"));
          var ann = sNativeAnnotation(1, sString(1, "Impl.met"));
          var myFunc = sAnnotatedFunc(2, ann, sStringType(), "myFunc", myFuncParams);

          var api = module(code).loadsWithSuccess();
          api.containsEvaluable(myFunc);
          api.containsEvaluable(value);
        }

        @Test
        void param_with_vars_can_have_default_value() {
          module("""
              A myFunc(A value = "abc") = value;
              """)
              .loadsWithSuccess();
        }

        @Test
        void default_arg_gets_converted_to_param_type() {
          module(
                  """
              [String] myFunc(String param1, [String] param2 = []) = param2;
              [String] result = myFunc("abc");
              """)
              .loadsWithSuccess();
        }
      }

      @Nested
      class _param_list {
        @Test
        void can_have_trailing_comma() {
          module(funcDeclaration("String param1,"))
              .loadsWithSuccess()
              .containsEvaluable(sFunc(
                  1,
                  sStringType(),
                  "myFunc",
                  nlist(sItem(1, sStringType(), "param1")),
                  sString(1, "abc")));
        }

        @Test
        void cannot_have_only_comma() {
          module(funcDeclaration(",")).loadsWithProblems();
        }

        @Test
        void cannot_have_leading_comma() {
          module(funcDeclaration(",String string")).loadsWithProblems();
        }

        @Test
        void cannot_have_two_trailing_commas() {
          module(funcDeclaration("String string,,")).loadsWithProblems();
        }

        private String funcDeclaration(String string) {
          return """
              String myFunc(PLACEHOLDER) = "abc";
              """
              .replace("PLACEHOLDER", string);
        }
      }
    }
  }

  @Nested
  class _expressions {
    @Nested
    class _reference {
      @Test
      void illegal_reference_causes_error() {
        var code = """
            myValue = abc::def;
            """;
        module(code)
            .loadsWithError(
                1, "Illegal reference `abc::def`. It must not contain \"::\" substring.");
      }
    }

    @Nested
    class _call {
      @Nested
      class _func {
        @Test
        void passing_more_positional_args_than_params_causes_error() {
          var code =
              """
              myIdentity(String param) = param;
              result = myIdentity("abc", "def");
              """;
          var called = sFuncType(sStringType(), sStringType());
          var args = list(sStringType(), sStringType());
          module(code).loadsWithError(2, illegalCallMessage(called, args));
        }

        @Test
        void passing_less_positional_args_than_params_causes_error() {
          var code =
              """
              returnFirst(String param1, String param2) = param1;
              result = returnFirst("abc");
              """;
          module(code).loadsWithError(2, "Parameter `param2` must be specified.");
        }

        @Test
        void passing_less_positional_args_than_params_causes_error_version_without_name() {
          var code =
              """
              returnFirst(String param1, String param2) = param1;
              funcValue = returnFirst;
              result = funcValue("abc");
              """;
          var called = sFuncType(sStringType(), sStringType(), sStringType());
          var args = list(sStringType());
          module(code).loadsWithError(3, illegalCallMessage(called, args));
        }

        @Test
        void named_arg_which_doesnt_exist_causes_error() {
          var code =
              """
              myIdentity(String param) = param;
              result = myIdentity(wrongName="abc");
              """;
          module(code).loadsWithError(2, "Unknown parameter `wrongName`.");
        }

        @Test
        void named_args_can_be_passed_in_the_same_order_as_params() {
          var code =
              """
              returnFirst(String param1, String param2) = param1;
              result = returnFirst(param1="abc", param2="def");
              """;
          module(code).loadsWithSuccess();
        }

        @Test
        void named_args_can_be_passed_in_different_order_than_params() {
          var code =
              """
              returnFirst(String param1, String param2) = param1;
              result = returnFirst(param2="def", param1="abc");
              """;
          module(code).loadsWithSuccess();
        }

        @Test
        void all_named_args_must_come_after_positional() {
          var code =
              """
              returnFirst(String param1, String param2) = param1;
              result = returnFirst(param2="def", "abc");
              """;
          module(code)
              .loadsWithError(2, "Positional arguments must be placed before named arguments.");
        }

        @Test
        void assigning_arg_by_name_twice_causes_error() {
          var code =
              """
              myIdentity(String param) = param;
              result = myIdentity(param="abc", param="abc");
              """;
          module(code).loadsWithError(2, "`param` is already assigned.");
        }

        @Test
        void assigning_by_name_arg_that_is_assigned_by_position_causes_error() {
          var code =
              """
              myIdentity(String param) = param;
              result = myIdentity("abc", param="abc");
              """;
          module(code).loadsWithError(2, "`param` is already assigned.");
        }

        @Test
        void param_with_default_value_can_be_assigned_positionally() {
          var code =
              """
              myIdentity(String param1="abc", String param2="def") = param1;
              result = myIdentity("abc", "def");
              """;
          module(code).loadsWithSuccess();
        }

        @Test
        void param_with_default_value_can_be_assigned_by_name() {
          var code =
              """
            myIdentity(String param1="abc", String param2="def") = param1;
            result = myIdentity(param1="abc", param2="def");
            """;
          module(code).loadsWithSuccess();
        }

        @Test
        void lambda_parameter_names_are_always_stripped() {
          var code =
              """
            myFunc(String param) = param;
            valueReferencingFunc = myFunc;
            result = ((Int int) -> int)(param=7);
            """;
          module(code).loadsWithError(3, "Unknown parameter `param`.");
        }

        @Test
        void named_function_parameter_names_are_stripped_during_assignment() {
          var code =
              """
            myFunc(String param) = param;
            valueReferencingFunc = myFunc;
            result = valueReferencingFunc(param="abc");
            """;
          module(code).loadsWithError(3, "Unknown parameter `param`.");
        }

        @Test
        void named_function_parameter_default_values_are_stripped_during_assignment() {
          var code =
              """
            myFunc(String param = "abc") = param;
            valueReferencingFunc = myFunc;
            result = valueReferencingFunc();
            """;
          var called = sFuncType(sStringType(), sStringType());
          module(code).loadsWithError(3, illegalCallMessage(called, list()));
        }
      }

      @Nested
      class _ctor {
        @Test
        void creating_empty_struct_instance_is_allowed() {
          var code =
              """
              MyStruct{}
              result = MyStruct();
              """;
          module(code).loadsWithSuccess();
        }

        @Test
        void creating_non_empty_struct_is_allowed() {
          var code =
              """
              MyStruct{
                String field,
              }
              result = MyStruct("abc");
              """;
          module(code).loadsWithSuccess();
        }

        @Test
        void calling_ctor_without_all_params_causes_error() {
          var code =
              """
              MyStruct{
                String field,
              }
              result = MyStruct();
              """;
          module(code).loadsWithError(4, "Parameter `field` must be specified.");
        }
      }

      @Nested
      class _arg_list {
        @Test
        void named_arg_that_is_illegal_fails() {
          module(
                  """
                String myFunc(String name) = "abc";
                result = myFunc(Name="abc");
                """)
              .loadsWithError(
                  2, "`Name` is illegal parameter name. It must start with lowercase letter.");
        }

        @Test
        void can_have_trailing_comma() {
          module(funcCall("7,"))
              .loadsWithSuccess()
              .containsEvaluable(sValue(
                  2, sIntType(), "result", sCall(2, sInstantiate(2, intIdSFunc()), sInt(2, 7))));
        }

        @Test
        void cannot_have_only_comma() {
          module(funcCall(",")).loadsWithProblems();
        }

        @Test
        void cannot_have_leading_comma() {
          module(funcCall(",7")).loadsWithProblems();
        }

        @Test
        void cannot_have_two_trailing_commas() {
          module(funcCall("1,,")).loadsWithProblems();
        }

        private String funcCall(String string) {
          return """
              Int myIntId(Int i) = i;
              result = myIntId(PLACEHOLDER);
              """
              .replace("PLACEHOLDER", string);
        }
      }
    }

    @Nested
    class _lambda {
      @Test
      void with_no_params() {
        var code = """
            result = () -> 7;
            """;
        module(code).loadsWithSuccess();
      }

      @Test
      void with_one_params() {
        var code = """
            result = (Int int) -> 7;
            """;
        module(code).loadsWithSuccess();
      }

      @Test
      void with_two_params() {
        var code = """
            result = (Int int, String string) -> 7;
            """;
        module(code).loadsWithSuccess();
      }

      @Test
      void with_default_value_fails() {
        var code = """
            result = (Int int = 8) -> 7;
            """;
        module(code).loadsWithError(1, "Parameter `int` of lambda cannot have default value.");
      }

      @Test
      void pipe_inside_body() {
        var code = """
            result = () -> (7 > []);
            """;
        module(code).loadsWithSuccess();
      }

      @Test
      void without_body_fails() {
        var code = """
            result = () -> ;
            """;
        module(code)
            .loadsWithError(
                1,
                """
                mismatched input ';' expecting {'(', '[', NAME, INT, BLOB, STRING}
                result = () -> ;
                               ^""");
      }

      @Nested
      class _param {
        @Test
        void with_poly_type() {
          var code = """
              result = (A a) -> 7;
              """;
          module(code).loadsWithSuccess();
        }

        @Test
        void with_default_value_fails() {
          var code = """
              result = (Int int = 7) -> 7;
              """;
          module(code).loadsWithError(1, "Parameter `int` of lambda cannot have default value.");
        }
      }

      @Nested
      class _param_list {
        @Test
        void can_have_trailing_comma() {
          var code = """
            result = (Int x,) -> 7;
            """;
          module(code).loadsWithSuccess();
        }

        @Test
        void cannot_have_only_comma() {
          var code = """
              result = (,) -> 7;
              """;
          module(code).loadsWithProblems();
        }

        @Test
        void cannot_have_leading_comma() {
          var code = """
              result = (,Int x) -> 7;
              """;
          module(code).loadsWithProblems();
        }

        @Test
        void cannot_have_two_trailing_commas() {
          var code = """
              result = (Int x,,) -> 7;
              """;
          module(code).loadsWithProblems();
        }
      }
    }

    @Nested
    class _order {
      @ParameterizedTest
      @ArgumentsSource(Literals.class)
      public void with_one_elem(String literal) {
        module("result = [" + literal + "];").loadsWithSuccess();
      }

      @ParameterizedTest
      @ArgumentsSource(Literals.class)
      public void with_two_elements(String literal) {
        module("result = [" + literal + ", " + literal + "];").loadsWithSuccess();
      }

      @ParameterizedTest
      @ArgumentsSource(Literals.class)
      public void with_array_containing_one_elem(String literal) {
        module("result = [[" + literal + "]];").loadsWithSuccess();
      }

      @ParameterizedTest
      @ArgumentsSource(Literals.class)
      public void with_array_and_empty_array_elements(String literal) {
        module("result = [[" + literal + "], []];").loadsWithSuccess();
      }

      @ParameterizedTest
      @ArgumentsSource(Literals.class)
      public void with_array_containing_two_elements(String literal) {
        module("result = [[" + literal + ", " + literal + "]];").loadsWithSuccess();
      }

      @Nested
      class _elem_list {
        @Test
        void can_have_trailing_comma() {
          module(arrayLiteral("0x07,"))
              .loadsWithSuccess()
              .containsEvaluable(
                  sValue(1, sBlobArrayT(), "result", sOrder(1, sBlobType(), sBlob(1, 7))));
        }

        @Test
        void cannot_have_only_comma() {
          module(arrayLiteral(",")).loadsWithProblems();
        }

        @Test
        void cannot_have_leading_comma() {
          module(arrayLiteral(",0x01")).loadsWithProblems();
        }

        @Test
        void cannot_have_two_trailing_commas() {
          module(arrayLiteral("0x01,,")).loadsWithProblems();
        }

        private String arrayLiteral(String string) {
          return """
              result = [PLACEHOLDER];
              """
              .replace("PLACEHOLDER", string);
        }
      }

      @Test
      void error_in_first_elem_not_suppresses_error_in_second_elem() {
        module(
                """
            myFunc() = "abc";
            result = [
              myFunc(unknown1=""),
              myFunc(unknown2="")
            ];
            """)
            .loadsWith(
                err(3, "Unknown parameter `unknown1`."), err(4, "Unknown parameter `unknown2`."));
      }
    }

    @Nested
    class _select {
      @Test
      void reading_field() {
        var code =
            """
            MyStruct{
              String field,
            }
            String result = MyStruct("abc").field;
            """;
        module(code).loadsWithSuccess();
      }

      @Test
      void reading_field_that_does_not_exist_causes_error() {
        var code =
            """
            MyStruct{
              String field,
            }
            result = MyStruct("abc").otherField;
            """;
        module(code).loadsWithError(4, "Struct `MyStruct` has no field `otherField`.");
      }

      @Test
      void specifying_field_name_that_is_illegal_causes_error() {
        var code =
            """
            MyStruct{
              String field,
            }
            result = MyStruct("abc").Field;
            """;
        module(code)
            .loadsWithError(
                4, "`Field` is illegal field name. It must start with lowercase letter.");
      }
    }

    @Nested
    class _literal {
      @Nested
      class _declaring_blob_literal {
        @ParameterizedTest
        @ValueSource(
            strings = {
              "0x",
              "0x12",
              "0x1234",
              "0x12345678",
              "0xabcdef",
              "0xABCDEF",
              "0xabcdefABCDEF"
            })
        public void is_legal(String literal) {
          module("result = " + literal + ";").loadsWithSuccess();
        }

        @Nested
        class _causes_error_when {
          @Test
          void has_only_one_digit() {
            module("result = 0x1;").loadsWithError(1, "Illegal Blob literal: Digits count is odd.");
          }

          @Test
          void has_odd_number_of_digits() {
            module("result = 0x123;")
                .loadsWithError(1, "Illegal Blob literal: Digits count is odd.");
          }

          @Test
          void has_non_digit_character() {
            module("result = 0xGG;")
                .loadsWithError(
                    1,
                    """
              extraneous input 'GG' expecting ';'
              result = 0xGG;
                         ^^""");
          }
        }
      }

      @Nested
      class _declaring_int_literal {
        @ParameterizedTest
        @ValueSource(
            strings = {
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
          module("result = " + literal + ";").loadsWithSuccess();
        }

        @Nested
        class _causes_error_when {
          @ParameterizedTest
          @ValueSource(
              strings = {
                "00", "01", "001", "-0", "-00", "-01", "-001",
              })
          public void has_leading_zeros(String literal) {
            module("result = " + literal + ";")
                .loadsWithError(1, "Illegal Int literal: `" + literal + "`.");
          }

          @Test
          void has_two_minus_signs() {
            module("result = --1;")
                .loadsWithError(
                    1,
                    """
                    token recognition error at: '--'
                    result = --1;
                             ^""");
          }

          @Test
          void has_space_inside() {
            module("result = 12 3;")
                .loadsWithError(
                    1,
                    """
                  extraneous input '3' expecting ';'
                  result = 12 3;
                              ^""");
          }

          @Test
          void has_space_after_minus_sign() {
            module("result = - 123;")
                .loadsWithError(
                    1,
                    """
                  token recognition error at: '- '
                  result = - 123;
                           ^""");
          }
        }
      }

      @Nested
      class _declaring_string_literal {
        @ParameterizedTest
        @ValueSource(
            strings = {
              "",
              "abc",
              "abcdefghijklmnopqrstuvwxyz",
              "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
              "0123456789", // digits
              "abc←", // unicode character
              "#", // smooth language comment opening character
              "'", // single quote
              "\\\\", // escaped backslash
              "\\t", // escaped tab
              "\\b", // escaped backspace
              "\\n", // escaped new line
              "\\r", // escaped carriage return
              "\\f", // escaped form feed
              "\\\"" // escaped double quotes
            })
        public void is_legal(String literal) {
          module("result = \"" + literal + "\";").loadsWithSuccess();
        }

        @Nested
        class causes_error_when {
          @Test
          void has_no_closing_quote() {
            module("""
                result = "abc;
                """).loadsWithProblems();
          }

          @Test
          void spans_to_next_line() {
            module("""
                result = "ab
                cd";
                """)
                .loadsWithProblems();
          }

          @Test
          void has_illegal_escape_seq() {
            module("""
                result = "\\A";
                """)
                .loadsWithError(
                    1,
                    "Illegal String literal: "
                        + "Illegal escape sequence at char index = 1. "
                        + "Legal sequences are: \\t \\b \\n \\r \\f \\\" \\\\.");
          }

          @Test
          void has_escape_seq_without_code() {
            var code = """
                result = "\\";
                """;
            module(code)
                .loadsWithError(
                    1,
                    "Illegal String literal: "
                        + "Missing escape code after backslash \\ at char index = 0.");
          }
        }
      }

      @Nested
      class _declaring_native_literal {
        @Nested
        class _causes_error_when {
          @Test
          void path_has_illegal_escape_seq() {
            var module =
                """
                @Native("\\A")
                String myFunc();
                """;
            var error = err(
                1,
                "Illegal String literal: "
                    + "Illegal escape sequence at char index = 1. "
                    + "Legal sequences are: \\t \\b \\n \\r \\f \\\" \\\\.");

            module(module).loadsWith(error);
          }

          @Test
          void path_has_escape_seq_without_code() {
            var module = """
                @Native("\\")
                String myFunc();""";
            var error = err(
                1,
                "Illegal String literal:"
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
        module("result = (" + literal + ");").loadsWithSuccess();
      }

      @Test
      void with_additional_comma() {
        module("result = (7, );")
            .loadsWithError(
                1,
                """
                extraneous input ',' expecting ')'
                result = (7, );
                           ^""");
      }
    }

    @Nested
    class _piped_value_consumption {
      @Test
      void not_consumed_by_select() {
        var code =
            """
            MyStruct{
              String myField,
            }
            myValue = myStruct("def");
            result = "abc" > myValue.myField;
            """;
        module(code).loadsWithError(5, "Piped value is not consumed.");
      }

      @Test
      void not_consumed_by_int_literal() {
        var code = """
            result = "abc" > 7;
            """;
        module(code).loadsWithError(1, "Piped value is not consumed.");
      }

      @Test
      void not_consumed_by_string_literal() {
        var code = """
            result = "abc" > "def";
            """;
        module(code).loadsWithError(1, "Piped value is not consumed.");
      }

      @Test
      void not_consumed_by_blob_literal() {
        var code = """
            result = "abc" > 0xAA;
            """;
        module(code).loadsWithError(1, "Piped value is not consumed.");
      }

      @Test
      void not_consumed_by_value_ref() {
        var code =
            """
            myValue = 7;
            result = "abc" > myValue;
            """;
        module(code).loadsWithError(2, "Piped value is not consumed.");
      }

      @Test
      void not_consumed_by_consuming_expr_inside_lambda_body() {
        var code = """
            result = 7 > () -> [];
            """;
        module(code).loadsWithError(1, "Piped value is not consumed.");
      }

      @Test
      void not_consumed_by_expression_inside_parens() {
        var code = """
            result = "abc" > (7);
            """;
        module(code).loadsWithError(1, "Piped value is not consumed.");
      }

      @Test
      void not_consumed_by_first_expr_of_inner_pipe_inside_parens() {
        var code = """
            result = "abc" > (7 > []);
            """;
        module(code).loadsWithError(1, "Piped value is not consumed.");
      }

      @Test
      void consumed_by_expression_after_parens_containing_inner_pipe() {
        var code =
            """
            String stringId(String string) = string;
            A id(A a) = a;
            result = "abc" > (stringId > id())();
            """;
        module(code).loadsWithSuccess();
      }

      @Test
      void not_consumed_by_expression_after_parens_containing_inner_pipe() {
        var code =
            """
            MyStruct{
              Int myField,
            }
            v = myStruct(7);
            A id(A a) = a;
            result = "abc" > (v > id()).myField;
            """;
        module(code).loadsWithError(6, "Piped value is not consumed.");
      }
    }
  }

  @Nested
  class _comments {
    @Test
    void full_line_comment() {
      module("""
          # ((( full line comment "
          result = "";
          """)
          .loadsWithSuccess();
    }

    @Test
    void trailing_comment() {
      module("""
          result = "" ;  # comment at the end of line
          """)
          .loadsWithSuccess();
    }
  }

  private static class TestedTypes implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      return TESTED_TYPES.stream().map(Arguments::of);
    }
  }

  private static class Literals implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(arguments("[]"), arguments("0x01"), arguments("\"abc\""), arguments("7"));
    }
  }

  @Nested
  class _func_type_literal {
    @Test
    void cannot_have_trailing_comma() {
      module(funcTDeclaration("String,")).loadsWithProblems();
    }

    @Test
    void cannot_have_only_comma() {
      module(funcTDeclaration(",")).loadsWithProblems();
    }

    @Test
    void cannot_have_leading_comma() {
      module(funcTDeclaration(",String")).loadsWithProblems();
    }

    @Test
    void cannot_have_two_trailing_commas() {
      module(funcTDeclaration("String,,")).loadsWithProblems();
    }

    private String funcTDeclaration(String string) {
      return """
              @Native("Impl.met")
              (PLACEHOLDER)->Blob myFunc();
              """
          .replace("PLACEHOLDER", string);
    }
  }

  private static String identifierNameWithIllegalCharacterError(String name, String character) {
    return "`"
        + name
        + "` is illegal identifier name. It must not contain '"
        + character
        + "' character.";
  }

  private static String structNameWithIllegalCharacterError(String name, String character) {
    return "`"
        + name
        + "` is illegal struct name. It must not contain '"
        + character
        + "' character.";
  }

  private static String identifierWithUppercaseFirstCharacterError(String name) {
    return "`" + name + "` is illegal identifier name. It must start with lowercase letter.";
  }

  private static String structWithLowercaseFirstCharacterError(String name) {
    return "`" + name + "` is illegal struct name. It must start with uppercase letter.";
  }
}
