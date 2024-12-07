package org.smoothbuild.compilerfrontend.acceptance;

import static org.smoothbuild.common.collect.NList.nlist;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.compilerfrontend.testing.FrontendCompileTester;

public class VisibilityTest extends FrontendCompileTester {
  @Nested
  class _visibility {
    @Nested
    class _local {
      @Test
      void value_declared_above_is_visible() {
        var code =
            """
            String myValue = "abc";
            result = myValue;
            """;
        module(code).loadsWithSuccess();
      }

      @Test
      void value_declared_below_is_visible() {
        var code =
            """
            result = myValue;
            String myValue = "abc";
            """;
        module(code).loadsWithSuccess();
      }

      @Test
      void func_declared_above_is_visible() {
        var code =
            """
            String myFunc() = "abc";
            result = myFunc;
            """;
        module(code).loadsWithSuccess();
      }

      @Test
      void func_declared_below_is_visible() {
        var code =
            """
            result = myFunc;
            String myFunc() = "abc";
            """;
        module(code).loadsWithSuccess();
      }

      @Test
      void constructor_declared_above_is_visible() {
        var code = """
            MyStruct {}
            result = MyStruct;
            """;
        module(code).loadsWithSuccess();
      }

      @Test
      void constructor_declared_below_is_visible() {
        var code = """
            result = MyStruct;
            MyStruct {}
            """;
        module(code).loadsWithSuccess();
      }

      @Test
      void struct_declared_above_is_visible() {
        var code =
            """
            MyStruct {}
            @Native("impl.met")
            MyStruct myFunc();
            """;
        module(code).loadsWithSuccess();
      }

      @Test
      void struct_declared_below_is_visible() {
        var code =
            """
            @Native("impl.met")
            MyStruct myFunc();
            MyStruct {}
            """;
        module(code).loadsWithSuccess();
      }
    }

    @Nested
    class _imported {
      @Test
      void value_is_visible() {
        var imported = "Int otherModuleValue = 7;";
        var code = "myValue = otherModuleValue;";
        module(code).withImported(imported).loadsWithSuccess();
      }

      @Test
      void func_is_visible() {
        var imported = "Int otherModuleFunc() = 7;";
        var code = "myValue = otherModuleFunc;";
        module(code).withImported(imported).loadsWithSuccess();
      }

      @Test
      void constructor_is_visible() {
        var imported = "OtherModuleStruct {}";
        var code = "myValue = OtherModuleStruct;";
        module(code).withImported(imported).loadsWithSuccess();
      }

      @Test
      void struct_is_visible() {
        var imported = "OtherModuleStruct {}";
        var code =
            """
            @Native("impl.met")
            OtherModuleStruct myFunc();
            """;
        module(code).withImported(imported).loadsWithSuccess();
      }
    }

    @Nested
    class _param {
      @Nested
      class default_value {
        @Test
        void is_not_visible_outside_function_body_via_function_name_prefixed_reference() {
          // Despite internally CallsPreprocessor creates references to parameter default values
          // that are further in processing handled correctly, for now it is not available
          // in the language to simplify it.
          var code =
              """
              myFunc(Int param = 7) = 8;
              myResult = myFunc:param;
              """;
          module(code).loadsWithProblems();
        }
      }

      @Nested
      class _of_named_function {
        @Test
        void is_visible_in_its_body() {
          var code = """
              myFunc(String param) = param;
              """;
          module(code).loadsWithSuccess();
        }

        @Test
        void is_not_visible_outside_its_body() {
          var code =
              """
              myFunc(String param) = "abc";
              result = param;
              """;
          module(code).loadsWithError(2, "`param` is undefined.");
        }

        @Test
        void is_not_visible_in_its_default_value() {
          var code = "func(String withDefault = withDefault) = withDefault;";
          module(code).loadsWithError(1, "`withDefault` is undefined.");
        }

        @Test
        void is_not_visible_in_default_value_of_other_param() {
          var code = "func(String param, String withDefault = param) = param;";
          module(code).loadsWithError(1, "`param` is undefined.");
        }
      }

