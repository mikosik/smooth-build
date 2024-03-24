package org.smoothbuild.compilerfrontend.acceptance;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.lang.type.STypes.INT;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;
import static org.smoothbuild.compilerfrontend.testing.FrontendCompilerTester.module;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.idSFunc;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.intIdSFunc;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.returnIntSFunc;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sArrayType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sBlob;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sBlobType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sBytecodeFunc;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sBytecodeValue;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sCall;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sConstructor;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sFunc;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sInt;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sItem;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sItemPoly;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sLambda;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sOrder;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sParamRef;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sSig;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sString;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sStringType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sStructType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.varA;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.varB;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.lang.define.SCall;
import org.smoothbuild.compilerfrontend.lang.define.SEvaluable;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SNamedExprValue;
import org.smoothbuild.compilerfrontend.testing.TestingSExpression;

public class SExprLoadingTest {
  @Nested
  class _literal {
    @Test
    public void blob_literal() {
      module("""
          result =
            0x07;
          """)
          .loadsWithSuccess()
          .containsEvaluable(TestingSExpression.sValue(1, sBlobType(), "result", sBlob(2, 7)));
    }

    @Test
    public void int_literal() {
      module("""
          result =
            123;
          """)
          .loadsWithSuccess()
          .containsEvaluable(TestingSExpression.sValue(1, sIntType(), "result", sInt(2, 123)));
    }

    @Test
    public void string_literal() {
      module("""
          result =
            "abc";
          """)
          .loadsWithSuccess()
          .containsEvaluable(
              TestingSExpression.sValue(1, sStringType(), "result", sString(2, "abc")));
    }
  }

  @Nested
  class _expr {
    @Nested
    class _lambda {
      @Test
      public void mono_lambda() {
        var code =
            """
            result =
              (Int int)
                -> int;
            """;
        var lambda =
            sLambda(2, nlist(TestingSExpression.sItem(2, INT, "int")), sParamRef(3, INT, "int"));
        var instantiated = TestingSExpression.sInstantiate(2, lambda);
        var result = TestingSExpression.sValue(1, "result", instantiated);
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      public void mono_lambda_using_generic_type_of_enclosing_function() {
        var code =
            """
            (A)->A myFunc(A outerA) =
              (A a)
                -> a;
            """;
        var body = sParamRef(3, varA(), "a");
        var lambda = TestingSExpression.sLambda(
            2, varSetS(), nlist(TestingSExpression.sItem(2, varA(), "a")), body);
        var instantiated = TestingSExpression.sInstantiate(2, lambda);
        var myFunc = TestingSExpression.sFunc(
            1, "myFunc", nlist(TestingSExpression.sItem(1, varA(), "outerA")), instantiated);
        module(code).loadsWithSuccess().containsEvaluable(myFunc);
      }

      @Test
      public void mono_lambda_using_generic_type_of_enclosing_func_two_level_deep() {
        var code =
            """
            () -> (A)->A myFunc(A outerA) =
              ()
                -> (A a)
                  -> a;
            """;
        var deeperBody = sParamRef(4, varA(), "a");
        var deeperLambda = TestingSExpression.sLambda(
            3, varSetS(), nlist(TestingSExpression.sItem(3, varA(), "a")), deeperBody);
        var monoDeeperLambda = TestingSExpression.sInstantiate(3, deeperLambda);
        var anonFunc = TestingSExpression.sLambda(2, varSetS(), nlist(), monoDeeperLambda);
        var monoLambda = TestingSExpression.sInstantiate(2, anonFunc);
        var myFunc = TestingSExpression.sFunc(
            1, "myFunc", nlist(TestingSExpression.sItem(1, varA(), "outerA")), monoLambda);
        module(code).loadsWithSuccess().containsEvaluable(myFunc);
      }

      @Test
      public void poly_lambda() {
        var code =
            """
            result =
              (A a)
                -> a;
            """;
        var lambda =
            sLambda(2, nlist(TestingSExpression.sItem(2, varA(), "a")), sParamRef(3, varA(), "a"));
        var instantiateS = TestingSExpression.sInstantiate(2, list(varA()), lambda);
        var result = TestingSExpression.sValue(1, "result", instantiateS);
        module(code).loadsWithSuccess().containsEvaluable(result);
      }
    }

