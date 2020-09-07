package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.parse.component.TestModuleLoader.module;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ExpressionTest {
  @Nested
  class field_read_can_be_used_as {
    @Test
    public void argument() {
      module("""
          MyStruct {
            String field,
          }
          myValue = myStruct("abc");
          String myFunction(String param);
          result = myFunction(myValue.field);
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
          String myFunction(String value = value.field);
          """)
          .loadsSuccessfully();
    }
  }

  @Nested
  class pipe_can_be_used_as {
    @Test
    public void argument() {
      module("""
          A myIdentity(A value);
          String myFunction(String param);
          result = myFunction("abc" | myIdentity);
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
          String myIdentity(String string) = string;
          String myFunction(String param = "abc" | myIdentity());
          """)
          .loadsSuccessfully();
    }
  }

  @Nested
  class blob_literal_can_be_used_as {
    @Test
    public void argument() {
      module("""
          String myFunction(Blob param);
          result = myFunction(0x01);
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
          String myFunction(Blob value = 0x01);
          """)
      .loadsSuccessfully();
    }
  }

  @Nested
  class string_literal_can_be_used_as {
    @Test
    public void argument() {
      module("""
          String myFunction(String param);
          result = myFunction("abc");
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
          String myFunction(String value = "abc");
          """)
      .loadsSuccessfully();
    }
  }

  @Nested
  class call_can_be_used_as {
    @Test
    public void argument() {
      module("""
          String otherFunction();
          String myFunction(String param);
          result = myFunction(otherFunction());
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
          String myFunction();
          String otherFunction(String value = myFunction());
          """)
          .loadsSuccessfully();
    }
  }
}
