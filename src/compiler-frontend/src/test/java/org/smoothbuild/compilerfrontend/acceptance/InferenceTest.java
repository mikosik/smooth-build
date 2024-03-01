package org.smoothbuild.compilerfrontend.acceptance;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.testing.TestFrontendCompiler.module;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.lang.define.InstantiateS;
import org.smoothbuild.compilerfrontend.lang.define.NamedExprValueS;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;
import org.smoothbuild.compilerfrontend.testing.TestExpressionS;

public class InferenceTest extends TestExpressionS {
  @Nested
  class _infer_named_value_type {
    @Nested
    class _mono_type {
      @Test
      public void string_literal() {
        var code = """
          myValue = "abc";
          """;
        module(code).loadsWithSuccess().containsEvaluableWithSchema("myValue", schemaS(stringTS()));
      }

      @Test
      public void blob_literal() {
        var code = """
          myValue = 0x07;
          """;
        module(code).loadsWithSuccess().containsEvaluableWithSchema("myValue", schemaS(blobTS()));
      }

      @Test
      public void int_literal() {
        var code = """
          myValue = 123;
          """;
        module(code).loadsWithSuccess().containsEvaluableWithSchema("myValue", schemaS(intTS()));
      }

      @Test
      public void array_literal() {
        var code = """
          myValue = ["abc"];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", schemaS(arrayTS(stringTS())));
      }

      @Test
      public void mono_value_ref() {
        var code =
            """
          String stringValue = "abc";
          myValue = stringValue;
          """;
        module(code).loadsWithSuccess().containsEvaluableWithSchema("myValue", schemaS(stringTS()));
      }

      @Test
      public void mono_func_ref() {
        var code =
            """
          String myFunc(Blob param) = "abc";
          myValue = myFunc;
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", funcSchemaS(blobTS(), stringTS()));
      }

      @Test
      public void mono_func_ref_call() {
        var code =
            """
          String myFunc() = "abc";
          myValue = myFunc();
          """;
        module(code).loadsWithSuccess().containsEvaluableWithSchema("myValue", schemaS(stringTS()));
      }

      @Test
      public void poly_func_ref_call() {
        var code = """
          A myId(A a) = a;
          myValue = myId(7);
          """;
        module(code).loadsWithSuccess().containsEvaluableWithSchema("myValue", schemaS(intTS()));
      }
    }

    @Nested
    class _poly_type {
      @Test
      public void poly_literal() {
        var code = """
          myValue = [];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", schemaS(arrayTS(varA())));
      }

      @Test
      public void poly_value_ref() {
        var code = """
          [A] emptyArray = [];
          myValue = emptyArray;
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", schemaS(arrayTS(varA())));
      }

