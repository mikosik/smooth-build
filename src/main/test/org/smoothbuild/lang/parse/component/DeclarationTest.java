package org.smoothbuild.lang.parse.component;

import static java.lang.String.format;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.TestModuleLoader.err;
import static org.smoothbuild.lang.TestModuleLoader.module;
import static org.smoothbuild.lang.TestingLang.array;
import static org.smoothbuild.lang.TestingLang.blob;
import static org.smoothbuild.lang.TestingLang.call;
import static org.smoothbuild.lang.TestingLang.function;
import static org.smoothbuild.lang.TestingLang.nativ;
import static org.smoothbuild.lang.TestingLang.parameter;
import static org.smoothbuild.lang.TestingLang.reference;
import static org.smoothbuild.lang.TestingLang.string;
import static org.smoothbuild.lang.TestingLang.struct;
import static org.smoothbuild.lang.TestingLang.value;
import static org.smoothbuild.lang.base.type.TestedType.TESTED_MONOTYPES;
import static org.smoothbuild.lang.base.type.TestedType.TESTED_SINGLE_VARIABLE_POLYTYPES;
import static org.smoothbuild.lang.base.type.TestedType.TESTED_VALID_POLYTYPES;
import static org.smoothbuild.lang.base.type.TestingItemSignature.itemSignature;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.a;
import static org.smoothbuild.lang.base.type.TestingTypes.f;
import static org.smoothbuild.lang.base.type.TestingTypes.item;
import static org.smoothbuild.util.Strings.unlines;

import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.lang.TestModuleLoader;
import org.smoothbuild.lang.base.type.TestedType;

public class DeclarationTest {
  @Nested
  class _members {
    @Nested
    class _struct {
      @Test
      public void declaring_empty_struct_is_allowed() {
        module("MyStruct {}")
            .loadsSuccessfully();
      }

      @Test
      public void declaring_non_empty_struct_is_allowed() {
        String code = """
            MyStruct {
              String fieldA,
              String fieldB
            }
            """;
        module(code)
            .loadsSuccessfully();
      }

      @Nested
      class _name {
        @Test
        public void that_is_normal_name() {
          module("""
             MyStruct{}
             """)
              .loadsSuccessfully();
        }

        @Test
        public void that_is_illegal_fails() {
          module("""
             MyStruct^{}
             """)
              .loadsWithError(1, """
            token recognition error at: '^'
            MyStruct^{}
                    ^""");
        }

        @Test
        public void that_starts_with_small_letter_fails() {
          module("""
             myStruct{}
             """)
              .loadsWithError(1, """
              mismatched input '{' expecting {'=', ';', '('}
              myStruct{}
                      ^""");
        }

        @Test
        public void that_is_single_large_letter_fails() {
          module("""
             A{}
             """)
              .loadsWithError(1, "`A` is illegal struct name. It must have at least two characters.");
        }
      }

      @Nested
      class _field {
        @Nested
        class _type {
          @ParameterizedTest
          @ArgumentsSource(TestedMonotypes.class)
          public void can_be_monotype(TestedType testedType) {
            module(unlines(
                testedType.typeDeclarationsAsString(),
                "MyStruct {",
                "  " + testedType.name() + " field,",
                "}"))
                .loadsSuccessfully();
          }

          @ParameterizedTest
          @ArgumentsSource(TestedValidPolytypes.class)
          public void can_be_valid_polytype(TestedType testedType) {
            module(unlines(
                testedType.typeDeclarationsAsString(),
                "MyStruct {",
                "  " + testedType.name() + " field,",
                "}"))
                .loadsSuccessfully();
          }

          @ParameterizedTest
          @ArgumentsSource(TestedSingleVariablePolytypes.class)
          public void cannot_be_single_variable_polytype(TestedType testedType) {
            TestModuleLoader module = module(unlines(
                testedType.typeDeclarationsAsString(),
                "MyStruct {",
                "  " + testedType.name() + " field,",
                "}"));
            module.loadsWithError(3, "Type variable(s) `A` are used once in declaration of `field`. " +
                "This means each one can be replaced with `Any`.");
          }

          @Test
          public void cannot_be_type_which_encloses_it() {
            module("""
             MyStruct {
               MyStruct field
             }
             """)
                .loadsWithError("""
                  Type hierarchy contains cycle:
                  myBuild.smooth:2: MyStruct -> MyStruct""");
          }