      @Nested
      class _of_lambda {
        @Test
        void is_visible_in_its_body() {
          var code = """
              myValue = (String param) -> param;
              """;
          module(code).loadsWithSuccess();
        }

        @Test
        void is_not_visible_outside_its_body() {
          var code =
              """
              myValue = (String param) -> "abc";
              result = param;
              """;
          module(code).loadsWithError(2, "`param` is undefined.");
        }
      }
    }

    @Nested
    class _undefined {
      @Nested
      class _eval_cannot_be_used_as {
        @Test
        void lambda_argument() {
          var code = """
              myValue = ((Int int) -> int)(undefined);
              """;
          module(code).loadsWithError(1, "`undefined` is undefined.");
        }

        @Test
        void lambda_body() {
          var code = """
              myValue = () -> undefined;
              """;
          module(code).loadsWithError(1, "`undefined` is undefined.");
        }

        @Test
        void expression_function_argument() {
          var code =
              """
              String myFunc(Blob b) = "abc";
              result = myFunc(undefined);
              """;
          module(code).loadsWithError(2, "`undefined` is undefined.");
        }

        @Test
        void expression_function_body() {
          var code = """
              result() = undefined;
              """;
          module(code).loadsWithError(1, "`undefined` is undefined.");
        }

        @Test
        void expression_function_in_call_expression() {
          var code = """
              result = undefined();
              """;
          module(code).loadsWithError(1, "`undefined` is undefined.");
        }

        @Test
        void value_body() {
          var code = """
              result = undefined;
              """;
          module(code).loadsWithError(1, "`undefined` is undefined.");
        }

        @Test
        void array_elem() {
          var code = """
              result = [undefined];
              """;
          module(code).loadsWithError(1, "`undefined` is undefined.");
        }

        @Test
        void param_default_value() {
          var code = """
              String myFunc(Blob b = undefined) = "abc";
              """;
          module(code).loadsWithError(1, "`undefined` is undefined.");
        }
      }

      @Nested
      class _type_cannot_be_used {
        @Test
        void as_type_of_named_value() {
          var code =
              """
              @Bytecode("Impl.met")
              Undefined myValue;
              """;
          module(code).loadsWithError(2, "`Undefined` type is undefined.");
        }

        @Test
        void as_type_of_native_function_result() {
          var code =
              """
              @Native("Impl.met")
              Undefined myFunc();
              """;
          module(code).loadsWithError(2, "`Undefined` type is undefined.");
        }

        @Test
        void as_type_of_named_function_result() {
          var code = """
              [Undefined] myFunc() = [];
              """;
          module(code).loadsWithError(1, "`Undefined` type is undefined.");
        }

        @Test
        void as_type_of_named_function_parameter() {
          var code = """
              String myFunc(Undefined param) = "abc";
              """;
          module(code).loadsWithError(1, "`Undefined` type is undefined.");
        }

        @Test
        void inside_param_default_value() {
          var code =
              """
              Int myFunc((A)->Int param = (Undefined x) -> 7) = 7;
              """;
          module(code).loadsWithError(1, "`Undefined` type is undefined.");
        }

        @Test
        void as_type_of_lambda_parameter() {
          var code = """
              myValue = (Undefined param) -> 7;
              """;
          module(code).loadsWithError(1, "`Undefined` type is undefined.");
        }

        @Test
        void as_type_of_struct_field() {
          var code =
              """
              MyStruct {
                UndefinedType field
              }
              """;
          module(code).loadsWithError(2, "`UndefinedType` type is undefined.");
        }
      }
    }
  }

  @Nested
  class _cycle {
    @Nested
    class one_elem_cycle {
      @Test
      void value() {
        var code = """
            myValue = myValue;
            """;
        var error =
            """
            Reference graph contains cycle:
            {t-project}/module.smooth:1: myValue ~> myValue""";
        module(code).loadsWithError(error);
      }

