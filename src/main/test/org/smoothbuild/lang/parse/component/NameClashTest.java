package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.base.TestingModuleLocation.importedModuleLocation;
import static org.smoothbuild.lang.base.TestingModuleLocation.moduleLocation;
import static org.smoothbuild.lang.parse.component.TestModuleLoader.err;
import static org.smoothbuild.lang.parse.component.TestModuleLoader.module;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.Definitions;
import org.smoothbuild.lang.base.ModuleLocation;

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
            .withImportedModuleLocation()
            .loadsSuccessfully()
            .getModule();
        module("""
               otherModuleValue = "def";
               """)
            .withImported(imported)
            .loadsWithError(1, alreadyDefinedIn(importedModuleLocation(), "otherModuleValue"));
      }

      @Test
      public void function() {
        Definitions imported = module("""
            otherModuleFunction() = "abc";
            """)
            .withImportedModuleLocation()
            .loadsSuccessfully()
            .getModule();
        module("""
                otherModuleFunction = "def";
                """)
            .withImported(imported)
            .loadsWithError(1, alreadyDefinedIn(importedModuleLocation(), "otherModuleFunction"));
      }

      @Test
      public void constructor() {
        Definitions imported = module("""
            OtherModuleStruct {}
            """)
            .withImportedModuleLocation()
            .loadsSuccessfully()
            .getModule();
        module("""
                otherModuleStruct = "def";
                """)
            .withImported(imported)
            .loadsWithError(1, alreadyDefinedIn(importedModuleLocation(), "otherModuleStruct"));
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
            .loadsWithError(2, alreadyDefinedIn(moduleLocation(), "myValue"));
      }

      @Test
      public void function() {
        module("""
               myFunction() = "abc";
               myFunction = "def";
               """)
            .loadsWithError(2, alreadyDefinedIn(moduleLocation(), "myFunction"));
      }

      @Test
      public void constructor() {
        module("""
               MyStruct {}
               myStruct = "abc";
               """)
            .loadsWithError(2, alreadyDefinedIn(moduleLocation(), "myStruct"));
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
            .withImportedModuleLocation()
            .loadsSuccessfully()
            .getModule();
        module("""
                otherModuleValue() = "def";
                """)
            .withImported(imported)
            .loadsWithError(1, alreadyDefinedIn(importedModuleLocation(), "otherModuleValue"));
      }

      @Test
      public void function() {
        Definitions imported = module("""
            otherModuleFunction() = "abc";
            """)
            .withImportedModuleLocation()
            .loadsSuccessfully()
            .getModule();
        module("""
                otherModuleFunction() = "def";
                """)
            .withImported(imported)
            .loadsWithError(1, alreadyDefinedIn(importedModuleLocation(), "otherModuleFunction"));
      }

      @Test
      public void constructor() {
        Definitions imported = module("""
            OtherModuleStruct {}
            """)
            .withImportedModuleLocation()
            .loadsSuccessfully()
            .getModule();
        module("""
                otherModuleStruct() = "def";
                """)
            .withImported(imported)
            .loadsWithError(1, alreadyDefinedIn(importedModuleLocation(), "otherModuleStruct"));
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
            .loadsWithError(2, alreadyDefinedIn(moduleLocation(), "myValue"));
      }

      @Test
      public void function() {
        module("""
               myFunction() = "abc";
               myFunction() = "def";
               """)
            .loadsWithError(2, alreadyDefinedIn(moduleLocation(), "myFunction"));
      }

      @Test
      public void constructor() {
        module("""
               MyStruct {}
               myStruct() = "abc";
               """)
            .loadsWithError(2, alreadyDefinedIn(moduleLocation(), "myStruct"));
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
          .loadsWithError(3, alreadyDefinedIn(moduleLocation(), 2, "param"));
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
            .withImportedModuleLocation()
            .loadsSuccessfully()
            .getModule();
        module("""
                OtherModuleStruct {}
                """)
            .withImported(imported)
            .loadsWithErrors(List.of(
                    err(1, alreadyDefinedIn(importedModuleLocation(), "OtherModuleStruct")),
                    err(1, alreadyDefinedIn(importedModuleLocation(), "otherModuleStruct"))
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
                    err(2, alreadyDefinedIn(moduleLocation(), "OtherModuleStruct")),
                    err(2, alreadyDefinedIn(moduleLocation(), "otherModuleStruct"))
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
          .loadsWithError(3, alreadyDefinedIn(moduleLocation(), 2, "field"));
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
            .withImportedModuleLocation()
            .loadsSuccessfully()
            .getModule();
        module("""
                OtherModuleValue{}
                """)
            .withImported(imported)
            .loadsWithError(1, alreadyDefinedIn(importedModuleLocation(), "otherModuleValue"));
      }

      @Test
      public void function() {
        Definitions imported = module("""
            otherModuleFunction() = "abc";
            """)
            .withImportedModuleLocation()
            .loadsSuccessfully()
            .getModule();
        module("""
                OtherModuleFunction{}
                """)
            .withImported(imported)
            .loadsWithError(1, alreadyDefinedIn(importedModuleLocation(), "otherModuleFunction"));
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
            .loadsWithError(2, alreadyDefinedIn(moduleLocation(), "myValue"));
      }

      @Test
      public void function() {
        module("""
               myFunction() = "abc";
               MyFunction{}
               """)
            .loadsWithError(2, alreadyDefinedIn(moduleLocation(), "myFunction"));
      }
    }
  }

  private static String alreadyDefinedIn(ModuleLocation moduleLocation, String name) {
    return alreadyDefinedIn(moduleLocation, 1, name);
  }

  private static String alreadyDefinedIn(ModuleLocation moduleLocation, int line, String name) {
    return "`" + name + "` is already defined at " + moduleLocation.path()
        + ":" + line + ".";
  }
}
