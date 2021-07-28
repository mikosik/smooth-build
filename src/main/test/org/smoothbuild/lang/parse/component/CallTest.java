package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.TestModuleLoader.module;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.define.Definitions;

public class CallTest {
  @Nested
  class call_to_local {
    @Test
    public void value_of_non_function_type_fails() {
      module("""
             String myValue = "abc";
             result = myValue();
             """)
          .loadsWithError(2, "`myValue` cannot be called as it is not a function but `String`.");
    }

    @Test
    public void value_of_non_function_type_declared_below_fails() {
      module("""
             result = myValue();
             String myValue = "abc";
             """)
          .loadsWithError(1, "`myValue` cannot be called as it is not a function but `String`.");
    }

    @Test
    public void value_of_function_type_succeeds() {
      module("""
             @Native("impl")
             String() myFunctionValue;
             result = myFunctionValue();
             """)
          .loadsSuccessfully();
    }

    @Test
    public void value_of_function_with_parameter_type_succeeds() {
      module("""
             @Native("impl")
             String(String) myFunctionValue;
             result = myFunctionValue("abc");
             """)
          .loadsSuccessfully();
    }

    @Test
    public void value_of_function_type_declared_below_succeeds() {
      module("""
             result = myFunctionValue();
             @Native("Impl.met")
             String() myFunctionValue;
             """)
          .loadsSuccessfully();
    }

    @Test
    public void function_succeeds() {
      module("""
             String myFunction() = "abc";
             result = myFunction();
             """)
          .loadsSuccessfully();
    }

    @Test
    public void function_with_parameter_succeeds() {
      module("""
             String myFunction(String string) = "abc";
             result = myFunction("abc");
             """)
          .loadsSuccessfully();
    }

    @Test
    public void function_declared_below_succeeds() {
      module("""
             result = myFunction();
             String myFunction() = "abc";
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
  class call_to_imported {
    @Test
    public void value_of_non_function_type_fails() {
      Definitions imported = module("""
          String otherModuleValue = "abc";
          """)
          .loadsSuccessfully()
          .getModuleAsDefinitions();
      module("""
             result = otherModuleValue();
             """)
          .withImported(imported)
          .loadsWithError(1,
              "`otherModuleValue` cannot be called as it is not a function but `String`.");
    }

    @Test
    public void value_of_function_type_succeeds() {
      Definitions imported = module("""
          @Native("impl")
          String() otherModuleFunctionValue;
          """)
          .loadsSuccessfully()
          .getModuleAsDefinitions();
      module("""
             result = otherModuleFunctionValue();
             """)
          .withImported(imported)
          .loadsSuccessfully();
    }

    @Test
    public void function_succeeds() {
      Definitions imported = module("""
          String otherModuleFunction() = "abc";
          """)
          .loadsSuccessfully()
          .getModuleAsDefinitions();
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
          .getModuleAsDefinitions();
      module("""
             result = otherModuleStruct();
             """)
          .withImported(imported)
          .loadsSuccessfully();
    }
  }

  @Nested
  class call_to_parameter {
    @Test
    public void of_non_function_type_fails() {
      module("""
             myFunction(String param) = param();
             """)
          .loadsWithError(1, "`param` cannot be called as it is not a function but `String`.");
    }

    @Test
    public void of_function_type_succeeds() {
      module("""
             myFunction(String() param) = param();
             """)
          .loadsSuccessfully();
    }

    @Test
    public void of_function_with_parameter_type_succeeds() {
      module("""
             myFunction(String(String) param) = param("abc");
             """)
          .loadsSuccessfully();
    }
  }

  @Nested
  class call_to_undefined_function {
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