      @Test
      void func() {
        var code = """
            myFunc1() = myFunc1();
            """;
        var error =
            """
            Reference graph contains cycle:
            {t-project}/module.smooth:1: myFunc1 ~> myFunc1""";
        module(code).loadsWithError(error);
      }

      @Test
      void struct() {
        var code =
            """
            MyStruct {
              MyStruct myField
            }
            """;
        var error =
            """
            Type hierarchy contains cycle:
            {t-project}/module.smooth:2: MyStruct ~> MyStruct""";
        module(code).loadsWithError(error);
      }

      @Test
      void struct_through_array() {
        var code =
            """
            MyStruct {
              [MyStruct] myField
            }
            """;
        var error =
            """
            Type hierarchy contains cycle:
            {t-project}/module.smooth:2: MyStruct ~> MyStruct""";
        module(code).loadsWithError(error);
      }

      @Test
      void struct_through_func_result() {
        var code =
            """
            MyStruct {
              ()->MyStruct myField
            }
            """;
        var error =
            """
            Type hierarchy contains cycle:
            {t-project}/module.smooth:2: MyStruct ~> MyStruct""";
        module(code).loadsWithError(error);
      }

      @Test
      void struct_through_func_param() {
        var code =
            """
            MyStruct {
              (MyStruct)->Blob myField
            }
            """;
        var error =
            """
            Type hierarchy contains cycle:
            {t-project}/module.smooth:2: MyStruct ~> MyStruct""";
        module(code).loadsWithError(error);
      }
    }

    @Nested
    class two_elements_cycle {
      @Test
      void value_value() {
        var code =
            """
            myValue1 = myValue2;
            myValue2 = myValue1;
            """;
        var error =
            """
            Reference graph contains cycle:
            {t-project}/module.smooth:1: myValue1 ~> myValue2
            {t-project}/module.smooth:2: myValue2 ~> myValue1""";
        module(code).loadsWithError(error);
      }

      @Test
      void func_func() {
        var code =
            """
            myFunc1() = myFunc2();
            myFunc2() = myFunc1();
            """;
        var error =
            """
            Reference graph contains cycle:
            {t-project}/module.smooth:1: myFunc1 ~> myFunc2
            {t-project}/module.smooth:2: myFunc2 ~> myFunc1""";
        module(code).loadsWithError(error);
      }

      @Test
      void func_func_through_arg() {
        var code =
            """
            String myFunc() = myIdentity(myFunc());
            String myIdentity(String s) = s;
            """;
        var error =
            """
            Reference graph contains cycle:
            {t-project}/module.smooth:1: myFunc ~> myFunc""";
        module(code).loadsWithError(error);
      }

      @Test
      void value_value_through_arg() {
        var code =
            """
            String myIdentity(String s) = s;
            String myValue = myIdentity(myValue);
            """;
        var error =
            """
            Reference graph contains cycle:
            {t-project}/module.smooth:2: myValue ~> myValue""";
        module(code).loadsWithError(error);
      }

      @Test
      void struct_struct() {
        var code =
            """
            MyStruct1 {
              MyStruct2 myField
            }
            MyStruct2 {
              MyStruct1 myField
            }
            """;
        var error =
            """
            Type hierarchy contains cycle:
            {t-project}/module.smooth:2: MyStruct1 ~> MyStruct2
            {t-project}/module.smooth:5: MyStruct2 ~> MyStruct1""";
        module(code).loadsWithError(error);
      }

      @Test
      void struct_struct_through_array() {
        var code =
            """
            MyStruct1 {
              MyStruct2 myField
            }
            MyStruct2 {
              [MyStruct1] myField
            }
            """;
        var error =
            """
            Type hierarchy contains cycle:
            {t-project}/module.smooth:2: MyStruct1 ~> MyStruct2
            {t-project}/module.smooth:5: MyStruct2 ~> MyStruct1""";
        module(code).loadsWithError(error);
      }

