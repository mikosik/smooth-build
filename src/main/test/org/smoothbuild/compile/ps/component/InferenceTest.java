package org.smoothbuild.compile.ps.component;

import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class InferenceTest extends TestContext {

  @Nested
  class _rankness {
    @Test
    public void of_higher_order_is_not_possible() {
      var code = """
            Pair {
              String string,
              Int int,
            }
            Pair f(String s, Int i, A(A) id) = pair(id(s), id(i));
            """;
      module(code)
          .loadsWithError(5, "Illegal call.");
    }

    @Test
    public void of_higher_order_is_not_possible_2() {
      var code = """
            f(String s, A(A) id) = id(s);
            """;
      module(code)
          .loadsWithError(1, "<Add error message here> 4");
    }
  }

  @Test
  public void two_differently_monoized_calls_to_poly_func_with_poly_default_arg() {
    var code = """
          [A] empty([A] array = []) = array;
          Pair {
            [String] s,
            [Int] i
          }
          myValue = pair(empty(), empty());
          """;
    module(code)
        .loadsWithSuccess();
  }

  @Nested
  class _infer_value_type_from {
    @Test
    public void string_literal() {
      var code = """
          myValue = "abc";
          """;
      module(code)
          .loadsWithSuccess()
          .containsRefableWithType("myValue", schemaS(stringTS()));
    }

    @Test
    public void blob_literal() {
      var code = """
          myValue = 0x07;
          """;
      module(code)
          .loadsWithSuccess()
          .containsRefableWithType("myValue", schemaS(blobTS()));
    }

    @Test
    public void int_literal() {
      var code = """
          myValue = 123;
          """;
      module(code)
          .loadsWithSuccess()
          .containsRefableWithType("myValue", schemaS(intTS()));
    }

    @Test
    public void array_literal() {
      var code = """
          myValue = ["abc"];
          """;
      module(code)
          .loadsWithSuccess()
          .containsRefableWithType("myValue", schemaS(arrayTS(stringTS())));
    }

    @Test
    public void value_ref() {
      var code = """
          String stringValue = "abc";
          myValue = stringValue;
          """;
      module(code)
          .loadsWithSuccess()
          .containsRefableWithType("myValue", schemaS(stringTS()));
    }

    @Test
    public void mono_func_ref() {
      var code = """
          String myFunc(Blob param) = "abc";
          myValue = myFunc;
          """;
      module(code)
          .loadsWithSuccess()
          .containsRefableWithType("myValue", schemaS(funcTS(stringTS(), list(blobTS()))));
    }

    @Test
    public void poly_func_ref() {
      var code = """
          A myId(A a) = a;
          myValue = myId;
          """;
      module(code)
          .loadsWithSuccess()
          .containsRefableWithType("myValue", schemaS(funcTS(varA(), varA())));
    }

    @Test
    public void mono_func_ref_call() {
      var code = """
          String myFunc() = "abc";
          myValue = myFunc();
          """;
      module(code)
          .loadsWithSuccess()
          .containsRefableWithType("myValue", schemaS(stringTS()));
    }

    @Test
    public void poly_func_ref_call() {
      var code = """
          A myId(A a) = a;
          myValue = myId(7);
          """;
      module(code)
          .loadsWithSuccess()
          .containsRefableWithType("myValue", schemaS(intTS()));
    }
  }

  @Nested
  class _infer_mono_func_result_type_from {
    @Nested
    class _literal {
      @Test
      public void string_literal() {
        var code = """
          myFunc() = "abc";
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", schemaS(funcTS(stringTS())));
      }

      @Test
      public void blob_literal() {
        var code = """
          myFunc() = 0x07;
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", schemaS(funcTS(blobTS())));
      }

      @Test
      public void int_literal() {
        var code = """
          myFunc() = 123;
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", schemaS(funcTS(intTS())));
      }
    }

    @Nested
    class _array {
      @Test
      public void mono_array() {
        var code = """
          myFunc() = ["abc"];
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", schemaS(funcTS(arrayTS(stringTS()))));
      }
    }

    @Test
    public void value_ref() {
      var code = """
          String stringValue = "abc";
          myFunc() = stringValue;
          """;
      module(code)
          .loadsWithSuccess()
          .containsRefableWithType("myFunc", schemaS(funcTS(stringTS())));
    }

    @Test
    public void param_ref() {
      var code = """
          myFunc(Int int) = int;
          """;
      module(code)
          .loadsWithSuccess()
          .containsRefableWithType("myFunc", schemaS(funcTS(intTS(), list(intTS()))));
    }

    @Test
    public void param_func_call() {
      var code = """
          myFunc(Int() f) = f();
          """;
      module(code)
          .loadsWithSuccess()
          .containsRefableWithType("myFunc", schemaS(funcTS(intTS(), list(funcTS(intTS())))));
    }

    @Test
    public void mono_func_ref() {
      var code = """
          String otherFunc(Blob param) = "abc";
          myFunc() = otherFunc;
          """;
      module(code)
          .loadsWithSuccess()
          .containsRefableWithType("myFunc", schemaS(funcTS(funcTS(stringTS(), list(blobTS())))));
    }

    @Test
    public void mono_func_call() {
      var code = """
          String otherFunc() = "abc";
          myFunc() = otherFunc();
          """;
      module(code)
          .loadsWithSuccess()
          .containsRefableWithType("myFunc", schemaS(funcTS(stringTS())));
    }

    @Test
    public void poly_func_call() {
      var code = """
          A myId(A a) = a;
          myFunc() = myId(7);
          """;
      module(code)
          .loadsWithSuccess()
          .containsRefableWithType("myFunc", schemaS(funcTS(intTS())));
    }

    @Test
    public void func_mono_param() {
      var code = """
          myFunc(String param) = param;
          """;
      module(code)
          .loadsWithSuccess()
          .containsRefableWithType("myFunc", schemaS(funcTS(stringTS(), list(stringTS()))));
    }
  }

  @Nested
  class _infer_poly_func_result_type_from {
    @Nested
    class _literal {
      @Test
      public void string_literal() {
        var code = """
          myFunc(A a) = "abc";
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", funcSchemaS(stringTS(), list(varA())));
      }

      @Test
      public void blob_literal() {
        var code = """
          myFunc(A a) = 0x07;
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", funcSchemaS(blobTS(), list(varA())));
      }

      @Test
      public void int_literal() {
        var code = """
          myFunc(A a) = 123;
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", funcSchemaS(intTS(), list(varA())));
      }
    }

    @Nested
    class _array {
      @Test
      public void mono_array() {
        var code = """
          myFunc(A a) = ["abc"];
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", funcSchemaS(arrayTS(stringTS()), list(varA())));
      }

      @Test
      public void poly_array() {
        var code = """
          myFunc() = [];
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", funcSchemaS(arrayTS(varA())));
      }

      @Test
      public void poly_array_when_func_type_param_shadows_array_generic_param() {
        var code = """
          myFunc(A a) = [];
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", funcSchemaS(arrayTS(varB()), list(varA())));
      }

      @Test
      public void poly_array_with_when_func_type_param_forces_array_type() {
        var code = """
          [B] myFunc() = [];
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", funcSchemaS(arrayTS(varB())));
      }
    }

    @Nested
    class _value {
      @Test
      public void mono_value_ref() {
        var code = """
          String stringValue = "abc";
          myFunc() = stringValue;
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", funcSchemaS(stringTS(), list()));
      }

      @Test
      public void poly_value_ref() {
        var code = """
          [A] stringValue = [];
          myFunc() = stringValue;
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", funcSchemaS(arrayTS(varA()), list()));
      }

      @Test
      public void poly_value_ref_when_func_type_param_shadows_value_type() {
        var code = """
          [A] stringValue = [];
          myFunc(A a) = stringValue;
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", funcSchemaS(arrayTS(varB()), list(varA())));
      }

      @Test
      public void poly_value_ref_when_func_type_param_forces_value_type() {
        var code = """
          [A] stringValue = [];
          [B] myFunc() = stringValue;
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", funcSchemaS(arrayTS(varB())));
      }
    }

    @Nested
    class _param_ref {
      @Test
      public void param_ref_with_base_type() {
        var code = """
          myFunc(Int int) = int;
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", funcSchemaS(intTS(), list(intTS())));
      }

      @Test
      public void param_ref_with_poly_type() {
        var code = """
          myFunc(A param) = param;
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", funcSchemaS(varA(), list(varA())));
      }

      @Test
      public void param_ref_with_mono_func_type() {
        var code = """
          myFunc(Int(Bool) func) = func;
          """;
        var funcT = funcTS(intTS(), boolTS());
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", funcSchemaS(funcT, list(funcT)));
      }

      @Test
      public void param_ref_with_poly_func_type() {
        var code = """
          myFunc(A(A) param) = param;
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc",
                funcSchemaS(funcTS(varA(), list(varA())), list(funcTS(varA(), list(varA())))));
      }
    }

    @Nested
    class _call {
      @Test
      public void call_to_mono_param() {
        var code = """
          myFunc(Int() f) = f();
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc",
                funcSchemaS(intTS(), list(funcTS(intTS()))));
      }

      @Test
      public void call_to_poly_param() {
        var code = """
          myFunc(A() f) = f();
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc",
                funcSchemaS(varA(), list(funcTS(varA()))));
      }

      @Test
      public void call_to_mono_func() {
        var code = """
          Int otherFunc() = 7;
          myFunc() = otherFunc();
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc",
                funcSchemaS(intTS(), list()));
      }

      @Test
      public void call_to_poly_func() {
        var code = """
          A myIdentity(A a) = a;
          myFunc() = myIdentity(7);
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc",
                funcSchemaS(intTS(), list()));
      }

      @Test
      public void call_to_poly_func_when_argument_type_is_our_type_param() {
        var code = """
          A myIdentity(A a) = a;
          myFunc(B b) = myIdentity(b);
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", funcSchemaS(varB(), list(varB())));
      }
    }

    @Nested
    class _func_ref {
      @Test
      public void mono_func_ref() {
        var code = """
          String otherFunc(Blob param) = "abc";
          myFunc() = otherFunc;
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc",
                funcSchemaS(funcTS(stringTS(), list(blobTS())), list()));
      }

      @Test
      public void poly_func_ref() {
        var code = """
          A myId(A a) = a;
          myFunc() = myId;
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", funcSchemaS(funcTS(varA(), list(varA())), list()));
      }

      @Test
      public void poly_func_ref_when_func_type_param_shadows_referenced_func_type_param() {
        var code = """
          A myId(A a) = a;
          myFunc(A a) = myId;
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", funcSchemaS(funcTS(varB(), varB()), list(varA())));
      }

      @Test
      public void poly_func_ref_when_func_type_param_forces_referenced_func_type_param() {
        var code = """
          A myId(A a) = a;
          B(B) myFunc() = myId;
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myFunc", funcSchemaS(funcTS(varB(), varB()), list()));
      }
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
            .containsRefableWithType("result", schemaS(arrayTS(stringTS())));
      }

      @Test
      public void with_int_type() {
        var code = """
          result = [7];
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("result", schemaS(arrayTS(intTS())));
      }

      @Test
      public void with_blob_type() {
        var code = """
          result = [0xAB];
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("result", schemaS(arrayTS(blobTS())));
      }

      @Test
      public void with_array_type() {
        var code = """
          result = [[7]];
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("result", schemaS(arrayTS(arrayTS(intTS()))));
      }

      @Test
      public void with_value_ref() {
        var code = """
          Int myValue = 7;
          result = [myValue];
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("result", schemaS(arrayTS(intTS())));
      }

      @Test
      public void with_mono_func_ref() {
        var code = """
          Int myIntId(Int i) = i;
          result = [myIntId];
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("result", schemaS(arrayTS(funcTS(intTS(), list(intTS())))));
      }

      @Test
      public void with_poly_func_ref() {
        var code = """
          A myId(A a) = a;
          result = [myId];
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("result", schemaS(arrayTS(funcTS(varA(), list(varA())))));
      }
    }

    @Nested
    class _two_elems {
      @Test
      public void with_same_base_type() {
        var code = """
          result = ["abc", "def"];
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("result", schemaS(arrayTS(stringTS())));
      }

      @Test
      public void with_different_base_types() {
        var code = """
          result = [
            "abc",
            0x01,
          ];
          """;
        module(code)
            .loadsWithError(1,
                "Cannot infer type for array literal. Its element types are not compatible.");
      }

      @Test
      public void with_same_mono_func_types() {
        var code = """
          Int firstFunc() = 7;
          Int secondFunc() = 3;
          result = [
            firstFunc,
            secondFunc,
          ];
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("result", schemaS(arrayTS(funcTS(intTS()))));
      }

      @Test
      public void with_different_mono_func_types() {
        var code = """
          String firstFunc() = "abc";
          Blob secondFunc() = 0x01;
          result = [
            firstFunc,
            secondFunc,
          ];
          """;
        module(code)
            .loadsWithError(3,
                "Cannot infer type for array literal. Its element types are not compatible.");
      }

      @Test
      public void with_same_poly_funcs_types() {
        var code = """
          A myId(A a) = a;
          B myOtherId(B b) = b;
          result = [myId, myOtherId];
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("result", schemaS(arrayTS(idFuncS().mono().type())));
      }

      @Test
      public void with_different_poly_funcs_types() {
        var code = """
          A myId(A a) = a;
          B otherFunc(B b, C c) = b;
          result = [myId, otherFunc];
          """;
        module(code)
            .loadsWithError(3,
                "Cannot infer type for array literal. Its element types are not compatible.");
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
            .containsRefableWithType("result", schemaS(arrayTS(funcTS(intTS(), list(intTS())))));
      }

      @Test
      public void one_with_mono_type_one_with_poly_type_not_convertible_to_mono_one() {
        var code = """
          Int myIntId(Int i) = i;
          A myId(A a, A b) = a;
          result = [myIntId, myId];
          """;
        module(code)
            .loadsWithError(3,
                "Cannot infer type for array literal. Its element types are not compatible.");
      }
    }
  }

  @Nested
  class _infer_poly_func_call_type {
    @Nested
    class _fails_when_var_unifies_two_incompatible_types {
      @Test
      public void base_types() {
        var code = """
            String myEqual(A p1, A p2) = "true";
            result = myEqual("def", 0x01);
            """;
        module(code)
            .loadsWithError(2, "Illegal call.");
      }

      @Test
      public void arrays() {
        var code = """
            String myEqual(A p1, A p2) = "true";
            result = myEqual(["def"], [0x01]);
            """;
        module(code)
            .loadsWithError(2, "Illegal call.");
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
            .loadsWithError(10, "Illegal call.");
      }
    }

    @Nested
    class _identity_func_applied_to {
      @Test
      public void arg_of_base_type() {
        var code = """
            A myIdentity(A a) = a;
            myValue = myIdentity("abc");
            """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myValue", schemaS(stringTS()));
      }

      @Test
      public void array() {
        var code = """
            A myIdentity(A a) = a;
            myValue = myIdentity(["abc"]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myValue", schemaS(arrayTS(stringTS())));
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
            .containsRefableWithType("myValue", schemaS(funcTS(stringTS(), list(blobTS()))));
      }
    }

    @Nested
    class _first_elem_func_applied_to {
      @Test
      public void array() {
        var code = """
            @Native("impl.met")
            A firstElement([A] array);
            myValue = firstElement(["abc"]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myValue", schemaS(stringTS()));
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
            .containsRefableWithType("myValue", schemaS(arrayTS(stringTS())));
      }
    }

    @Nested
    class _single_elem_array_func_applied_to {
      @Test
      public void arg_of_base_type() {
        var code = """
            [A] singleElement(A a) = [a];
            myValue = singleElement("abc");
            """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myValue", schemaS(arrayTS(stringTS())));
      }

      @Test
      public void array() {
        var code = """
            [A] singleElement(A a) = [a];
            myValue = singleElement(["abc"]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsRefableWithType("myValue", schemaS(arrayTS(arrayTS(stringTS()))));
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
            .containsRefableWithType("myValue",
                schemaS(arrayTS(funcTS(stringTS(), list(blobTS())))));
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
          .containsRefableWithType("result", arrayTS(stringTS()));
    }
  }

  /**
   * Tests verifying that inferring error doesn't cause NPE when inferring type of expression
   * that uses element with inferring error.
   */
  @Nested
  class _regression {
    @Test
    public void select_with_selectable_with_infer_type_error() {
      var code = """
          @Native("impl")
          A firstElem([A] array);
          valueWithNoninferableType = firstElem(7);
          Int myValue = valueWithNoninferableType.field;
          """;
      module(code)
          .loadsWithProblems();
    }

    @Test
    public void order_with_element_with_infer_type_error() {
      var code = """
          @Native("impl")
          A firstElem([A] array);
          valueWithNoninferableType = firstElem(7);
          [Int] myValue = [valueWithNoninferableType];
          """;
      module(code)
          .loadsWithProblems();
    }

    @Test
    public void call_with_callee_with_infer_type_error() {
      var code = """
          @Native("impl")
          A firstElem([A] array);
          valueWithNoninferableType = firstElem(7);
          Int myValue = valueWithNoninferableType(7);
          """;
      module(code)
          .loadsWithProblems();
    }

    @Test
    public void monofunc_call_with_illegal_params() {
      var code = """
          @Native("impl")
          Int myFunc(String string);
          Int myValue = myFunc(7);
          """;
      module(code)
          .loadsWithProblems();
    }

    @Test
    public void polyfunc_call_with_illegal_params() {
      var code = """
          @Native("impl")
          A myId(A a, String string);
          Int myValue = myId(7, 7);
          """;
      module(code)
          .loadsWithProblems();
    }
  }
}
