package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.base.type.TestingTypesS.A;
import static org.smoothbuild.lang.base.type.TestingTypesS.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypesS.INT;
import static org.smoothbuild.lang.base.type.TestingTypesS.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypesS.STRING;
import static org.smoothbuild.lang.base.type.TestingTypesS.a;
import static org.smoothbuild.lang.base.type.TestingTypesS.f;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class InferenceTest extends TestingContext {
  @Nested
  class _inferring_value_type_from {
    @Test
    public void string_literal() {
      String code = """
          myValue = "abc";
          """;
      mod(code)
          .loadsSuccessfully()
          .containsEvalWithType("myValue", STRING);
    }

    @Test
    public void blob_literal() {
      String code = """
          myValue = 0x07;
          """;
      mod(code)
          .loadsSuccessfully()
          .containsEvalWithType("myValue", BLOB);
    }

    @Test
    public void int_literal() {
      String code = """
          myValue = 123;
          """;
      mod(code)
          .loadsSuccessfully()
          .containsEvalWithType("myValue", INT);
    }

    @Test
    public void array_literal() {
      String code = """
          myValue = [ "abc" ];
          """;
      mod(code)
          .loadsSuccessfully()
          .containsEvalWithType("myValue", a(STRING));
    }

    @Test
    public void value_reference() {
      String code = """
          String stringValue = "abc";
          myValue = stringValue;
          """;
      mod(code)
          .loadsSuccessfully()
          .containsEvalWithType("myValue", STRING);
    }

    @Test
    public void func_reference() {
      String code = """
          String myFunc(Blob param) = "abc";
          myValue = myFunc;
          """;
      mod(code)
          .loadsSuccessfully()
          .containsEvalWithType("myValue", f(STRING, BLOB));
    }

    @Test
    public void func_call() {
      String code = """
          String myFunc() = "abc";
          myValue = myFunc();
          """;
      mod(code)
          .loadsSuccessfully()
          .containsEvalWithType("myValue", STRING);
    }
  }

  @Nested
  class _inferring_func_result_type_from {
    @Test
    public void string_literal() {
      String code = """
          myFunc() = "abc";
          """;
      mod(code)
          .loadsSuccessfully()
          .containsEvalWithType("myFunc", f(STRING));
    }

    @Test
    public void blob_literal() {
      String code = """
          myFunc() = 0x07;
          """;
      mod(code)
          .loadsSuccessfully()
          .containsEvalWithType("myFunc", f(BLOB));
    }

    @Test
    public void int_literal() {
      String code = """
          myFunc() = 123;
          """;
      mod(code)
          .loadsSuccessfully()
          .containsEvalWithType("myFunc", f(INT));
    }

    @Test
    public void array_literal() {
      String code = """
          myFunc() = [ "abc" ];
          """;
      mod(code)
          .loadsSuccessfully()
          .containsEvalWithType("myFunc", f(a(STRING)));
    }

    @Test
    public void value_reference() {
      String code = """
          String stringValue = "abc";
          myFunc() = stringValue;
          """;
      mod(code)
          .loadsSuccessfully()
          .containsEvalWithType("myFunc", f(STRING));
    }

    @Test
    public void func_reference() {
      String code = """
          String otherFunc(Blob param) = "abc";
          myFunc() = otherFunc;
          """;
      mod(code)
          .loadsSuccessfully()
          .containsEvalWithType("myFunc", f(f(STRING, BLOB)));
    }

    @Test
    public void func_call() {
      String code = """
          String otherFunc() = "abc";
          myFunc() = otherFunc();
          """;
      mod(code)
          .loadsSuccessfully()
          .containsEvalWithType("myFunc", f(STRING));
    }

    @Test
    public void func_param() {
      String code = """
          myFunc(String param) = param;
          """;
      mod(code)
          .loadsSuccessfully()
          .containsEvalWithType("myFunc", f(STRING, STRING));
    }

    @Test
    public void func_generic_param() {
      String code = """
          myFunc(A param) = param;
          """;
      mod(code)
          .loadsSuccessfully()
          .containsEvalWithType("myFunc", f(A, A));
    }
  }

  @Nested
  class _inferring_array_literal_type {
    @Test
    public void when_elems_have_the_same_type() {
      String code = """
            result = [ "abc", "def" ];
            """;
      mod(code)
          .loadsSuccessfully()
          .containsEvalWithType("result", a(STRING));
    }

    @Test
    public void when_elems_have_convertible_types() {
      String code = """
            @Native("impl.met")
            Nothing myNothing();
            result = [ "abc", myNothing() ];
            """;
      mod(code)
          .loadsSuccessfully()
          .containsEvalWithType("result", a(STRING));
    }

    @Test
    public void when_elems_have_base_types_that_have_no_common_super_type() {
      String code = """
            result = [
              "abc",
              0x01,
            ];
            """;
      mod(code)
          .loadsWithError(3,"""
                  Array elems at indexes 0 and 1 doesn't have common super type.
                  Element at index 0 type = `String`
                  Element at index 1 type = `Blob`""");
    }

    @Test
    public void when_elems_have_func_types_that_have_no_common_super_type() {
      String code = """
            String firstFunc() = "abc";
            Blob secondFunc() = 0x01;
            result = [
              firstFunc,
              secondFunc,
            ];
            """;
      mod(code)
          .loadsWithError(5, """
                  Array elems at indexes 0 and 1 doesn't have common super type.
                  Element at index 0 type = `String()`
                  Element at index 1 type = `Blob()`""");
    }
  }

  @Nested
  class _inferring_func_param_generic_types_fails_when_var_has_two_inconvertible_lower_bounds {
    @Test
    public void base_types() {
      String code = """
          String myEqual(A p1, A p2) = "true";
          result = myEqual("def", 0x01);
          """;
      mod(code)
          .loadsWithError(2, "Cannot infer actual type for type var `A`.");
    }

    @Test
    public void arrays() {
      String code = """
          String myEqual(A p1, A p2) = "true";
          result = myEqual(["def"], [0x01]);
          """;
      mod(code)
          .loadsWithError(2, "Cannot infer actual type for type var `A`.");
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
      mod(code)
          .loadsWithError(10, "Cannot infer actual type for type var `A`.");
    }
  }

  @Nested
  class _inferring_call_result_type {
    @Nested
    class _identity_func_applied_to {
      @Test
      public void nothing() {
        String code = """
            @Native("impl.met")
            Nothing nothingFunc();
            A myIdentity(A a) = a;
            myValue = myIdentity(nothingFunc());
            """;
        mod(code)
            .loadsSuccessfully()
            .containsEvalWithType("myValue", NOTHING);
      }

      @Test
      public void arg_of_base_type() {
        String code = """
            A myIdentity(A a) = a;
            myValue = myIdentity("abc");
            """;
        mod(code)
            .loadsSuccessfully()
            .containsEvalWithType("myValue", STRING);
      }

      @Test
      public void array() {
        String code = """
            A myIdentity(A a) = a;
            myValue = myIdentity(["abc"]);
            """;
        mod(code)
            .loadsSuccessfully()
            .containsEvalWithType("myValue", a(STRING));
      }

      @Test
      public void func() {
        String code = """
            A myIdentity(A a) = a;
            String myFunc(Blob param) = "abc";
            myValue = myIdentity(myFunc);
            """;
        mod(code)
            .loadsSuccessfully()
            .containsEvalWithType("myValue", f(STRING, BLOB));
      }
    }

    @Nested
    class _first_elem_func_applied_to {
      @Test
      public void nothing_array() {
        String code = """
            @Native("impl.met")
            A firstElement([A] array);
            myValue = firstElement([]);
            """;
        mod(code)
            .loadsSuccessfully()
            .containsEvalWithType("myValue", NOTHING);
      }

      @Test
      public void array() {
        String code = """
            @Native("impl.met")
            A firstElement([A] array);
            myValue = firstElement(["abc"]);
            """;
        mod(code)
            .loadsSuccessfully()
            .containsEvalWithType("myValue", STRING);
      }

      @Test
      public void array2() {
        String code = """
            @Native("impl.met")
            A firstElement([A] array);
            myValue = firstElement([["abc"]]);
            """;
        mod(code)
            .loadsSuccessfully()
            .containsEvalWithType("myValue", a(STRING));
      }

      @Test
      public void func_array_with_convertible_funcs() {
        String code = """
            @Native("impl.met")
            A firstElement([A] array);
            String myFunc1(Blob param) = "abc";
            String myFunc2(String param) = "abc";
            myValue = firstElement([ myFunc1, myFunc2 ]);
            """;
        mod(code)
            .loadsSuccessfully()
            .containsEvalWithType("myValue", f(STRING, NOTHING));
      }
    }

    @Nested
    class _single_elem_array_func_applied_to {
      @Test
      public void nothing() {
        String code = """
            @Native("impl.met")
            Nothing nothingFunc();
            [A] singleElement(A a) = [a];
            myValue = singleElement(nothingFunc());
            """;
        mod(code)
            .loadsSuccessfully()
            .containsEvalWithType("myValue", a(NOTHING));
      }

      @Test
      public void arg_of_base_type() {
        String code = """
            [A] singleElement(A a) = [a];
            myValue = singleElement("abc");
            """;
        mod(code)
            .loadsSuccessfully()
            .containsEvalWithType("myValue", a(STRING));
      }

      @Test
      public void array() {
        String code = """
            [A] singleElement(A a) = [a];
            myValue = singleElement(["abc"]);
            """;
        mod(code)
            .loadsSuccessfully()
            .containsEvalWithType("myValue", a(a(STRING)));
      }

      @Test
      public void func() {
        String code = """
            [A] singleElement(A a) = [a];
            String myFunc(Blob param) = "abc";
            myValue = singleElement(myFunc);
            """;
        mod(code)
            .loadsSuccessfully()
            .containsEvalWithType("myValue", a(f(STRING, BLOB)));
      }
    }

    @Test
    @Disabled
    public void bug() {
      // This test fails because call to func `f` will infer bounds for 2 vars:
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
      mod(code)
          .loadsSuccessfully()
          .containsEvalWithType("result", a(STRING));
    }
  }
}