      @Test
      void struct_struct_through_func_result() {
        var code =
            """
            MyStruct1 {
              MyStruct2 myField
            }
            MyStruct2 {
              ()->MyStruct1 myField
            }
            """;
        var error =
            """
            Type hierarchy contains cycle:
            {t-project}/module.smooth:2: MyStruct1 ~> MyStruct2
            {t-project}/module.smooth:5: MyStruct2 ~> MyStruct1""";
        module(code).loadsWithError(error);
      }

      @Test
      void struct_struct_through_func_param() {
        var code =
            """
            MyStruct1 {
              MyStruct2 myField
            }
            MyStruct2 {
              (MyStruct1)->Blob myField
            }
            """;
        var error =
            """
            Type hierarchy contains cycle:
            {t-project}/module.smooth:2: MyStruct1 ~> MyStruct2
            {t-project}/module.smooth:5: MyStruct2 ~> MyStruct1""";
        module(code).loadsWithError(error);
      }
    }

    @Nested
    class three_elem_cycle {
      @Test
      void value_value_value() {
        var code =
            """
            myValue1 = myValue2;
            myValue2 = myValue3;
            myValue3 = myValue1;
            """;
        var error =
            """
            Reference graph contains cycle:
            {t-project}/module.smooth:1: myValue1 ~> myValue2
            {t-project}/module.smooth:2: myValue2 ~> myValue3
            {t-project}/module.smooth:3: myValue3 ~> myValue1""";
        module(code).loadsWithError(error);
      }

      @Test
      void func_func_func() {
        var code =
            """
            myFunc1() = myFunc2();
            myFunc2() = myFunc3();
            myFunc3() = myFunc1();
            """;
        var error =
            """
            Reference graph contains cycle:
            {t-project}/module.smooth:1: myFunc1 ~> myFunc2
            {t-project}/module.smooth:2: myFunc2 ~> myFunc3
            {t-project}/module.smooth:3: myFunc3 ~> myFunc1""";
        module(code).loadsWithError(error);
      }

      @Test
      void value_func_value() {
        var code =
            """
            myValue1 = myFunc();
            myFunc() = myValue2;
            myValue2 = myValue1;
            """;
        var error =
            """
            Reference graph contains cycle:
            {t-project}/module.smooth:1: myValue1 ~> myFunc
            {t-project}/module.smooth:2: myFunc ~> myValue2
            {t-project}/module.smooth:3: myValue2 ~> myValue1""";
        module(code).loadsWithError(error);
      }

      @Test
      void func_value_func() {
        var code =
            """
            myFunc1() = myValue;
            myValue = myFunc2();
            myFunc2() = myFunc1();
            """;
        var error =
            """
            Reference graph contains cycle:
            {t-project}/module.smooth:1: myFunc1 ~> myValue
            {t-project}/module.smooth:2: myValue ~> myFunc2
            {t-project}/module.smooth:3: myFunc2 ~> myFunc1""";
        module(code).loadsWithError(error);
      }

      @Test
      void struct_struct_struct_through_array() {
        var code =
            """
            MyStruct1 {
              MyStruct2 myField
            }
            MyStruct2 {
              MyStruct3 myField
            }
            MyStruct3 {
              [MyStruct1] myField
            }
            """;
        var error =
            """
            Type hierarchy contains cycle:
            {t-project}/module.smooth:2: MyStruct1 ~> MyStruct2
            {t-project}/module.smooth:5: MyStruct2 ~> MyStruct3
            {t-project}/module.smooth:8: MyStruct3 ~> MyStruct1""";
        module(code).loadsWithError(error);
      }

      @Test
      void struct_struct_struct_through_func_result() {
        var code =
            """
            MyStruct1 {
              [MyStruct2] myField
            }
            MyStruct2 {
              [MyStruct3] myField
            }
            MyStruct3 {
              ()->MyStruct1 myField
            }
            """;
        var error =
            """
            Type hierarchy contains cycle:
            {t-project}/module.smooth:2: MyStruct1 ~> MyStruct2
            {t-project}/module.smooth:5: MyStruct2 ~> MyStruct3
            {t-project}/module.smooth:8: MyStruct3 ~> MyStruct1""";
        module(code).loadsWithError(error);
      }

