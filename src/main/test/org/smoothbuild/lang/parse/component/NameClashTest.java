package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.io.fs.base.TestingFilePath.filePath;
import static org.smoothbuild.io.fs.base.TestingFilePath.importedFilePath;
import static org.smoothbuild.lang.TestModuleLoader.err;
import static org.smoothbuild.lang.TestModuleLoader.module;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.io.fs.base.FilePath;
import org.smoothbuild.lang.base.define.Definitions;

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
      public void function() {
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
      public void constructor() {
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
    class local {
      @Test
      public void value() {
        module("""
               myValue = "abc";
               myValue = "def";
               """)
            .loadsWithError(2, alreadyDefinedIn(filePath(), "myValue"));
      }

      @Test
      public void function() {
        module("""
               myFunction() = "abc";
               myFunction = "def";
               """)
            .loadsWithError(2, alreadyDefinedIn(filePath(), "myFunction"));
      }

      @Test
      public void constructor() {
        module("""
               MyStruct {}
               myStruct = "abc";
               """)
            .loadsWithError(2, alreadyDefinedIn(filePath(), "myStruct"));
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
      public void function() {
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
      public void constructor() {
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
    class local {
      @Test
      public void value() {
        module("""
               myValue = "abc";
               myValue() = "def";
               """)
            .loadsWithError(2, alreadyDefinedIn(filePath(), "myValue"));
      }

      @Test
      public void function() {
        module("""
               myFunction() = "abc";
               myFunction() = "def";
               """)
            .loadsWithError(2, alreadyDefinedIn(filePath(), "myFunction"));
      }

      @Test
      public void constructor() {
        module("""
               MyStruct {}
               myStruct() = "abc";
               """)
            .loadsWithError(2, alreadyDefinedIn(filePath(), "myStruct"));
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
          .loadsWithError(4, alreadyDefinedIn(filePath(), 3, "param"));
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
            .withImportedModuleFiles()
            .loadsSuccessfully()
            .getModuleAsDefinitions();
        module("""
                OtherModuleStruct {}
                """)
            .withImported(imported)
            .loadsWithErrors(List.of(
                    err(1, alreadyDefinedIn(importedFilePath(), "OtherModuleStruct")),
                    err(1, alreadyDefinedIn(importedFilePath(), "otherModuleStruct"))
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
                    err(2, alreadyDefinedIn(filePath(), "OtherModuleStruct")),
                    err(2, alreadyDefinedIn(filePath(), "otherModuleStruct"))
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
          .loadsWithError(3, alreadyDefinedIn(filePath(), 2, "field"));
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
      public void function() {
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
    class local {
      @Test
      public void value() {
        module("""
               myValue = "abc";
               MyValue{}
               """)
            .loadsWithError(2, alreadyDefinedIn(filePath(), "myValue"));
      }

      @Test
      public void function() {
        module("""
               myFunction() = "abc";
               MyFunction{}
               """)
            .loadsWithError(2, alreadyDefinedIn(filePath(), "myFunction"));
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