          @Test
          public void cannot_be_array_type_which_element_type_encloses_it() {
            module("""
              MyStruct {
                String firstField,
                [MyStruct] field
              }
              """)
                .loadsWithError("""
                  Type hierarchy contains cycle:
                  myBuild.smooth:3: MyStruct -> MyStruct""");
          }

          @Test
          public void cannot_declare_function_which_result_type_encloses_it() {
            module("""
              MyStruct {
                MyStruct() field
              }
              """)
                .loadsWithError("""
                  Type hierarchy contains cycle:
                  myBuild.smooth:2: MyStruct -> MyStruct""");
          }

          @Test
          public void cannot_declare_function_which_parameter_type_encloses_it() {
            module("""
              MyStruct {
                Blob(MyStruct) field
              }
              """)
                .loadsWithError("""
                  Type hierarchy contains cycle:
                  myBuild.smooth:2: MyStruct -> MyStruct""");
          }
        }

        @Nested
        class _name {
          @Test
          public void that_is_legal() {
            module("""
             MyStruct {
               String field
             }
             """)
                .loadsSuccessfully();
          }

          @Test
          public void that_is_illegal_fails() {
            module("""
             MyStruct {
               String field^
             }
             """)
                .loadsWithError(2, """
              token recognition error at: '^'
                String field^
                            ^""");
          }

          @Test
          public void that_starts_with_large_letter_fails() {
            module("""
             MyStruct {
               String Field
             }
             """)
                .loadsWithError(2, """
              mismatched input 'Field' expecting {'(', NAME}
                String Field
                       ^^^^^""");
          }

          @Test
          public void that_is_single_large_letter_fails() {
            module("""
             MyStruct {
               String A
             }
             """)
                .loadsWithError(2, """
              mismatched input 'A' expecting {'(', NAME}
                String A
                       ^""");
          }
        }
      }