      @Test
      void struct_struct_struct_through_func_param() {
        var code =
            """
            MyStruct1 {
              [MyStruct2] myField
            }
            MyStruct2 {
              [MyStruct3] myField
            }
            MyStruct3 {
              (MyStruct1)->Blob myField
            }
            """;
        var error =
            """
            Type hierarchy contains cycle:
            {t-project}/module.smooth:2: MyStruct1 ~> MyStruct2
            {t-project}/module.smooth:5: MyStruct2 ~> MyStruct3
            {t-project}/module.smooth:8: MyStruct3 ~> MyStruct1""";
        module(code).loadsWithError(error);
      }
    }
  }

  @Nested
  class _shadowing {
    @Nested
    class _named_value_shadowing {
      @Nested
      class _imported {
        @Test
        void value_succeeds() {
          var imported = "otherModuleValue = 7;";
          var code = "otherModuleValue = 8;";
          module(code)
              .withImported(imported)
              .loadsWithSuccess()
              .containsEvaluableWithSchema("otherModuleValue", sSchema(sIntType()));
        }

        @Test
        void func_succeeds() {
          var imported = "otherModuleFunc() = 8;";
          var code = "otherModuleFunc = 7;";
          module(code)
              .withImported(imported)
              .loadsWithSuccess()
              .containsEvaluableWithSchema("otherModuleFunc", sSchema(sIntType()));
        }
      }

      @Nested
      class _local {
        @Test
        void value_fails() {
          var code =
              """
              myValue = "abc";
              myValue = "def";
              """;
          module(code).loadsWithError(2, alreadyDefinedIn(moduleFullPath(), "myValue"));
        }

        @Test
        void func_fails() {
          var code =
              """
              myFunc() = "abc";
              myFunc = "def";
              """;
          module(code).loadsWithError(2, alreadyDefinedIn(moduleFullPath(), "myFunc"));
        }
      }
    }

    @Nested
    class _named_function_shadowing {
      @Nested
      class _imported {
        @Test
        void value_succeeds() {
          var imported = "otherModuleValue = 7;";
          var code = "otherModuleValue() = 8;";
          module(code)
              .withImported(imported)
              .loadsWithSuccess()
              .containsEvaluableWithSchema("otherModuleValue", sSchema(sIntFuncType()));
        }

        @Test
        void function_succeeds() {
          var imported = "otherModuleFunc() = 7;";
          var code = "otherModuleFunc() = 8;";
          module(code)
              .withImported(imported)
              .loadsWithSuccess()
              .containsEvaluableWithSchema("otherModuleFunc", sSchema(sIntFuncType()));
        }
      }

      @Nested
      class _local {
        @Test
        void named_value_fails() {
          var code =
              """
              myValue = "abc";
              myValue() = "def";
              """;
          module(code).loadsWithError(2, alreadyDefinedIn(moduleFullPath(), "myValue"));
        }

        @Test
        void named_function_fails() {
          var code =
              """
              myFunc() = "abc";
              myFunc() = "def";
              """;
          module(code).loadsWithError(2, alreadyDefinedIn(moduleFullPath(), "myFunc"));
        }
      }
    }

    @Nested
    class _named_function_param_shadowing {
      @Test
      void other_param_fails() {
        var code =
            """
            String myFunc(
              String param,
              String param) = "abc";
            """;
        module(code).loadsWithError(3, alreadyDefinedIn(moduleFullPath(), 2, "param"));
      }

      @Nested
      class _imported {
        @Test
        void named_value_succeeds() {
          var imported = "otherModuleValue = 7;";
          var code = "Int myFunc(String otherModuleValue) = 8;";
          module(code).withImported(imported).loadsWithSuccess();
        }

        @Test
        void named_function_succeeds() {
          var imported = "otherModuleFunc() = 7;";
          var code = "Int myFunc(String otherModuleFunc) = 8;";
          module(code).withImported(imported).loadsWithSuccess();
        }
      }

