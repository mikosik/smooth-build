package org.smoothbuild.parse.component;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.testing.TestingModLoader.err;
import static org.smoothbuild.testing.type.TestedTSF.TESTED_MONOTYPES;
import static org.smoothbuild.testing.type.TestedTSF.TESTED_SINGLE_VARIABLE_POLYTYPES;
import static org.smoothbuild.testing.type.TestedTSF.TESTED_VALID_POLYTYPES;
import static org.smoothbuild.util.Strings.unlines;
import static org.smoothbuild.util.collect.Lists.list;
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
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.type.TestedTS;

public class DeclarationTest extends TestingContext {
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
        module("MyStruct {}")
            .loadsWithSuccess();
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
            .loadsWithSuccess();
      }

      @Nested
      class _name {
        @Test
        public void that_is_normal_name() {
          module("""
             MyStruct{}
             """)
              .loadsWithSuccess();
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
          public void can_be_monotype(TestedTS testedT) {
            module(unlines(
                testedT.typeDeclarationsAsString(),
                "MyStruct {",
                "  " + testedT.name() + " field,",
                "}"))
                .loadsWithSuccess();
          }

          @Test
          public void cannot_be_polytype() {
            module("""
                MyStruct {
                 A(B) field
                }
                """)
                .loadsWithError(
                    2, "Field type cannot be polymorphic. Found field `field` with type `A(B)`.");
          }

          @Test
          public void cannot_be_polytype_array() {
            module("""
                MyStruct {
                 [A] field
                }
                """)
                .loadsWithError(
                    2, "Field type cannot be polymorphic. Found field `field` with type `[A]`.");
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
          public void cannot_be_array_type_which_elem_type_encloses_it() {
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
          public void cannot_declare_func_which_result_type_encloses_it() {
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
          public void cannot_declare_func_which_param_type_encloses_it() {
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
                .loadsWithSuccess();
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
              .loadsWithSuccess()
              .containsType(structTS("MyStruct", nList(sigS(stringTS(), "field"))));
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
              .loadsWithSuccess();
        }

        @ParameterizedTest
        @ArgumentsSource(TestedMonotypes.class)
        public void can_be_monotype(TestedTS type) {
          module(unlines(
              "@Native(\"Impl.met\")",
              type.name() + " myFunc();",
              type.name() + " myValue = myFunc();",
              type.typeDeclarationsAsString()))
              .loadsWithSuccess();
        }

        @Test
        public void cannot_be_polytype() {
          var code = """
              @Native("Impl.met")
              A myId(A param);
              A myValue = myId("abc");
          """;
          module(code)
              .loadsWithError(3, "Unknown type variable(s): A");
        }

        @Test
        public void cannot_be_polytype_assigned_from_func() {
          var code = """
              @Native("Impl.met")
              A myId(A param);
              A(A) myValue = myId;
          """;
          module(code)
              .loadsWithError(3, "Unknown type variable(s): A");
        }

        @Test
        public void can_be_monotype_function_assigned_from_polytype_func() {
          var code = """
              @Native("Impl.met")
              A myId(A param);
              Int(Int) myValue = myId;
          """;
          module(code)
              .loadsWithSuccess();
        }

        @Test
        public void can_be_type_which_is_supertype_of_its_body_type() {
          module("""
              @Native("impl")
              Nothing nothingFunc();
              String myValue = nothingFunc();
              """)
              .loadsWithSuccess();
        }

        @Test
        public void cannot_be_assigned_to_non_convertible_type_even_when_its_body_is_convertible() {
          module("""
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
          @ArgumentsSource(TestedMonotypes.class)
          public void can_be_monotype(TestedTS type) {
            module(unlines(
                "@Native(\"impl\")",
                type.name() + " myFunc();",
                type.declarationsAsString()))
                .loadsWithSuccess();
          }

          @Test
          public void cannot_contain_var_not_present_in_any_param_type() {
            var code = """
                @Native("Impl.met")
                A myFunc(B b, C c);
                """;
            module(code)
                .loadsWithError(2, "Unknown type variable(s): A");
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

          @Test
          public void cannot_be_polytype_func() {
            var code = """
                @Native("Impl.met")
                A(A) myFunc(B b, C c);
                """;
            module(code)
                .loadsWithError(2, "Unknown type variable(s): A");
          }

          @Test
          public void can_be_supertype_of_func_body() {
            module("""
                @Native("impl")
                Nothing nothingFunc();
                String myFunc() = nothingFunc();
                """)
                .loadsWithSuccess();
          }

          @Test
          public void cannot_be_assigned_to_non_convertible_type_even_when_its_body_type_is_convertible() {
            module("""
                @Native("impl")
                Nothing nothingFunc();
                String myFunc() = nothingFunc();
                Nothing result = myFunc();
                """)
                .loadsWithError(4, "`result` has body which type is `String` and it is not "
                    + "convertible to its declared type `Nothing`.");
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
              .loadsWithError(1, """
                missing NAME at '='
                MyFunc() = "abc";
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
      class _param {
        @Nested
        class _type {
          @ParameterizedTest
          @ArgumentsSource(TestedMonotypes.class)
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
                String myFunc(A(A) f);
                """;
            module(code)
                .loadsWithSuccess();
          }

          @Test
          public void can_be_single_var_polytype() {
            var code = """
                @Native("Impl.met")
                String myFunc(Int(A) f);
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
             String myFunc(String Name);
             """)
                .loadsWithError(1, """
              mismatched input 'Name' expecting {'(', NAME}
              String myFunc(String Name);
                                   ^^^^""");
          }

          @Test
          public void that_is_single_large_letter_fails() {
            module("""
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
          module("""
            @Native("Impl.met")
            String myFunc(
              String default = "value",
              String nonDefault);
            """)
              .loadsWithSuccess()
              .containsTopRefable(natFuncS(2, stringTS(), "myFunc", nList(
                  itemS(3, stringTS(), "default", stringS(3, "value")),
                  itemS(4, stringTS(), "nonDefault")), nativeS(1, stringS(1, "Impl.met"))));
        }

        @Test
        public void param_with_vars_can_have_default_arg() {
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
              .containsTopRefable(defFuncS(1, stringTS(), "myFunc", stringS(1, "abc"),
                  nList(itemS(1, stringTS(), "param1"))));
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

      @Nested
      class _type_param_list {
        @Test
        public void can_have_trailing_comma() {
          module(funcTDeclaration("String,"))
              .loadsWithSuccess()
              .containsTopRefable(natFuncS(2, funcTS(funcTS(blobTS(), list(stringTS()))), "myFunc",
                  nList(), nativeS(1, stringS(1, "Impl.met"))));
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
          module(code)
              .loadsWithError(2, "In call to function with parameters `(String param)`:"
                  + " Too many positional arguments.");
        }

        @Test
        public void passing_less_positional_args_than_params_causes_error() {
          String code = """
              returnFirst(String param1, String param2) = param1;
              result = returnFirst("abc");
              """;
          module(code)
              .loadsWithError(2, "In call to function with parameters `(String param1,"
                  + " String param2)`: Parameter `param2` must be specified.");
        }

        @Test
        public void passing_less_positional_args_than_params_causes_error_version_without_name() {
          String code = """
              returnFirst(String param1, String param2) = param1;
              funcValue = returnFirst;
              result = funcValue("abc");
              """;
          module(code)
              .loadsWithError(3, "In call to function with parameters `(String, String)`:"
                  + " Parameter #2 must be specified.");
        }

        @Test
        public void named_arg_which_doesnt_exist_causes_error() {
          String code = """
              myIdentity(String param) = param;
              result = myIdentity(wrongName="abc");
              """;
          module(code)
              .loadsWithError(2, "In call to function with parameters `(String param)`: "
                  + "Unknown parameter `wrongName`.");
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
              .loadsWithError(2,
                  "In call to function with parameters `(String param1, String param2)`: "
                      + "Positional arguments must be placed before named arguments.");
        }

        @Test
        public void assigning_arg_by_name_twice_causes_error() {
          String code = """
              myIdentity(String param) = param;
              result = myIdentity(param="abc", param="abc");
              """;
          module(code)
              .loadsWithError(2, "In call to function with parameters `(String param)`:"
                  + " `param` is already assigned.");
        }

        @Test
        public void assigning_by_name_arg_that_is_assigned_by_position_causes_error() {
          String code = """
              myIdentity(String param) = param;
              result = myIdentity("abc", param="abc");
              """;
          module(code)
              .loadsWithError(2, "In call to function with parameters `(String param)`: "
                  + "`param` is already assigned.");
        }

        @Test
        public void param_with_default_arg_can_be_assigned_positionally() {
          String code = """
              myIdentity(String param1="abc", String param2="def") = param1;
              result = myIdentity("abc", "def");
              """;
          module(code)
              .loadsWithSuccess();
        }

        @Test
        public void param_with_default_arg_can_be_assigned_by_name() {
          String code = """
            myIdentity(String param1="abc", String param2="def") = param1;
            result = myIdentity(param1="abc", param2="def");
            """;
          module(code)
              .loadsWithSuccess();
        }

        @Test
        public void func_param_name_is_stripped_during_assignment() {
          String code = """
            myFunc(String param) = param;
            valueReferencingFunc = myFunc;
            result = valueReferencingFunc(param="abc");
            """;
          module(code)
              .loadsWithError(3,
                  "In call to function with parameters `(String)`: Unknown parameter `param`.");
        }

        @Test
        public void func_default_arg_is_stripped_during_assignment() {
          String code = """
            myFunc(String param = "abc") = param;
            valueReferencingFunc = myFunc;
            result = valueReferencingFunc();
            """;
          module(code)
              .loadsWithError(3, "In call to function with parameters `(String)`: "
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
          module(code)
              .loadsWithSuccess();
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
              .loadsWithSuccess();
        }

        @Test
        public void calling_ctor_without_all_params_causes_error() {
          String code = """
              MyStruct {
                String field,
              }
              result = myStruct();
              """;
          module(code)
              .loadsWithError(4, "In call to function with parameters `(String field)`:" +
                  " Parameter `field` must be specified.");
        }
      }

      @Nested
      class _arg_list {
        @Test
        public void can_have_trailing_comma() {
          module(funcCall("0x07,"))
              .loadsWithSuccess()
              .containsTopRefable(defValS(2, blobTS(), "result",
                  callS(2, blobTS(),
                      refS(2, funcTS(blobTS(), list(blobTS())), "myFunc"),
                      blobS(2, 7))));
        }

        @Test
        public void cannot_have_only_comma() {
          module(funcCall(","))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_leading_comma() {
          module(funcCall(","))
              .loadsWithProblems();
        }

        @Test
        public void cannot_have_two_trailing_commas() {
          module(funcCall("0x01,,"))
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
        module(code)
            .loadsWithSuccess();
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
              .loadsWithSuccess();
        }

        @Nested
        class _causes_error_when {
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
                .loadsWithError(1, "Illegal escape sequence at char index = 1. "
                    + "Legal sequences are: \\t \\b \\n \\r \\f \\\" \\\\.");
          }

          @Test
          public void has_escape_seq_without_code() {
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
        public void with_one_elem(String literal) {
          module("result = [" + literal + "];")
              .loadsWithSuccess();
        }

        @ParameterizedTest
        @ArgumentsSource(ArrayElements.class)
        public void with_two_elems(String literal) {
          module("result = [" + literal + ", " + literal + "];")
              .loadsWithSuccess();
        }

        @ParameterizedTest
        @ArgumentsSource(ArrayElements.class)
        public void with_array_containing_one_elem(String literal) {
          module("result = [[" + literal + "]];")
              .loadsWithSuccess();
        }

        @ParameterizedTest
        @ArgumentsSource(ArrayElements.class)
        public void with_array_and_empty_array_elems(String literal) {
          module("result = [[" + literal + "], []];")
              .loadsWithSuccess();
        }

        @ParameterizedTest
        @ArgumentsSource(ArrayElements.class)
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
                .containsTopRefable(defValS(1, arrayTS(blobTS()), "result",
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
          module("""
            myFunc() = "abc";
            result = [
              myFunc(unknown1=""),
              myFunc(unknown2="")
            ];
            """)
              .loadsWith(
                  err(3, "In call to function with parameters `()`: Unknown parameter `unknown1`."),
                  err(4, "In call to function with parameters `()`: Unknown parameter `unknown2`.")
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

            module(module).loadsWith(error);
          }

          @Test
          public void path_has_escape_seq_without_code() {
            var module = """
                @Native("\\")
                String myFunc();""";
            var error = err(1, "Missing escape code after backslash \\ at char index = 0.");

            module(module).loadsWith(error);
          }
        }
      }
    }

    @Nested
    class _pipe {
      @Test
      public void regression_test_error_in_expression_of_arg_of_not_first_elem_of_pipe() {
        module("""
            String myFunc(String a, String b) = "abc";
            String myIdentity(String s) = s;
            result = "abc" | myIdentity(myFunc(unknown=""));
            """)
                .loadsWith(
                    err(3, "In call to function with parameters `(String a, String b)`:"
                        + " Unknown parameter `unknown`."),
                    err(3, "In call to function with parameters `(String s)`:"
                        + " Too many positional arguments.")
        );
      }

      @Test
      public void non_first_chain_in_a_pipe_must_have_func_call() {
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
