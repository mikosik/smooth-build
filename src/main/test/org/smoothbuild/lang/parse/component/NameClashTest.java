package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.parse.component.TestModuleLoader.IMPORTED_INFO;
import static org.smoothbuild.lang.parse.component.TestModuleLoader.MODULE_INFO;
import static org.smoothbuild.lang.parse.component.TestModuleLoader.err;
import static org.smoothbuild.lang.parse.component.TestModuleLoader.module;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.Definitions;
import org.smoothbuild.lang.base.ModuleInfo;

public class NameClashTest {
  @Nested
  class value_clashes_with {
    @Nested
    class imported {
      @Test
      public void value() {
        Definitions imported = module("""
            otherModuleValue = "abc";
            """)
            .withImportedModuleInfo()
            .loadsSuccessfully();
        module("""
               otherModuleValue = "def";
               """)
            .withImported(imported)
            .loadsWithError(1, alreadyDefinedIn(IMPORTED_INFO, "otherModuleValue"));
      }

      @Test
      public void function() {
        Definitions imported = module("""
            otherModuleFunction() = "abc";
            """)
            .withImportedModuleInfo()
            .loadsSuccessfully();
        module("""
                otherModuleFunction = "def";
                """)
            .withImported(imported)
            .loadsWithError(1, alreadyDefinedIn(IMPORTED_INFO, "otherModuleFunction"));
      }

      @Test
      public void constructor() {
        Definitions imported = module("""
            OtherModuleStruct {}
            """)
            .withImportedModuleInfo()
            .loadsSuccessfully();
        module("""
                otherModuleStruct = "def";
                """)
            .withImported(imported)
            .loadsWithError(1, alreadyDefinedIn(IMPORTED_INFO, "otherModuleStruct"));
      }
    }

    @Nested
    class local {
      @Test
      public void value() {
        module("""
               myValue = "abc";
               myValue = "def";
               """)
            .loadsWithError(2, alreadyDefinedIn(MODULE_INFO, "myValue"));
      }

      @Test
      public void function() {
        module("""
               myFunction() = "abc";
               myFunction = "def";
               """)
            .loadsWithError(2, alreadyDefinedIn(MODULE_INFO, "myFunction"));
      }

      @Test
      public void constructor() {
        module("""
               MyStruct {}
               myStruct = "abc";
               """)
            .loadsWithError(2, alreadyDefinedIn(MODULE_INFO, "myStruct"));
      }
    }
  }

  @Nested
  class function_clashes_with {
    @Nested
    class imported {
      @Test
      public void value() {
        Definitions imported = module("""
            otherModuleValue = "abc";
            """)
            .withImportedModuleInfo()
            .loadsSuccessfully();
        module("""
                otherModuleValue() = "def";
                """)
            .withImported(imported)
            .loadsWithError(1, alreadyDefinedIn(IMPORTED_INFO, "otherModuleValue"));
      }

      @Test
      public void function() {
        Definitions imported = module("""
            otherModuleFunction() = "abc";
            """)
            .withImportedModuleInfo()
            .loadsSuccessfully();
        module("""
                otherModuleFunction() = "def";
                """)
            .withImported(imported)
            .loadsWithError(1, alreadyDefinedIn(IMPORTED_INFO, "otherModuleFunction"));
      }

      @Test
      public void constructor() {
        Definitions imported = module("""
            OtherModuleStruct {}
            """)
            .withImportedModuleInfo()
            .loadsSuccessfully();
        module("""
                otherModuleStruct() = "def";
                """)
            .withImported(imported)
            .loadsWithError(1, alreadyDefinedIn(IMPORTED_INFO, "otherModuleStruct"));
      }
    }

    @Nested
    class local {
      @Test
      public void value() {
        module("""
               myValue = "abc";
               myValue() = "def";
               """)
            .loadsWithError(2, alreadyDefinedIn(MODULE_INFO, "myValue"));
      }

      @Test
      public void function() {
        module("""
               myFunction() = "abc";
               myFunction() = "def";
               """)
            .loadsWithError(2, alreadyDefinedIn(MODULE_INFO, "myFunction"));
      }

      @Test
      public void constructor() {
        module("""
               MyStruct {}
               myStruct() = "abc";
               """)
            .loadsWithError(2, alreadyDefinedIn(MODULE_INFO, "myStruct"));
      }
    }
  }

  @Nested
  class parameter_clashes_with {
    @Test
    public void other_parameter() {
      module("""
             String myFunction(
               String param,
               String param);    
               """)
          .loadsWithError(3, alreadyDefinedIn(MODULE_INFO, 2, "param"));
    }
  }

  @Nested
  class struct_clashes_with {
    @Nested
    class imported {
      @Test
      public void basic_type() {
        module("""
               String {}
               """)
            .loadsWithError(1, "`" + "String" + "` is already defined.");
      }

      @Test
      public void struct() {
        Definitions imported = module("""
            OtherModuleStruct {}
            """)
            .withImportedModuleInfo()
            .loadsSuccessfully();
        module("""
                OtherModuleStruct {}
                """)
            .withImported(imported)
            .loadsWithErrors(List.of(
                    err(1, alreadyDefinedIn(IMPORTED_INFO, "OtherModuleStruct")),
                    err(1, alreadyDefinedIn(IMPORTED_INFO, "otherModuleStruct"))
                ));
      }
    }

    @Nested
    class local {
      @Test
      public void struct() {
        module("""
               OtherModuleStruct {}
               OtherModuleStruct {}
               """)
            .loadsWithErrors(List.of(
                    err(2, alreadyDefinedIn(MODULE_INFO, "OtherModuleStruct")),
                    err(2, alreadyDefinedIn(MODULE_INFO, "otherModuleStruct"))
                ));
      }
    }
  }

  @Nested
  class field_clashes_with {
    @Test
    public void other_field() {
      module("""
             MyStruct {
               String field,
               String field
             }
             """)
          .loadsWithError(3, alreadyDefinedIn(MODULE_INFO, 2, "field"));
    }
  }

  @Nested
  class constructor_clashes_with {
    @Nested
    class imported {
      @Test
      public void value() {
        Definitions imported = module("""
            otherModuleValue = "abc";
            """)
            .withImportedModuleInfo()
            .loadsSuccessfully();
        module("""
                OtherModuleValue{}
                """)
            .withImported(imported)
            .loadsWithError(1, alreadyDefinedIn(IMPORTED_INFO, "otherModuleValue"));
      }

      @Test
      public void function() {
        Definitions imported = module("""
            otherModuleFunction() = "abc";
            """)
            .withImportedModuleInfo()
            .loadsSuccessfully();
        module("""
                OtherModuleFunction{}
                """)
            .withImported(imported)
            .loadsWithError(1, alreadyDefinedIn(IMPORTED_INFO, "otherModuleFunction"));
      }
    }

    @Nested
    class local {
      @Test
      public void value() {
        module("""
               myValue = "abc";
               MyValue{}
               """)
            .loadsWithError(2, alreadyDefinedIn(MODULE_INFO, "myValue"));
      }

      @Test
      public void function() {
        module("""
               myFunction() = "abc";
               MyFunction{}
               """)
            .loadsWithError(2, alreadyDefinedIn(MODULE_INFO, "myFunction"));
      }
    }
  }

  private static String alreadyDefinedIn(ModuleInfo moduleInfo, String name) {
    return alreadyDefinedIn(moduleInfo, 1, name);
  }

  private static String alreadyDefinedIn(ModuleInfo moduleInfo, int line, String name) {
    return "`" + name + "` is already defined at " + moduleInfo.smooth().shorted()
        + ":" + line + ".";
  }
}
