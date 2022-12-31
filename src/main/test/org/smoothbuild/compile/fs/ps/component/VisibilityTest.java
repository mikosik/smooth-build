package org.smoothbuild.compile.fs.ps.component;

import static org.smoothbuild.testing.TestingModuleLoader.err;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.testing.TestContext;

public class VisibilityTest extends TestContext {
  @Nested
  class _visibility {
    @Nested
    class _local {
      @Test
      public void value_declared_above_is_visible() {
        module("""
             String myValue = "abc";
             result = myValue;
             """)
            .loadsWithSuccess();
      }

      @Test
      public void value_declared_below_is_visible() {
        module("""
             result = myValue;
             String myValue = "abc";
             """)
            .loadsWithSuccess();
      }

      @Test
      public void func_declared_above_is_visible() {
        module("""
             String myFunc() = "abc";
             result = myFunc;
             """)
            .loadsWithSuccess();
      }

      @Test
      public void func_declared_below_is_visible() {
        module("""
             result = myFunc;
             String myFunc() = "abc";
             """)
            .loadsWithSuccess();
      }

      @Test
      public void ctor_declared_above_is_visible() {
        module("""
             MyStruct()
             result = MyStruct;
             """)
            .loadsWithSuccess();
      }

      @Test
      public void ctor_declared_below_is_visible() {
        module("""
             result = MyStruct;
             MyStruct()
             """)
            .loadsWithSuccess();
      }

      @Test
      public void struct_declared_above_is_visible() {
        module("""
             MyStruct()
             @Native("impl.met")
             MyStruct myFunc();
             """)
            .loadsWithSuccess();
      }

      @Test
      public void struct_declared_below_is_visible() {
        module("""
             @Native("impl.met")
             MyStruct myFunc();
             MyStruct()
             """)
            .loadsWithSuccess();
      }
    }

    @Nested
    class _imported {
      @Test
      public void value_is_visible() {
        module("myValue = otherModuleValue;")
            .withImported("Int otherModuleValue = 7;")
            .loadsWithSuccess();
      }

      @Test
      public void func_is_visible() {
        module("myValue = otherModuleFunc;")
            .withImported("Int otherModuleFunc() = 7;")
            .loadsWithSuccess();
      }

      @Test
      public void constructor_is_visible() {
        module("myValue = OtherModuleStruct;")
            .withImported("OtherModuleStruct()")
            .loadsWithSuccess();
      }

      @Test
      public void struct_is_visible() {
        var code = """
            @Native("impl.met")
            OtherModuleStruct myFunc();
            """;
        module(code)
            .withImported("OtherModuleStruct()")
            .loadsWithSuccess();
      }
    }

    @Nested
    class _param {
      @Nested
      class default_value {
        @Test
        public void is_not_visible_outside_function_body_via_function_name_prefixed_reference() {
          // Despite internally CallsPreprocessor creates references to parameter default values
          // that are further in processing handled correctly, for now it is not available
          // in the language to simplify it.
          var code = """
              myFunc(Int param = 7) = 8;
              myResult = myFunc:param;
              """;
          module(code)
              .loadsWithProblems();
        }
      }

      @Nested
      class _of_named_function {
        @Test
        public void is_visible_in_its_body() {
          module("""
             myFunc(String param) = param;
             """)
              .loadsWithSuccess();
        }

        @Test
        public void is_not_visible_outside_its_body() {
          module("""
             myFunc(String param) = "abc";
             result = param;
             """)
              .loadsWithError(2, "`param` is undefined.");
        }

        @Test
        public void is_not_visible_in_its_default_value() {
          module("func(String withDefault = withDefault) = withDefault;")
              .loadsWithError(1, "`withDefault` is undefined.");
        }

        @Test
        public void is_not_visible_in_default_value_of_other_param() {
          module("func(String param, String withDefault = param) = param;")
              .loadsWithError(1, "`param` is undefined.");
        }
      }

      @Nested
      class _of_anonymous_function {
        @Test
        public void is_visible_in_its_body() {
          module("""
             myValue = (String param) -> param;
             """)
              .loadsWithSuccess();
        }

        @Test
        public void is_not_visible_outside_its_body() {
          module("""
             myValue = (String param) -> "abc";
             result = param;
             """)
              .loadsWithError(2, "`param` is undefined.");
        }
      }
    }

