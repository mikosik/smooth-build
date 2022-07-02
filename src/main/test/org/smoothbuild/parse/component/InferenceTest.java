package org.smoothbuild.parse.component;

import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class InferenceTest extends TestContext {
  @Nested
  class _infer_value_type_from {
    @Test
    public void string_literal() {
      var code = """
          myValue = "abc";
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myValue", stringTS());
    }

    @Test
    public void blob_literal() {
      var code = """
          myValue = 0x07;
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myValue", blobTS());
    }

    @Test
    public void int_literal() {
      var code = """
          myValue = 123;
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myValue", intTS());
    }

    @Test
    public void array_literal() {
      var code = """
          myValue = ["abc"];
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myValue", arrayTS(stringTS()));
    }

    @Test
    public void value_ref() {
      var code = """
          String stringValue = "abc";
          myValue = stringValue;
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myValue", stringTS());
    }

    @Test
    public void mono_func_ref() {
      var code = """
          String myFunc(Blob param) = "abc";
          myValue = myFunc;
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myValue", funcTS(stringTS(), list(blobTS())));
    }

    @Test
    public void poly_func_ref_fails() {
      var code = """
          A myId(A a) = a;
          myValue = myId;
          """;
      module(code)
          .loadsWithError(2, "Cannot infer type parameters to convert function reference `myId` "
              + "to monomorphic function. You need to specify type of `myValue` explicitly.");
    }

    @Test
    public void mono_func_ref_call() {
      var code = """
          String myFunc() = "abc";
          myValue = myFunc();
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myValue", stringTS());
    }

    @Test
    public void poly_func_ref_call() {
      var code = """
          A myId(A a) = a;
          myValue = myId(7);
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myValue", intTS());
    }
  }

  @Nested
  class _infer_mono_func_result_type_from {
    @Test
    public void string_literal() {
      var code = """
          myFunc() = "abc";
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc", funcTS(stringTS()));
    }

    @Test
    public void blob_literal() {
      var code = """
          myFunc() = 0x07;
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc", funcTS(blobTS()));
    }

    @Test
    public void int_literal() {
      var code = """
          myFunc() = 123;
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc", funcTS(intTS()));
    }

    @Test
    public void array_literal() {
      var code = """
          myFunc() = ["abc"];
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc", funcTS(arrayTS(stringTS())));
    }

    @Test
    public void value_ref() {
      var code = """
          String stringValue = "abc";
          myFunc() = stringValue;
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc", funcTS(stringTS()));
    }

    @Test
    public void param_ref() {
      var code = """
          myFunc(Int int) = int;
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc", funcTS(intTS(), list(intTS())));
    }

    @Test
    public void param_func_call() {
      var code = """
          myFunc(Int() f) = f();
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc", funcTS(intTS(), list(funcTS(intTS()))));
    }

    @Test
    public void mono_func_ref() {
      var code = """
          String otherFunc(Blob param) = "abc";
          myFunc() = otherFunc;
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc", funcTS(funcTS(stringTS(), list(blobTS()))));
    }

    @Test
    public void poly_func_ref_fails() {
      var code = """
          A myId(A a) = a;
          myFunc() = myId;
          """;
      module(code)
          .loadsWithError(2, "Cannot infer type parameters to convert function reference `myId` "
              + "to monomorphic function. You need to specify type of `myFunc` explicitly.");
    }

    @Test
    public void mono_func_call() {
      var code = """
          String otherFunc() = "abc";
          myFunc() = otherFunc();
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc", funcTS(stringTS()));
    }

    @Test
    public void poly_func_call() {
      var code = """
          A myId(A a) = a;
          myFunc() = myId(7);
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc", funcTS(intTS()));
    }

    @Test
    public void func_mono_param() {
      var code = """
          myFunc(String param) = param;
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc", funcTS(stringTS(), list(stringTS())));
    }
  }

  @Nested
  class _infer_poly_func_result_type_from {
    @Test
    public void string_literal() {
      var code = """
          myFunc(A a) = "abc";
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc", polyFuncTS(stringTS(), list(varA())));
    }

    @Test
    public void blob_literal() {
      var code = """
          myFunc(A a) = 0x07;
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc", polyFuncTS(blobTS(), list(varA())));
    }

    @Test
    public void int_literal() {
      var code = """
          myFunc(A a) = 123;
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc", polyFuncTS(intTS(), list(varA())));
    }

    @Test
    public void array_literal() {
      var code = """
          myFunc(A a) = ["abc"];
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc", polyFuncTS(arrayTS(stringTS()), list(varA())));
    }

    @Test
    public void value_ref() {
      var code = """
          String stringValue = "abc";
          myFunc(A a) = stringValue;
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc", polyFuncTS(stringTS(), list(varA())));
    }

    @Test
    public void param_ref() {
      var code = """
          myFunc(A a, Int int) = int;
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc", polyFuncTS(intTS(), list(varA(), intTS())));
    }

    @Test
    public void param_mono_func_call() {
      var code = """
          myFunc(A a, Int() f) = f();
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc",
              polyFuncTS(intTS(), list(varA(), funcTS(intTS()))));
    }

    @Test
    public void mono_func_ref() {
      var code = """
          String otherFunc(Blob param) = "abc";
          myFunc(A a) = otherFunc;
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc",
              polyFuncTS(funcTS(stringTS(), list(blobTS())), list(varA())));
    }

    @Test
    public void poly_func_ref_fails() {
      var code = """
          A myId(A a) = a;
          myFunc(A a) = myId;
          """;
      module(code)
          .loadsWithError(2, "Cannot infer type parameters to convert function reference `myId` "
              + "to monomorphic function. You need to specify type of `myFunc` explicitly.");
    }

    @Test
    public void mono_func_ref_call() {
      var code = """
          String otherFunc() = "abc";
          myFunc(A a) = otherFunc();
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc", polyFuncTS(stringTS(), list(varA())));
    }

    @Test
    public void poly_func_ref_call() {
      var code = """
          A myId(A a) = a;
          myFunc(A a) = myId(7);
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc", polyFuncTS(intTS(), list(varA())));
    }

    @Test
    public void func_mono_param() {
      var code = """
          myFunc(A a, String param) = param;
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc",
              polyFuncTS(stringTS(), list(varA(), stringTS())));
    }

    @Test
    public void func_poly_param() {
      var code = """
          myFunc(A param) = param;
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc", polyFuncTS(varA(), list(varA())));
    }

    @Test
    public void func_poly_param_func() {
      var code = """
          myFunc(A(A) param) = param;
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("myFunc",
              polyFuncTS(funcTS(varA(), list(varA())), list(funcTS(varA(), list(varA())))));
    }
  }

  @Nested
  class _infer_array_literal_type {
    @Nested
    class _single_elem {
      @Test
      public void with_string_type() {
        var code = """
          result = ["abc"];
          """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("result", arrayTS(stringTS()));
      }

      @Test
      public void with_int_type() {
        var code = """
          result = [7];
          """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("result", arrayTS(intTS()));
      }

      @Test
      public void with_blob_type() {
        var code = """
          result = [0xAB];
          """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("result", arrayTS(blobTS()));
      }

      @Test
      public void with_array_type() {
        var code = """
          result = [[7]];
          """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("result", arrayTS(arrayTS(intTS())));
      }

      @Test
      public void with_value_ref() {
        var code = """
          Int myValue = 7;
          result = [myValue];
          """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("result", arrayTS(intTS()));
      }

      @Test
      public void with_mono_func_ref() {
        var code = """
          Int myIntId(Int i) = i;
          result = [myIntId];
          """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("result", arrayTS(funcTS(intTS(), list(intTS()))));
      }

      @Test
      public void with_poly_func_ref_fails() {
        var code = """
          A myId(A a) = a;
          result = [myId];
          """;
        module(code)
            .loadsWithError(2, "Cannot infer type parameters to convert function reference "
                + "`myId` to monomorphic function.");
      }
    }

    @Nested
    class _two_elems {
      @Test
      public void with_the_same_base_type() {
        var code = """
          result = ["abc", "def"];
          """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("result", arrayTS(stringTS()));
      }

      @Test
      public void with_convertible_types() {
        var code = """
          @Native("impl.met")
          Nothing myNothing();
          result = ["abc", myNothing()];
          """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("result", arrayTS(stringTS()));
      }

      @Test
      public void with_base_types_that_have_no_common_super_type() {
        var code = """
          result = [
            "abc",
            0x01,
          ];
          """;
        module(code)
            .loadsWithError(1,"Array elements don't have common super type.");
      }

      @Test
      public void with_mono_func_types_that_have_common_super_type() {
        var code = """
          [Int] firstFunc() = [7];
          [Nothing] secondFunc() = [];
          result = [
            firstFunc,
            secondFunc,
          ];
          """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("result", arrayTS(funcTS(arrayTS(intTS()))));
      }

      @Test
      public void with_mono_func_types_that_have_no_common_super_type() {
        var code = """
          String firstFunc() = "abc";
          Blob secondFunc() = 0x01;
          result = [
            firstFunc,
            secondFunc,
          ];
          """;
        module(code)
            .loadsWithError(3, "Array elements don't have common super type.");
      }

      @Test
      public void with_poly_func_type_fails() {
        var code = """
          A myId(A a) = a;
          A myOtherId(A a) = a;
          result = [myId, myOtherId];
          """;
        module(code)
            .loadsWithError(3, "Cannot infer type parameters to convert function reference "
                + "`myId` to monomorphic function.");
      }

      @Test
      public void one_with_mono_type_one_with_poly_type_convertible_to_mono_one() {
        var code = """
          Int myIntId(Int i) = i;
          A myId(A a) = a;
          result = [myIntId, myId];
          """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("result", arrayTS(funcTS(intTS(), list(intTS()))));
      }

      @Test
      public void one_with_mono_type_one_with_poly_type_not_convertible_to_mono_one() {
        var code = """
          Int myIntId(Int i) = i;
          A myId(A a, A b) = a;
          result = [myIntId, myId];
          """;
        module(code)
            .loadsWithError(3, "Array elements don't have common super type.");
      }
    }
  }

  @Nested
  class _infer_poly_func_call_type {
    @Nested
    class _fails_when_var_has_two_inconvertible_lower_bounds {
      @Test
      public void base_types() {
        var code = """
            String myEqual(A p1, A p2) = "true";
            result = myEqual("def", 0x01);
            """;
        module(code)
            .loadsWithError(2, "Cannot infer actual type for `A`.");
      }

      @Test
      public void arrays() {
        var code = """
            String myEqual(A p1, A p2) = "true";
            result = myEqual(["def"], [0x01]);
            """;
        module(code)
            .loadsWithError(2, "Cannot infer actual type for `A`.");
      }

      @Test
      public void structs_with_the_same_object_db_representation() {
        var code = """
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
            .loadsWithError(10, "Cannot infer actual type for `A`.");
      }
    }

    @Nested
    class _identity_func_applied_to {
      @Test
      public void nothing() {
        var code = """
            @Native("impl.met")
            Nothing nothingFunc();
            A myIdentity(A a) = a;
            myValue = myIdentity(nothingFunc());
            """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("myValue", nothingTS());
      }

      @Test
      public void arg_of_base_type() {
        var code = """
            A myIdentity(A a) = a;
            myValue = myIdentity("abc");
            """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("myValue", stringTS());
      }

      @Test
      public void array() {
        var code = """
            A myIdentity(A a) = a;
            myValue = myIdentity(["abc"]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("myValue", arrayTS(stringTS()));
      }

      @Test
      public void func() {
        var code = """
            A myIdentity(A a) = a;
            String myFunc(Blob param) = "abc";
            myValue = myIdentity(myFunc);
            """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("myValue", funcTS(stringTS(), list(blobTS())));
      }
    }

    @Nested
    class _first_elem_func_applied_to {
      @Test
      public void nothing_array() {
        var code = """
            @Native("impl.met")
            A firstElement([A] array);
            myValue = firstElement([]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("myValue", nothingTS());
      }

      @Test
      public void array() {
        var code = """
            @Native("impl.met")
            A firstElement([A] array);
            myValue = firstElement(["abc"]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("myValue", stringTS());
      }

      @Test
      public void array2() {
        var code = """
            @Native("impl.met")
            A firstElement([A] array);
            myValue = firstElement([["abc"]]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("myValue", arrayTS(stringTS()));
      }

      @Test
      public void func_array_with_convertible_funcs() {
        var code = """
            @Native("impl.met")
            A firstElement([A] array);
            String myFunc1(Blob param) = "abc";
            String myFunc2(String param) = "abc";
            myValue = firstElement([myFunc1, myFunc2]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("myValue", funcTS(stringTS(), list(nothingTS())));
      }
    }

    @Nested
    class _single_elem_array_func_applied_to {
      @Test
      public void nothing() {
        var code = """
            @Native("impl.met")
            Nothing nothingFunc();
            [A] singleElement(A a) = [a];
            myValue = singleElement(nothingFunc());
            """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("myValue", arrayTS(nothingTS()));
      }

      @Test
      public void arg_of_base_type() {
        var code = """
            [A] singleElement(A a) = [a];
            myValue = singleElement("abc");
            """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("myValue", arrayTS(stringTS()));
      }

      @Test
      public void array() {
        var code = """
            [A] singleElement(A a) = [a];
            myValue = singleElement(["abc"]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("myValue", arrayTS(arrayTS(stringTS())));
      }

      @Test
      public void func() {
        var code = """
            [A] singleElement(A a) = [a];
            String myFunc(Blob param) = "abc";
            myValue = singleElement(myFunc);
            """;
        module(code)
            .loadsWithSuccess()
            .containsTopRefableWithType("myValue", arrayTS(funcTS(stringTS(), list(blobTS()))));
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
      var code = """
          B f(A item, B(A) convert) = convert(item);
          [C] single(C elem) = [elem];
          result = f("abc", single);
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefableWithType("result", arrayTS(stringTS()));
    }
  }
}
