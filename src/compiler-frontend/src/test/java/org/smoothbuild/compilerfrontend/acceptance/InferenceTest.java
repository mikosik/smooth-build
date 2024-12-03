package org.smoothbuild.compilerfrontend.acceptance;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.NList.nlist;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.lang.define.SInstantiate;
import org.smoothbuild.compilerfrontend.lang.define.SNamedExprValue;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.testing.FrontendCompileTester;

public class InferenceTest extends FrontendCompileTester {
  @Nested
  class _infer_named_value_type {
    @Nested
    class _mono_type {
      @Test
      void string_literal() {
        var code = """
          myValue = "abc";
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", sSchema(sStringType()));
      }

      @Test
      void blob_literal() {
        var code = """
          myValue = 0x07;
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", sSchema(sBlobType()));
      }

      @Test
      void int_literal() {
        var code = """
          myValue = 123;
          """;
        module(code).loadsWithSuccess().containsEvaluableWithSchema("myValue", sSchema(sIntType()));
      }

      @Test
      void array_literal() {
        var code = """
          myValue = ["abc"];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", sSchema(sArrayType(sStringType())));
      }

      @Test
      void mono_value_ref() {
        var code =
            """
          String stringValue = "abc";
          myValue = stringValue;
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", sSchema(sStringType()));
      }

      @Test
      void mono_func_ref() {
        var code =
            """
          String myFunc(Blob param) = "abc";
          myValue = myFunc;
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", sFuncSchema(sBlobType(), sStringType()));
      }

      @Test
      void mono_func_ref_call() {
        var code =
            """
          String myFunc() = "abc";
          myValue = myFunc();
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", sSchema(sStringType()));
      }

      @Test
      void poly_func_ref_call() {
        var code = """
          A myId(A a) = a;
          myValue = myId(7);
          """;
        module(code).loadsWithSuccess().containsEvaluableWithSchema("myValue", sSchema(sIntType()));
      }
    }

    @Nested
    class _poly_type {
      @Test
      void poly_literal() {
        var code = """
          myValue = [];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", sSchema(sArrayType(varA())));
      }

      @Test
      void poly_value_ref() {
        var code = """
          [A] emptyArray = [];
          myValue = emptyArray;
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", sSchema(sArrayType(varA())));
      }