    @Nested
    class _call {
      @Nested
      class _with_default_arg {
        @Test
        public void with_reference_to_poly_val() {
          var polyVal = sBytecodeValue(4, varA(), "polyVal");
          var instantiateS = TestingSExpression.sInstantiate(1, list(varA()), polyVal);
          var arg = TestingSExpression.sInstantiate(
              2, list(sIntType()), TestingSExpression.sValue(1, "myFunc:b", instantiateS));
          test_default_arg("polyVal", arg);
        }

        @Test
        public void with_reference_to_poly_func() {
          var polyFunc = TestingSExpression.sBytecodeFunc(6, varA(), "polyFunc", nlist());
          var instantiateS = TestingSExpression.sInstantiate(1, list(varA()), polyFunc);
          var paramDefaultValue = TestingSExpression.sValue("myFunc:b", sCall(1, instantiateS));
          var expected = TestingSExpression.sInstantiate(2, list(sIntType()), paramDefaultValue);
          test_default_arg("polyFunc()", expected);
        }

        @Test
        public void with_reference_to_int() {
          var paramDefaultValue = TestingSExpression.sValue("myFunc:b", sInt(1, 7));
          test_default_arg("7", TestingSExpression.sInstantiate(2, paramDefaultValue));
        }

        private void test_default_arg(String bodyCode, SExpr expected) {
          var code =
              """
            B myFunc(B b = $$$) = b;
            Int result = myFunc();
            @Bytecode("impl")
            A polyVal;
            @Bytecode("impl")
            A polyFunc();

            """
                  .replace("$$$", bodyCode);

          SEvaluable result = module(code)
              .loadsWithSuccess()
              .getLoadedModule()
              .members()
              .evaluables()
              .get("result");
          SExpr actualDefArg =
              ((SCall) ((SNamedExprValue) result).body()).args().elems().get(0);
          assertThat(actualDefArg).isEqualTo(expected);
        }
      }

