package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.TestModuleLoader.err;
import static org.smoothbuild.lang.TestModuleLoader.module;
import static org.smoothbuild.lang.base.define.TestingFileLocation.fileLocation;
import static org.smoothbuild.lang.base.define.TestingFileLocation.importedFileLocation;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.FileLocation;

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
            .loadsWithError(1, alreadyDefinedIn(importedFileLocation(), "otherModuleValue"));
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
            .loadsWithError(1, alreadyDefinedIn(importedFileLocation(), "otherModuleFunction"));
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
            .loadsWithError(1, alreadyDefinedIn(importedFileLocation(), "otherModuleStruct"));
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
            .loadsWithError(2, alreadyDefinedIn(fileLocation(), "myValue"));
      }

      @Test
      public void function() {
        module("""
               myFunction() = "abc";
               myFunction = "def";
               """)
            .loadsWithError(2, alreadyDefinedIn(fileLocation(), "myFunction"));
      }

      @Test
      public void constructor() {
        module("""
               MyStruct {}
               myStruct = "abc";
               """)
            .loadsWithError(2, alreadyDefinedIn(fileLocation(), "myStruct"));
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
            .loadsWithError(1, alreadyDefinedIn(importedFileLocation(), "otherModuleValue"));
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
            .loadsWithError(1, alreadyDefinedIn(importedFileLocation(), "otherModuleFunction"));
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
            .loadsWithError(1, alreadyDefinedIn(importedFileLocation(), "otherModuleStruct"));
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
            .loadsWithError(2, alreadyDefinedIn(fileLocation(), "myValue"));
      }

      @Test
      public void function() {
        module("""
               myFunction() = "abc";
               myFunction() = "def";
               """)
            .loadsWithError(2, alreadyDefinedIn(fileLocation(), "myFunction"));
      }

      @Test
      public void constructor() {
        module("""
               MyStruct {}
               myStruct() = "abc";
               """)
            .loadsWithError(2, alreadyDefinedIn(fileLocation(), "myStruct"));
      }
    }
  }

  @Nested
  class parameter_clashes_with {
    @Test
    public void other_parameter() {
      module("""
             @Native("impl")
             String myFunction(
               String param,
               String param);    
               """)
          .loadsWithError(4, alreadyDefinedIn(fileLocation(), 3, "param"));
    }
  }

  @Nested
  class struct_clashes_with {
    @Nested
    class imported {
      @Test
      public void base_type() {
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
                    err(1, alreadyDefinedIn(importedFileLocation(), "OtherModuleStruct")),
                    err(1, alreadyDefinedIn(importedFileLocation(), "otherModuleStruct"))
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
                    err(2, alreadyDefinedIn(fileLocation(), "OtherModuleStruct")),
                    err(2, alreadyDefinedIn(fileLocation(), "otherModuleStruct"))
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
          .loadsWithError(3, alreadyDefinedIn(fileLocation(), 2, "field"));
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
            .loadsWithError(1, alreadyDefinedIn(importedFileLocation(), "otherModuleValue"));
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
            .loadsWithError(1, alreadyDefinedIn(importedFileLocation(), "otherModuleFunction"));
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
            .loadsWithError(2, alreadyDefinedIn(fileLocation(), "myValue"));
      }

      @Test
      public void function() {
        module("""
               myFunction() = "abc";
               MyFunction{}
               """)
            .loadsWithError(2, alreadyDefinedIn(fileLocation(), "myFunction"));
      }
    }
  }

  private static String alreadyDefinedIn(FileLocation fileLocation, String name) {
    return alreadyDefinedIn(fileLocation, 1, name);
  }

  private static String alreadyDefinedIn(FileLocation fileLocation, int line, String name) {
    return "`" + name + "` is already defined at " + fileLocation.path() + ":" + line + ".";
  }
}