      @Test
      void poly_func_ref() {
        var code = """
          A myId(A a) = a;
          myValue = myId;
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", sFuncSchema(varA(), varA()));
      }
    }
  }

  @Nested
  class _infer_named_function_result_type extends _abstract_infer_function_result_type_suite {
    @Override
    public void assertInferredFunctionType(
        String declarations, String params, String body, SSchema expected) {
      var code = declarations + "\n" + "myFunc(" + params + ") = " + body + ";";
      module(code).loadsWithSuccess().containsEvaluableWithSchema("myFunc", expected);
    }
  }

  @Nested
  class _infer_lambda_result_type extends _abstract_infer_function_result_type_suite {
    @Override
    public void assertInferredFunctionType(
        String declarations, String params, String body, SSchema expected) {
      var code = declarations + "\n" + "myValue = (" + params + ") -> " + body + ";";
      var myValue = module(code)
          .loadsWithSuccess()
          .getLoadedModule()
          .members()
          .evaluables()
          .get("myValue");
      var myValueBody = ((SNamedExprValue) myValue).body();
      var lambda = ((SInstantiate) myValueBody).sPolymorphic();
      assertThat(lambda.schema()).isEqualTo(expected);
    }
  }

  abstract class _abstract_infer_function_result_type_suite {
    public void assertInferredFunctionType(String params, String body, SSchema expected) {
      assertInferredFunctionType("", params, body, expected);
    }

    public abstract void assertInferredFunctionType(
        String declarations, String params, String body, SSchema expected);

    @Nested
    class _of_mono_function_from {
      @Nested
      class _literal {
        @Test
        void string_literal() {
          assertInferredFunctionType("", "\"abc\"", sFuncSchema(sStringType()));
        }

        @Test
        void blob_literal() {
          assertInferredFunctionType("", "0x07", sFuncSchema(sBlobType()));
        }

        @Test
        void int_literal() {
          assertInferredFunctionType("", "123", sFuncSchema(sIntType()));
        }
      }

      @Nested
      class _array {
        @Test
        void mono_array() {
          assertInferredFunctionType("", "[17]", sFuncSchema(sArrayType(sIntType())));
        }
      }

      @Test
      void value_ref() {
        assertInferredFunctionType("Int intValue = 7;", "", "intValue", sFuncSchema(sIntType()));
      }

      @Test
      void ref() {
        assertInferredFunctionType("Int int", "int", sFuncSchema(sIntType(), sIntType()));
      }

      @Test
      void param_function_call() {
        assertInferredFunctionType(
            "()->Int f", "f()", sFuncSchema(sFuncType(sIntType()), sIntType()));
      }

      @Test
      void mono_function_ref() {
        assertInferredFunctionType(
            "Int otherFunc(Blob param) = 7;",
            "",
            "otherFunc",
            sFuncSchema(sFuncType(sBlobType(), sIntType())));
      }

      @Test
      void mono_function_call() {
        assertInferredFunctionType(
            "Int otherFunc() = 7;", "", "otherFunc()", sFuncSchema(sIntType()));
      }

      @Test
      void poly_function_call() {
        assertInferredFunctionType("A myId(A a) = a;", "", "myId(7)", sFuncSchema(sIntType()));
      }

      @Test
      void function_mono_param() {
        assertInferredFunctionType("Int param", "param", sFuncSchema(sIntType(), sIntType()));
      }
    }

    @Nested
    class _of_poly_func_from {
      @Nested
      class _literal {
        @Test
        void string_literal() {
          assertInferredFunctionType("A a", "\"abc\"", sFuncSchema(varA(), sStringType()));
        }

        @Test
        void blob_literal() {
          assertInferredFunctionType("A a", "0x07", sFuncSchema(varA(), sBlobType()));
        }

        @Test
        void int_literal() {
          assertInferredFunctionType("A a", "7", sFuncSchema(varA(), sIntType()));
        }
      }

      @Nested
      class _array {
        @Test
        void mono_array() {
          assertInferredFunctionType("A a", "[7]", sFuncSchema(varA(), sArrayType(sIntType())));
        }

        @Test
        void poly_array() {
          assertInferredFunctionType("", "[]", sFuncSchema(sArrayType(varA())));
        }

        @Test
        void poly_array_when_param_list_already_uses_A_as_var_name() {
          assertInferredFunctionType("A a", "[]", sFuncSchema(varA(), sArrayType(varB())));
        }
      }

      @Nested
      class _value {
        @Test
        void mono_value_ref() {
          assertInferredFunctionType("Int intValue = 7;", "", "intValue", sFuncSchema(sIntType()));
        }

        @Test
        void poly_value_ref() {
          assertInferredFunctionType(
              "[A] emptyArray = [];", "", "emptyArray", sFuncSchema(sArrayType(varA())));
        }

        @Test
        void poly_value_ref_when_param_list_already_uses_A_as_var_name() {
          assertInferredFunctionType(
              "[A] emptyArray = [];", "A a", "emptyArray", sFuncSchema(varA(), sArrayType(varB())));
        }
      }

      @Nested
      class _param_ref {
        @Test
        void ref_with_base_type() {
          assertInferredFunctionType("Int int", "int", sFuncSchema(sIntType(), sIntType()));
        }

        @Test
        void ref_with_poly_type() {
          assertInferredFunctionType("A param", "param", sFuncSchema(varA(), varA()));
        }

        @Test
        void ref_with_mono_function_type() {
          var funcT = sFuncType(sBoolType(), sIntType());
          assertInferredFunctionType("(Bool)->Int func", "func", sFuncSchema(funcT, funcT));
        }

        @Test
        void ref_with_poly_function_type() {
          assertInferredFunctionType(
              "(A)->A param",
              "param",
              sFuncSchema(sFuncType(varA(), varA()), sFuncType(varA(), varA())));
        }
      }

      @Nested
      class _call {
        @Test
        void call_to_mono_param() {
          assertInferredFunctionType(
              "()->Int f", "f()", sFuncSchema(sFuncType(sIntType()), sIntType()));
        }

        @Test
        void call_to_poly_param() {
          assertInferredFunctionType("()->A f", "f()", sFuncSchema(sFuncType(varA()), varA()));
        }

        @Test
        void call_to_mono_function() {
          assertInferredFunctionType(
              "Int otherFunc() = 7;", "", "otherFunc()", sFuncSchema(sIntType()));
        }

        @Test
        void call_to_poly_function() {
          assertInferredFunctionType(
              "A myIdentity(A a) = a;", "", "myIdentity(7)", sFuncSchema(sIntType()));
        }

        @Test
        void call_to_poly_function_when_argument_type_is_our_type_param() {
          assertInferredFunctionType(
              "A myIdentity(A a) = a;", "B b", "myIdentity(b)", sFuncSchema(varB(), varB()));
        }
      }

      @Nested
      class _func_ref {
        @Test
        void mono_function_ref() {
          assertInferredFunctionType(
              "Int otherFunc(Blob param) = 7;",
              "",
              "otherFunc",
              sFuncSchema(sFuncType(sBlobType(), sIntType())));
        }

        @Test
        void poly_function_ref() {
          assertInferredFunctionType(
              "A myId(A a) = a;", "", "myId", sFuncSchema(sFuncType(varA(), varA())));
        }

        @Test
        void poly_function_ref_when_function_type_param_shadows_referenced_function_res_type() {
          assertInferredFunctionType(
              "A myId(A a) = a;", "A a", "myId", sFuncSchema(varA(), sFuncType(varB(), varB())));
        }
      }
    }
  }

  @Nested
  class _infer_order_type {
    @Nested
    class _when_order_has_zero_elements {
      @Test
      void zero_elements_order() {
        var code = """
          result = [];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("result", sSchema(sArrayType(varA())));
      }
    }