    @Nested
    class _undefined {
      @Nested
      class _eval_cannot_be_used_as {
        @Test
        public void anonymous_function_argument() {
          var code = """
              myValue = ((Int int) -> int)(undefined);
              """;
          module(code)
              .loadsWithError(1, "`undefined` is undefined.");
        }

        @Test
        public void anonymous_function_body() {
          var code = """
              myValue = () -> undefined;
              """;
          module(code)
              .loadsWithError(1, "`undefined` is undefined.");
        }

        @Test
        public void expression_function_argument() {
          var code = """
              String myFunc(Blob b) = "abc";
              result = myFunc(undefined);
              """;
          module(code)
              .loadsWithError(2, "`undefined` is undefined.");
        }

        @Test
        public void expression_function_body() {
          var code = """
              result() = undefined;
              """;
          module(code)
              .loadsWithError(1, "`undefined` is undefined.");
        }

        @Test
        public void expression_function_in_call_expression() {
          var code = """
              result = undefined();
              """;
          module(code)
              .loadsWithError(1, "`undefined` is undefined.");
        }

        @Test
        public void value_body() {
          var code = """
              result = undefined;
              """;
          module(code)
              .loadsWithError(1, "`undefined` is undefined.");
        }

        @Test
        public void array_elem() {
          var code = """
              result = [undefined];
              """;
          module(code)
              .loadsWithError(1, "`undefined` is undefined.");
        }

        @Test
        public void param_default_value() {
          var code = """
              String myFunc(Blob b = undefined) = "abc";
              """;
          module(code)
              .loadsWithError(1, "`undefined` is undefined.");
        }
      }

      @Nested
      class _type_cannot_be_used {
        @Test
        public void as_type_of_named_value() {
          var code = """
              @Bytecode("Impl.met")
              Undefined myValue;
              """;
          module(code)
              .loadsWithError(2, "`Undefined` type is undefined.");
        }

        @Test
        public void as_type_of_native_function_result() {
          var code = """
              @Native("Impl.met")
              Undefined myFunc();
              """;
          module(code)
              .loadsWithError(2, "`Undefined` type is undefined.");
        }

        @Test
        public void as_type_of_named_function_result() {
          var code = """
              [Undefined] myFunc() = [];
              """;
          module(code)
              .loadsWithError(1, "`Undefined` type is undefined.");
        }

        @Test
        public void as_type_of_named_function_parameter() {
          var code = """
              String myFunc(Undefined param) = "abc";
              """;
          module(code)
              .loadsWithError(1, "`Undefined` type is undefined.");
        }

        @Test
        public void inside_param_default_value() {
          var code = """
              Int myFunc((A)->Int param = (Undefined x) -> 7) = 7;
              """;
          module(code)
              .loadsWithError(1, "`Undefined` type is undefined.");
        }

        @Test
        public void as_type_of_anonymous_function_parameter() {
          var code = """
              myValue = (Undefined param) -> 7;
              """;
          module(code)
              .loadsWithError(1, "`Undefined` type is undefined.");
        }

        @Test
        public void as_type_of_struct_field() {
          var code = """
              MyStruct(
                UndefinedType field
              )
              """;
          module(code)
              .loadsWithError(2, "`UndefinedType` type is undefined.");
        }
      }
    }
  }

  @Nested
  class _cycle {
    @Nested
    class one_elem_cycle {
      @Test
      public void value() {
        module("""
             myValue = myValue;
             """)
            .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myValue ~> myValue""");
      }

      @Test
      public void func() {
        module("""
             myFunc1() = myFunc1();
             """)
            .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myFunc1 ~> myFunc1""");
      }

      @Test
      public void struct() {
        module("""
             MyStruct(
               MyStruct myField
             )
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct ~> MyStruct""");
      }

      @Test
      public void struct_through_array() {
        module("""
             MyStruct(
               [MyStruct] myField
             )
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct ~> MyStruct""");
      }

      @Test
      public void struct_through_func_result() {
        module("""
             MyStruct(
               ()->MyStruct myField
             )
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct ~> MyStruct""");
      }

