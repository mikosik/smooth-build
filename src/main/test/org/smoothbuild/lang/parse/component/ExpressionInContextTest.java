package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.TestModuleLoader.module;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ExpressionInContextTest {
  @Nested
  class blob_literal_can_be_used_as {
    @Test
    public void argument() {
      module("""
          @Native("impl")
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
        @Native("impl")
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
        @Native("impl")
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
        @Native("impl")
        String myFunction(String value = "abc");
        """)
          .loadsSuccessfully();
    }
  }

  @Nested
  class field_read_can_be_used_as {
    @Test
    public void argument() {
      module("""
        MyStruct {
          String field,
        }
        myValue = myStruct("abc");
        @Native("impl")
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
        @Native("impl")
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
          @Native("impl")
          A myIdentity(A value);
          @Native("impl")
          String myFunction(String param);
          result = myFunction("abc" | myIdentity());
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
          @Native("impl")
          String myFunction(String param = "abc" | myIdentity());
          """)
          .loadsSuccessfully();
    }
  }

  @Nested
  class call_can_be_used_as {
    @Test
    public void argument() {
      module("""
        @Native("impl")
        String otherFunction();
        @Native("impl")
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
        @Native("impl")
        String myFunction();
        @Native("impl")
         String otherFunction(String value = myFunction());
        """)
          .loadsSuccessfully();
    }
  }

  @Nested
  class function_can_be_used_as {
    @Test
    public void argument() {
      module("""
          @Native("Impl.met")
          String otherFunction();
          @Native("Impl.met")
          String myFunction(String() param);
          result = myFunction(otherFunction);
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
          @Native("Impl.met")
          String myFunction();
          @Native("Impl.met")
          String otherFunction(String() value = myFunction);
          """)
          .loadsSuccessfully();
    }
  }

  @Nested
  class value_can_be_used_as {
    @Test
    public void argument() {
      module("""
          @Native("Impl.met")
          String myValue;
          @Native("Impl.met")
          String myFunction(String param);
          result = myFunction(myValue);
          """)
          .loadsSuccessfully();
    }

    @Test
    public void array_element() {
      module("""
           @Native("Impl.met")
           String myValue;
           result = [ myValue ];
           """)
          .loadsSuccessfully();
    }

    @Test
    public void parameter_default_value() {
      module("""
          @Native("Impl.met")
          String myValue;
          @Native("Impl.met")
          String myFunction(String value = myValue);
          """)
          .loadsSuccessfully();
    }
  }
}