    @Nested
    class _when_order_has_one_elem {
      @Test
      void with_string_type() {
        var code = """
          result = ["abc"];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("result", sSchema(sArrayType(sStringType())));
      }

      @Test
      void with_int_type() {
        var code = """
          result = [7];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("result", sSchema(sArrayType(sIntType())));
      }

      @Test
      void with_blob_type() {
        var code = """
          result = [0xAB];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("result", sSchema(sArrayType(sBlobType())));
      }

      @Test
      void with_array_type() {
        var code = """
          result = [[7]];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("result", sSchema(sArrayType(sArrayType(sIntType()))));
      }

      @Test
      void with_value_ref() {
        var code = """
          Int myValue = 7;
          result = [myValue];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("result", sSchema(sArrayType(sIntType())));
      }

      @Test
      void with_mono_function_ref() {
        var code =
            """
          Int myIntId(Int i) = i;
          result = [myIntId];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema(
                "result", sSchema(sArrayType(sFuncType(sIntType(), sIntType()))));
      }

      @Test
      void with_poly_function_ref() {
        var code = """
          A myId(A a) = a;
          result = [myId];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("result", sSchema(sArrayType(sFuncType(varA(), varA()))));
      }
    }

    @Nested
    class _when_order_has_two_elements {
      @Test
      void with_same_base_type() {
        var code = """
          result = ["abc", "def"];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("result", sSchema(sArrayType(sStringType())));
      }

      @Test
      void with_different_base_types_fails() {
        var code =
            """
          result = [
            "abc",
            0x01,
          ];
          """;
        module(code)
            .loadsWithError(
                1, "Cannot infer type for array literal. Its element types are not compatible.");
      }

      @Test
      void with_same_mono_function_types() {
        var code =
            """
          Int firstFunc() = 7;
          Int secondFunc() = 3;
          result = [
            firstFunc,
            secondFunc,
          ];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("result", sSchema(sArrayType(sFuncType(sIntType()))));
      }

      @Test
      void with_different_mono_function_types_fails() {
        var code =
            """
          String firstFunc() = "abc";
          Blob secondFunc() = 0x01;
          result = [
            firstFunc,
            secondFunc,
          ];
          """;
        module(code)
            .loadsWithError(
                3, "Cannot infer type for array literal. Its element types are not compatible.");
      }

      @Test
      void with_same_poly_function_types() {
        var code =
            """
          A myId(A a) = a;
          B myOtherId(B b) = b;
          result = [myId, myOtherId];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema(
                "result", sSchema(sArrayType(idSFunc().schema().type())));
      }