      @Test
      public void struct_through_func_param() {
        module("""
             MyStruct(
               (MyStruct)->Blob myField
             )
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct ~> MyStruct""");
      }
    }

    @Nested
    class two_elems_cycle {
      @Test
      public void value_value() {
        module("""
             myValue1 = myValue2;
             myValue2 = myValue1;
             """)
            .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myValue1 ~> myValue2
              myBuild.smooth:2: myValue2 ~> myValue1""");
      }

      @Test
      public void func_func() {
        module("""
             myFunc1() = myFunc2();
             myFunc2() = myFunc1();
             """)
            .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myFunc1 ~> myFunc2
              myBuild.smooth:2: myFunc2 ~> myFunc1""");
      }

      @Test
      public void func_func_through_arg() {
        module("""
             String myFunc() = myIdentity(myFunc());
             String myIdentity(String s) = s;
             """)
            .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myFunc ~> myFunc""");
      }

      @Test
      public void value_value_through_arg() {
        module("""
             String myIdentity(String s) = s;
             String myValue = myIdentity(myValue);
             """)
            .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:2: myValue ~> myValue""");
      }

      @Test
      public void struct_struct() {
        module("""
             MyStruct1(
               MyStruct2 myField
             )
             MyStruct2(
               MyStruct1 myField
             )
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct1 ~> MyStruct2
              myBuild.smooth:5: MyStruct2 ~> MyStruct1""");
      }

      @Test
      public void struct_struct_through_array() {
        module("""
             MyStruct1(
               MyStruct2 myField
             )
             MyStruct2(
               [MyStruct1] myField
             )
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct1 ~> MyStruct2
              myBuild.smooth:5: MyStruct2 ~> MyStruct1""");
      }

      @Test
      public void struct_struct_through_func_result() {
        module("""
             MyStruct1(
               MyStruct2 myField
             )
             MyStruct2(
               ()->MyStruct1 myField
             )
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct1 ~> MyStruct2
              myBuild.smooth:5: MyStruct2 ~> MyStruct1""");
      }

      @Test
      public void struct_struct_through_func_param() {
        module("""
             MyStruct1(
               MyStruct2 myField
             )
             MyStruct2(
               (MyStruct1)->Blob myField
             )
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct1 ~> MyStruct2
              myBuild.smooth:5: MyStruct2 ~> MyStruct1""");
      }
    }

    @Nested
    class three_elem_cycle {
      @Test
      public void value_value_value() {
        module("""
             myValue1 = myValue2;
             myValue2 = myValue3;
             myValue3 = myValue1;
             """)
            .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myValue1 ~> myValue2
              myBuild.smooth:2: myValue2 ~> myValue3
              myBuild.smooth:3: myValue3 ~> myValue1""");
      }

      @Test
      public void func_func_func() {
        module("""
             myFunc1() = myFunc2();
             myFunc2() = myFunc3();
             myFunc3() = myFunc1();
             """)
            .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myFunc1 ~> myFunc2
              myBuild.smooth:2: myFunc2 ~> myFunc3
              myBuild.smooth:3: myFunc3 ~> myFunc1""");
      }

      @Test
      public void value_func_value() {
        module("""
             myValue1 = myFunc();
             myFunc() = myValue2;
             myValue2 = myValue1;
             """)
            .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myValue1 ~> myFunc
              myBuild.smooth:2: myFunc ~> myValue2
              myBuild.smooth:3: myValue2 ~> myValue1""");
      }

      @Test
      public void func_value_func() {
        module("""
             myFunc1() = myValue;
             myValue = myFunc2();
             myFunc2() = myFunc1();
             """)
            .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myFunc1 ~> myValue
              myBuild.smooth:2: myValue ~> myFunc2
              myBuild.smooth:3: myFunc2 ~> myFunc1""");
      }

      @Test
      public void struct_struct_struct_through_array() {
        module("""
             MyStruct1(
               MyStruct2 myField
             )
             MyStruct2(
               MyStruct3 myField
             )
             MyStruct3(
               [MyStruct1] myField
             )
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct1 ~> MyStruct2
              myBuild.smooth:5: MyStruct2 ~> MyStruct3
              myBuild.smooth:8: MyStruct3 ~> MyStruct1""");
      }

      @Test
      public void struct_struct_struct_through_func_result() {
        module("""
             MyStruct1(
               [MyStruct2] myField
             )
             MyStruct2(
               [MyStruct3] myField
             )
             MyStruct3(
               ()->MyStruct1 myField
             )
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct1 ~> MyStruct2
              myBuild.smooth:5: MyStruct2 ~> MyStruct3
              myBuild.smooth:8: MyStruct3 ~> MyStruct1""");
      }

      @Test
      public void struct_struct_struct_through_func_param() {
        module("""
             MyStruct1(
               [MyStruct2] myField
             )
             MyStruct2(
               [MyStruct3] myField
             )
             MyStruct3(
               (MyStruct1)->Blob myField
             )
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct1 ~> MyStruct2
              myBuild.smooth:5: MyStruct2 ~> MyStruct3
              myBuild.smooth:8: MyStruct3 ~> MyStruct1""");
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
        public void value_succeeds() {
          module("otherModuleValue = 8;")
              .withImported("otherModuleValue = 7;")
              .loadsWithSuccess();
        }

        @Test
        public void func_succeeds() {
          module("otherModuleFunc = 7;")
              .withImported("otherModuleFunc() = 8;")
              .loadsWithSuccess();
        }

        @Test
        public void constructor_succeeds() {
          module("otherModuleStruct = 7;")
              .withImported("OtherModuleStruct()")
              .loadsWithSuccess();
        }
      }

      @Nested
      class _local {
        @Test
        public void value_fails() {
          module("""
               myValue = "abc";
               myValue = "def";
               """)
              .loadsWithError(2, alreadyDefinedIn(filePath(), "myValue"));
        }

        @Test
        public void func_fails() {
          module("""
               myFunc() = "abc";
               myFunc = "def";
               """)
              .loadsWithError(2, alreadyDefinedIn(filePath(), "myFunc"));
        }
      }
    }

    @Nested
    class _named_function_shadowing {
      @Nested
      class _imported {
        @Test
        public void value_succeeds() {
          module("otherModuleValue() = 8;")
              .withImported("otherModuleValue = 7;")
              .loadsWithSuccess();
        }

        @Test
        public void func_succeeds() {
          module("otherModuleFunc() = 8;")
              .withImported("otherModuleFunc() = 7;")
              .loadsWithSuccess();
        }

        @Test
        public void ctor_succeeds() {
          module("otherModuleStruct() = 7;")
              .withImported("OtherModuleStruct()")
              .loadsWithSuccess();
        }
      }

      @Nested
      class _local {
        @Test
        public void named_value_fails() {
          module("""
               myValue = "abc";
               myValue() = "def";
               """)
              .loadsWithError(2, alreadyDefinedIn(filePath(), "myValue"));
        }

        @Test
        public void named_function_fails() {
          module("""
               myFunc() = "abc";
               myFunc() = "def";
               """)
              .loadsWithError(2, alreadyDefinedIn(filePath(), "myFunc"));
        }
      }
    }

    @Nested
    class _named_function_param_shadowing {
      @Test
      public void other_param_fails() {
        module("""
             String myFunc(
               String param,
               String param) = "abc";
               """)
            .loadsWithError(3, alreadyDefinedIn(filePath(), 2, "param"));
      }

      @Nested
      class _imported {
        @Test
        public void named_value_succeeds() {
          module("Int myFunc(String otherModuleValue) = 8;")
              .withImported("otherModuleValue = 7;")
              .loadsWithSuccess();
        }

        @Test
        public void named_function_succeeds() {
          module("Int myFunc(String otherModuleFunc) = 8;")
              .withImported("otherModuleFunc() = 7;")
              .loadsWithSuccess();
        }

        @Test
        public void constructor_succeeds() {
          module("Int myFunc(String otherModuleStruct) = 7;")
              .withImported("OtherModuleStruct()")
              .loadsWithSuccess();
        }
      }

      @Nested
      class _local {
        @Test
        public void named_value_succeeds() {
          module("""
              myValue = "abc";
              String myFunc(String myValue) = "abc";
              """)
              .loadsWithSuccess();
        }

        @Test
        public void named_function_succeeds() {
          module("""
              myFunc() = "abc";
              String myOtherFunc(String myFunc) = "abc";
              """)
              .loadsWithSuccess();
        }

        @Test
        public void constructor_succeeds() {
          module("""
             MyStruct()
             String myFunc(String myStruct) = "abc";
             """)
              .loadsWithSuccess();
        }
      }
    }

    @Nested
    class _anonymous_function_param_shadowing {
      @Test
      public void other_param_fails() {
        module("""
             myValue = (
               String param,
               String param) -> 7;
               """)
            .loadsWithError(3, alreadyDefinedIn(filePath(), 2, "param"));
      }

      @Nested
      class _imported {
        @Test
        public void named_value_succeeds() {
          module("myValue = (String otherModuleValue) -> 7;")
              .withImported("otherModuleValue = 8;")
              .loadsWithSuccess();
        }

        @Test
        public void named_function_succeeds() {
          module("myValue = (String otherModuleFunc) -> 7;")
              .withImported("otherModuleFunc() = 8;")
              .loadsWithSuccess();
        }

        @Test
        public void constructor_succeeds() {
          module("myValue = (String otherModuleStruct) -> 7;")
              .withImported("OtherModuleStruct()")
              .loadsWithSuccess();
        }
      }

      @Nested
      class _local {
        @Test
        public void named_value_succeeds() {
          module("""
              myValue = "abc";
              otherValue = (String myValue) -> 7;
              """)
              .loadsWithSuccess();
        }

        @Test
        public void named_function_succeeds() {
          module("""
              myFunc() = "abc";
              myValue = (String myFunc) -> 7;
              """)
              .loadsWithSuccess();
        }

        @Test
        public void constructor_succeeds() {
          module("""
             MyStruct()
             myValue = (String myStruct) -> 7;
             """)
              .loadsWithSuccess();
        }
      }
    }

    @Nested
    class _struct_shadowing {
      @Nested
      class _imported {
        @Test
        public void base_type_succeeds() {
          var code = "String()";
          module(code)
              .loadsWithSuccess();
        }

        @Test
        public void struct_succeeds() {
          module("OtherModuleStruct()")
              .withImported("OtherModuleStruct()")
              .loadsWithSuccess();
        }
      }

      @Nested
      class _local {
        @Test
        public void struct_fails() {
          module("""
               OtherModuleStruct()
               OtherModuleStruct()
               """)
              .loadsWith(
                  err(2, alreadyDefinedIn(filePath(), "OtherModuleStruct"))
              );
        }
      }
    }

    @Nested
    class _field_shadowing {
      @Test
      public void other_field_fails() {
        module("""
             MyStruct(
               String field,
               String field
             )
             """)
            .loadsWithError(3, alreadyDefinedIn(filePath(), 2, "field"));
      }


      @Nested
      class _imported {
        @Test
        public void value_succeeds() {
          module("MyStruct(Int otherModuleValue)")
              .withImported("otherModuleValue = 7;")
              .loadsWithSuccess();
        }

        @Test
        public void func_succeeds() {
          module("MyStruct(String otherModuleFunc)")
              .withImported("otherModuleFunc() = 7;")
              .loadsWithSuccess();
        }

        @Test
        public void ctor_succeeds() {
          module("MyStruct(String otherModuleStruct)")
              .withImported("OtherModuleStruct()")
              .loadsWithSuccess();
        }
      }

      @Nested
      class _local {
        @Test
        public void value_succeeds() {
          module("""
              myValue = "abc";
              MyStruct(
                String myValue,
              )
              """)
              .loadsWithSuccess();
        }

        @Test
        public void func_succeeds() {
          module("""
              myFunc() = "abc";
              MyStruct(
                String myFunc,
              )
              """)
              .loadsWithSuccess();
        }

        @Test
        public void ctor_succeeds() {
          module("""
             MyStruct()
             MyOtherStruct(
                String myStruct,
             )
             """)
              .loadsWithSuccess();
        }
      }
    }

    private static String alreadyDefinedIn(FilePath filePath, String name) {
      return alreadyDefinedIn(filePath, 1, name);
    }

    private static String alreadyDefinedIn(FilePath filePath, int line, String name) {
      return "`" + name + "` is already defined at " + filePath.path() + ":" + line + ".";
    }
  }
}