      @Test
      public void with_mono_func_reference() {
        var code =
            """
            Int myReturnInt() = 3;
            result = myReturnInt();
            """;
        var myReturnInt = returnIntSFunc();
        var result = TestingSExpression.sValue(
            2, sIntType(), "result", sCall(2, TestingSExpression.sInstantiate(2, myReturnInt)));
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      public void with_mono_func_reference_and_arg() {
        var code =
            """
            Int myIntId(Int i) = i;
            result = myIntId(3);
            """;
        var myIntId = intIdSFunc();
        var result = TestingSExpression.sValue(
            2,
            sIntType(),
            "result",
            sCall(2, TestingSExpression.sInstantiate(2, myIntId), sInt(2, 3)));
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      public void with_mono_func_reference_and_named_arg() {
        var code =
            """
            Int myIntId(Int i) = i;
            result = myIntId(i=
              7);
            """;
        var myIntId = intIdSFunc();
        var result = TestingSExpression.sValue(
            2,
            sIntType(),
            "result",
            sCall(2, TestingSExpression.sInstantiate(2, myIntId), sInt(3, 7)));
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      public void with_mono_func_reference_and_piped_arg() {
        var code =
            """
            Int myIntId(Int i) = i;
            result = 7
              > myIntId();
            """;
        var myIntId = intIdSFunc();
        var result = TestingSExpression.sValue(
            2,
            sIntType(),
            "result",
            sCall(3, TestingSExpression.sInstantiate(3, myIntId), sInt(2, 7)));
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      public void with_poly_func_reference_and_arg() {
        module("""
            A myId(A a) = a;
            result = myId(7);
            """)
            .loadsWithSuccess()
            .containsEvaluable(TestingSExpression.sValue(
                2,
                sIntType(),
                "result",
                sCall(
                    2,
                    TestingSExpression.sInstantiate(2, list(sIntType()), idSFunc()),
                    sInt(2, 7))));
      }

      @Test
      public void with_value_reference() {
        var code =
            """
            Int myReturnInt() = 3;
            ()->Int myValue = myReturnInt;
            result = myValue();
            """;
        var myReturnInt = returnIntSFunc();
        var myValue = TestingSExpression.sValue(
            2,
            TestingSExpression.sFuncType(sIntType()),
            "myValue",
            TestingSExpression.sInstantiate(2, myReturnInt));
        var result = TestingSExpression.sValue(
            3, sIntType(), "result", sCall(3, TestingSExpression.sInstantiate(3, myValue)));
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      public void with_value_reference_and_arg() {
        var code =
            """
            Int myIntId(Int i) = i;
            (Int)->Int myValue = myIntId;
            result = myValue(
              7);
            """;
        var myIntId = intIdSFunc();
        var myValue = TestingSExpression.sValue(
            2,
            TestingSExpression.sFuncType(sIntType(), sIntType()),
            "myValue",
            TestingSExpression.sInstantiate(2, myIntId));
        var result = TestingSExpression.sValue(
            3,
            sIntType(),
            "result",
            sCall(3, TestingSExpression.sInstantiate(3, myValue), sInt(4, 7)));
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      public void with_ctor_reference() {
        var struct = sStructType("MyStruct", nlist(sSig(sStringType(), "field")));
        var constructor = sConstructor(1, struct, "MyStruct");
        module("""
            MyStruct(
              String field
            )
            """)
            .loadsWithSuccess()
            .containsEvaluable(constructor);
      }

      @Test
      public void with_ctor_reference_and_arg() {
        var code =
            """
            MyStruct(
              String field
            )
            result = MyStruct(
              "aaa");
            """;
        var struct = sStructType("MyStruct", nlist(sSig(sStringType(), "field")));
        var constructor = TestingSExpression.sConstructor(1, struct);
        var resultBody =
            sCall(4, TestingSExpression.sInstantiate(4, constructor), sString(5, "aaa"));
        var result = TestingSExpression.sValue(4, struct, "result", resultBody);
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      public void with_ref() {
        module("""
            result(()->String f) = f();
            """)
            .loadsWithSuccess()
            .containsEvaluable(sFunc(
                1,
                sStringType(),
                "result",
                nlist(
                    TestingSExpression.sItem(1, TestingSExpression.sFuncType(sStringType()), "f")),
                sCall(
                    1,
                    TestingSExpression.sParamRef(
                        TestingSExpression.sFuncType(sStringType()), "f"))));
      }

      @Test
      public void with_ref_and_arg() {
        module("""
            result((Blob)->String f) = f(0x09);
            """)
            .loadsWithSuccess()
            .containsEvaluable(sFunc(
                1,
                sStringType(),
                "result",
                nlist(TestingSExpression.sItem(
                    1, TestingSExpression.sFuncType(sBlobType(), sStringType()), "f")),
                sCall(
                    1,
                    TestingSExpression.sParamRef(
                        TestingSExpression.sFuncType(sBlobType(), sStringType()), "f"),
                    sBlob(1, 9))));
      }
    }

    @Nested
    class _poly_ref {
      @Test
      public void to_mono_value() {
        var code =
            """
            String myValue = "abc";
            String result =
              myValue;
            """;
        var myValue = TestingSExpression.sValue(
            1, sStringType(), "myValue", TestingSExpression.sString("abc"));
        var result = TestingSExpression.sValue(
            2, sStringType(), "result", TestingSExpression.sInstantiate(3, myValue));
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      public void to_poly_value() {
        module(
                """
            [A] myValue = [];
            [Int] result =
              myValue;
            """)
            .loadsWithSuccess()
            .containsEvaluable(TestingSExpression.sValue(
                2,
                sArrayType(sIntType()),
                "result",
                TestingSExpression.sInstantiate(
                    3,
                    list(sIntType()),
                    TestingSExpression.sValue(
                        1, sArrayType(varA()), "myValue", TestingSExpression.sOrder(varA())))));
      }

      @Test
      public void to_mono_func() {
        var code =
            """
            String myFunc() = "abc";
            ()->String result =
              myFunc;
            """;
        var myFunc =
            TestingSExpression.sFunc(1, "myFunc", nlist(), TestingSExpression.sString("abc"));
        var result = TestingSExpression.sValue(
            2,
            TestingSExpression.sFuncType(sStringType()),
            "result",
            TestingSExpression.sInstantiate(3, myFunc));
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      public void to_poly_func() {
        var code =
            """
            A myId(A a) = a;
            (Int)->Int result =
              myId;
            """;
        var myId = TestingSExpression.sFunc(
            1, "myId", nlist(TestingSExpression.sItem(varA(), "a")), sParamRef(1, varA(), "a"));
        var resultBody = TestingSExpression.sInstantiate(3, list(sIntType()), myId);
        var result = TestingSExpression.sValue(
            2, TestingSExpression.sFuncType(sIntType(), sIntType()), "result", resultBody);
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      public void to_constructor() {
        var code =
            """
            MyStruct()
            ()->MyStruct result =
              MyStruct;
            """;
        var structT = sStructType("MyStruct", nlist());
        var constructor = TestingSExpression.sConstructor(1, structT);
        var result = TestingSExpression.sValue(
            2,
            TestingSExpression.sFuncType(structT),
            "result",
            TestingSExpression.sInstantiate(3, constructor));
        module(code).loadsWithSuccess().containsEvaluable(result);
      }
    }

    @Nested
    class _order {
      @Test
      public void order() {
        module(
                """
            result =
            [
              0x07,
              0x08
            ];
            """)
            .loadsWithSuccess()
            .containsEvaluable(TestingSExpression.sValue(
                1,
                sArrayType(sBlobType()),
                "result",
                sOrder(2, sBlobType(), sBlob(3, 7), sBlob(4, 8))));
      }

      @Test
      public void order_with_piped_value() {
        module(
                """
            result = 0x07 >
            [
              0x08
            ];
            """)
            .loadsWithSuccess()
            .containsEvaluable(TestingSExpression.sValue(
                1,
                sArrayType(sBlobType()),
                "result",
                sOrder(2, sBlobType(), sBlob(1, 7), sBlob(3, 8))));
      }

      @Test
      public void order_of_funcs() {
        var code =
            """
          Int returnInt() = 7;
          result =
          [
            returnInt,
          ];
          """;
        var returnInt = TestingSExpression.sFunc(1, "returnInt", nlist(), sInt(1, 7));
        var orderS = TestingSExpression.sOrder(3, TestingSExpression.sInstantiate(4, returnInt));
        var expected = TestingSExpression.sValue(2, "result", orderS);
        module(code).loadsWithSuccess().containsEvaluable(expected);
      }
    }

    @Test
    public void param_ref() {
      var code = """
          Blob myFunc(Blob param1)
            = param1;
          """;
      var body = sParamRef(2, sBlobType(), "param1");
      var myFunc = sFunc(
          1,
          sBlobType(),
          "myFunc",
          nlist(TestingSExpression.sItem(1, sBlobType(), "param1")),
          body);
      module(code).loadsWithSuccess().containsEvaluable(myFunc);
    }

    @Test
    public void select() {
      var code =
          """
          @Native("impl")
          MyStruct getStruct();
          MyStruct(
            String field,
          )
          result = getStruct()
            .field;
          """;
      var myStruct = sStructType("MyStruct", nlist(sSig(sStringType(), "field")));
      var getStruct = TestingSExpression.sAnnotatedFunc(
          2, TestingSExpression.sNativeAnnotation(), myStruct, "getStruct", nlist());
      var resultBody = TestingSExpression.sSelect(
          7, sCall(6, TestingSExpression.sInstantiate(6, getStruct)), "field");
      var result = TestingSExpression.sValue(6, sStringType(), "result", resultBody);
      module(code).loadsWithSuccess().containsEvaluable(result);
    }
  }

  @Nested
  class _definition_of {
    @Nested
    class _value {
      @Test
      public void mono_expression_value() {
        var code = """
          Blob myValue =
            0x07;
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(TestingSExpression.sValue(1, sBlobType(), "myValue", sBlob(2, 7)));
      }

      @Test
      public void poly_expression_value() {
        var code = """
          [A] myValue =
            [];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(
                TestingSExpression.sValue(1, sArrayType(varA()), "myValue", sOrder(2, varA())));
      }

      @Test
      public void mono_bytecode_value() {
        var code = """
          @Bytecode("impl")
          Blob myValue;
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(sBytecodeValue(2, sBlobType(), "myValue"));
      }

      @Test
      public void poly_bytecode_value() {
        var code = """
          @Bytecode("impl")
          A myValue;
          """;
        module(code).loadsWithSuccess().containsEvaluable(sBytecodeValue(2, varA(), "myValue"));
      }
    }

    @Nested
    class _func {
      @Test
      public void mono_expression_function() {
        module("""
            Blob myFunc() =
              0x07;
            """)
            .loadsWithSuccess()
            .containsEvaluable(sFunc(1, sBlobType(), "myFunc", nlist(), sBlob(2, 7)));
      }

      @Test
      public void poly_expression_function() {
        module("""
            [A] myFunc() =
              [];
            """)
            .loadsWithSuccess()
            .containsEvaluable(sFunc(1, sArrayType(varA()), "myFunc", nlist(), sOrder(2, varA())));
      }

      @Test
      public void mono_expression_function_with_param() {
        module(
                """
            String myFunc(
              Blob param1)
              = "abc";
            """)
            .loadsWithSuccess()
            .containsEvaluable(sFunc(
                1,
                sStringType(),
                "myFunc",
                nlist(TestingSExpression.sItem(2, sBlobType(), "param1")),
                sString(3, "abc")));
      }

      @Test
      public void poly_expression_function_with_param() {
        module("""
            A myFunc(
              A a)
              = a;
            """)
            .loadsWithSuccess()
            .containsEvaluable(sFunc(
                1,
                varA(),
                "myFunc",
                nlist(TestingSExpression.sItem(2, varA(), "a")),
                sParamRef(3, varA(), "a")));
      }

      @Test
      public void mono_expression_function_with_param_with_default_value() {
        var code =
            """
            String myFunc(
              Blob param1 =
                0x07)
                = "abc";
            """;
        var params = nlist(sItem(
            2, sBlobType(), "param1", TestingSExpression.sValue(2, "myFunc:param1", sBlob(3, 7))));
        var myFunc = sFunc(1, sStringType(), "myFunc", params, sString(4, "abc"));
        module(code).loadsWithSuccess().containsEvaluable(myFunc);
      }

      @Test
      public void poly_expression_function_with_param_with_default_value() {
        var code =
            """
            A myFunc(
              A a =
                7)
                = a;
            """;
        var params =
            nlist(sItem(2, varA(), "a", TestingSExpression.sValue(2, "myFunc:a", sInt(3, 7))));
        var myFunc = sFunc(1, varA(), "myFunc", params, sParamRef(4, varA(), "a"));
        module(code).loadsWithSuccess().containsEvaluable(myFunc);
      }

      @Test
      public void mono_native_impure_function() {
        module(
                """
            @NativeImpure("Impl.met")
            String myFunc();
            """)
            .loadsWithSuccess()
            .containsEvaluable(TestingSExpression.sAnnotatedFunc(
                2,
                TestingSExpression.sNativeAnnotation(1, sString(1, "Impl.met"), false),
                sStringType(),
                "myFunc",
                nlist()));
      }

      @Test
      public void poly_native_impure_function() {
        module("""
            @NativeImpure("Impl.met")
            A myFunc();
            """)
            .loadsWithSuccess()
            .containsEvaluable(TestingSExpression.sAnnotatedFunc(
                2,
                TestingSExpression.sNativeAnnotation(1, sString(1, "Impl.met"), false),
                varA(),
                "myFunc",
                nlist()));
      }

      @Test
      public void mono_native_pure_function() {
        module("""
            @Native("Impl.met")
            String myFunc();
            """)
            .loadsWithSuccess()
            .containsEvaluable(TestingSExpression.sAnnotatedFunc(
                2,
                TestingSExpression.sNativeAnnotation(1, sString(1, "Impl.met"), true),
                sStringType(),
                "myFunc",
                nlist()));
      }

      @Test
      public void poly_native_pure_function() {
        module("""
            @Native("Impl.met")
            A myFunc();
            """)
            .loadsWithSuccess()
            .containsEvaluable(TestingSExpression.sAnnotatedFunc(
                2,
                TestingSExpression.sNativeAnnotation(1, sString(1, "Impl.met"), true),
                varA(),
                "myFunc",
                nlist()));
      }

      @Test
      public void mono_native_pure_function_with_param_with_default_value() {
        var code =
            """
            @Native("Impl.met")
            String myFunc(
              Blob p1 =
                0x07);
            """;
        var params = nlist(
            sItem(3, sBlobType(), "p1", TestingSExpression.sValue(3, "myFunc:p1", sBlob(4, 7))));
        var ann = TestingSExpression.sNativeAnnotation(1, sString(1, "Impl.met"), true);
        var myFunc = TestingSExpression.sAnnotatedFunc(2, ann, sStringType(), "myFunc", params);
        module(code).loadsWithSuccess().containsEvaluable(myFunc);
      }

      @Test
      public void poly_native_pure_function_with_param_with_default_value() {
        var code =
            """
            @Native("Impl.met")
            A myFunc(
              Blob p1 =
                0x07);
            """;
        var params = nlist(
            sItem(3, sBlobType(), "p1", TestingSExpression.sValue(3, "myFunc:p1", sBlob(4, 7))));
        var ann = TestingSExpression.sNativeAnnotation(1, sString(1, "Impl.met"), true);
        var myFunc = TestingSExpression.sAnnotatedFunc(2, ann, varA(), "myFunc", params);
        module(code).loadsWithSuccess().containsEvaluable(myFunc);
      }

      @Test
      public void mono_bytecode_function() {
        module("""
            @Bytecode("impl")
            String myFunc();
            """)
            .loadsWithSuccess()
            .containsEvaluable(
                TestingSExpression.sBytecodeFunc(2, sStringType(), "myFunc", nlist()));
      }

      @Test
      public void poly_bytecode_function() {
        module("""
            @Bytecode("impl")
            A myFunc();
            """)
            .loadsWithSuccess()
            .containsEvaluable(TestingSExpression.sBytecodeFunc(2, varA(), "myFunc", nlist()));
      }

      @Test
      public void mono_bytecode_function_with_param_with_default_value() {
        var code =
            """
            @Bytecode("Impl.met")
            String myFunc(
              Blob param1 =
                0x07);
            """;
        var params = nlist(sItem(
            3, sBlobType(), "param1", TestingSExpression.sValue(3, "myFunc:param1", sBlob(4, 7))));
        var myFunc = sBytecodeFunc(2, "Impl.met", sStringType(), "myFunc", params);
        module(code).loadsWithSuccess().containsEvaluable(myFunc);
      }

      @Test
      public void poly_bytecode_function_with_param_with_default_value() {
        var code =
            """
            @Bytecode("Impl.met")
            A myFunc(
              Blob param1 =
                0x07);
            """;
        var params = nlist(sItem(
            3, sBlobType(), "param1", TestingSExpression.sValue(3, "myFunc:param1", sBlob(4, 7))));
        var myFunc = sBytecodeFunc(2, "Impl.met", varA(), "myFunc", params);
        module(code).loadsWithSuccess().containsEvaluable(myFunc);
      }
    }

    @Nested
    class _struct {
      @Test
      public void empty_struct_type() {
        module("""
            MyStruct(
            )
            """)
            .loadsWithSuccess()
            .containsType(sStructType("MyStruct", nlist()));
      }

      @Test
      public void struct_type() {
        module("""
            MyStruct(
              String field
            )
            """)
            .loadsWithSuccess()
            .containsType(sStructType("MyStruct", nlist(sSig(sStringType(), "field"))));
      }
    }

    @Nested
    class _param_default_value {
      @Test
      public void default_val_referencing_poly_value() {
        var code =
            """
            Int myFunc(
              [Int] param1 =
                empty)
                = 7;
            [A] empty = [];
            """;
        var empty = TestingSExpression.sValue(5, "empty", sOrder(5, varA()));
        var defaultValue = TestingSExpression.sValue(
            2, "myFunc:param1", TestingSExpression.sInstantiate(3, list(varA()), empty));
        var params = nlist(sItem(2, sArrayType(sIntType()), "param1", defaultValue));
        var func = sFunc(1, sIntType(), "myFunc", params, sInt(4, 7));
        module(code).loadsWithSuccess().containsEvaluable(func);
      }

      @Test
      public void default_val_for_generic_param_referencing_poly_value() {
        var code =
            """
            Int myFunc(
              [B] param1 =
                empty)
                = 7;
            [A] empty = [];
            """;
        var empty = TestingSExpression.sValue(5, "empty", sOrder(5, varA()));
        var defaultValue = TestingSExpression.sValue(
            2, "myFunc:param1", TestingSExpression.sInstantiate(3, list(varA()), empty));
        var params = nlist(sItemPoly(2, sArrayType(varB()), "param1", some(defaultValue)));
        var func = sFunc(1, sIntType(), "myFunc", params, sInt(4, 7));
        module(code).loadsWithSuccess().containsEvaluable(func);
      }

      @Test
      public void default_value_using_literal() {
        var code =
            """
            Int myFunc(
              Int param1 =
                11)
                = 7;
            """;
        var defaultValue = TestingSExpression.sValue(2, "myFunc:param1", sInt(3, 11));
        var params = nlist(sItem(2, sIntType(), "param1", defaultValue));
        var func = sFunc(1, sIntType(), "myFunc", params, sInt(4, 7));
        module(code).loadsWithSuccess().containsEvaluable(func);
      }
    }
  }

  @Nested
  class _parens {
    @Test
    public void with_literal() {
      module("""
          result =
            (0x07);
          """)
          .loadsWithSuccess()
          .containsEvaluable(TestingSExpression.sValue(1, sBlobType(), "result", sBlob(2, 7)));
    }

    @Test
    public void with_operator() {
      module(
              """
          result =
          ([
            0x07,
            0x08
          ]);
          """)
          .loadsWithSuccess()
          .containsEvaluable(TestingSExpression.sValue(
              1,
              sArrayType(sBlobType()),
              "result",
              sOrder(2, sBlobType(), sBlob(3, 7), sBlob(4, 8))));
    }
  }
}