      @Test
      public void poly_func_ref() {
        var code = """
          A myId(A a) = a;
          myValue = myId;
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", funcSchemaS(varA(), varA()));
      }
    }
  }

  @Nested
  class _infer_named_function_result_type extends _abstract_infer_function_result_type_suite {
    @Override
    public void assertInferredFunctionType(
        String declarations, String params, String body, SchemaS expected) {
      var code = declarations + "\n" + "myFunc(" + params + ") = " + body + ";";
      module(code).loadsWithSuccess().containsEvaluableWithSchema("myFunc", expected);
    }
  }

  @Nested
  class _infer_lambda_result_type extends _abstract_infer_function_result_type_suite {
    @Override
    public void assertInferredFunctionType(
        String declarations, String params, String body, SchemaS expected) {
      var code = declarations + "\n" + "myValue = (" + params + ") -> " + body + ";";
      var myValue =
          module(code).loadsWithSuccess().getLoadedDefinitions().evaluables().get("myValue");
      var myValueBody = ((NamedExprValueS) myValue).body();
      var lambda = ((InstantiateS) myValueBody).polymorphicS();
      assertThat(lambda.schema()).isEqualTo(expected);
    }
  }

  abstract static class _abstract_infer_function_result_type_suite {
    public void assertInferredFunctionType(String params, String body, SchemaS expected) {
      assertInferredFunctionType("", params, body, expected);
    }

    public abstract void assertInferredFunctionType(
        String declarations, String params, String body, SchemaS expected);

    @Nested
    class _of_mono_function_from {
      @Nested
      class _literal {
        @Test
        public void string_literal() {
          assertInferredFunctionType("", "\"abc\"", funcSchemaS(stringTS()));
        }

        @Test
        public void blob_literal() {
          assertInferredFunctionType("", "0x07", funcSchemaS(blobTS()));
        }

        @Test
        public void int_literal() {
          assertInferredFunctionType("", "123", funcSchemaS(intTS()));
        }
      }

      @Nested
      class _array {
        @Test
        public void mono_array() {
          assertInferredFunctionType("", "[17]", funcSchemaS(arrayTS(intTS())));
        }
      }

      @Test
      public void value_ref() {
        assertInferredFunctionType("Int intValue = 7;", "", "intValue", funcSchemaS(intTS()));
      }

      @Test
      public void ref() {
        assertInferredFunctionType("Int int", "int", funcSchemaS(intTS(), intTS()));
      }

      @Test
      public void param_function_call() {
        assertInferredFunctionType("()->Int f", "f()", funcSchemaS(funcTS(intTS()), intTS()));
      }

      @Test
      public void mono_function_ref() {
        assertInferredFunctionType(
            "Int otherFunc(Blob param) = 7;",
            "",
            "otherFunc",
            funcSchemaS(funcTS(blobTS(), intTS())));
      }

      @Test
      public void mono_function_call() {
        assertInferredFunctionType("Int otherFunc() = 7;", "", "otherFunc()", funcSchemaS(intTS()));
      }

      @Test
      public void poly_function_call() {
        assertInferredFunctionType("A myId(A a) = a;", "", "myId(7)", funcSchemaS(intTS()));
      }

      @Test
      public void function_mono_param() {
        assertInferredFunctionType("Int param", "param", funcSchemaS(intTS(), intTS()));
      }
    }

    @Nested
    class _of_poly_func_from {
      @Nested
      class _literal {
        @Test
        public void string_literal() {
          assertInferredFunctionType("A a", "\"abc\"", funcSchemaS(varA(), stringTS()));
        }

        @Test
        public void blob_literal() {
          assertInferredFunctionType("A a", "0x07", funcSchemaS(varA(), blobTS()));
        }

        @Test
        public void int_literal() {
          assertInferredFunctionType("A a", "7", funcSchemaS(varA(), intTS()));
        }
      }

      @Nested
      class _array {
        @Test
        public void mono_array() {
          assertInferredFunctionType("A a", "[7]", funcSchemaS(varA(), arrayTS(intTS())));
        }

        @Test
        public void poly_array() {
          assertInferredFunctionType("", "[]", funcSchemaS(arrayTS(varA())));
        }

        @Test
        public void poly_array_when_param_list_already_uses_A_as_var_name() {
          assertInferredFunctionType("A a", "[]", funcSchemaS(varA(), arrayTS(varB())));
        }
      }

      @Nested
      class _value {
        @Test
        public void mono_value_ref() {
          assertInferredFunctionType("Int intValue = 7;", "", "intValue", funcSchemaS(intTS()));
        }

        @Test
        public void poly_value_ref() {
          assertInferredFunctionType(
              "[A] emptyArray = [];", "", "emptyArray", funcSchemaS(arrayTS(varA())));
        }

        @Test
        public void poly_value_ref_when_param_list_already_uses_A_as_var_name() {
          assertInferredFunctionType(
              "[A] emptyArray = [];", "A a", "emptyArray", funcSchemaS(varA(), arrayTS(varB())));
        }
      }

      @Nested
      class _param_ref {
        @Test
        public void ref_with_base_type() {
          assertInferredFunctionType("Int int", "int", funcSchemaS(intTS(), intTS()));
        }

        @Test
        public void ref_with_poly_type() {
          assertInferredFunctionType("A param", "param", funcSchemaS(varA(), varA()));
        }

        @Test
        public void ref_with_mono_function_type() {
          var funcT = funcTS(boolTS(), intTS());
          assertInferredFunctionType("(Bool)->Int func", "func", funcSchemaS(funcT, funcT));
        }

        @Test
        public void ref_with_poly_function_type() {
          assertInferredFunctionType(
              "(A)->A param", "param", funcSchemaS(funcTS(varA(), varA()), funcTS(varA(), varA())));
        }
      }

      @Nested
      class _call {
        @Test
        public void call_to_mono_param() {
          assertInferredFunctionType("()->Int f", "f()", funcSchemaS(funcTS(intTS()), intTS()));
        }

        @Test
        public void call_to_poly_param() {
          assertInferredFunctionType("()->A f", "f()", funcSchemaS(funcTS(varA()), varA()));
        }

        @Test
        public void call_to_mono_function() {
          assertInferredFunctionType(
              "Int otherFunc() = 7;", "", "otherFunc()", funcSchemaS(intTS()));
        }

        @Test
        public void call_to_poly_function() {
          assertInferredFunctionType(
              "A myIdentity(A a) = a;", "", "myIdentity(7)", funcSchemaS(intTS()));
        }

        @Test
        public void call_to_poly_function_when_argument_type_is_our_type_param() {
          assertInferredFunctionType(
              "A myIdentity(A a) = a;", "B b", "myIdentity(b)", funcSchemaS(varB(), varB()));
        }
      }

      @Nested
      class _func_ref {
        @Test
        public void mono_function_ref() {
          assertInferredFunctionType(
              "Int otherFunc(Blob param) = 7;",
              "",
              "otherFunc",
              funcSchemaS(funcTS(blobTS(), intTS())));
        }

        @Test
        public void poly_function_ref() {
          assertInferredFunctionType(
              "A myId(A a) = a;", "", "myId", funcSchemaS(funcTS(varA(), varA())));
        }

        @Test
        public void
            poly_function_ref_when_function_type_param_shadows_referenced_function_res_type() {
          assertInferredFunctionType(
              "A myId(A a) = a;", "A a", "myId", funcSchemaS(varA(), funcTS(varB(), varB())));
        }
      }
    }
  }

  @Nested
  class _infer_order_type {
    @Nested
    class _when_order_has_zero_elements {
      @Test
      public void zero_elements_order() {
        var code = """
          result = [];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("result", schemaS(arrayTS(varA())));
      }
    }