      @Test
      void with_different_poly_function_types_fails() {
        var code =
            """
          A myId(A a) = a;
          B otherFunc(B b, C c) = b;
          result = [myId, otherFunc];
          """;
        module(code)
            .loadsWithError(
                3, "Cannot infer type for array literal. Its element types are not compatible.");
      }

      @Test
      void one_with_mono_type_one_with_poly_type_convertible_to_mono_one() {
        var code =
            """
          Int myIntId(Int i) = i;
          A myId(A a) = a;
          result = [myIntId, myId];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema(
                "result", sSchema(sArrayType(sFuncType(sIntType(), sIntType()))));
      }

      @Test
      void one_with_mono_type_one_with_poly_type_not_convertible_to_mono_one_fails() {
        var code =
            """
          Int myIntId(Int i) = i;
          A myId(A a, A b) = a;
          result = [myIntId, myId];
          """;
        module(code)
            .loadsWithError(
                3, "Cannot infer type for array literal. Its element types are not compatible.");
      }
    }
  }

  @Nested
  class _infer_instantiation_type_arguments {
    @Nested
    class _fails_when_var_unifies_two_incompatible_types {
      @Test
      void base_types() {
        var code =
            """
            String myEqual(A p1, A p2) = "true";
            result = myEqual("def", 0x01);
            """;
        module(code).loadsWithError(2, "Illegal call.");
      }

      @Test
      void base_type_and_array_of_that_base_type() {
        var code =
            """
            String myEqual(A p1, A p2) = "true";
            result = myEqual(7, [7]);
            """;
        module(code).loadsWithError(2, "Illegal call.");
      }

      @Test
      void arrays() {
        var code =
            """
            String myEqual(A p1, A p2) = "true";
            result = myEqual(["def"], [0x01]);
            """;
        module(code).loadsWithError(2, "Illegal call.");
      }

      @Test
      void structs_with_the_same_object_db_representation() {
        var code =
            """
            MyStruct1{
              String x,
              String y,
            }
            MyStruct2{
              String a,
              String b,
            }
            A myEqual(A a1, A a2) = a1;
            result = myEqual(MyStruct1("aaa", "bbb"), MyStruct2("aaa", "bbb"));
            """;
        module(code).loadsWithError(10, "Illegal call.");
      }
    }

    @Nested
    class _identity_function_applied_to {
      @Test
      void arg_of_base_type() {
        var code =
            """
            A myIdentity(A a) = a;
            myValue = myIdentity("abc");
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", sSchema(sStringType()));
      }

      @Test
      void array() {
        var code =
            """
            A myIdentity(A a) = a;
            myValue = myIdentity(["abc"]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", sSchema(sArrayType(sStringType())));
      }

      @Test
      void func() {
        var code =
            """
            A myIdentity(A a) = a;
            String myFunc(Blob param) = "abc";
            myValue = myIdentity(myFunc);
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", sFuncSchema(sBlobType(), sStringType()));
      }
    }

    @Nested
    class _first_elem_function_applied_to {
      @Test
      void array() {
        var code =
            """
            @Native("impl.met")
            A firstElement([A] array);
            myValue = firstElement(["abc"]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", sSchema(sStringType()));
      }

      @Test
      void array2() {
        var code =
            """
            @Native("impl.met")
            A firstElement([A] array);
            myValue = firstElement([["abc"]]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", sSchema(sArrayType(sStringType())));
      }
    }

