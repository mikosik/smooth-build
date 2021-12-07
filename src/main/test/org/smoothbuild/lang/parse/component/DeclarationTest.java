package org.smoothbuild.lang.parse.component;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.type.TestedT.TESTED_MONOTYPES;
import static org.smoothbuild.lang.base.type.TestedT.TESTED_SINGLE_VARIABLE_POLYTYPES;
import static org.smoothbuild.lang.base.type.TestedT.TESTED_VALID_POLYTYPES;
import static org.smoothbuild.lang.base.type.TestingTsS.BLOB;
import static org.smoothbuild.lang.base.type.TestingTsS.STRING;
import static org.smoothbuild.lang.base.type.TestingTsS.a;
import static org.smoothbuild.lang.base.type.TestingTsS.f;
import static org.smoothbuild.testing.TestingModLoader.err;
import static org.smoothbuild.util.Strings.unlines;
import static org.smoothbuild.util.collect.NList.nList;

import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.lang.base.type.TestedT;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.TestingModLoader;

public class DeclarationTest extends TestingContext {
  @Nested
  class _members {
    @Nested
    class _struct {
      @Test
      public void declaring_empty_struct_is_allowed() {
        mod("MyStruct {}")
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
        mod(code)
            .loadsSuccessfully();
      }

      @Nested
      class _name {
        @Test
        public void that_is_normal_name() {
          mod("""
             MyStruct{}
             """)
              .loadsSuccessfully();
        }

        @Test
        public void that_is_illegal_fails() {
          mod("""
             MyStruct^{}
             """)
              .loadsWithError(1, """
            token recognition error at: '^'
            MyStruct^{}
                    ^""");
        }

        @Test
        public void that_starts_with_small_letter_fails() {
          mod("""
             myStruct{}
             """)
              .loadsWithError(1, """
              mismatched input '{' expecting {'=', ';', '('}
              myStruct{}
                      ^""");
        }

