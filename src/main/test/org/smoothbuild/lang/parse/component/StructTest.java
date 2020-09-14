package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.parse.component.TestModuleLoader.module;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StructTest {
  @Test
  public void declaring_empty_struct_is_allowed() {
    module("MyStruct {}")
        .loadsSuccessfully();
  }

  @Test
  public void creating_empty_struct_instance_is_allowed() {
    module("""
        MyStruct {}
        result = myStruct();
        """)
        .loadsSuccessfully();
  }

  @Test
  public void creating_non_empty_struct_is_allowed() {
    module("""
        MyStruct {
          String field,
        }
        result = myStruct("abc");
        """)
        .loadsSuccessfully();
  }

  @Test
  public void calling_constructor_without_all_parameters_causes_error() {
    module("""
        MyStruct {
          String field,
        }
        result = myStruct();
        """)
        .loadsWithError(4, "In call to `myStruct`: Parameter `field` must be specified.");
  }

  @Nested
  class field {
    @Test
    public void read_struct_field() {
      module("""
          MyStruct {
            String field,
          }
          String result = myStruct("abc").field;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void chaining_field_read_is_allowed() {
      module("""
          S1 {
            S2 f1,
          }
          S2 {
            S3 f2,
          }
          S3 {
            String f3,
          }
          String result = s1(s2(s3("abc"))).f1.f2.f3;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void reading_field_of_string_value_causes_error() {
      module(
          """
          result = "abc".accessedField;
          """)
          .loadsWithError(1, "Type `String` doesn't have field `accessedField`.");
    }

    @Test
    public void reading_field_that_does_not_exist_causes_error() {
      module("""
          MyStruct {
            String field,
          }
          result = myStruct("abc").otherField;
          """)
          .loadsWithError(4, "Type `MyStruct` doesn't have field `otherField`.");
    }

    @Test
    public void reading_field_suffixed_with_parentheses_causes_error() {
      module("""
          MyStruct {
            String field,
          } 
          String result = myStruct("abc").field();
          """)
          .loadsWithError(4, """
              mismatched input '(' expecting {';', '|', '.'}
              String result = myStruct("abc").field();
                                                   ^""");
    }

    @Test
    public void field_cannot_be_called_as_normal_function() {
      module("""
          MyStruct {
            String field,
          }  
          String result = MyStruct.field(myStruct("abc"));
          """)
          .loadsWithError(4, """
              mismatched input 'MyStruct' expecting {'[', IDENTIFIER, STRING, BLOB}
              String result = MyStruct.field(myStruct("abc"));
                              ^^^^^^^^""");
    }
  }
}
