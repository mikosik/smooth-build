package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.io.fs.base.TestingFilePath.filePath;
import static org.smoothbuild.io.fs.base.TestingFilePath.importedFilePath;
import static org.smoothbuild.testing.TestingModLoader.err;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.testing.TestingContext;

public class VisibilityTest extends TestingContext {
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
            .loadsSuccessfully();
      }

      @Test
      public void value_declared_below_is_visible() {
        module("""
             result = myValue;
             String myValue = "abc";
             """)
            .loadsSuccessfully();
      }

      @Test
      public void func_declared_above_is_visible() {
        module("""
             String myFunc() = "abc";
             result = myFunc;
             """)
            .loadsSuccessfully();
      }

      @Test
      public void func_declared_below_is_visible() {
        module("""
             result = myFunc;
             String myFunc() = "abc";
             """)
            .loadsSuccessfully();
      }

      @Test
      public void ctor_declared_above_is_visible() {
        module("""
             MyStruct {}
             result = myStruct;
             """)
            .loadsSuccessfully();
      }

      @Test
      public void ctor_declared_below_is_visible() {
        module("""
             result = myStruct;
             MyStruct {}
             """)
            .loadsSuccessfully();
      }

      @Test
      public void struct_declared_above_is_visible() {
        module("""
             MyStruct {}
             @Native("impl.met")
             MyStruct myFunc();
             """)
            .loadsSuccessfully();
      }

      @Test
      public void struct_declared_below_is_visible() {
        module("""
             @Native("impl.met")
             MyStruct myFunc();
             MyStruct {}
             """)
            .loadsSuccessfully();
      }
    }

    @Nested
    class _imported {
      @Test
      public void value_is_visible() {
        DefsS imported = module("""
          String otherModuleValue = "abc";
          """)
            .loadsSuccessfully()
            .getModAsDefinitions();
        module("""
          myValue = otherModuleValue;
          """)
            .withImported(imported)
            .loadsSuccessfully();
      }

      @Test
      public void func_is_visible() {
        DefsS imported = module("""
          String otherModuleFunc() = "abc";
          """)
            .loadsSuccessfully()
            .getModAsDefinitions();
        module("""
              myValue = otherModuleFunc;
              """)
            .withImported(imported)
            .loadsSuccessfully();
      }

      @Test
      public void ctor_is_visible() {
        DefsS imported = module("""
          OtherModuleStruct{}
          """)
            .loadsSuccessfully()
            .getModAsDefinitions();
        module("""
              myValue = otherModuleStruct;
              """)
            .withImported(imported)
            .loadsSuccessfully();
      }

      @Test
      public void struct_is_visible() {
        DefsS imported = module("""
          OtherModuleStruct{}
          """)
            .loadsSuccessfully()
            .getModAsDefinitions();
        module("""
          @Native("impl.met")
          OtherModuleStruct myFunc();
          """)
            .withImported(imported)
            .loadsSuccessfully();
      }
    }

    @Nested
    class _param {
      @Test
      public void in_func_body_is_visible() {
        module("""
             myFunc(String param) = param;
             """)
            .loadsSuccessfully();
      }

      @Test
      public void outside_its_func_body_is_not_visible() {
        module("""
             myFunc(String param) = "abc";
             result = param;
             """)
            .loadsWithError(2, "`param` is undefined.");
      }

      @Test
      public void in_default_arg_body_of_other_param_is_not_visible() {
        module("""
        func(String param, String withDefault = param) = param;
        """)
            .loadsWithError(1, "`param` is undefined.");
      }

      @Test
      public void in_its_default_arg_body_is_not_visible() {
        module("""
        func(String withDefault = withDefault) = withDefault;
        """)
            .loadsWithError(1, "`withDefault` is undefined.");
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
              myBuild.smooth:1: myValue -> myValue""");
      }

      @Test
      public void func() {
        module("""
             myFunc1() = myFunc1();
             """)
            .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myFunc1 -> myFunc1""");
      }

      @Test
      public void struct() {
        module("""
             MyStruct {
               MyStruct myField
             }
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct -> MyStruct""");
      }

      @Test
      public void struct_through_array() {
        module("""
             MyStruct {
               [MyStruct] myField
             }
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct -> MyStruct""");
      }

      @Test
      public void struct_through_func_result() {
        module("""
             MyStruct {
               MyStruct() myField
             }
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct -> MyStruct""");
      }

      @Test
      public void struct_through_func_param() {
        module("""
             MyStruct {
               Blob(MyStruct) myField
             }
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct -> MyStruct""");
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
              myBuild.smooth:1: myValue1 -> myValue2
              myBuild.smooth:2: myValue2 -> myValue1""");
      }

      @Test
      public void func_func() {
        module("""
             myFunc1() = myFunc2();
             myFunc2() = myFunc1();
             """)
            .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myFunc1 -> myFunc2
              myBuild.smooth:2: myFunc2 -> myFunc1""");
      }

      @Test
      public void func_func_through_arg() {
        module("""
             String myFunc() = myIdentity(myFunc());
             String myIdentity(String s) = s;
             """)
            .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myFunc -> myFunc""");
      }

      @Test
      public void value_value_through_arg() {
        module("""
             String myIdentity(String s) = s;
             String myValue = myIdentity(myValue);
             """)
            .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:2: myValue -> myValue""");
      }

      @Test
      public void struct_struct() {
        module("""
             MyStruct1 {
               MyStruct2 myField
             }
             MyStruct2 {
               MyStruct1 myField
             }
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct1 -> MyStruct2
              myBuild.smooth:5: MyStruct2 -> MyStruct1""");
      }

      @Test
      public void struct_struct_through_array() {
        module("""
             MyStruct1 {
               MyStruct2 myField
             }
             MyStruct2 {
               [MyStruct1] myField
             }
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct1 -> MyStruct2
              myBuild.smooth:5: MyStruct2 -> MyStruct1""");
      }

      @Test
      public void struct_struct_through_func_result() {
        module("""
             MyStruct1 {
               MyStruct2 myField
             }
             MyStruct2 {
               MyStruct1() myField
             }
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct1 -> MyStruct2
              myBuild.smooth:5: MyStruct2 -> MyStruct1""");
      }

      @Test
      public void struct_struct_through_func_param() {
        module("""
             MyStruct1 {
               MyStruct2 myField
             }
             MyStruct2 {
               Blob(MyStruct1) myField
             }
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct1 -> MyStruct2
              myBuild.smooth:5: MyStruct2 -> MyStruct1""");
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
              myBuild.smooth:1: myValue1 -> myValue2
              myBuild.smooth:2: myValue2 -> myValue3
              myBuild.smooth:3: myValue3 -> myValue1""");
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
              myBuild.smooth:1: myFunc1 -> myFunc2
              myBuild.smooth:2: myFunc2 -> myFunc3
              myBuild.smooth:3: myFunc3 -> myFunc1""");
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
              myBuild.smooth:1: myValue1 -> myFunc
              myBuild.smooth:2: myFunc -> myValue2
              myBuild.smooth:3: myValue2 -> myValue1""");
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
              myBuild.smooth:1: myFunc1 -> myValue
              myBuild.smooth:2: myValue -> myFunc2
              myBuild.smooth:3: myFunc2 -> myFunc1""");
      }

      @Test
      public void struct_struct_struct_through_array() {
        module("""
             MyStruct1 {
               MyStruct2 myField
             }
             MyStruct2 {
               MyStruct3 myField
             }
             MyStruct3 {
               [MyStruct1] myField
             }
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct1 -> MyStruct2
              myBuild.smooth:5: MyStruct2 -> MyStruct3
              myBuild.smooth:8: MyStruct3 -> MyStruct1""");
      }

      @Test
      public void struct_struct_struct_through_func_result() {
        module("""
             MyStruct1 {
               [MyStruct2] myField
             }
             MyStruct2 {
               [MyStruct3] myField
             }
             MyStruct3 {
               MyStruct1() myField
             }
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct1 -> MyStruct2
              myBuild.smooth:5: MyStruct2 -> MyStruct3
              myBuild.smooth:8: MyStruct3 -> MyStruct1""");
      }

      @Test
      public void struct_struct_struct_through_func_param() {
        module("""
             MyStruct1 {
               [MyStruct2] myField
             }
             MyStruct2 {
               [MyStruct3] myField
             }
             MyStruct3 {
               Blob(MyStruct1) myField
             }
             """)
            .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct1 -> MyStruct2
              myBuild.smooth:5: MyStruct2 -> MyStruct3
              myBuild.smooth:8: MyStruct3 -> MyStruct1""");
      }
    }
  }

  @Nested
  class _shadowing {
    @Nested
    class _value_shadowing {
      @Nested
      class _imported {
        @Test
        public void value_fails() {
          DefsS imported = module("""
            otherModuleValue = "abc";
            """)
              .withImportedModFiles()
              .loadsSuccessfully()
              .getModAsDefinitions();
          module("""
               otherModuleValue = "def";
               """)
              .withImported(imported)
              .loadsWithError(1, alreadyDefinedIn(importedFilePath(), "otherModuleValue"));
        }

        @Test
        public void func_fails() {
          DefsS imported = module("""
            otherModuleFunc() = "abc";
            """)
              .withImportedModFiles()
              .loadsSuccessfully()
              .getModAsDefinitions();
          module("""
                otherModuleFunc = "def";
                """)
              .withImported(imported)
              .loadsWithError(1, alreadyDefinedIn(importedFilePath(), "otherModuleFunc"));
        }

        @Test
        public void ctor_fails() {
          DefsS imported = module("""
            OtherModuleStruct {}
            """)
              .withImportedModFiles()
              .loadsSuccessfully()
              .getModAsDefinitions();
          module("""
                otherModuleStruct = "def";
                """)
              .withImported(imported)
              .loadsWithError(1, alreadyDefinedIn(importedFilePath(), "otherModuleStruct"));
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

        @Test
        public void ctor_fails() {
          module("""
               MyStruct {}
               myStruct = "abc";
               """)
              .loadsWithError(2, alreadyDefinedIn(filePath(), "myStruct"));
        }
      }

      @Nested
      class _internal {
        @Test
        public void func_fails() {
          module("""
                if = "def";
                """)
              .loadsWithError(1, alreadyDefinedInternally("if"));
        }
      }
    }

    @Nested
    class _func_shadowing {
      @Nested
      class _imported {
        @Test
        public void value_fails() {
          DefsS imported = module("""
            otherModuleValue = "abc";
            """)
              .withImportedModFiles()
              .loadsSuccessfully()
              .getModAsDefinitions();
          module("""
                otherModuleValue() = "def";
                """)
              .withImported(imported)
              .loadsWithError(1, alreadyDefinedIn(importedFilePath(), "otherModuleValue"));
        }

        @Test
        public void func_fails() {
          DefsS imported = module("""
            otherModuleFunc() = "abc";
            """)
              .withImportedModFiles()
              .loadsSuccessfully()
              .getModAsDefinitions();
          module("""
                otherModuleFunc() = "def";
                """)
              .withImported(imported)
              .loadsWithError(1, alreadyDefinedIn(importedFilePath(), "otherModuleFunc"));
        }

        @Test
        public void ctor_fails() {
          DefsS imported = module("""
            OtherModuleStruct {}
            """)
              .withImportedModFiles()
              .loadsSuccessfully()
              .getModAsDefinitions();
          module("""
                otherModuleStruct() = "def";
                """)
              .withImported(imported)
              .loadsWithError(1, alreadyDefinedIn(importedFilePath(), "otherModuleStruct"));
        }
      }

      @Nested
      class _local {
        @Test
        public void value_fails() {
          module("""
               myValue = "abc";
               myValue() = "def";
               """)
              .loadsWithError(2, alreadyDefinedIn(filePath(), "myValue"));
        }

        @Test
        public void func_fails() {
          module("""
               myFunc() = "abc";
               myFunc() = "def";
               """)
              .loadsWithError(2, alreadyDefinedIn(filePath(), "myFunc"));
        }

        @Test
        public void ctor_fails() {
          module("""
               MyStruct {}
               myStruct() = "abc";
               """)
              .loadsWithError(2, alreadyDefinedIn(filePath(), "myStruct"));
        }
      }

      @Nested
      class _internal {
        @Test
        public void func_fails() {
          module("""
                if() = "def";
                """)
              .loadsWithError(1, alreadyDefinedInternally("if"));
        }
      }
    }

    @Nested
    class _param_shadowing {
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
        public void value_succeeds() {
          DefsS imported = module("""
              otherModuleValue = "abc";
              """)
              .withImportedModFiles()
              .loadsSuccessfully()
              .getModAsDefinitions();
          module("""
              String myFunc(String otherModuleValue) = "abc";
              """)
              .withImported(imported)
              .loadsSuccessfully();
        }

        @Test
        public void func_succeeds() {
          DefsS imported = module("""
              otherModuleFunc() = "abc";
              """)
              .withImportedModFiles()
              .loadsSuccessfully()
              .getModAsDefinitions();
          module("""
              String myFunc(String otherModuleFunc) = "abc";
              """)
              .withImported(imported)
              .loadsSuccessfully();
        }

        @Test
        public void ctor_succeeds() {
          DefsS imported = module("""
              OtherModuleStruct {}
              """)
              .withImportedModFiles()
              .loadsSuccessfully()
              .getModAsDefinitions();
          module("""
              String myFunc(String otherModuleStruct) = "abc";
              """)
              .withImported(imported)
              .loadsSuccessfully();
        }
      }

      @Nested
      class _local {
        @Test
        public void value_succeeds() {
          module("""
              myValue = "abc";
              String myFunc(String myValue) = "abc";
              """)
              .loadsSuccessfully();
        }

        @Test
        public void func_succeeds() {
          module("""
              myFunc() = "abc";
              String myOtherFunc(String myFunc) = "abc";
              """)
              .loadsSuccessfully();
        }

        @Test
        public void ctor_succeeds() {
          module("""
             MyStruct {}
             String myFunc(String myStruct) = "abc";
             """)
              .loadsSuccessfully();
        }
      }

      @Nested
      class _internal {
        @Test
        public void func_succeeds() {
          module("""
              verifying_that_internal_if_func_is_defined = if;
              String myFunc(String if) = "abc";
              """)
              .loadsSuccessfully();
        }
      }
    }

    @Nested
    class _struct_shadowing {
      @Nested
      class _imported {
        @Test
        public void base_type_fails() {
          module("""
               String {}
               """)
              .loadsWithError(1, "`" + "String" + "` is already defined internally.");
        }

        @Test
        public void struct_fails() {
          DefsS imported = module("""
            OtherModuleStruct {}
            """)
              .withImportedModFiles()
              .loadsSuccessfully()
              .getModAsDefinitions();
          module("""
                OtherModuleStruct {}
                """)
              .withImported(imported)
              .loadsWith(
                  err(1, alreadyDefinedIn(importedFilePath(), "OtherModuleStruct")),
                  err(1, alreadyDefinedIn(importedFilePath(), "otherModuleStruct"))
              );
        }
      }

      @Nested
      class _local {
        @Test
        public void struct_fails() {
          module("""
               OtherModuleStruct {}
               OtherModuleStruct {}
               """)
              .loadsWith(
                  err(2, alreadyDefinedIn(filePath(), "OtherModuleStruct")),
                  err(2, alreadyDefinedIn(filePath(), "otherModuleStruct"))
              );
        }
      }
    }

    @Nested
    class _field_shadowing {
      @Test
      public void other_field_fails() {
        module("""
             MyStruct {
               String field,
               String field
             }
             """)
            .loadsWithError(3, alreadyDefinedIn(filePath(), 2, "field"));
      }


      @Nested
      class _imported {
        @Test
        public void value_succeeds() {
          DefsS imported = module("""
              otherModuleValue = "abc";
              """)
              .withImportedModFiles()
              .loadsSuccessfully()
              .getModAsDefinitions();
          module("""
              MyStruct {
                String otherModuleValue,
              }
              """)
              .withImported(imported)
              .loadsSuccessfully();
        }

        @Test
        public void func_succeeds() {
          DefsS imported = module("""
              otherModuleFunc() = "abc";
              """)
              .withImportedModFiles()
              .loadsSuccessfully()
              .getModAsDefinitions();
          module("""
              MyStruct {
                String otherModuleFunc,
              }
              """)
              .withImported(imported)
              .loadsSuccessfully();
        }

        @Test
        public void ctor_succeeds() {
          DefsS imported = module("""
              OtherModuleStruct {}
              """)
              .withImportedModFiles()
              .loadsSuccessfully()
              .getModAsDefinitions();
          module("""
              MyStruct {
                String otherModuleStruct,
              }
              """)
              .withImported(imported)
              .loadsSuccessfully();
        }
      }

      @Nested
      class _local {
        @Test
        public void value_succeeds() {
          module("""
              myValue = "abc";
              MyStruct {
                String myValue,
              }
              """)
              .loadsSuccessfully();
        }

        @Test
        public void func_succeeds() {
          module("""
              myFunc() = "abc";
              MyStruct {
                String myFunc,
              }
              """)
              .loadsSuccessfully();
        }

        @Test
        public void ctor_succeeds() {
          module("""
             MyStruct {}
             MyOtherStruct {
                String myStruct,
             }
             """)
              .loadsSuccessfully();
        }
      }

      @Nested
      class _internal {
        @Test
        public void func_succeeds() {
          module("""
              verifying_that_internal_if_func_is_defined = if;
              MyStruct {
                String if,
              }
              """)
              .loadsSuccessfully();
        }
      }
    }

    @Nested
    class _ctor_shadowing {
      @Nested
      class _imported {
        @Test
        public void value_fails() {
          DefsS imported = module("""
            otherModuleValue = "abc";
            """)
              .withImportedModFiles()
              .loadsSuccessfully()
              .getModAsDefinitions();
          module("""
                OtherModuleValue{}
                """)
              .withImported(imported)
              .loadsWithError(1, alreadyDefinedIn(importedFilePath(), "otherModuleValue"));
        }

        @Test
        public void func_fails() {
          DefsS imported = module("""
            otherModuleFunc() = "abc";
            """)
              .withImportedModFiles()
              .loadsSuccessfully()
              .getModAsDefinitions();
          module("""
                OtherModuleFunc{}
                """)
              .withImported(imported)
              .loadsWithError(1, alreadyDefinedIn(importedFilePath(), "otherModuleFunc"));
        }
      }

      @Nested
      class _local {
        @Test
        public void value_fails() {
          module("""
               myValue = "abc";
               MyValue{}
               """)
              .loadsWithError(2, alreadyDefinedIn(filePath(), "myValue"));
        }

        @Test
        public void func_fails() {
          module("""
               myFunc() = "abc";
               MyFunc{}
               """)
              .loadsWithError(2, alreadyDefinedIn(filePath(), "myFunc"));
        }
      }

      @Nested
      class _internal {
        @Test
        public void func_fails() {
          module("""
                If{}
                """)
              .loadsWithError(1, alreadyDefinedInternally("if"));
        }
      }
    }

    private static String alreadyDefinedIn(FilePath filePath, String name) {
      return alreadyDefinedIn(filePath, 1, name);
    }

    private static String alreadyDefinedIn(FilePath filePath, int line, String name) {
      return "`" + name + "` is already defined at " + filePath.path() + ":" + line + ".";
    }

    private static String alreadyDefinedInternally(String name) {
      return "`" + name + "` is already defined internally.";
    }
  }
}