    @Nested
    class _when_order_has_one_elem {
      @Test
      public void with_string_type() {
        var code = """
          result = ["abc"];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("result", schemaS(arrayTS(stringTS())));
      }

      @Test
      public void with_int_type() {
        var code = """
          result = [7];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("result", schemaS(arrayTS(intTS())));
      }

      @Test
      public void with_blob_type() {
        var code = """
          result = [0xAB];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("result", schemaS(arrayTS(blobTS())));
      }

      @Test
      public void with_array_type() {
        var code = """
          result = [[7]];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("result", schemaS(arrayTS(arrayTS(intTS()))));
      }

      @Test
      public void with_value_ref() {
        var code = """
          Int myValue = 7;
          result = [myValue];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("result", schemaS(arrayTS(intTS())));
      }

      @Test
      public void with_mono_function_ref() {
        var code =
            """
          Int myIntId(Int i) = i;
          result = [myIntId];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("result", schemaS(arrayTS(funcTS(intTS(), intTS()))));
      }

      @Test
      public void with_poly_function_ref() {
        var code = """
          A myId(A a) = a;
          result = [myId];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("result", schemaS(arrayTS(funcTS(varA(), varA()))));
      }
    }

    @Nested
    class _when_order_has_two_elements {
      @Test
      public void with_same_base_type() {
        var code = """
          result = ["abc", "def"];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("result", schemaS(arrayTS(stringTS())));
      }

