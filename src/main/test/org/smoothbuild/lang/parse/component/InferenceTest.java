package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.base.type.TestingTypes.A;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.INT;
import static org.smoothbuild.lang.base.type.TestingTypes.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.a;
import static org.smoothbuild.lang.base.type.TestingTypes.f;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContextImpl;

public class InferenceTest extends TestingContextImpl {
  @Nested
  class _inferring_value_type_from {
    @Test
    public void string_literal() {
      String code = """
          myValue = "abc";
          """;
      module(code)
          .loadsSuccessfully()
          .containsReferencableWithType("myValue", STRING);
    }

    @Test
    public void blob_literal() {
      String code = """
          myValue = 0x07;
          """;
      module(code)
          .loadsSuccessfully()
          .containsReferencableWithType("myValue", BLOB);
    }

    @Test
    public void int_literal() {
      String code = """
          myValue = 123;
          """;
      module(code)
          .loadsSuccessfully()
          .containsReferencableWithType("myValue", INT);
    }

    @Test
    public void array_literal() {
      String code = """
          myValue = [ "abc" ];
          """;
      module(code)
          .loadsSuccessfully()
          .containsReferencableWithType("myValue", a(STRING));
    }

    @Test
    public void value_reference() {
      String code = """
          String stringValue = "abc";
          myValue = stringValue;
          """;
      module(code)
          .loadsSuccessfully()
          .containsReferencableWithType("myValue", STRING);
    }

    @Test
    public void function_reference() {
      String code = """
          String myFunction(Blob param) = "abc";
          myValue = myFunction;
          """;
      module(code)
          .loadsSuccessfully()
          .containsReferencableWithType("myValue", f(STRING, BLOB));
    }

    @Test
    public void function_call() {
      String code = """
          String myFunction() = "abc";
          myValue = myFunction();
          """;
      module(code)
          .loadsSuccessfully()
          .containsReferencableWithType("myValue", STRING);
    }
  }

  @Nested
  class _inferring_function_result_type_from {
    @Test
    public void string_literal() {
      String code = """
          myFunction() = "abc";
          """;
      module(code)
          .loadsSuccessfully()
          .containsReferencableWithType("myFunction", f(STRING));
    }

    @Test
    public void blob_literal() {
      String code = """
          myFunction() = 0x07;
          """;
      module(code)
          .loadsSuccessfully()
          .containsReferencableWithType("myFunction", f(BLOB));
    }

    @Test
    public void int_literal() {
      String code = """
          myFunction() = 123;
          """;
      module(code)
          .loadsSuccessfully()
          .containsReferencableWithType("myFunction", f(INT));
    }

    @Test
    public void array_literal() {
      String code = """
          myFunction() = [ "abc" ];
          """;
      module(code)
          .loadsSuccessfully()
          .containsReferencableWithType("myFunction", f(a(STRING)));
    }

    @Test
    public void value_reference() {
      String code = """
          String stringValue = "abc";
          myFunction() = stringValue;
          """;
      module(code)
          .loadsSuccessfully()
          .containsReferencableWithType("myFunction", f(STRING));
    }

    @Test
    public void function_reference() {
      String code = """
          String otherFunction(Blob param) = "abc";
          myFunction() = otherFunction;
          """;
      module(code)
          .loadsSuccessfully()
          .containsReferencableWithType("myFunction", f(f(STRING, BLOB)));
    }

    @Test
    public void function_call() {
      String code = """
          String otherFunction() = "abc";
          myFunction() = otherFunction();
          """;
      module(code)
          .loadsSuccessfully()
          .containsReferencableWithType("myFunction", f(STRING));
    }

    @Test
    public void function_parameter() {
      String code = """
          myFunction(String param) = param;
          """;
      module(code)
          .loadsSuccessfully()
          .containsReferencableWithType("myFunction", f(STRING, STRING));
    }

    @Test
    public void function_generic_parameter() {
      String code = """
          myFunction(A param) = param;
          """;
      module(code)
          .loadsSuccessfully()
          .containsReferencableWithType("myFunction", f(A, A));
    }
  }

  @Nested
  class _inferring_array_literal_type {
    @Test
    public void when_elements_have_the_same_type() {
      String code = """
            result = [ "abc", "def" ];
            """;
      module(code)
          .loadsSuccessfully()
          .containsReferencableWithType("result", a(STRING));
    }

    @Test
    public void when_elements_have_convertible_types() {
      String code = """
            @Native("impl.met")
            Nothing myNothing();
            result = [ "abc", myNothing() ];
            """;
      module(code)
          .loadsSuccessfully()
          .containsReferencableWithType("result", a(STRING));
    }

    @Test
    public void when_elements_have_base_types_that_have_no_common_super_type() {
      String code = """
            result = [
              "abc",
              0x01,
            ];
            """;
      module(code)
          .loadsWithError(3,"""
                  Array elements at indexes 0 and 1 doesn't have common super type.
                  Element at index 0 type = `String`
                  Element at index 1 type = `Blob`""");
    }

    @Test
    public void when_elements_have_function_types_that_have_no_common_super_type() {
      String code = """
            String firstFunction() = "abc";
            Blob secondFunction() = 0x01;
            result = [
              firstFunction,
              secondFunction,
            ];
            """;
      module(code)
          .loadsWithError(5, """
                  Array elements at indexes 0 and 1 doesn't have common super type.
                  Element at index 0 type = `String()`
                  Element at index 1 type = `Blob()`""");
    }
  }

