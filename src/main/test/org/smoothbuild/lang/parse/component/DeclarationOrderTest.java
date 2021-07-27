package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.TestModuleLoader.module;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class DeclarationOrderTest {
  @Nested
  class _type_declared_below {
    @Test
    public void struct_can_be_used_as_field_type() {
      module("""
             ReferencingStruct {
               MyStruct field
             }
             MyStruct {}
             """)
          .loadsSuccessfully();
    }

    @Test
    public void struct_can_be_used_as_field_arrayed_type() {
      module("""
             ReferencingStruct {
               String firstField,
               [MyStruct] field
             }
             MyStruct {}
             """)
          .loadsSuccessfully();
    }

    @Test
    public void value_can_be_used_as_its_type() {
      module("""
             @Native("Impl.met")
             MyStruct myValue;
             MyStruct {}
             """)
          .loadsSuccessfully();
    }

    @Test
    public void value_can_be_used_as_its_arrayed_type() {
      module("""
             @Native("Impl.met")
             [MyStruct] myValue;
             MyStruct {}
             """)
          .loadsSuccessfully();
    }

    @Test
    public void function_can_be_used_as_parameter_type() {
      module("""
             String myFunction(MyStruct param) = "abc";
             MyStruct {}
             """)
          .loadsSuccessfully();
    }

    @Test
    public void function_can_be_used_as_parameter_arrayed_type() {
      module("""
             String myFunction([MyStruct] param) = "abc";
             MyStruct {}
             """)
          .loadsSuccessfully();
    }

    @Test
    public void function_can_be_used_as_result_type() {
      module("""
             @Native("Impl.met")
             MyStruct myFunction(String param);
             MyStruct {}
             """)
          .loadsSuccessfully();
    }

    @Test
    public void function_can_be_used_as_arrayed_result_type() {
      module("""
             [MyStruct] myFunction(String param) = [];
             MyStruct {}
             """)
          .loadsSuccessfully();
    }
  }
}