      @Test
      public void with_different_base_types_fails() {
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
      public void with_same_mono_function_types() {
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
            .containsEvaluableWithSchema("result", schemaS(arrayTS(funcTS(intTS()))));
      }

      @Test
      public void with_different_mono_function_types_fails() {
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
      public void with_same_poly_function_types() {
        var code =
            """
          A myId(A a) = a;
          B myOtherId(B b) = b;
          result = [myId, myOtherId];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema(
                "result", schemaS(arrayTS(idFuncS().schema().type())));
      }

      @Test
      public void with_different_poly_function_types_fails() {
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
      public void one_with_mono_type_one_with_poly_type_convertible_to_mono_one() {
        var code =
            """
          Int myIntId(Int i) = i;
          A myId(A a) = a;
          result = [myIntId, myId];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("result", schemaS(arrayTS(funcTS(intTS(), intTS()))));
      }

      @Test
      public void one_with_mono_type_one_with_poly_type_not_convertible_to_mono_one_fails() {
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
      public void base_types() {
        var code =
            """
            String myEqual(A p1, A p2) = "true";
            result = myEqual("def", 0x01);
            """;
        module(code).loadsWithError(2, "Illegal call.");
      }

      @Test
      public void base_type_and_array_of_that_base_type() {
        var code =
            """
            String myEqual(A p1, A p2) = "true";
            result = myEqual(7, [7]);
            """;
        module(code).loadsWithError(2, "Illegal call.");
      }

      @Test
      public void arrays() {
        var code =
            """
            String myEqual(A p1, A p2) = "true";
            result = myEqual(["def"], [0x01]);
            """;
        module(code).loadsWithError(2, "Illegal call.");
      }

      @Test
      public void structs_with_the_same_object_db_representation() {
        var code =
            """
            MyStruct1(
              String x,
              String y,
            )
            MyStruct2(
              String a,
              String b,
            )
            A myEqual(A a1, A a2) = a1;
            result = myEqual(MyStruct1("aaa", "bbb"), MyStruct2("aaa", "bbb"));
            """;
        module(code).loadsWithError(10, "Illegal call.");
      }
    }

    @Nested
    class _identity_function_applied_to {
      @Test
      public void arg_of_base_type() {
        var code =
            """
            A myIdentity(A a) = a;
            myValue = myIdentity("abc");
            """;
        module(code).loadsWithSuccess().containsEvaluableWithSchema("myValue", schemaS(stringTS()));
      }

      @Test
      public void array() {
        var code =
            """
            A myIdentity(A a) = a;
            myValue = myIdentity(["abc"]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", schemaS(arrayTS(stringTS())));
      }

      @Test
      public void func() {
        var code =
            """
            A myIdentity(A a) = a;
            String myFunc(Blob param) = "abc";
            myValue = myIdentity(myFunc);
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", funcSchemaS(blobTS(), stringTS()));
      }
    }

    @Nested
    class _first_elem_function_applied_to {
      @Test
      public void array() {
        var code =
            """
            @Native("impl.met")
            A firstElement([A] array);
            myValue = firstElement(["abc"]);
            """;
        module(code).loadsWithSuccess().containsEvaluableWithSchema("myValue", schemaS(stringTS()));
      }

      @Test
      public void array2() {
        var code =
            """
            @Native("impl.met")
            A firstElement([A] array);
            myValue = firstElement([["abc"]]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", schemaS(arrayTS(stringTS())));
      }
    }

    @Nested
    class _single_elem_array_function_applied_to {
      @Test
      public void arg_of_base_type() {
        var code =
            """
            [A] singleElement(A a) = [a];
            myValue = singleElement("abc");
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", schemaS(arrayTS(stringTS())));
      }

      @Test
      public void array() {
        var code =
            """
            [A] singleElement(A a) = [a];
            myValue = singleElement(["abc"]);
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", schemaS(arrayTS(arrayTS(stringTS()))));
      }

      @Test
      public void function() {
        var code =
            """
            [A] singleElement(A a) = [a];
            String myFunc(Blob param) = "abc";
            myValue = singleElement(myFunc);
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", schemaS(arrayTS(funcTS(blobTS(), stringTS()))));
      }
    }

    @Nested
    class _from_default_arg {
      @Test
      public void generic_param_with_default_value_with_concrete_type() {
        var code =
            """
              A myFunc(A a = 7) = a;
              myValue = myFunc();
              """;
        module(code).loadsWithSuccess().containsEvaluableWithSchema("myValue", schemaS(intTS()));
      }

      @Test
      public void generic_param_with_default_value_with_polymorphic_type() {
        var code =
            """
              A myId(A a) = a;
              (B)->A myFunc(A a, (B)->A f = myId) = f;
              myValue = myFunc(7);
              """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluableWithSchema("myValue", funcSchemaS(intTS(), intTS()));
      }

      @Test
      public void generic_param_with_default_value_with_concrete_type_error_case() {
        var code =
            """
              A myFunc(A a, A other = 7) = a;
              myValue = myFunc("abc");
              """;
        module(code).loadsWithError(2, "Illegal call.");
      }

      @Test
      public void
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
      public void two_param_default_values_with_different_vars_referencing_same_poly_function() {
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
    public void converter_applier() {
      var code =
          """
          B converterApplier(A item, (A)->B convert) = convert(item);
          [C] single(C elem) = [elem];
          result = converterApplier("abc", single);
          """;
      module(code)
          .loadsWithSuccess()
          .containsEvaluableWithSchema("result", schemaS(arrayTS(stringTS())));
    }
  }

  @Nested
  class _infer_unit_type {
    @Test
    public void expression_function() {
      var code =
          """
              Int myFunc(A a) = 7;
              result = myFunc([]);
              """;
      var myFunc = funcS(1, "myFunc", nlist(itemS(1, varA(), "a")), intS(1, 7));
      var emptyArray = orderS(2, tupleTS());
      var call = callS(2, instantiateS(2, list(arrayTS(tupleTS())), myFunc), emptyArray);
      module(code).loadsWithSuccess().containsEvaluable(valueS(2, "result", call));
    }

    @Test
    public void lambda() {
      var code = """
              result = ((A a) -> 7)([]);
              """;
      var lambda = lambdaS(1, nlist(itemS(1, varA(), "a")), intS(1, 7));
      var emptyArray = orderS(1, tupleTS());
      var call = callS(1, instantiateS(1, list(arrayTS(tupleTS())), lambda), emptyArray);
      module(code).loadsWithSuccess().containsEvaluable(valueS(1, "result", call));
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
    public void order_with_element_with_infer_type_error() {
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
    public void call_with_callee_with_infer_type_error() {
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
    public void mono_func_call_with_illegal_params() {
      var code =
          """
          @Native("impl")
          Int myFunc(String string);
          Int myValue = myFunc(7);
          """;
      module(code).loadsWithProblems();
    }

    @Test
    public void poly_function_call_with_illegal_params() {
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
