package org.smoothbuild.parse.component;

import static org.smoothbuild.util.collect.Lists.list;

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
      module(code)
          .loadsWithSuccess()
          .containsEvalWithType("myValue", stringTS());
    }

    @Test
    public void blob_literal() {
      String code = """
          myValue = 0x07;
          """;
      module(code)
          .loadsWithSuccess()
          .containsEvalWithType("myValue", blobTS());
    }

    @Test
    public void int_literal() {
      String code = """
          myValue = 123;
          """;
      module(code)
          .loadsWithSuccess()
          .containsEvalWithType("myValue", intTS());
    }

    @Test
    public void array_literal() {
      String code = """
          myValue = ["abc"];
          """;
      module(code)
          .loadsWithSuccess()
          .containsEvalWithType("myValue", arrayTS(stringTS()));
    }

    @Test
    public void value_reference() {
      String code = """
          String stringValue = "abc";
          myValue = stringValue;
          """;
      module(code)
          .loadsWithSuccess()
          .containsEvalWithType("myValue", stringTS());
    }

    @Test
    public void func_reference() {
      String code = """
          String myFunc(Blob param) = "abc";
          myValue = myFunc;
          """;
      module(code)
          .loadsWithSuccess()
          .containsEvalWithType("myValue", funcTS(stringTS(), list(blobTS())));
    }

    @Test
    public void func_call() {
      String code = """
          String myFunc() = "abc";
          myValue = myFunc();
          """;
      module(code)
          .loadsWithSuccess()
          .containsEvalWithType("myValue", stringTS());
    }
  }

  @Nested
  class _inferring_func_result_type_from {
    @Test
    public void string_literal() {
      String code = """
          myFunc() = "abc";
          """;
      module(code)
          .loadsWithSuccess()
          .containsEvalWithType("myFunc", funcTS(stringTS()));
    }

    @Test
    public void blob_literal() {
      String code = """
          myFunc() = 0x07;
          """;
      module(code)
          .loadsWithSuccess()
          .containsEvalWithType("myFunc", funcTS(blobTS()));
    }

    @Test
    public void int_literal() {
      String code = """
          myFunc() = 123;
          """;
      module(code)
          .loadsWithSuccess()
          .containsEvalWithType("myFunc", funcTS(intTS()));
    }

    @Test
    public void array_literal() {
      String code = """
          myFunc() = ["abc"];
          """;
      module(code)
          .loadsWithSuccess()
          .containsEvalWithType("myFunc", funcTS(arrayTS(stringTS())));
    }

    @Test
    public void value_reference() {
      String code = """
          String stringValue = "abc";
          myFunc() = stringValue;
          """;
      module(code)
          .loadsWithSuccess()
          .containsEvalWithType("myFunc", funcTS(stringTS()));
    }

    @Test
    public void func_reference() {
      String code = """
          String otherFunc(Blob param) = "abc";
          myFunc() = otherFunc;
          """;
      module(code)
          .loadsWithSuccess()
          .containsEvalWithType("myFunc", funcTS(funcTS(stringTS(), list(blobTS()))));
    }

    @Test
    public void func_call() {
      String code = """
          String otherFunc() = "abc";
          myFunc() = otherFunc();
          """;
      module(code)
          .loadsWithSuccess()
          .containsEvalWithType("myFunc", funcTS(stringTS()));
    }

    @Test
    public void func_param() {
      String code = """
          myFunc(String param) = param;
          """;
      module(code)
          .loadsWithSuccess()
          .containsEvalWithType("myFunc", funcTS(stringTS(), list(stringTS())));
    }

    @Test
    public void func_generic_param() {
      String code = """
          myFunc(A param) = param;
          """;
      module(code)
          .loadsWithSuccess()
          .containsEvalWithType("myFunc", funcTS(oVarTS("A"), list(oVarTS("A"))));
    }
  }

  @Nested
  class _inferring_array_literal_type {
    @Test
    public void when_elems_have_the_same_type() {
      String code = """
            result = ["abc", "def"];
            """;
      module(code)
          .loadsWithSuccess()
          .containsEvalWithType("result", arrayTS(stringTS()));
    }

    @Test
    public void when_elems_have_convertible_types() {
      String code = """
            @Native("impl.met")
            Nothing myNothing();
            result = ["abc", myNothing()];
            """;
      module(code)
          .loadsWithSuccess()
          .containsEvalWithType("result", arrayTS(stringTS()));
    }

    @Test
    public void when_elems_have_base_types_that_have_no_common_super_type() {
      String code = """
            result = [
              "abc",
              0x01,
            ];
            """;
      module(code)
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
      module(code)
          .loadsWithError(5, """
                  Array elems at indexes 0 and 1 doesn't have common super type.
                  Element at index 0 type = `String()`
                  Element at index 1 type = `Blob()`""");
    }
  }

  @Nested
  class _inferring_func_param_types {
    @Nested
    class _fails_when_var_has_two_inconvertible_lower_bounds {
      @Test
      public void base_types() {
        String code = """
          String myEqual(A p1, A p2) = "true";
          result = myEqual("def", 0x01);
          """;
        module(code)
            .loadsWithError(2, "Cannot infer actual type for type var `A`.");
      }

      @Test
      public void arrays() {
        String code = """
          String myEqual(A p1, A p2) = "true";
          result = myEqual(["def"], [0x01]);
          """;
        module(code)
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
        module(code)
            .loadsWithError(10, "Cannot infer actual type for type var `A`.");
      }
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
        module(code)
            .loadsWithSuccess()
            .containsEvalWithType("myValue", nothingTS());
      }

      @Test
      public void arg_of_base_type() {
        String code = """
            A myIdentity(A a) = a;
            myValue = myIdentity("abc");
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvalWithType("myValue", stringTS());
      }

      @Test
      public void array() {
        String code = """
            A myIdentity(A a) = a;
            myValue = myIdentity(["abc"]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvalWithType("myValue", arrayTS(stringTS()));
      }

      @Test
      public void func() {
        String code = """
            A myIdentity(A a) = a;
            String myFunc(Blob param) = "abc";
            myValue = myIdentity(myFunc);
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvalWithType("myValue", funcTS(stringTS(), list(blobTS())));
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
        module(code)
            .loadsWithSuccess()
            .containsEvalWithType("myValue", nothingTS());
      }

      @Test
      public void array() {
        String code = """
            @Native("impl.met")
            A firstElement([A] array);
            myValue = firstElement(["abc"]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvalWithType("myValue", stringTS());
      }

      @Test
      public void array2() {
        String code = """
            @Native("impl.met")
            A firstElement([A] array);
            myValue = firstElement([["abc"]]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvalWithType("myValue", arrayTS(stringTS()));
      }

      @Test
      public void func_array_with_convertible_funcs() {
        String code = """
            @Native("impl.met")
            A firstElement([A] array);
            String myFunc1(Blob param) = "abc";
            String myFunc2(String param) = "abc";
            myValue = firstElement([myFunc1, myFunc2]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvalWithType("myValue", funcTS(stringTS(), list(nothingTS())));
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
        module(code)
            .loadsWithSuccess()
            .containsEvalWithType("myValue", arrayTS(nothingTS()));
      }

      @Test
      public void arg_of_base_type() {
        String code = """
            [A] singleElement(A a) = [a];
            myValue = singleElement("abc");
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvalWithType("myValue", arrayTS(stringTS()));
      }

      @Test
      public void array() {
        String code = """
            [A] singleElement(A a) = [a];
            myValue = singleElement(["abc"]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvalWithType("myValue", arrayTS(arrayTS(stringTS())));
      }

      @Test
      public void func() {
        String code = """
            [A] singleElement(A a) = [a];
            String myFunc(Blob param) = "abc";
            myValue = singleElement(myFunc);
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvalWithType("myValue", arrayTS(funcTS(stringTS(), list(blobTS()))));
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
      module(code)
          .loadsWithSuccess()
          .containsEvalWithType("result", arrayTS(stringTS()));
    }
  }
}