    @Nested
    class _single_elem_array_function_applied_to {
      @Test
      void arg_of_base_type() {
        var code =
            """
            [A] singleElement(A a) = [a];
            myValue = singleElement("abc");
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", sSchema(sArrayType(sStringType())));
      }

      @Test
      void array() {
        var code =
            """
            [A] singleElement(A a) = [a];
            myValue = singleElement(["abc"]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", sSchema(sArrayType(sArrayType(sStringType()))));
      }

      @Test
      void function() {
        var code =
            """
            [A] singleElement(A a) = [a];
            String myFunc(Blob param) = "abc";
            myValue = singleElement(myFunc);
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema(
                "myValue", sSchema(sArrayType(sFuncType(sBlobType(), sStringType()))));
      }
    }

    @Nested
    class _from_default_arg {
      @Test
      void generic_param_with_default_value_with_concrete_type() {
        var code =
            """
              A myFunc(A a = 7) = a;
              myValue = myFunc();
              """;
        module(code).loadsWithSuccess().containsEvaluableWithSchema("myValue", sSchema(sIntType()));
      }

      @Test
      void generic_param_with_default_value_with_polymorphic_type() {
        var code =
            """
              A myId(A a) = a;
              (B)->A myFunc(A a, (B)->A f = myId) = f;
              myValue = myFunc(7);
              """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", sFuncSchema(sIntType(), sIntType()));
      }

      @Test
      void generic_param_with_default_value_with_concrete_type_error_case() {
        var code =
            """
              A myFunc(A a, A other = 7) = a;
              myValue = myFunc("abc");
              """;
        module(code).loadsWithError(2, "Illegal call.");
      }

      @Test
      void
          two_differently_instantitaed_calls_to_poly_function_with_poly_default_value_within_one_expr() {
        var code =
            """
          [A] empty([A] array = []) = array;
          myFunc([String] s, [Int] i) = 7;
          myValue = myFunc(empty(), empty());
          """;
        module(code).loadsWithSuccess();
      }

      @Test
      void two_param_default_values_with_different_vars_referencing_same_poly_function() {
        var code =
            """
          A myId(A a) = a;
          myFunc(A a, B b, (A)->A f1 = myId, (B)->B f2 = myId) = 0x33;
          myValue = myFunc(7, "abc");
          """;
        module(code).loadsWithSuccess();
      }
    }

    @Test
    void converter_applier() {
      var code =
          """
          B converterApplier(A item, (A)->B convert) = convert(item);
          [C] single(C elem) = [elem];
          result = converterApplier("abc", single);
          """;
      module(code)
          .loadsWithSuccess()
          .containsEvaluableWithSchema("result", sSchema(sArrayType(sStringType())));
    }
  }

  @Nested
  class _infer_unit_type {
    @Test
    void expression_function() {
      var code =
          """
              Int myFunc(A a) = 7;
              result = myFunc([]);
              """;
      var myFunc = sFunc(1, "myFunc", nlist(sItem(1, varA(), "a")), sInt(1, 7));
      var emptyArray = sOrder(2, sTupleType());
      var call = sCall(2, sInstantiate(2, list(sArrayType(sTupleType())), myFunc), emptyArray);
      module(code).loadsWithSuccess().containsEvaluable(sValue(2, "result", call));
    }

    @Test
    void lambda() {
      var code = """
              result = ((A a) -> 7)([]);
              """;
      var lambda = sLambda(1, nlist(sItem(1, varA(), "a")), sInt(1, 7));
      var emptyArray = sOrder(1, sTupleType());
      var call = sCall(1, sInstantiate(1, list(sArrayType(sTupleType())), lambda), emptyArray);
      module(code).loadsWithSuccess().containsEvaluable(sValue(1, "result", call));
    }
  }

  /**
   * Tests verifying that inferring error doesn't cause NPE when inferring type of expression
   * that uses element with inferring error.
   */
  @Nested
  class _regression {
    @Test
    void select_with_selectable_with_infer_type_error() {
      var code =
          """
          @Native("impl")
          A firstElem([A] array);
          valueWithNoninferableType = firstElem(7);
          Int myValue = valueWithNoninferableType.field;
          """;
      module(code).loadsWithProblems();
    }

    @Test
    void order_with_element_with_infer_type_error() {
      var code =
          """
          @Native("impl")
          A firstElem([A] array);
          valueWithNonInferableType = firstElem(7);
          [Int] myValue = [valueWithNonInferableType];
          """;
      module(code).loadsWithProblems();
    }

    @Test
    void call_with_callee_with_infer_type_error() {
      var code =
          """
          @Native("impl")
          A firstElem([A] array);
          valueWithNonInferableType = firstElem(7);
          Int myValue = valueWithNonInferableType(7);
          """;
      module(code).loadsWithProblems();
    }

    @Test
    void mono_func_call_with_illegal_params() {
      var code =
          """
          @Native("impl")
          Int myFunc(String string);
          Int myValue = myFunc(7);
          """;
      module(code).loadsWithProblems();
    }

    @Test
    void poly_function_call_with_illegal_params() {
      var code =
          """
          @Native("impl")
          A myId(A a, String string);
          Int myValue = myId(7, 7);
          """;
      module(code).loadsWithProblems();
    }
  }
}