        @Test
        public void that_is_single_large_letter_fails() {
          mod("""
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
          public void can_be_monotype(TestedT testedT) {
            mod(unlines(
                testedT.typeDeclarationsAsString(),
                "MyStruct {",
                "  " + testedT.name() + " field,",
                "}"))
                .loadsSuccessfully();
          }

          @ParameterizedTest
          @ArgumentsSource(TestedValidPolytypes.class)
          public void can_be_valid_polytype(TestedT testedT) {
            mod(unlines(
                testedT.typeDeclarationsAsString(),
                "MyStruct {",
                "  " + testedT.name() + " field,",
                "}"))
                .loadsSuccessfully();
          }

          @ParameterizedTest
          @ArgumentsSource(TestedSingleVarPolytypes.class)
          public void cannot_be_single_var_polytype(TestedT testedT) {
            TestingModLoader module = mod(unlines(
                testedT.typeDeclarationsAsString(),
                "MyStruct {",
                "  " + testedT.name() + " field,",
                "}"));
            module.loadsWithError(3, "Type var(s) `A` are used once in declaration of `field`. " +
                "This means each one can be replaced with `Any`.");
          }

          @Test
          public void cannot_be_type_which_encloses_it() {
            mod("""
             MyStruct {
               MyStruct field
             }
             """)
                .loadsWithError("""
                  Type hierarchy contains cycle:
                  myBuild.smooth:2: MyStruct -> MyStruct""");
          }

          @Test
          public void cannot_be_array_type_which_elem_type_encloses_it() {
            mod("""
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
          public void cannot_declare_func_which_result_type_encloses_it() {
            mod("""
              MyStruct {
                MyStruct() field
              }
              """)
                .loadsWithError("""
                  Type hierarchy contains cycle:
                  myBuild.smooth:2: MyStruct -> MyStruct""");
          }

          @Test
          public void cannot_declare_func_which_param_type_encloses_it() {
            mod("""
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
            mod("""
             MyStruct {
               String field
             }
             """)
                .loadsSuccessfully();
          }

          @Test
          public void that_is_illegal_fails() {
            mod("""
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
            mod("""
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
            mod("""
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
          mod(structDeclaration("String field,"))
              .loadsSuccessfully()
              .containsType(structTS("MyStruct", nList(sigS(STRING, "field"))));
        }

        @Test
        public void cannot_have_only_comma() {
          mod(structDeclaration(","))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_leading_comma() {
          mod(structDeclaration(", String field"))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_two_trailing_commas() {
          mod(structDeclaration("String field,,"))
              .loadsWithProblems();
        }

        private String structDeclaration(String string) {
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
          mod("""
            myValue = "abc";
            """)
              .loadsSuccessfully();
        }

        @ParameterizedTest
        @ArgumentsSource(TestedMonotypes.class)
        public void can_be_monotype(TestedT type) {
          mod(unlines(
              "@Native(\"Impl.met\")",
              type.name() + " myFunc();",
              type.name() + " myValue = myFunc();",
              type.typeDeclarationsAsString()))
              .loadsSuccessfully();
        }

        @ParameterizedTest
        @ArgumentsSource(TestedValidPolytypes.class)
        public void can_be_valid_polytype(TestedT type) {
          mod(unlines(
              "@Native(\"Impl.met\")",
              type.name() + " myFunc();",
              type.name() + " myValue = myFunc();",
              type.typeDeclarationsAsString()))
              .loadsSuccessfully();
        }

        @ParameterizedTest
        @ArgumentsSource(TestedSingleVarPolytypes.class)
        public void cannot_be_single_var_polytype(TestedT type) {
          mod(unlines(
              "@Native(\"Impl.met\")",
              type.name() + " myFunc(" + type.name() + " param);",
              type.name() + " myValue = myFunc(\"abc\");",
              type.typeDeclarationsAsString()))
              .loadsWithError(3, "Type var(s) `A` are used once in declaration of `myValue`." +
                  " This means each one can be replaced with `Any`.");
        }

        @Test
        public void can_be_type_which_is_supertype_of_its_body_type() {
          mod("""
              @Native("impl")
              Nothing nothingFunc();
              String myValue = nothingFunc();
              """)
              .loadsSuccessfully();
        }

        @Test
        public void cannot_be_assigned_to_non_convertible_type_even_when_its_body_is_convertible() {
          mod("""
              @Native("impl")
              Nothing nothingFunc();
              String myValue = nothingFunc();
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
          mod("""
             myValue = "abc";
             """)
              .loadsSuccessfully();
        }

        @Test
        public void that_is_illegal_fails() {
          mod("""
             myValue^ = "abc";
             """)
              .loadsWithError(1, """
            token recognition error at: '^'
            myValue^ = "abc";
                   ^""");
        }

        @Test
        public void that_starts_with_large_letter_fails() {
          mod("""
             MyValue = "abc";
             """)
              .loadsWithError(1, """
            no viable alternative at input 'MyValue='
            MyValue = "abc";
                    ^""");
        }

        @Test
        public void that_is_single_large_letter_fails() {
          mod("""
             A = "abc";
             """)
              .loadsWithError(1, """
            no viable alternative at input 'A='
            A = "abc";
              ^""");
        }
      }

      @Test
      public void without_body_fails() {
        mod("""
            String result;
            """)
            .loadsWithError(1, "Value cannot have empty body.");
      }
    }

    @Nested
    class _func {
      @Test
      public void non_nat_func_without_body() {
        mod("""
          String myFunc();
          """)
            .loadsWithError(1, "Non native function cannot have empty body.");
      }

      @Test
      public void nat_func_with_body() {
        mod("""
          @Native("Impl.met")
          String myFunc() = "abc";
          """)
            .loadsWithError(2, "Native function cannot have body.");
      }

      @Test
      public void nat_func_without_declared_result_type_fails() {
        mod("""
        @Native("Impl.met")
        myFunc();
        """)
            .loadsWithError(2, "`myFunc` is native so it should have declared result type.");
      }

      @Test
      public void result_cannot_have_type_var_that_is_not_present_elsewhere() {
        mod("""
          @Native("Impl.met")
          A myFunc(String param);
          """)
            .loadsWithError(2, "Type var(s) `A` are used once in declaration of `myFunc`."
                + " This means each one can be replaced with `Any`.");
      }

      @Test
      public void result_cannot_have_arrayed_type_var_that_is_not_present_elsewhere() {
        mod("""
          [A] myFunc(String param) = [];
          """)
            .loadsWithError(1, "Type var(s) `A` are used once in declaration of `myFunc`."
                + " This means each one can be replaced with `Any`.");

      }

      @Nested
      class _result {
        @Nested
        class _type {
          @Test
          public void result_type_can_be_omitted() {
            mod("""
            myFunc() = "abc";
            """)
                .loadsSuccessfully();
          }

          @ParameterizedTest
          @ArgumentsSource(TestedMonotypes.class)
          public void can_be_monotype(TestedT type) {
            mod(unlines(
                "@Native(\"impl\")",
                type.name() + " myFunc();",
                type.declarationsAsString()))
                .loadsSuccessfully();
          }

          @ParameterizedTest
          @ArgumentsSource(TestedValidPolytypes.class)
          public void can_be_valid_polytype(TestedT type) {
            mod(unlines(
                "@Native(\"impl\")",
                type.name() + " myFunc();",
                type.typeDeclarationsAsString()))
                .loadsSuccessfully();
          }

          @ParameterizedTest
          @ArgumentsSource(TestedSingleVarPolytypes.class)
          public void cannot_be_single_var_polytype(TestedT type) {
            mod(unlines(
                "@Native(\"impl\")",
                type.name() + " myFunc();",
                type.typeDeclarationsAsString()))
                .loadsWithError(2, "Type var(s) `A` are used once in declaration of `myFunc`."
                    + " This means each one can be replaced with `Any`.");
          }

          @Test
          public void can_be_supertype_of_func_expression() {
            mod("""
                @Native("impl")
                Nothing nothingFunc();
                String myFunc() = nothingFunc();
                """)
                .loadsSuccessfully();
          }

          @Test
          public void cannot_be_assigned_to_non_convertible_type_even_when_its_body_type_is_convertible() {
            mod("""
                @Native("impl")
                Nothing nothingFunc();
                String myFunc() = nothingFunc();
                Nothing result = myFunc();
                """)
                .loadsWithError(4, "`result` has body which type is `String` and it is not "
                    + "convertible to its declared type `Nothing`.");
          }

          @ParameterizedTest
          @ArgumentsSource(TestedSingleVarPolytypes.class)
          public void can_be_single_var_polytype_when_param_type_has_such_var(TestedT type) {
            mod(unlines(
                "@Native(\"Impl.met\")",
                type.name() + " myFunc(" + type.name() + " param);",
                type.typeDeclarationsAsString()))
                .loadsSuccessfully();
          }
        }
      }

      @Nested
      class _name {
        @Test
        public void that_is_legal() {
          mod("""
             myFunc() = "abc";
             """)
              .loadsSuccessfully();
        }

        @Test
        public void that_is_illegal_fails() {
          mod("""
             myFunc^() = "abc";
             """)
              .loadsWithError(1, """
            token recognition error at: '^'
            myFunc^() = "abc";
                  ^""");
        }

        @Test
        public void that_starts_with_large_letter_fails() {
          mod("""
             MyFunc() = "abc";
             """)
              .loadsWithError(1, """
                missing NAME at '='
                MyFunc() = "abc";
                         ^""");
        }

        @Test
        public void that_is_single_large_letter_fails() {
          mod("""
             A() = "abc";
             """)
              .loadsWithError(1, """
              missing NAME at '='
              A() = "abc";
                  ^""");
        }
      }

      @Nested
      class _param {
        @Nested
        class _type {
          @ParameterizedTest
          @ArgumentsSource(TestedMonotypes.class)
          public void can_be_monotype(TestedT type) {
            mod(unlines(
                "@Native(\"Impl.met\")",
                "String myFunc(" + type.name() + " param);",
                type.typeDeclarationsAsString()))
                .loadsSuccessfully();
          }

          @ParameterizedTest
          @ArgumentsSource(TestedValidPolytypes.class)
          public void can_be_valid_polytype(TestedT type) {
            mod(unlines(
                "@Native(\"Impl.met\")",
                "String myFunc(" + type.name() + " param);",
                type.typeDeclarationsAsString()))
                .loadsSuccessfully();
          }

          @ParameterizedTest
          @ArgumentsSource(TestedSingleVarPolytypes.class)
          public void cannot_be_single_var_polytype(TestedT type) {
            mod(unlines(
                "@Native(\"Impl.met\")",
                "String myFunc(" + type.name() + " param);",
                type.typeDeclarationsAsString()))
                .loadsWithError(2, "Type var(s) `A` are used once in declaration of"
                    + " `myFunc`. This means each one can be replaced with `Any`.");
          }

          @ParameterizedTest
          @ArgumentsSource(TestedSingleVarPolytypes.class)
          public void can_be_single_var_polytype_param_when_some_other_param_has_such_type(
              TestedT type) {
            mod(unlines(
                "@Native(\"Impl.met\")",
                "Blob myFunc(" + type.name() + " param, " + type.name() + " param2);",
                type.typeDeclarationsAsString()))
                .loadsSuccessfully();
          }
        }

        @Nested
        class _name {
          @Test
          public void that_is_legal() {
            mod("""
             String myFunc(String name) = "abc";
             """)
                .loadsSuccessfully();
          }

          @Test
          public void that_is_illegal_fails() {
            mod("""
             String myFunc(String name^);
             """)
                .loadsWithError(1, """
              token recognition error at: '^'
              String myFunc(String name^);
                                       ^""");
          }

          @Test
          public void that_starts_with_large_letter_fails() {
            mod("""
             String myFunc(String Name);
             """)
                .loadsWithError(1, """
              mismatched input 'Name' expecting {'(', NAME}
              String myFunc(String Name);
                                   ^^^^""");
          }

          @Test
          public void that_is_single_large_letter_fails() {
            mod("""
             String myFunc(String A);
             """)
                .loadsWithError(1, """
              mismatched input 'A' expecting {'(', NAME}
              String myFunc(String A);
                                   ^""");
          }
        }

        @Test
        public void default_param_before_non_default_is_allowed() {
          mod("""
            @Native("Impl.met")
            String myFunc(
              String default = "value",
              String nonDefault);
            """)
              .loadsSuccessfully()
              .containsEval(natFuncS(2, STRING, "myFunc",
                  annS(1, stringS(1, "Impl.met")),
                  itemS(3, STRING, "default", stringS(3, "value")),
                  itemS(4, STRING, "nonDefault")));
        }

        @Test
        public void polytype_param_can_have_default_arg() {
          mod("""
        A myFunc(A value = "abc") = value;
        """)
              .loadsSuccessfully();
        }

        @Test
        public void default_arg_gets_converted_to_polytype_param() {
          mod("""
        [A] myFunc(A param1, [A] param2 = []) = param2;
        [String] result = myFunc("abc");
        """)
              .loadsSuccessfully();
        }
      }

      @Nested
      class _param_list {
        @Test
        public void can_have_trailing_comma() {
          mod(funcDeclaration("String param1,"))
              .loadsSuccessfully()
              .containsEval(defFuncS(1, STRING, "myFunc",
                  stringS(1, "abc"), itemS(1, STRING, "param1")));
        }

        @Test
        public void cannot_have_only_comma() {
          mod(funcDeclaration(","))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_leading_comma() {
          mod(funcDeclaration(",String string"))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_two_trailing_commas() {
          mod(funcDeclaration("String string,,"))
              .loadsWithProblems();
        }

        private String funcDeclaration(String string) {
          return """
              String myFunc(PLACEHOLDER) = "abc";
              """.replace("PLACEHOLDER", string);
        }
      }

      @Nested
      class _type_param_list {
        @Test
        public void can_have_trailing_comma() {
          mod(funcTDeclaration("String,"))
              .loadsSuccessfully()
              .containsEval(natFuncS(2, f(f(BLOB, STRING)), "myFunc", nList(),
                  annS(1, stringS(1, "Impl.met"))));
        }

        @Test
        public void cannot_have_only_comma() {
          mod(funcTDeclaration(","))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_leading_comma() {
          mod(funcTDeclaration(",String"))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_two_trailing_commas() {
          mod(funcTDeclaration("String,,"))
              .loadsWithProblems();
        }

        private String funcTDeclaration(String string) {
          return """
              @Native("Impl.met")
              Blob(PLACEHOLDER) myFunc();
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
          mod(code)
              .loadsWithError(2, "In call to function with type `String(String param)`:"
                  + " Too many positional arguments.");
        }

        @Test
        public void passing_less_positional_args_than_params_causes_error() {
          String code = """
              returnFirst(String param1, String param2) = param1;
              result = returnFirst("abc");
              """;
          mod(code)
              .loadsWithError(2, "In call to function with type `String(String param1,"
                  + " String param2)`: Parameter `param2` must be specified.");
        }

        @Test
        public void passing_less_positional_args_than_params_causes_error_version_without_name() {
          String code = """
              returnFirst(String param1, String param2) = param1;
              funcValue = returnFirst;
              result = funcValue("abc");
              """;
          mod(code)
              .loadsWithError(3, "In call to function with type `String(String, String)`:"
                  + " Parameter #2 must be specified.");
        }

        @Test
        public void named_arg_which_doesnt_exist_causes_error() {
          String code = """
              myIdentity(String param) = param;
              result = myIdentity(wrongName="abc");
              """;
          mod(code)
              .loadsWithError(2, "In call to function with type `String(String param)`: "
                  + "Unknown parameter `wrongName`.");
        }

        @Test
        public void named_args_can_be_passed_in_the_same_order_as_params() {
          String code = """
              returnFirst(String param1, String param2) = param1;
              result = returnFirst(param1="abc", param2="def");
              """;
          mod(code)
              .loadsSuccessfully();
        }

        @Test
        public void named_args_can_be_passed_in_different_order_than_params() {
          String code = """
              returnFirst(String param1, String param2) = param1;
              result = returnFirst(param2="def", param1="abc");
              """;
          mod(code)
              .loadsSuccessfully();
        }

        @Test
        public void all_named_args_must_come_after_positional() {
          String code = """
              returnFirst(String param1, String param2) = param1;
              result = returnFirst(param2="def", "abc");
              """;
          mod(code)
              .loadsWithError(2,
                  "In call to function with type `String(String param1, String param2)`: "
                      + "Positional arguments must be placed before named arguments.");
        }

        @Test
        public void assigning_arg_by_name_twice_causes_error() {
          String code = """
              myIdentity(String param) = param;
              result = myIdentity(param="abc", param="abc");
              """;
          mod(code)
              .loadsWithError(2, "In call to function with type `String(String param)`:"
                  + " `param` is already assigned.");
        }

        @Test
        public void assigning_by_name_arg_that_is_assigned_by_position_causes_error() {
          String code = """
              myIdentity(String param) = param;
              result = myIdentity("abc", param="abc");
              """;
          mod(code)
              .loadsWithError(2, "In call to function with type `String(String param)`: "
                  + "`param` is already assigned.");
        }

        @Test
        public void param_with_default_arg_can_be_assigned_positionally() {
          String code = """
              myIdentity(String param1="abc", String param2="def") = param1;
              result = myIdentity("abc", "def");
              """;
          mod(code)
              .loadsSuccessfully();
        }

        @Test
        public void param_with_default_arg_can_be_assigned_by_name() {
          String code = """
            myIdentity(String param1="abc", String param2="def") = param1;
            result = myIdentity(param1="abc", param2="def");
            """;
          mod(code)
              .loadsSuccessfully();
        }

        @Test
        public void func_param_name_is_stripped_during_assignment() {
          String code = """
            myFunc(String param) = param;
            valueReferencingFunc = myFunc;
            result = valueReferencingFunc(param="abc");
            """;
          mod(code)
              .loadsWithError(3,
                  "In call to function with type `String(String)`: Unknown parameter `param`.");
        }

        @Test
        public void func_default_arg_is_stripped_during_assignment() {
          String code = """
            myFunc(String param = "abc") = param;
            valueReferencingFunc = myFunc;
            result = valueReferencingFunc();
            """;
          mod(code)
              .loadsWithError(3, "In call to function with type `String(String)`: "
                  + "Parameter #1 must be specified.");
        }
      }

      @Nested
      class _ctor {
        @Test
        public void creating_empty_struct_instance_is_allowed() {
          String code = """
              MyStruct {}
              result = myStruct();
              """;
          mod(code)
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
          mod(code)
              .loadsSuccessfully();
        }

        @Test
        public void calling_ctor_without_all_params_causes_error() {
          String code = """
              MyStruct {
                String field,
              }
              result = myStruct();
              """;
          mod(code)
              .loadsWithError(4, "In call to function with type `MyStruct(String field)`:" +
                  " Parameter `field` must be specified.");
        }
      }

      @Nested
      class _arg_list {
        @Test
        public void can_have_trailing_comma() {
          mod(funcCall("0x07,"))
              .loadsSuccessfully()
              .containsEval(defValS(2, BLOB, "result",
                  callS(2, BLOB,
                      topRefS(2, f(BLOB, BLOB), "myFunc"),
                      blobS(2, 7))));
        }

        @Test
        public void cannot_have_only_comma() {
          mod(funcCall(","))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_leading_comma() {
          mod(funcCall(","))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_two_trailing_commas() {
          mod(funcCall("0x01,,"))
              .loadsWithProblems();
        }

        private String funcCall(String string) {
          return """
              Blob myFunc(Blob b) = b;
              result = myFunc(PLACEHOLDER);
              """.replace("PLACEHOLDER", string);
        }
      }
    }

    @Nested
    class _select {
      @Test
      public void reading_field() {
        String code = """
            MyStruct {
              String field,
            }
            String result = myStruct("abc").field;
            """;
        mod(code)
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
        mod(code)
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
          mod("result = " + literal + ";")
              .loadsSuccessfully();
        }

        @Nested
        class _causes_error_when {
          @Test
          public void has_only_one_digit() {
            mod("result = 0x1;")
                .loadsWithError(1, "Illegal Blob literal. Expected even number of digits.");
          }

          @Test
          public void has_odd_number_of_digits() {
            mod("result = 0x123;")
                .loadsWithError(1, "Illegal Blob literal. Expected even number of digits.");
          }

          @Test
          public void has_non_digit_character() {
            mod("result = 0xGG;")
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
          mod("result = " + literal + ";")
              .loadsSuccessfully();
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
            mod("result = " + literal + ";")
                .loadsWithError(1, "Illegal Int literal: `" + literal + "`.");
          }

          @Test
          public void has_two_minus_signs() {
            mod("result = --1;")
                .loadsWithError(1, """
                    token recognition error at: '--'
                    result = --1;
                             ^""");
          }

          @Test
          public void has_space_inside() {
            mod("result = 12 3;")
                .loadsWithError(1, """
                  extraneous input '3' expecting ';'
                  result = 12 3;
                              ^""");
          }

          @Test
          public void has_space_after_minus_sign() {
            mod("result = - 123;")
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
          mod("result = \"" + literal + "\";")
              .loadsSuccessfully();
        }

        @Nested
        class causes_error_when {
          @Test
          public void has_no_closing_quote() {
            mod("""
             result = "abc;
             """)
                .loadsWithProblems();
          }

          @Test
          public void spans_to_next_line() {
            mod("""
             result = "ab
             cd";
             """)
                .loadsWithProblems();
          }

          @Test
          public void has_illegal_escape_seq() {
            mod("""
             result = "\\A";
             """)
                .loadsWithError(1, "Illegal escape sequence at char index = 1. "
                    + "Legal sequences are: \\t \\b \\n \\r \\f \\\" \\\\.");
          }

          @Test
          public void has_escape_seq_without_code() {
            mod("""
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
        public void with_one_elem(String literal) {
          mod("result = [" + literal + "];")
              .loadsSuccessfully();
        }

        @ParameterizedTest
        @ArgumentsSource(ArrayElements.class)
        public void with_two_elems(String literal) {
          mod("result = [" + literal + ", " + literal + "];")
              .loadsSuccessfully();
        }

        @ParameterizedTest
        @ArgumentsSource(ArrayElements.class)
        public void with_array_containing_one_elem(String literal) {
          mod("result = [[" + literal + "]];")
              .loadsSuccessfully();
        }

        @ParameterizedTest
        @ArgumentsSource(ArrayElements.class)
        public void with_array_and_empty_array_elems(String literal) {
          mod("result = [[" + literal + "], []];")
              .loadsSuccessfully();
        }

        @ParameterizedTest
        @ArgumentsSource(ArrayElements.class)
        public void with_array_containing_two_elems(String literal) {
          mod("result = [[" + literal + ", " + literal + "]];")
              .loadsSuccessfully();
        }

        @Nested
        class _elem_list {
          @Test
          public void can_have_trailing_comma() {
            mod(arrayLiteral("0x07,"))
                .loadsSuccessfully()
                .containsEval(defValS(1, a(BLOB), "result",
                    orderS(1, BLOB, blobS(1, 7))));
          }

          @Test
          public void cannot_have_only_comma() {
            mod(arrayLiteral(","))
                .loadsWithProblems();
          }

          @Test
          public void cannot_have_leading_comma() {
            mod(arrayLiteral(",0x01"))
                .loadsWithProblems();
          }

          @Test
          public void cannot_have_two_trailing_commas() {
            mod(arrayLiteral("0x01,,"))
                .loadsWithProblems();
          }

          private String arrayLiteral(String string) {
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
        public void error_in_first_elem_doesnt_suppress_error_in_second_elem() {
          mod("""
            myFunc() = "abc";
            result = [
              myFunc(unknown1=""),
              myFunc(unknown2="")
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
          @Test
          public void path_has_illegal_escape_seq() {
            var module = """
                @Native("\\A")
                String myFunc();
                """;
            var error = err(1, "Illegal escape sequence at char index = 1. "
                + "Legal sequences are: \\t \\b \\n \\r \\f \\\" \\\\.");

            mod(module).loadsWith(error);
          }

          @Test
          public void path_has_escape_seq_without_code() {
            var module = """
                @Native("\\")
                String myFunc();""";
            var error = err(1, "Missing escape code after backslash \\ at char index = 0.");

            mod(module).loadsWith(error);
          }
        }
      }
    }

    @Nested
    class _pipe {
      @Test
      public void regression_test_error_in_expression_of_arg_of_not_first_elem_of_pipe() {
        mod("""
            String myFunc(String a, String b) = "abc";
            String myIdentity(String s) = s;
            result = "abc" | myIdentity(myFunc(unknown=""));
            """)
                .loadsWith(
                    err(3, "In call to function with type `String(String a, String b)`:"
                        + " Unknown parameter `unknown`."),
                    err(3, "In call to function with type `String(String s)`:"
                        + " Too many positional arguments.")
        );
      }

      @Test
      public void non_first_chain_in_a_pipe_must_have_func_call() {
        mod("""
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
      mod("""
           # ((( full line comment "
           result = "";
           """)
          .loadsSuccessfully();
    }

    @Test
    public void trailing_comment() {
      mod("""
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

  private static class TestedSingleVarPolytypes implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      return TESTED_SINGLE_VARIABLE_POLYTYPES.stream()
          .map(Arguments::of);
    }
  }
}
