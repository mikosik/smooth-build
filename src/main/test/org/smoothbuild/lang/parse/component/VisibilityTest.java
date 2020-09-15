package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.parse.component.TestModuleLoader.module;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.Definitions;

public class VisibilityTest {
  @Test
  public void reference_in_default_value_expression_to_other_parameter_fails() {
    module("""
        func(String param, String withDefault = param) = param;
        """)
        .loadsWithError(1, "`param` is undefined.");
  }

  @Nested
  class value_reference {
    @Nested
    class to_local {
      @Test
      public void value_succeeds() {
        module("""
               String myValue;
               result = myValue;
               """)
            .loadsSuccessfully();
      }

      @Test
      public void value_declared_below_succeeds() {
        module("""
               result = myValue;
               String myValue;
               """)
            .loadsSuccessfully();
      }

      @Test
      public void function_fails() {
        module("""
               String myFunction();
               result = myFunction;
               """)
            .loadsWithError(2, "`myFunction` is a function and cannot be accessed as a value.");
      }

      @Test
      public void constructor_fails() {
        module("""
               MyStruct {}
               result = myStruct;
               """)
            .loadsWithError(2, "`myStruct` is a function and cannot be accessed as a value.");
      }
    }

    @Nested
    class to_imported {
      @Test
      public void value_succeeds() {
        Definitions imported = module("""
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
      public void function_fails() {
        Definitions imported = module("""
            String otherModuleFunction();
            """)
            .loadsSuccessfully()
            .getModule();
        module("""
                myValue = otherModuleFunction;
                """)
            .withImported(imported)
            .loadsWithError(1,
                "`otherModuleFunction` is a function and cannot be accessed as a value.");
      }

      @Test
      public void constructor_fails() {
        Definitions imported = module("""
            OtherModuleStruct{}
            """)
            .loadsSuccessfully()
            .getModule();
        module("""
                myValue = otherModuleStruct;
                """)
            .withImported(imported)
            .loadsWithError(1,
                "`otherModuleStruct` is a function and cannot be accessed as a value.");
      }
    }

    @Nested
    class to_parameter {
      @Test
      public void succeeds() {
        module("""
               myFunction(String param) = param;
               """)
            .loadsSuccessfully();
      }
    }

    @Nested
    class to_undefined_value {
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
  class call {
    @Nested
    class to_local {
      @Test
      public void value_fails() {
        module("""
               String myValue;
               result = myValue();
               """)
            .loadsWithError(2, "`myValue` cannot be called as it is a value.");
      }

      @Test
      public void function_succeeds() {
        module("""
               String myFunction();
               result = myFunction();
               """)
            .loadsSuccessfully();
      }

      @Test
      public void function_declared_below_succeeds() {
        module("""
               result = myFunction();
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
    class to_imported {
      @Test
      public void value_fails() {
        Definitions imported = module("""
            String otherModuleValue;
            """)
            .loadsSuccessfully()
            .getModule();
        module("""
               result = otherModuleValue();
               """)
            .withImported(imported)
            .loadsWithError(1, "`otherModuleValue` cannot be called as it is a value.");
      }

      @Test
      public void function_succeeds() {
        Definitions imported = module("""
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
    class to_parameter {
      @Test
      public void fails() {
        module("""
               myFunction(String param) = param();
               """)
            .loadsWithError(1, "Parameter `param` cannot be called as it is not a function.");
      }
    }

    @Nested
    class to_undefined_function {
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

  @Nested
  class undefined_declared_as_a_type_of {
    @Test
    public void value() {
      module("""
             Undefined myValue;
             """)
          .loadsWithError(1, "Undefined type `Undefined`.");
    }

    @Test
    public void function() {
      module("""
             Undefined myFunction();
             """)
          .loadsWithError(1, "Undefined type `Undefined`.");
    }

    @Test
    public void parameter() {
      module("""
             String myFunction(Undefined param);
             """)
          .loadsWithError(1, "Undefined type `Undefined`.");
    }

    @Test
    public void field() {
      module("""
             MyStruct {
               Undefined field
             }
             """)
          .loadsWithError(2, "Undefined type `Undefined`.");
    }
  }
}
