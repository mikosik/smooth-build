package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.TestModuleLoader.module;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.define.Definitions;

public class ReferenceTest {
  @Nested
  class _referencing_local {
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
  class _referencing_imported {
    @Test
    public void value_succeeds() {
      Definitions imported = module("""
          @Native("impl")
          String otherModuleValue;
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
    public void function_succeeds() {
      Definitions imported = module("""
          @Native("impl")
          String otherModuleFunction();
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
    public void constructor_succeeds() {
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
  }

  @Nested
  class _referencing_parameter {
    @Test
    public void from_function_body_succeeds() {
      module("""
             myFunction(String param) = param;
             """)
          .loadsSuccessfully();
    }

    @Test
    public void from_default_value_expression_of_other_parameter_fails() {
      module("""
        func(String param, String withDefault = param) = param;
        """)
          .loadsWithError(1, "`param` is undefined.");
    }
  }

  @Nested
  class _referencing_undefined_value {
    @Test
    public void fails() {
      module("""
             result = undefinedValue;
             """)
          .loadsWithError(1, "`undefinedValue` is undefined.");
    }
  }
}