  @Nested
  class _inferring_function_parameter_generic_types_fails_when_variable_has_two_inconvertible_lower_bounds {
    @Test
    public void base_types() {
      String code = """
          String myEqual(A p1, A p2) = "true";
          result = myEqual("def", 0x01);
          """;
      module(code)
          .loadsWithError(2, "Cannot infer actual type for type variable `A`.");
    }

    @Test
    public void arrays() {
      String code = """
          String myEqual(A p1, A p2) = "true";
          result = myEqual(["def"], [0x01]);
          """;
      module(code)
          .loadsWithError(2, "Cannot infer actual type for type variable `A`.");
    }

    @Test
    public void structs_with_the_same_object_db_representation() {
      String code = """
          Vector {
            String x,
            String y,
          }
          Tuple {
            String a,
            String b,
          }
          String myEqual(A p1, A p2) = "true";
          result = myEqual(vector("aaa", "bbb"), tuple("aaa", "bbb"));
          """;
      module(code)
          .loadsWithError(10, "Cannot infer actual type for type variable `A`.");
    }
  }

  @Nested
  class _inferring_call_result_type {
    @Nested
    class _identity_function_applied_to {
      @Test
      public void nothing() {
        String code = """
            @Native("impl.met")
            Nothing nothingValue;
            A myIdentity(A a) = a;
            myValue = myIdentity(nothingValue);
            """;
        module(code)
            .loadsSuccessfully()
            .containsReferencableWithType("myValue", NOTHING);
      }

      @Test
      public void argument_of_base_type() {
        String code = """
            A myIdentity(A a) = a;
            myValue = myIdentity("abc");
            """;
        module(code)
            .loadsSuccessfully()
            .containsReferencableWithType("myValue", STRING);
      }

      @Test
      public void array() {
        String code = """
            A myIdentity(A a) = a;
            myValue = myIdentity(["abc"]);
            """;
        module(code)
            .loadsSuccessfully()
            .containsReferencableWithType("myValue", a(STRING));
      }

      @Test
      public void function() {
        String code = """
            A myIdentity(A a) = a;
            String myFunction(Blob param) = "abc";
            myValue = myIdentity(myFunction);
            """;
        module(code)
            .loadsSuccessfully()
            .containsReferencableWithType("myValue", f(STRING, BLOB));
      }
    }

    @Nested
    class _first_element_function_applied_to {
      @Test
      public void nothing_array() {
        String code = """
            @Native("impl.met")
            A firstElement([A] array);
            myValue = firstElement([]);
            """;
        module(code)
            .loadsSuccessfully()
            .containsReferencableWithType("myValue", NOTHING);
      }

      @Test
      public void array() {
        String code = """
            @Native("impl.met")
            A firstElement([A] array);
            myValue = firstElement(["abc"]);
            """;
        module(code)
            .loadsSuccessfully()
            .containsReferencableWithType("myValue", STRING);
      }

      @Test
      public void array2() {
        String code = """
            @Native("impl.met")
            A firstElement([A] array);
            myValue = firstElement([["abc"]]);
            """;
        module(code)
            .loadsSuccessfully()
            .containsReferencableWithType("myValue", a(STRING));
      }

      @Test
      public void function_array_with_convertible_functions() {
        String code = """
            @Native("impl.met")
            A firstElement([A] array);
            String myFunction1(Blob param) = "abc";
            String myFunction2(String param) = "abc";
            myValue = firstElement([ myFunction1, myFunction2 ]);
            """;
        module(code)
            .loadsSuccessfully()
            .containsReferencableWithType("myValue", f(STRING, NOTHING));
      }
    }

    @Nested
    class _single_element_array_function_applied_to {
      @Test
      public void nothing() {
        String code = """
            @Native("impl.met")
            Nothing nothingValue;
            [A] singleElement(A a) = [a];
            myValue = singleElement(nothingValue);
            """;
        module(code)
            .loadsSuccessfully()
            .containsReferencableWithType("myValue", a(NOTHING));
      }

      @Test
      public void argument_of_base_type() {
        String code = """
            [A] singleElement(A a) = [a];
            myValue = singleElement("abc");
            """;
        module(code)
            .loadsSuccessfully()
            .containsReferencableWithType("myValue", a(STRING));
      }

      @Test
      public void array() {
        String code = """
            [A] singleElement(A a) = [a];
            myValue = singleElement(["abc"]);
            """;
        module(code)
            .loadsSuccessfully()
            .containsReferencableWithType("myValue", a(a(STRING)));
      }

      @Test
      public void function() {
        String code = """
            [A] singleElement(A a) = [a];
            String myFunction(Blob param) = "abc";
            myValue = singleElement(myFunction);
            """;
        module(code)
            .loadsSuccessfully()
            .containsReferencableWithType("myValue", a(f(STRING, BLOB)));
      }
    }

    @Test
    @Disabled
    public void bug() {
      // This test fails because call to function `f` will infer bounds for 2 variables:
      // A: (String, C)
      // B: ([C], Any)
      // Algorithm infers that B is upper than `[C]` so it takes `[C]` as call result, but
      // it's wrong. Algorithm is unable to notice that `C` is upper from `String` so it doesn't
      // do that substitution and can't find correct result `[String]`.
      // Fixing it is not easy because there can be cases with longer chain of dependencies
      // between `C` and `String` and there can be cases with circular dependencies that are
      // illegal. This probably means that we need more powerful inferring algorithm.
      String code = """
            B f(A item, B(A) convert) = convert(item);
            [C] single(C elem) = [elem];
            result = f("abc", single);
            """;
      module(code)
          .loadsSuccessfully()
          .containsReferencableWithType("result", a(STRING));
    }
  }
}