      @Nested
      class _local {
        @Test
        void named_value_succeeds() {
          var code =
              """
              myValue = "abc";
              String myFunc(String myValue) = "abc";
              """;
          module(code).loadsWithSuccess();
        }

        @Test
        void named_function_succeeds() {
          var code =
              """
              myFunc() = "abc";
              String myOtherFunc(String myFunc) = "abc";
              """;
          module(code).loadsWithSuccess();
        }
      }
    }

    @Nested
    class _lambda_param_shadowing {
      @Test
      void other_param_fails() {
        var code =
            """
            myValue = (
              String param,
              String param) -> 7;
            """;
        module(code).loadsWithError(3, alreadyDefinedIn(moduleFullPath(), 2, "param"));
      }

      @Nested
      class _imported {
        @Test
        void named_value_succeeds() {
          var imported = "otherModuleValue = 8;";
          var code = "myValue = (String otherModuleValue) -> 7;";
          module(code).withImported(imported).loadsWithSuccess();
        }

        @Test
        void named_function_succeeds() {
          var imported = "otherModuleFunc() = 8;";
          var code = "myValue = (String otherModuleFunc) -> 7;";
          module(code).withImported(imported).loadsWithSuccess();
        }
      }

      @Nested
      class _local {
        @Test
        void named_value_succeeds() {
          var code =
              """
              myValue = "abc";
              otherValue = (String myValue) -> 7;
              """;
          module(code).loadsWithSuccess();
        }

        @Test
        void named_function_succeeds() {
          var code =
              """
              myFunc() = "abc";
              myValue = (String myFunc) -> 7;
              """;
          module(code).loadsWithSuccess();
        }
      }
    }

    @Nested
    class _struct_shadowing {
      @Nested
      class _imported {
        @Test
        void base_type_succeeds() {
          var code = "String {}";
          module(code).loadsWithSuccess().containsType(sStructType("String", nlist()));
        }

        @Test
        void struct_succeeds() {
          var code = "OtherModuleStruct {}";
          var imported = "OtherModuleStruct {Int int}";
          module(code)
              .withImported(imported)
              .loadsWithSuccess()
              .containsType(sStructType("OtherModuleStruct", nlist()));
        }
      }

      @Nested
      class _local {
        @Test
        void struct_fails() {
          var code =
              """
              OtherModuleStruct {}
              OtherModuleStruct {}
              """;
          module(code).loadsWith(err(2, alreadyDefinedIn(moduleFullPath(), "OtherModuleStruct")));
        }
      }
    }

    @Nested
    class _field_shadowing {
      @Test
      void other_field_fails() {
        var code =
            """
            MyStruct {
              String field,
              String field
            }
            """;
        module(code).loadsWithError(3, alreadyDefinedIn(moduleFullPath(), 2, "field"));
      }

      @Nested
      class _imported {
        @Test
        void value_succeeds() {
          var imported = "otherModuleValue = 7;";
          var code = "MyStruct {Int otherModuleValue}";
          module(code).withImported(imported).loadsWithSuccess();
        }

        @Test
        void func_succeeds() {
          var imported = "otherModuleFunc() = 7;";
          var code = "MyStruct {String otherModuleFunc}";
          module(code).withImported(imported).loadsWithSuccess();
        }
      }

      @Nested
      class _local {
        @Test
        void value_succeeds() {
          var code =
              """
              myValue = "abc";
              MyStruct {
                String myValue,
              }
              """;
          module(code).loadsWithSuccess();
        }

        @Test
        void func_succeeds() {
          var code =
              """
              myFunc() = "abc";
              MyStruct {
                String myFunc,
              }
              """;
          module(code).loadsWithSuccess();
        }
      }
    }

    private static String alreadyDefinedIn(FullPath fullPath, String name) {
      return alreadyDefinedIn(fullPath, 1, name);
    }

    private static String alreadyDefinedIn(FullPath fullPath, int line, String name) {
      return "`" + name + "` is already defined at " + fullPath + ":" + line + ".";
    }
  }
}
