package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.TestModuleLoader.module;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.define.Definitions;

public class ReferenceTest {
  @Test
  public void reference_in_default_value_expression_to_other_parameter_fails() {
    module("""
        func(String param, String withDefault = param) = param;
        """)
        .loadsWithError(1, "`param` is undefined.");
  }

  @Nested
  class _reference {
    @Nested
    class _to_local {
      @Test
      public void value_succeeds() {
        module("""
               @Native("impl")
               String myValue;
               result = myValue;
               """)
            .loadsSuccessfully();
      }

      @Test
      public void value_declared_below_succeeds() {
        module("""
               result = myValue;
               @Native("impl")
               String myValue;
               """)
            .loadsSuccessfully();
      }

      @Test
      public void function_succeeds() {
        module("""
               @Native("impl")
               String myFunction();
               result = myFunction;
               """)
            .loadsSuccessfully();
      }

      @Test
      public void function_declared_below_succeeds() {
        module("""
               result = myFunction;
               @Native("Impl.met")
               String myFunction();
               """)
            .loadsSuccessfully();
      }

      @Test
      public void constructor_succeeds() {
        module("""
               MyStruct {}
               result = myStruct;
               """)
            .loadsSuccessfully();
      }

      @Test
      public void constructor_declared_below_succeeds() {
        module("""
               result = myStruct;
               MyStruct {}
               """)
            .loadsSuccessfully();
      }
    }

    @Nested
    class _to_imported {
      @Test
      public void value_succeeds() {
        Definitions imported = module("""
            @Native("impl")
            String otherModuleValue;
            """)
            .loadsSuccessfully()
            .getModule();
        module("""
            myValue = otherModuleValue;
            """)
            .withImported(imported)
            .loadsSuccessfully();
      }

      @Test
      public void function_succeeds() {
        Definitions imported = module("""
            @Native("impl")
            String otherModuleFunction();
            """)
            .loadsSuccessfully()
            .getModule();
        module("""
                myValue = otherModuleFunction;
                """)
            .withImported(imported)
            .loadsSuccessfully();
      }

      @Test
      public void constructor_succeeds() {
        Definitions imported = module("""
            OtherModuleStruct{}
            """)
            .loadsSuccessfully()
            .getModule();
        module("""
                myValue = otherModuleStruct;
                """)
            .withImported(imported)
            .loadsSuccessfully();
      }
    }

    @Nested
    class _to_parameter {
      @Test
      public void succeeds() {
        module("""
               myFunction(String param) = param;
               """)
            .loadsSuccessfully();
      }
    }

    @Nested
    class _to_undefined_value {
      @Test
      public void fails() {
        module("""
               result = undefinedValue;
               """)
            .loadsWithError(1, "`undefinedValue` is undefined.");
      }
    }
  }

  @Nested
  class _call {
    @Nested
    class _to_local {
      @Test
      public void value_fails() {
        module("""
               @Native("impl")
               String myValue;
               result = myValue();
               """)
            .loadsWithError(3, "`myValue` cannot be called as it is not a function.");
      }

      @Test
      public void value_declared_below_fails() {
        module("""
               result = myValue();
               @Native("Impl.met")
               String myValue;
               """)
            .loadsWithError(1, "`myValue` cannot be called as it is not a function.");
      }

      @Test
      public void function_succeeds() {
        module("""
               @Native("impl")
               String myFunction();
               result = myFunction();
               """)
            .loadsSuccessfully();
      }

      @Test
      public void function_declared_below_succeeds() {
        module("""
               result = myFunction();
               @Native("impl")
               String myFunction();
               """)
            .loadsSuccessfully();
      }

      @Test
      public void constructor_succeeds() {
        module("""
               MyStruct {}
               result = myStruct();
               """)
            .loadsSuccessfully();
      }

      @Test
      public void constructor_declared_below_succeeds() {
        module("""
               result = myStruct();
               MyStruct {}
               """)
            .loadsSuccessfully();
      }
    }

    @Nested
    class _to_imported {
      @Test
      public void value_fails() {
        Definitions imported = module("""
            @Native("impl")
            String otherModuleValue;
            """)
            .loadsSuccessfully()
            .getModule();
        module("""
               result = otherModuleValue();
               """)
            .withImported(imported)
            .loadsWithError(1, "`otherModuleValue` cannot be called as it is not a function.");
      }

      @Test
      public void function_succeeds() {
        Definitions imported = module("""
            @Native("impl")
            String otherModuleFunction();
            """)
            .loadsSuccessfully()
            .getModule();
        module("""
               result = otherModuleFunction();
               """)
            .withImported(imported)
            .loadsSuccessfully();
      }

      @Test
      public void constructor_succeeds() {
        Definitions imported = module("""
            OtherModuleStruct {}
            """)
            .loadsSuccessfully()
            .getModule();
        module("""
               result = otherModuleStruct();
               """)
            .withImported(imported)
            .loadsSuccessfully();
      }
    }

    @Nested
    class _to_parameter {
      @Test
      public void fails() {
        module("""
               myFunction(String param) = param();
               """)
            .loadsWithError(1, "`param` cannot be called as it is not a function.");
      }
    }

    @Nested
    class _to_undefined_function {
      @Test
      public void without_arguments_fails() {
        module("""
               result = undefinedFunction();
               """)
            .loadsWithError(1, "`undefinedFunction` is undefined.");
      }

      @Test
      public void with_argument_fails() {
        module("""
               result = undefinedFunction("abc");
               """)
            .loadsWithError(1, "`undefinedFunction` is undefined.");
      }
    }
  }
}
