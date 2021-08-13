package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.io.fs.base.TestingFilePath.filePath;
import static org.smoothbuild.io.fs.base.TestingFilePath.importedFilePath;
import static org.smoothbuild.lang.TestModuleLoader.err;
import static org.smoothbuild.lang.TestModuleLoader.module;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.lang.base.define.Definitions;

public class VisibilityTest {
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
      public void function_declared_above_is_visible() {
        module("""
             String myFunction() = "abc";
             result = myFunction;
             """)
            .loadsSuccessfully();
      }

      @Test
      public void function_declared_below_is_visible() {
        module("""
             result = myFunction;
             String myFunction() = "abc";
             """)
            .loadsSuccessfully();
      }

      @Test
      public void constructor_declared_above_is_visible() {
        module("""
             MyStruct {}
             result = myStruct;
             """)
            .loadsSuccessfully();
      }

      @Test
      public void constructor_declared_below_is_visible() {
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
             MyStruct result;
             """)
            .loadsSuccessfully();
      }

      @Test
      public void struct_declared_below_is_visible() {
        module("""
             @Native("impl.met")
             MyStruct result;
             MyStruct {}
             """)
            .loadsSuccessfully();
      }
    }

    @Nested
    class _imported {
      @Test
      public void value_is_visible() {
        Definitions imported = module("""
          String otherModuleValue = "abc";
          """)
            .loadsSuccessfully()
            .getModuleAsDefinitions();
        module("""
          myValue = otherModuleValue;
          """)
            .withImported(imported)
            .loadsSuccessfully();
      }

      @Test
      public void function_is_visible() {
        Definitions imported = module("""
          String otherModuleFunction() = "abc";
          """)
            .loadsSuccessfully()
            .getModuleAsDefinitions();
        module("""
              myValue = otherModuleFunction;
              """)
            .withImported(imported)
            .loadsSuccessfully();
      }

      @Test
      public void constructor_is_visible() {
        Definitions imported = module("""
          OtherModuleStruct{}
          """)
            .loadsSuccessfully()
            .getModuleAsDefinitions();
        module("""
              myValue = otherModuleStruct;
              """)
            .withImported(imported)
            .loadsSuccessfully();
      }

      @Test
      public void struct_is_visible() {
        Definitions imported = module("""
          OtherModuleStruct{}
          """)
            .loadsSuccessfully()
            .getModuleAsDefinitions();
        module("""
          @Native("impl.met")
          OtherModuleStruct result;
          """)
            .withImported(imported)
            .loadsSuccessfully();
      }
    }

    @Nested
    class _parameter {
      @Test
      public void in_function_body_is_visible() {
        module("""
             myFunction(String param) = param;
             """)
            .loadsSuccessfully();
      }

      @Test
      public void outside_its_function_body_is_not_visible() {
        module("""
             myFunction(String param) = "abc";
             result = param;
             """)
            .loadsWithError(2, "`param` is undefined.");
      }

      @Test
      public void in_default_argument_body_of_other_parameter_is_not_visible() {
        module("""
        func(String param, String withDefault = param) = param;
        """)
            .loadsWithError(1, "`param` is undefined.");
      }

      @Test
      public void in_its_default_argument_body_is_not_visible() {
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
    class one_element_cycle {
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
      public void function() {
        module("""
             myFunction1() = myFunction1();
             """)
            .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myFunction1 -> myFunction1""");
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
      public void struct_through_function_result() {
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
      public void struct_through_function_parameter() {
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
    class two_elements_cycle {
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
      public void function_function() {
        module("""
             myFunction1() = myFunction2();
             myFunction2() = myFunction1();
             """)
            .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myFunction1 -> myFunction2
              myBuild.smooth:2: myFunction2 -> myFunction1""");
      }

      @Test
      public void function_function_through_argument() {
        module("""
             String myFunction() = myIdentity(myFunction());
             String myIdentity(String s) = s;
             """)
            .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myFunction -> myFunction""");
      }

      @Test
      public void value_value_through_argument() {
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
      public void struct_struct_through_function_result() {
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
      public void struct_struct_through_function_parameter() {
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
    class three_element_cycle {
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
      public void function_function_function() {
        module("""
             myFunction1() = myFunction2();
             myFunction2() = myFunction3();
             myFunction3() = myFunction1();
             """)
            .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myFunction1 -> myFunction2
              myBuild.smooth:2: myFunction2 -> myFunction3
              myBuild.smooth:3: myFunction3 -> myFunction1""");
      }

      @Test
      public void value_function_value() {
        module("""
             myValue1 = myFunction();
             myFunction() = myValue2;
             myValue2 = myValue1;
             """)
            .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myValue1 -> myFunction
              myBuild.smooth:2: myFunction -> myValue2
              myBuild.smooth:3: myValue2 -> myValue1""");
      }

      @Test
      public void function_value_function() {
        module("""
             myFunction1() = myValue;
             myValue = myFunction2();
             myFunction2() = myFunction1();
             """)
            .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myFunction1 -> myValue
              myBuild.smooth:2: myValue -> myFunction2
              myBuild.smooth:3: myFunction2 -> myFunction1""");
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
      public void struct_struct_struct_through_function_result() {
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
      public void struct_struct_struct_through_function_parameter() {
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
          Definitions imported = module("""
            otherModuleValue = "abc";
            """)
              .withImportedModuleFiles()
              .loadsSuccessfully()
              .getModuleAsDefinitions();
          module("""
               otherModuleValue = "def";
               """)
              .withImported(imported)
              .loadsWithError(1, alreadyDefinedIn(importedFilePath(), "otherModuleValue"));
        }

        @Test
        public void function_fails() {
          Definitions imported = module("""
            otherModuleFunction() = "abc";
            """)
              .withImportedModuleFiles()
              .loadsSuccessfully()
              .getModuleAsDefinitions();
          module("""
                otherModuleFunction = "def";
                """)
              .withImported(imported)
              .loadsWithError(1, alreadyDefinedIn(importedFilePath(), "otherModuleFunction"));
        }

        @Test
        public void constructor_fails() {
          Definitions imported = module("""
            OtherModuleStruct {}
            """)
              .withImportedModuleFiles()
              .loadsSuccessfully()
              .getModuleAsDefinitions();
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
        public void function_fails() {
          module("""
               myFunction() = "abc";
               myFunction = "def";
               """)
              .loadsWithError(2, alreadyDefinedIn(filePath(), "myFunction"));
        }

        @Test
        public void constructor_fails() {
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
        public void function_fails() {
          module("""
                if = "def";
                """)
              .loadsWithError(1, alreadyDefinedInternally("if"));
        }
      }
    }

    @Nested
    class _function_shadowing {
      @Nested
      class _imported {
        @Test
        public void value_fails() {
          Definitions imported = module("""
            otherModuleValue = "abc";
            """)
              .withImportedModuleFiles()
              .loadsSuccessfully()
              .getModuleAsDefinitions();
          module("""
                otherModuleValue() = "def";
                """)
              .withImported(imported)
              .loadsWithError(1, alreadyDefinedIn(importedFilePath(), "otherModuleValue"));
        }

        @Test
        public void function_fails() {
          Definitions imported = module("""
            otherModuleFunction() = "abc";
            """)
              .withImportedModuleFiles()
              .loadsSuccessfully()
              .getModuleAsDefinitions();
          module("""
                otherModuleFunction() = "def";
                """)
              .withImported(imported)
              .loadsWithError(1, alreadyDefinedIn(importedFilePath(), "otherModuleFunction"));
        }

        @Test
        public void constructor_fails() {
          Definitions imported = module("""
            OtherModuleStruct {}
            """)
              .withImportedModuleFiles()
              .loadsSuccessfully()
              .getModuleAsDefinitions();
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
        public void function_fails() {
          module("""
               myFunction() = "abc";
               myFunction() = "def";
               """)
              .loadsWithError(2, alreadyDefinedIn(filePath(), "myFunction"));
        }

        @Test
        public void constructor_fails() {
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
        public void function_fails() {
          module("""
                if() = "def";
                """)
              .loadsWithError(1, alreadyDefinedInternally("if"));
        }
      }
    }

    @Nested
    class _parameter_shadowing {
      @Test
      public void other_parameter_fails() {
        module("""
             String myFunction(
               String param,
               String param) = "abc";
               """)
            .loadsWithError(3, alreadyDefinedIn(filePath(), 2, "param"));
      }

      @Nested
      class _imported {
        @Test
        public void value_succeeds() {
          Definitions imported = module("""
              otherModuleValue = "abc";
              """)
              .withImportedModuleFiles()
              .loadsSuccessfully()
              .getModuleAsDefinitions();
          module("""
              String myFunction(String otherModuleValue) = "abc";
              """)
              .withImported(imported)
              .loadsSuccessfully();
        }

        @Test
        public void function_succeeds() {
          Definitions imported = module("""
              otherModuleFunction() = "abc";
              """)
              .withImportedModuleFiles()
              .loadsSuccessfully()
              .getModuleAsDefinitions();
          module("""
              String myFunction(String otherModuleFunction) = "abc";
              """)
              .withImported(imported)
              .loadsSuccessfully();
        }

        @Test
        public void constructor_succeeds() {
          Definitions imported = module("""
              OtherModuleStruct {}
              """)
              .withImportedModuleFiles()
              .loadsSuccessfully()
              .getModuleAsDefinitions();
          module("""
              String myFunction(String otherModuleStruct) = "abc";
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
              String myFunction(String myValue) = "abc";
              """)
              .loadsSuccessfully();
        }

        @Test
        public void function_succeeds() {
          module("""
              myFunction() = "abc";
              String myOtherFunction(String myFunction) = "abc";
              """)
              .loadsSuccessfully();
        }

        @Test
        public void constructor_succeeds() {
          module("""
             MyStruct {}
             String myFunction(String myStruct) = "abc";
             """)
              .loadsSuccessfully();
        }
      }

      @Nested
      class _internal {
        @Test
        public void function_succeeds() {
          module("""
              verifying_that_internal_if_function_is_defined = if;
              String myFunction(String if) = "abc";
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
          Definitions imported = module("""
            OtherModuleStruct {}
            """)
              .withImportedModuleFiles()
              .loadsSuccessfully()
              .getModuleAsDefinitions();
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
          Definitions imported = module("""
              otherModuleValue = "abc";
              """)
              .withImportedModuleFiles()
              .loadsSuccessfully()
              .getModuleAsDefinitions();
          module("""
              MyStruct {
                String otherModuleValue,
              }
              """)
              .withImported(imported)
              .loadsSuccessfully();
        }

        @Test
        public void function_succeeds() {
          Definitions imported = module("""
              otherModuleFunction() = "abc";
              """)
              .withImportedModuleFiles()
              .loadsSuccessfully()
              .getModuleAsDefinitions();
          module("""
              MyStruct {
                String otherModuleFunction,
              }
              """)
              .withImported(imported)
              .loadsSuccessfully();
        }

        @Test
        public void constructor_succeeds() {
          Definitions imported = module("""
              OtherModuleStruct {}
              """)
              .withImportedModuleFiles()
              .loadsSuccessfully()
              .getModuleAsDefinitions();
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
        public void function_succeeds() {
          module("""
              myFunction() = "abc";
              MyStruct {
                String myFunction,
              }
              """)
              .loadsSuccessfully();
        }

        @Test
        public void constructor_succeeds() {
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
        public void function_succeeds() {
          module("""
              verifying_that_internal_if_function_is_defined = if;
              MyStruct {
                String if,
              }
              """)
              .loadsSuccessfully();
        }
      }
    }

    @Nested
    class _constructor_shadowing {
      @Nested
      class _imported {
        @Test
        public void value_fails() {
          Definitions imported = module("""
            otherModuleValue = "abc";
            """)
              .withImportedModuleFiles()
              .loadsSuccessfully()
              .getModuleAsDefinitions();
          module("""
                OtherModuleValue{}
                """)
              .withImported(imported)
              .loadsWithError(1, alreadyDefinedIn(importedFilePath(), "otherModuleValue"));
        }

        @Test
        public void function_fails() {
          Definitions imported = module("""
            otherModuleFunction() = "abc";
            """)
              .withImportedModuleFiles()
              .loadsSuccessfully()
              .getModuleAsDefinitions();
          module("""
                OtherModuleFunction{}
                """)
              .withImported(imported)
              .loadsWithError(1, alreadyDefinedIn(importedFilePath(), "otherModuleFunction"));
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
        public void function_fails() {
          module("""
               myFunction() = "abc";
               MyFunction{}
               """)
              .loadsWithError(2, alreadyDefinedIn(filePath(), "myFunction"));
        }
      }

      @Nested
      class _internal {
        @Test
        public void function_fails() {
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
