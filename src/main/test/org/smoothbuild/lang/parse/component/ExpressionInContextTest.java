package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.TestModuleLoader.module;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ExpressionInContextTest {
  @Nested
  class blob_literal_can_be_used_as {
    @Test
    public void function_argument() {
      module("""
          String myFunction(Blob b) = "abc";
          result = myFunction(0x01);
          """)
          .loadsSuccessfully();
    }

    @Test
    public void function_body() {
      module("""
          result() = 0x01;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      module("""
          result = 0x01;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_element() {
      module("""
           result = [ 0x01 ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void parameter_default_value() {
      module("""
        String myFunction(Blob b = 0x01) = "abc";
        """)
          .loadsSuccessfully();
    }
  }

  @Nested
  class string_literal_can_be_used_as {
    @Test
    public void function_argument() {
      module("""
        String myFunction(String param) = "abc";
        result = myFunction("abc");
        """)
          .loadsSuccessfully();
    }

    @Test
    public void function_body() {
      module("""
          result() = "abc";
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      module("""
          result = "abc";
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_element() {
      module("""
           result = [ "abc" ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void parameter_default_value() {
      module("""
        String myFunction(String s = "abc") = "abc";
        """)
          .loadsSuccessfully();
    }
  }

  @Nested
  class field_read_can_be_used_as {
    @Test
    public void function_argument() {
      module("""
        MyStruct {
          String field,
        }
        myValue = myStruct("abc");
        String myFunction(String s) = "abc";
        result = myFunction(myValue.field);
        """)
          .loadsSuccessfully();
    }

    @Test
    public void function_body() {
      module("""
          MyStruct {
            String field,
          }
          myValue = myStruct("abc");
          result() = myValue.field;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      module("""
          MyStruct {
            String field,
          }
          myValue = myStruct("abc");
          result = myValue.field;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_element() {
      module("""
           MyStruct {
             String field,
           }
           myValue = myStruct("abc");
           result = [ myValue.field ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void parameter_default_value() {
      module("""
        MyStruct {
          String field,
        }
        value = myStruct("abc");
        String myFunction(String value = value.field) = "abc";
        """)
          .loadsSuccessfully();
    }
  }

  @Nested
  class pipe_can_be_used_as {
    @Test
    public void function_argument() {
      module("""
          A myIdentity(A a) = a;
          String myFunction(String param) = "abc";
          result = myFunction("abc" | myIdentity());
          """)
          .loadsSuccessfully();
    }

    @Test
    public void function_body() {
      module("""
          A myIdentity(A a) = a;
          String myFunction(String param) = "abc";
          result() = "abc" | myIdentity();
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      module("""
          A myIdentity(A a) = a;
          String myFunction(String param) = "abc";
          result = "abc" | myIdentity();
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_element() {
      module("""
             String myIdentity(String string) = string;
             result = [ "abc" | myIdentity() ];
             """)
          .loadsSuccessfully();
    }

    @Test
    public void parameter_default_value() {
      module("""
          A myIdentity(A a) = a;
          String myFunction(String param = "abc" | myIdentity()) = "abc";
          """)
          .loadsSuccessfully();
    }
  }

  @Nested
  class call_can_be_used_as {
    @Test
    public void function_argument() {
      module("""
        String otherFunction() = "abc";
        String myFunction(String param) = "abc";
        result = myFunction(otherFunction());
        """)
          .loadsSuccessfully();
    }

    @Test
    public void function_body() {
      module("""
          String otherFunction() = "abc";
          result() = otherFunction();
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      module("""
          String otherFunction() = "abc";
          result = otherFunction();
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_element() {
      module("""
           String myFunction() = "abc";
           result = [ myFunction() ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void parameter_default_value() {
      module("""
        String myFunction() = "abc";
        String otherFunction(String value = myFunction()) = "abc";
        """)
          .loadsSuccessfully();
    }
  }

  @Nested
  class function_can_be_used_as {
    @Test
    public void function_argument() {
      module("""
          String otherFunction() = "abc";
          String myFunction(String() param) = "abc";
          result = myFunction(otherFunction);
          """)
          .loadsSuccessfully();
    }

    @Test
    public void function_body() {
      module("""
          String otherFunction() = "abc";
          result() = otherFunction;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      module("""
          String otherFunction() = "abc";
          result = otherFunction;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_element() {
      module("""
           String myFunction() = "abc";
           result = [ myFunction ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void parameter_default_value() {
      module("""
          String myFunction() = "abc";
          String otherFunction(String() value = myFunction) = "abc";
          """)
          .loadsSuccessfully();
    }
  }

  @Nested
  class value_can_be_used_as {
    @Test
    public void function_argument() {
      module("""
          String myValue = "abc";
          String myFunction(String param) = "abc";
          result = myFunction(myValue);
          """)
          .loadsSuccessfully();
    }

    @Test
    public void function_body() {
      module("""
          String myValue = "abc";
          result() = myValue;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void value_body() {
      module("""
          String myValue = "abc";
          result = myValue;
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_element() {
      module("""
           String myValue = "abc";
           result = [ myValue ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void parameter_default_value() {
      module("""
          String myValue = "abc";
          String myFunction(String value = myValue) = "abc";
          """)
          .loadsSuccessfully();
    }
  }
}