      @Nested
      class _field_list {
        @Test
        public void can_have_trailing_comma() {
          module(structDeclaration("String field,"))
              .loadsSuccessfully()
              .containsType(struct("MyStruct", itemSignature(STRING, "field")));
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

        private String structDeclaration(CharSequence string) {
          return """
              MyStruct { PLACEHOLDER }
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
              .loadsSuccessfully();
        }

        @ParameterizedTest
        @ArgumentsSource(TestedMonotypes.class)
        public void can_be_monotype(TestedType type) {
          module(unlines(
              "@Native(\"Impl.met\")",
              type.name() + " myValue;",
              type.typeDeclarationsAsString()))
              .loadsSuccessfully();
        }

        @ParameterizedTest
        @ArgumentsSource(TestedValidPolytypes.class)
        public void can_be_valid_polytype(TestedType type) {
          module(unlines(
              "@Native(\"Impl.met\")",
              type.name() + " myValue;",
              type.typeDeclarationsAsString()))
              .loadsSuccessfully();
        }

        @ParameterizedTest
        @ArgumentsSource(TestedSingleVariablePolytypes.class)
        public void cannot_be_single_variable_polytype(TestedType type) {
          module(unlines(
              "@Native(\"Impl.met\")",
              type.name() + " myValue;",
              type.typeDeclarationsAsString()))
              .loadsWithError(2, "Type variable(s) `A` are used once in declaration of `myValue`." +
                  " This means each one can be replaced with `Any`.");
        }

        @Test
        public void can_be_type_which_is_supertype_of_its_body_type() {
          module("""
              @Native("impl")
              Nothing nothing;
              String myValue = nothing;
              """)
              .loadsSuccessfully();
        }

        @Test
        public void cannot_be_assigned_to_non_convertible_type_even_when_its_body_is_convertible() {
          module("""
              @Native("impl")
              Nothing nothing;
              String myValue = nothing;
              Nothing result = myValue;
              """)
              .loadsWithError(4, "`result` has body which type is `String` and it is not "
                  + "convertible to its declared type `Nothing`.");
        }
      }

      @Nested
      class _name {
        @Test
        public void that_is_legal() {
          module("""
             myValue = "abc";
             """)
              .loadsSuccessfully();
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
              .loadsWithError(1, """
            no viable alternative at input 'MyValue='
            MyValue = "abc";
                    ^""");
        }

        @Test
        public void that_is_single_large_letter_fails() {
          module("""
             A = "abc";
             """)
              .loadsWithError(1, """
            no viable alternative at input 'A='
            A = "abc";
              ^""");
        }
      }

      @Test
      public void non_native_value_without_body_fails() {
        module("""
            String result;
            """)
            .loadsWithError(1, "Non native value cannot have empty body.");
      }

      @Test
      public void native_value_with_body_fails() {
        module("""
            @Native("Impl.met")
            String myValue = "abc";
            """)
            .loadsWithError(2, "Native value cannot have body.");
      }

      @Test
      public void native_value_without_declared_result_type_fails() {
        module("""
        @Native("Impl.met")
        myFunction;""")
            .loadsWithError(2, "`myFunction` is native so it should have declared result type.");
      }
    }

    @Nested
    class _function {
      @Test
      public void non_native_function_without_body() {
        module("""
          String myFunction();
          """)
            .loadsWithError(1, "Non native function cannot have empty body.");
      }

      @Test
      public void native_function_with_body() {
        module("""
          @Native("Impl.met")
          String myFunction() = "abc";
          """)
            .loadsWithError(2, "Native function cannot have body.");
      }

      @Test
      public void native_function_without_declared_result_type_fails() {
        module("""
        @Native("Impl.met")
        myFunction();
        """)
            .loadsWithError(2, "`myFunction` is native so it should have declared result type.");
      }

      @Test
      public void result_cannot_have_type_variable_that_is_not_present_elsewhere() {
        module("""
          @Native("Impl.met")
          A myFunction(String param);
          """)
            .loadsWithError(2, "Type variable(s) `A` are used once in declaration of `myFunction`."
                + " This means each one can be replaced with `Any`.");
      }

      @Test
      public void result_cannot_have_arrayed_type_variable_that_is_not_present_elsewhere() {
        module("""
          [A] myFunction(String param) = [];
          """)
            .loadsWithError(1, "Type variable(s) `A` are used once in declaration of `myFunction`."
                + " This means each one can be replaced with `Any`.");

      }

      @Nested
      class _result {
        @Nested
        class _type {
          @Test
          public void result_type_can_be_omitted() {
            module("""
            myFunction() = "abc";
            """)
                .loadsSuccessfully();
          }

          @ParameterizedTest
          @ArgumentsSource(TestedMonotypes.class)
          public void can_be_monotype(TestedType type) {
            module(unlines(
                "@Native(\"impl\")",
                type.name() + " myFunction();",
                type.declarationsAsString()))
                .loadsSuccessfully();
          }

          @ParameterizedTest
          @ArgumentsSource(TestedValidPolytypes.class)
          public void can_be_valid_polytype(TestedType type) {
            module(unlines(
                "@Native(\"impl\")",
                type.name() + " myFunction();",
                type.typeDeclarationsAsString()))
                .loadsSuccessfully();
          }

          @ParameterizedTest
          @ArgumentsSource(TestedSingleVariablePolytypes.class)
          public void cannot_be_single_variable_polytype(TestedType type) {
            module(unlines(
                "@Native(\"impl\")",
                type.name() + " myFunction();",
                type.typeDeclarationsAsString()))
                .loadsWithError(2, "Type variable(s) `A` are used once in declaration of `myFunction`."
                    + " This means each one can be replaced with `Any`.");
          }

          @Test
          public void can_be_supertype_of_function_expression() {
            module("""
                @Native("impl")
                Nothing nothing;
                String myFunction() = nothing;
                """)
                .loadsSuccessfully();
          }

          @Test
          public void cannot_be_assigned_to_non_convertible_type_even_when_its_body_type_is_convertible() {
            module("""
                @Native("impl")
                Nothing nothing;
                String myFunction() = nothing;
                Nothing result = myFunction();
                """)
                .loadsWithError(4, "`result` has body which type is `String` and it is not "
                    + "convertible to its declared type `Nothing`.");
          }

          @ParameterizedTest
          @ArgumentsSource(TestedSingleVariablePolytypes.class)
          public void can_be_single_variable_polytype_when_param_type_has_such_variable(TestedType type) {
            module(unlines(
                "@Native(\"Impl.met\")",
                type.name() + " myFunction(" + type.name() + " param);",
                type.typeDeclarationsAsString()))
                .loadsSuccessfully();
          }
        }
      }

      @Nested
      class _name {
        @Test
        public void that_is_legal() {
          module("""
             myFunction() = "abc";
             """)
              .loadsSuccessfully();
        }

        @Test
        public void that_is_illegal_fails() {
          module("""
             myFunction^() = "abc";
             """)
              .loadsWithError(1, """
            token recognition error at: '^'
            myFunction^() = "abc";
                      ^""");
        }

        @Test
        public void that_starts_with_large_letter_fails() {
          module("""
             MyFunction() = "abc";
             """)
              .loadsWithError(1, """
                missing NAME at '='
                MyFunction() = "abc";
                             ^""");
        }

        @Test
        public void that_is_single_large_letter_fails() {
          module("""
             A() = "abc";
             """)
              .loadsWithError(1, """
              missing NAME at '='
              A() = "abc";
                  ^""");
        }
      }

      @Nested
      class _parameter {
        @Nested
        class _type {
          @ParameterizedTest
          @ArgumentsSource(TestedMonotypes.class)
          public void can_be_monotype(TestedType type) {
            module(unlines(
                "@Native(\"Impl.met\")",
                "String myFunction(" + type.name() + " param);",
                type.typeDeclarationsAsString()))
                .loadsSuccessfully();
          }

          @ParameterizedTest
          @ArgumentsSource(TestedValidPolytypes.class)
          public void can_be_valid_polytype(TestedType type) {
            module(unlines(
                "@Native(\"Impl.met\")",
                "String myFunction(" + type.name() + " param);",
                type.typeDeclarationsAsString()))
                .loadsSuccessfully();
          }

          @ParameterizedTest
          @ArgumentsSource(TestedSingleVariablePolytypes.class)
          public void cannot_be_single_variable_polytype(TestedType type) {
            module(unlines(
                "@Native(\"Impl.met\")",
                "String myFunction(" + type.name() + " param);",
                type.typeDeclarationsAsString()))
                .loadsWithError(2, "Type variable(s) `A` are used once in declaration of"
                    + " `myFunction`. This means each one can be replaced with `Any`.");
          }

          @ParameterizedTest
          @ArgumentsSource(TestedSingleVariablePolytypes.class)
          public void can_be_single_variable_polytype_param_when_some_other_param_has_such_type(
              TestedType type) {
            module(unlines(
                "@Native(\"Impl.met\")",
                "Blob myFunction(" + type.name() + " param, " + type.name() + " param2);",
                type.typeDeclarationsAsString()))
                .loadsSuccessfully();
          }
        }

        @Nested
        class _name {
          @Test
          public void that_is_legal() {
            module("""
             String myFunction(String name) = "abc";
             """)
                .loadsSuccessfully();
          }

          @Test
          public void that_is_illegal_fails() {
            module("""
             String myFunction(String name^);
             """)
                .loadsWithError(1, """
              token recognition error at: '^'
              String myFunction(String name^);
                                           ^""");
          }

          @Test
          public void that_starts_with_large_letter_fails() {
            module("""
             String myFunction(String Name);
             """)
                .loadsWithError(1, """
              mismatched input 'Name' expecting {'(', NAME}
              String myFunction(String Name);
                                       ^^^^""");
          }

          @Test
          public void that_is_single_large_letter_fails() {
            module("""
             String myFunction(String A);
             """)
                .loadsWithError(1, """
              mismatched input 'A' expecting {'(', NAME}
              String myFunction(String A);
                                       ^""");
          }
        }

        @Test
        public void default_parameter_before_non_default_is_allowed() {
          module("""
            @Native("Impl.met")
            String myFunction(
              String default = "value",
              String nonDefault);
            """)
              .loadsSuccessfully()
              .containsReferencable(function(2, STRING, "myFunction",
                  nativ(1, string(1, "Impl.met")),
                  parameter(3, STRING, "default", string(3, "value")),
                  parameter(4, STRING, "nonDefault")));
        }

        @Test
        public void polytype_parameter_can_have_default_value() {
          module("""
        A myFunc(A value = "abc") = value;
        """)
              .loadsSuccessfully();
        }

        @Test
        public void default_value_gets_converted_to_polytype_parameter() {
          module("""
        [A] myFunc(A param1, [A] param2 = []) = param2;
        [String] result = myFunc("abc");
        """)
              .loadsSuccessfully();
        }
      }

      @Nested
      class _parameter_list {
        @Test
        public void can_have_trailing_comma() {
          module(functionDeclaration("String param1,"))
              .loadsSuccessfully()
              .containsReferencable(
                  function(1, STRING, "myFunction", string(1, "abc"), parameter(1, STRING, "param1")));
        }

        @Test
        public void cannot_have_only_comma() {
          module(functionDeclaration(","))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_leading_comma() {
          module(functionDeclaration(",String string"))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_two_trailing_commas() {
          module(functionDeclaration("String string,,"))
              .loadsWithProblems();
        }

        private String functionDeclaration(CharSequence string) {
          return """
              String myFunction(PLACEHOLDER) = "abc";
              """.replace("PLACEHOLDER", string);
        }
      }

      @Nested
      class _type_parameter_list {
        @Test
        public void can_have_trailing_comma() {
          module(functionTypeDeclaration("String,"))
              .loadsSuccessfully()
              .containsReferencable(
                  value(2, f(BLOB, STRING), "myValue", nativ(1, string(1, "Impl.met"))));
        }

        @Test
        public void cannot_have_only_comma() {
          module(functionTypeDeclaration(","))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_leading_comma() {
          module(functionTypeDeclaration(",String"))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_two_trailing_commas() {
          module(functionTypeDeclaration("String,,"))
              .loadsWithProblems();
        }

        private String functionTypeDeclaration(CharSequence string) {
          return """
              @Native("Impl.met")
              Blob(PLACEHOLDER) myValue;
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
      class _function {
        @Test
        public void passing_more_positional_arguments_than_parameters_causes_error() {
          String code = """
              myIdentity(String param) = param;
              result = myIdentity("abc", "def");
              """;
          module(code)
              .loadsWithError(2, "In call to function with type `String(String param)`:"
                  + " Too many positional arguments.");
        }

        @Test
        public void passing_less_positional_arguments_than_parameters_causes_error() {
          String code = """
              returnFirst(String param1, String param2) = param1;
              result = returnFirst("abc");
              """;
          module(code)
              .loadsWithError(2, "In call to function with type `String(String param1,"
                  + " String param2)`: Parameter `param2` must be specified.");
        }

        @Test
        public void passing_less_positional_arguments_than_parameters_causes_error_version_without_name() {
          String code = """
              returnFirst(String param1, String param2) = param1;
              functionValue = returnFirst;
              result = functionValue("abc");
              """;
          module(code)
              .loadsWithError(3, "In call to function with type `String(String, String)`:"
                  + " Parameter #2 must be specified.");
        }

        @Test
        public void named_argument_which_doesnt_exist_causes_error() {
          String code = """
              myIdentity(String param) = param;
              result = myIdentity(wrongName="abc");
              """;
          module(code)
              .loadsWithError(2, "In call to function with type `String(String param)`: "
                  + "Unknown parameter `wrongName`.");
        }

        @Test
        public void named_arguments_can_be_passed_in_the_same_order_as_parameters() {
          String code = """
              returnFirst(String param1, String param2) = param1;
              result = returnFirst(param1="abc", param2="def");
              """;
          module(code)
              .loadsSuccessfully();
        }

        @Test
        public void named_arguments_can_be_passed_in_different_order_than_parameters() {
          String code = """
              returnFirst(String param1, String param2) = param1;
              result = returnFirst(param2="def", param1="abc");
              """;
          module(code)
              .loadsSuccessfully();
        }

        @Test
        public void all_named_arguments_must_come_after_positional() {
          String code = """
              returnFirst(String param1, String param2) = param1;
              result = returnFirst(param2="def", "abc");
              """;
          module(code)
              .loadsWithError(2,
                  "In call to function with type `String(String param1, String param2)`: "
                      + "Positional arguments must be placed before named arguments.");
        }

        @Test
        public void assigning_argument_by_name_twice_causes_error() {
          String code = """
              myIdentity(String param) = param;
              result = myIdentity(param="abc", param="abc");
              """;
          module(code)
              .loadsWithError(2, "In call to function with type `String(String param)`:"
                  + " `param` is already assigned.");
        }

        @Test
        public void assigning_by_name_argument_that_is_assigned_by_position_causes_error() {
          String code = """
              myIdentity(String param) = param;
              result = myIdentity("abc", param="abc");
              """;
          module(code)
              .loadsWithError(2, "In call to function with type `String(String param)`: "
                  + "`param` is already assigned.");
        }

        @Test
        public void parameter_with_default_value_can_be_assigned_positionally() {
          String code = """
              myIdentity(String param1="abc", String param2="def") = param1;
              result = myIdentity("abc", "def");
              """;
          module(code)
              .loadsSuccessfully();
        }

        @Test
        public void parameter_with_default_value_can_be_assigned_by_name() {
          String code = """
            myIdentity(String param1="abc", String param2="def") = param1;
            result = myIdentity(param1="abc", param2="def");
            """;
          module(code)
              .loadsSuccessfully();
        }
      }

      @Nested
      class _constructor {
        @Test
        public void creating_empty_struct_instance_is_allowed() {
          String code = """
              MyStruct {}
              result = myStruct();
              """;
          module(code)
              .loadsSuccessfully();
        }

        @Test
        public void creating_non_empty_struct_is_allowed() {
          String code = """
              MyStruct {
                String field,
              }
              result = myStruct("abc");
              """;
          module(code)
              .loadsSuccessfully();
        }

        @Test
        public void calling_constructor_without_all_parameters_causes_error() {
          String code = """
              MyStruct {
                String field,
              }
              result = myStruct();
              """;
          module(code)
              .loadsWithError(4, "In call to function with type `MyStruct(String field)`:" +
                  " Parameter `field` must be specified.");
        }
      }

      @Nested
      class _argument_list {
        @Test
        public void can_have_trailing_comma() {
          module(functionCall("0x07,"))
              .loadsSuccessfully()
              .containsReferencable(value(2, BLOB, "result",
                  call(2, BLOB, reference(2, f(BLOB, item(BLOB, "b")), "myFunction"), blob(2, 7))));
        }

        @Test
        public void cannot_have_only_comma() {
          module(functionCall(","))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_leading_comma() {
          module(functionCall(","))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_two_trailing_commas() {
          module(functionCall("0x01,,"))
              .loadsWithProblems();
        }

        private String functionCall(CharSequence string) {
          return """
              Blob myFunction(Blob b) = b;
              result = myFunction(PLACEHOLDER);
              """.replace("PLACEHOLDER", string);
        }
      }
    }

    @Nested
    class _field_read {
      @Test
      public void reading_field() {
        String code = """
            MyStruct {
              String field,
            }
            String result = myStruct("abc").field;
            """;
        module(code)
            .loadsSuccessfully();
      }

      @Test
      public void reading_field_that_does_not_exist_causes_error() {
        String code = """
            MyStruct {
              String field,
            }
            result = myStruct("abc").otherField;
            """;
        module(code)
            .loadsWithError(4, "Struct `MyStruct` doesn't have field `otherField`.");
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
              .loadsSuccessfully();
        }

        @Nested
        class causes_error_when {
          @Test
          public void has_only_one_digit() {
            module("result = 0x1;")
                .loadsWithError(1, "Illegal Blob literal. Expected even number of digits.");
          }

          @Test
          public void has_odd_number_of_digits() {
            module("result = 0x123;")
                .loadsWithError(1, "Illegal Blob literal. Expected even number of digits.");
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
              .loadsSuccessfully();
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
          public void has_illegal_escape_sequence() {
            module("""
             result = "\\A";
             """)
                .loadsWithError(1, "Illegal escape sequence at char index = 1. "
                    + "Legal sequences are: \\t \\b \\n \\r \\f \\\" \\\\.");
          }

          @Test
          public void has_escape_sequence_without_code() {
            module("""
             result = "\\";
             """)
                .loadsWithError(1, "Missing escape code after backslash \\ at char index = 0.");
          }
        }
      }

      @Nested
      class _declaring_array_literal {
        @ParameterizedTest
        @ArgumentsSource(ArrayElements.class)
        public void with_one_element(String literal) {
          module("result = [" + literal + "];")
              .loadsSuccessfully();
        }

        @ParameterizedTest
        @ArgumentsSource(ArrayElements.class)
        public void with_two_elements(String literal) {
          module("result = [" + literal + ", " + literal + "];")
              .loadsSuccessfully();
        }

        @ParameterizedTest
        @ArgumentsSource(ArrayElements.class)
        public void with_array_containing_one_element(String literal) {
          module("result = [[" + literal + "]];")
              .loadsSuccessfully();
        }

        @ParameterizedTest
        @ArgumentsSource(ArrayElements.class)
        public void with_array_and_empty_array_elements(String literal) {
          module("result = [[" + literal + "], []];")
              .loadsSuccessfully();
        }

        @ParameterizedTest
        @ArgumentsSource(ArrayElements.class)
        public void with_array_containing_two_elements(String literal) {
          module("result = [[" + literal + ", " + literal + "]];")
              .loadsSuccessfully();
        }

        @Nested
        class _element_list {
          @Test
          public void can_have_trailing_comma() {
            module(arrayLiteral("0x07,"))
                .loadsSuccessfully()
                .containsReferencable(value(1, a(BLOB), "result", array(1, BLOB, blob(1, 7))));
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

          private String arrayLiteral(CharSequence string) {
            return """
              result = [ PLACEHOLDER ];
              """.replace("PLACEHOLDER", string);
          }
        }

        private static class ArrayElements implements ArgumentsProvider {
          @Override
          public Stream<Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                arguments("[]"),
                arguments("0x01"),
                arguments("\"abc\"")
            );
          }
        }

        @Test
        public void error_in_first_element_doesnt_suppress_error_in_second_element() {
          module("""
            myFunction() = "abc";
            result = [
              myFunction(unknown1=""),
              myFunction(unknown2="")
            ];
            """)
              .loadsWith(
                  err(3, "In call to function with type `String()`: Unknown parameter `unknown1`."),
                  err(4, "In call to function with type `String()`: Unknown parameter `unknown2`.")
              );
        }
      }

      @Nested
      class _declaring_native_literal {
        @Nested
        class _causes_error_when {
          @ParameterizedTest
          @ValueSource(strings = {"", "()"})
          public void path_has_illegal_escape_sequence(String valueOrFunction) {
            var module = format("""
          @Native("\\A")
          String value%s;
          """, valueOrFunction);
            var error = err(1, "Illegal escape sequence at char index = 1. "
                + "Legal sequences are: \\t \\b \\n \\r \\f \\\" \\\\.");

            module(module).loadsWith(error);
          }

          @ParameterizedTest
          @ValueSource(strings = {"", "()"})
          public void path_has_escape_sequence_without_code(String valueOrFunction) {
            var module = format("""
          @Native("\\")
          String value%s;
             """, valueOrFunction);
            var error = err(1, "Missing escape code after backslash \\ at char index = 0.");

            module(module).loadsWith(error);
          }
        }
      }
    }

    @Nested
    class _pipe {
      @Test
      public void regression_test_error_in_expression_of_argument_of_not_first_element_of_pipe() {
        module("""
            String myFunction(String a, String b) = "abc";
            String myIdentity(String s) = s;
            result = "abc" | myIdentity(myFunction(unknown=""));
            """)
                .loadsWith(
                    err(3, "In call to function with type `String(String a, String b)`:"
                        + " Unknown parameter `unknown`."),
                    err(3, "In call to function with type `String(String s)`:"
                        + " Too many positional arguments.")
        );
      }

      @Test
      public void non_first_chain_in_a_pipe_must_have_function_call() {
        module("""
            MyStruct {
              String myField
            }
            myValue = myStruct("def");
            result = "abc" | myValue.myField;
            """)
            .loadsWithError(5, """
                extraneous input ';' expecting {'(', '.'}
                result = "abc" | myValue.myField;
                                                ^""");
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
          .loadsSuccessfully();
    }

    @Test
    public void trailing_comment() {
      module("""
           result = "" ;  # comment at the end of line
           """)
          .loadsSuccessfully();
    }
  }

  private static class TestedMonotypes implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      return TESTED_MONOTYPES.stream()
          .map(Arguments::of);
    }
  }

  private static class TestedValidPolytypes implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      return TESTED_VALID_POLYTYPES.stream()
          .map(Arguments::of);
    }
  }

  private static class TestedSingleVariablePolytypes implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      return TESTED_SINGLE_VARIABLE_POLYTYPES.stream()
          .map(Arguments::of);
    }
  }
}