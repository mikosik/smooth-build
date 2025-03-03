package org.smoothbuild.compilerfrontend.acceptance;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;
import static org.smoothbuild.compilerfrontend.lang.name.NList.nlist;
import static org.smoothbuild.compilerfrontend.lang.name.Name.referenceableName;
import static org.smoothbuild.compilerfrontend.lang.type.STypes.INT;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.lang.define.SCall;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SNamedExprValue;
import org.smoothbuild.compilerfrontend.testing.FrontendCompileTester;

public class SExprLoadingTest extends FrontendCompileTester {
  @Nested
  class _literal {
    @Test
    void blob_literal() {
      module("""
          result =
            0x07;
          """)
          .loadsWithSuccess()
          .containsEvaluable(sValue(1, sBlobType(), "result", sBlob(2, 7)));
    }

    @Test
    void int_literal() {
      module("""
          result =
            123;
          """)
          .loadsWithSuccess()
          .containsEvaluable(sValue(1, sIntType(), "result", sInt(2, 123)));
    }

    @Test
    void string_literal() {
      module("""
          result =
            "abc";
          """)
          .loadsWithSuccess()
          .containsEvaluable(sValue(1, sStringType(), "result", sString(2, "abc")));
    }
  }

  @Nested
  class _expr {
    @Nested
    class _lambda {
      @Test
      void mono_lambda() {
        var code =
            """
            result =
              (Int int)
                -> int;
            """;
        var param = sItem(2, INT, fqn("result:lambda~1:int"));
        var lambda = sLambda(2, fqn("result:lambda~1"), nlist(param), sParamRef(3, INT, "int"));
        var result = sValue(1, "result", lambda);
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      void mono_lambda_using_generic_type_of_enclosing_function() {
        var code =
            """
            (A)->A myFunc<A>(A outerA) =
              (A a)
                -> a;
            """;
        var body = sParamRef(3, varA(), "a");
        var fqn = fqn("myFunc:lambda~1");
        var lambdaParam = sItem(2, varA(), fqn("myFunc:lambda~1:a"));
        var lambda = sLambda(2, fqn, nlist(lambdaParam), body);
        var funcParam = sItem(1, varA(), fqn("myFunc:outerA"));
        var myFunc = sFunc(1, "myFunc", nlist(funcParam), lambda);
        module(code).loadsWithSuccess().containsEvaluable(myFunc);
      }

      @Test
      void mono_lambda_using_generic_type_of_enclosing_func_two_level_deep() {
        var code =
            """
            () -> (A)->A myFunc<A>(A outerA) =
              ()
                -> (A a)
                  -> a;
            """;
        var deepFqn = fqn("myFunc:lambda~1:lambda~1");
        var deeperBody = sParamRef(4, varA(), "a");
        var param = sItem(3, varA(), deepFqn.append(fqn("a")));
        var deeperLambda = sLambda(3, deepFqn, nlist(param), deeperBody);
        var lambda = sLambda(2, fqn("myFunc:lambda~1"), nlist(), deeperLambda);
        var myFunc = sFunc(1, "myFunc", nlist(sItem(1, varA(), "outerA")), lambda);
        module(code).loadsWithSuccess().containsEvaluable(myFunc);
      }
    }

    @Nested
    class _call {
      @Nested
      class _with_default_arg {
        @Test
        void with_reference_to_poly_val() {
          var polyVal = sBytecodeValue(4, varA(), "polyVal");
          var instantiateS = sInstantiate(1, list(varA()), polyVal);
          var arg = sInstantiate(2, list(sIntType()), sValue(1, "myFunc~b", instantiateS));
          test_default_arg("polyVal", arg);
        }

        @Test
        void with_reference_to_poly_func() {
          var polyFunc = sBytecodeFunc(6, varA(), "polyFunc", nlist());
          var instantiateS = sInstantiate(1, list(varA()), polyFunc);
          var paramDefaultValue = sValue("myFunc~b", sCall(1, instantiateS));
          var expected = sInstantiate(2, list(sIntType()), paramDefaultValue);
          test_default_arg("polyFunc()", expected);
        }

        @Test
        void with_reference_to_int() {
          var paramDefaultValue = sValue("myFunc~b", sInt(1, 7));
          test_default_arg("7", sInstantiate(2, paramDefaultValue));
        }

        private void test_default_arg(String bodyCode, SExpr expected) {
          var code =
              """
            B myFunc<B>(B b = $$$) = b;
            Int result = myFunc();
            @Bytecode("impl")
            A polyVal<A>;
            @Bytecode("impl")
            A polyFunc<A>();

            """
                  .replace("$$$", bodyCode);

          var result = module(code)
              .loadsWithSuccess()
              .getLoadedModule()
              .evaluables()
              .get(referenceableName("result"));
          var body = (SCall) ((SNamedExprValue) result).body();
          var actualDefArg = body.args().elements().get(0);
          assertThat(actualDefArg).isEqualTo(expected);
        }
      }

      @Test
      void with_mono_func_reference() {
        var code =
            """
            Int myReturnInt() = 3;
            result = myReturnInt();
            """;
        var myReturnInt = returnIntSFunc();
        var result = sValue(2, sIntType(), "result", sCall(2, sInstantiate(2, myReturnInt)));
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      void with_mono_func_reference_and_arg() {
        var code =
            """
            Int myIntId(Int i) = i;
            result = myIntId(3);
            """;
        var myIntId = intIdSFunc();
        var result =
            sValue(2, sIntType(), "result", sCall(2, sInstantiate(2, myIntId), sInt(2, 3)));
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      void with_mono_func_reference_and_named_arg() {
        var code =
            """
            Int myIntId(Int i) = i;
            result = myIntId(i=
              7);
            """;
        var myIntId = intIdSFunc();
        var result =
            sValue(2, sIntType(), "result", sCall(2, sInstantiate(2, myIntId), sInt(3, 7)));
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      void with_mono_func_reference_and_piped_arg() {
        var code =
            """
            Int myIntId(Int i) = i;
            result = 7
              > myIntId();
            """;
        var myIntId = intIdSFunc();
        var result =
            sValue(2, sIntType(), "result", sCall(3, sInstantiate(3, myIntId), sInt(2, 7)));
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      void with_poly_func_reference_and_arg() {
        module("""
            A myId<A>(A a) = a;
            result = myId(7);
            """)
            .loadsWithSuccess()
            .containsEvaluable(sValue(
                2,
                sIntType(),
                "result",
                sCall(2, sInstantiate(2, list(sIntType()), idSFunc()), sInt(2, 7))));
      }

      @Test
      void with_value_reference() {
        var code =
            """
            Int myReturnInt() = 3;
            ()->Int myValue = myReturnInt;
            result = myValue();
            """;
        var myReturnInt = returnIntSFunc();
        var myValue = sValue(2, sIntFuncType(), "myValue", sInstantiate(2, myReturnInt));
        var result = sValue(3, sIntType(), "result", sCall(3, sInstantiate(3, myValue)));
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      void with_value_reference_and_arg() {
        var code =
            """
            Int myIntId(Int i) = i;
            (Int)->Int myValue = myIntId;
            result = myValue(
              7);
            """;
        var myIntId = intIdSFunc();
        var myValue =
            sValue(2, sFuncType(sIntType(), sIntType()), "myValue", sInstantiate(2, myIntId));
        var result =
            sValue(3, sIntType(), "result", sCall(3, sInstantiate(3, myValue), sInt(4, 7)));
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      void with_constructor_reference() {
        var struct = sStructType("MyStruct", nlist(sSig(sStringType(), "field")));
        var constructor = sConstructor(1, struct, "MyStruct");
        module("""
            MyStruct{
              String field
            }
            """)
            .loadsWithSuccess()
            .containsEvaluable(constructor);
      }

      @Test
      void with_constructor_reference_and_argument() {
        var code =
            """
            MyStruct {
              String field
            }
            result = MyStruct(
              "aaa");
            """;
        var struct = sStructType("MyStruct", nlist(sSig(sStringType(), "field")));
        var constructor = sConstructor(1, struct);
        var resultBody = sCall(4, sInstantiate(4, constructor), sString(5, "aaa"));
        var result = sValue(4, struct, "result", resultBody);
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      void with_ref() {
        module("""
            result(()->String f) = f();
            """)
            .loadsWithSuccess()
            .containsEvaluable(sFunc(
                1,
                sStringType(),
                "result",
                nlist(sItem(1, sStringFuncType(), fqn("result:f"))),
                sCall(1, sParamRef(sStringFuncType(), "f"))));
      }

      @Test
      void with_ref_and_arg() {
        module("""
            result((Blob)->String f) = f(0x09);
            """)
            .loadsWithSuccess()
            .containsEvaluable(sFunc(
                1,
                sStringType(),
                "result",
                nlist(sItem(1, sFuncType(sBlobType(), sStringType()), fqn("result:f"))),
                sCall(1, sParamRef(sFuncType(sBlobType(), sStringType()), "f"), sBlob(1, 9))));
      }
    }

    @Nested
    class _poly_ref {
      @Test
      void to_mono_value() {
        var code =
            """
            String myValue = "abc";
            String result =
              myValue;
            """;
        var myValue = sValue(1, sStringType(), "myValue", sString("abc"));
        var result = sValue(2, sStringType(), "result", sInstantiate(3, myValue));
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      void to_poly_value() {
        var code =
            """
            [A] myValue<A> = [];
            [Int] result =
              myValue;
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(sValue(
                2,
                sIntArrayT(),
                "result",
                sInstantiate(
                    3,
                    list(sIntType()),
                    sValue(1, sArrayType(varA()), "myValue", sOrder(varA())))));
      }

      @Test
      void to_mono_func() {
        var code =
            """
            String myFunc() = "abc";
            ()->String result =
              myFunc;
            """;
        var myFunc = sFunc(1, "myFunc", nlist(), sString("abc"));
        var result = sValue(2, sStringFuncType(), "result", sInstantiate(3, myFunc));
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      void to_poly_func() {
        var code =
            """
            A myId<A>(A a) = a;
            (Int)->Int result =
              myId;
            """;
        var myId = sFunc(1, "myId", nlist(sItem(varA(), "a")), sParamRef(1, varA(), "a"));
        var resultBody = sInstantiate(3, list(sIntType()), myId);
        var result = sValue(2, sFuncType(sIntType(), sIntType()), "result", resultBody);
        module(code).loadsWithSuccess().containsEvaluable(result);
      }

      @Test
      void to_constructor() {
        var code =
            """
            MyStruct{}
            ()->MyStruct result =
              MyStruct;
            """;
        var structT = sStructType("MyStruct", nlist());
        var constructor = sConstructor(1, structT);
        var result = sValue(2, sFuncType(structT), "result", sInstantiate(3, constructor));
        module(code).loadsWithSuccess().containsEvaluable(result);
      }
    }

    @Nested
    class _order {
      @Test
      void order() {
        module(
                """
            result =
            [
              0x07,
              0x08
            ];
            """)
            .loadsWithSuccess()
            .containsEvaluable(sValue(
                1, sBlobArrayT(), "result", sOrder(2, sBlobType(), sBlob(3, 7), sBlob(4, 8))));
      }

      @Test
      void order_with_piped_value() {
        module(
                """
            result = 0x07 >
            [
              0x08
            ];
            """)
            .loadsWithSuccess()
            .containsEvaluable(sValue(
                1, sBlobArrayT(), "result", sOrder(2, sBlobType(), sBlob(1, 7), sBlob(3, 8))));
      }

      @Test
      void order_of_funcs() {
        var code =
            """
          Int returnInt() = 7;
          result =
          [
            returnInt,
          ];
          """;
        var returnInt = sFunc(1, "returnInt", nlist(), sInt(1, 7));
        var orderS = sOrder(3, sInstantiate(4, returnInt));
        var expected = sValue(2, "result", orderS);
        module(code).loadsWithSuccess().containsEvaluable(expected);
      }
    }

    @Test
    void param_ref() {
      var code = """
          Blob myFunc(Blob param1)
            = param1;
          """;
      var body = sParamRef(2, sBlobType(), "param1");
      var myFunc = sFunc(1, sBlobType(), "myFunc", nlist(sItem(1, sBlobType(), "param1")), body);
      module(code).loadsWithSuccess().containsEvaluable(myFunc);
    }

    @Test
    void select() {
      var code =
          """
          @Native("impl")
          MyStruct getStruct();
          MyStruct{
            String field,
          }
          result = getStruct()
            .field;
          """;
      var myStruct = sStructType("MyStruct", nlist(sSig(sStringType(), "field")));
      var getStruct = sAnnotatedFunc(2, sNativeAnnotation(), myStruct, "getStruct", nlist());
      var resultBody = sSelect(7, sCall(6, sInstantiate(6, getStruct)), "field");
      var result = sValue(6, sStringType(), "result", resultBody);
      module(code).loadsWithSuccess().containsEvaluable(result);
    }
  }

  @Nested
  class _definition_of {
    @Nested
    class _value {
      @Test
      void mono_expression_value() {
        var code = """
          Blob myValue =
            0x07;
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(sValue(1, sBlobType(), "myValue", sBlob(2, 7)));
      }

      @Test
      void poly_expression_value() {
        var code = """
          [A] myValue<A> =
            [];
          """;
        var expected = sValue(1, sArrayType(varA()), "myValue", sOrder(2, varA()));
        module(code).loadsWithSuccess().containsEvaluable(expected);
      }

      @Test
      void mono_bytecode_value() {
        var code = """
          @Bytecode("impl")
          Blob myValue;
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(sBytecodeValue(2, sBlobType(), "myValue"));
      }

      @Test
      void poly_bytecode_value() {
        var code = """
          @Bytecode("impl")
          A myValue<A>;
          """;
        var expected = sBytecodeValue(2, varA(), "myValue");
        module(code).loadsWithSuccess().containsEvaluable(expected);
      }
    }

    @Nested
    class _func {
      @Test
      void mono_expression_function() {
        module("""
            Blob myFunc() =
              0x07;
            """)
            .loadsWithSuccess()
            .containsEvaluable(sFunc(1, sBlobType(), "myFunc", nlist(), sBlob(2, 7)));
      }

      @Test
      void poly_expression_function() {
        var code = """
            [A] myFunc<A>() =
              [];
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(sFunc(1, sArrayType(varA()), "myFunc", nlist(), sOrder(2, varA())));
      }

      @Test
      void mono_expression_function_with_param() {
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
                nlist(sItem(2, sBlobType(), "param1")),
                sString(3, "abc")));
      }

      @Test
      void poly_expression_function_with_param() {
        var code =
            """
            A myFunc<A>(
              A a)
              = a;
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(sFunc(
                1, varA(), "myFunc", nlist(sItem(2, varA(), "a")), sParamRef(3, varA(), "a")));
      }

      @Test
      void mono_expression_function_with_param_with_default_value() {
        var code =
            """
            String myFunc(
              Blob param1 =
                0x07)
                = "abc";
            """;
        var value = sValue(3, "myFunc~param1", sBlob(3, 7));
        var params = nlist(sItem(2, sBlobType(), "param1", "myFunc~param1"));
        var myFunc = sFunc(1, sStringType(), "myFunc", params, sString(4, "abc"));
        var api = module(code).loadsWithSuccess();
        api.containsEvaluable(myFunc);
        api.containsEvaluable(value);
      }

      @Test
      void poly_expression_function_with_param_with_default_value() {
        var code =
            """
            A myFunc<A>(
              A a =
                7)
                = a;
            """;
        var value = sValue(3, "myFunc~a", sInt(3, 7));
        var params = nlist(sItem(2, varA(), "a", "myFunc~a"));
        var myFunc = sFunc(1, varA(), "myFunc", params, sParamRef(4, varA(), "a"));
        var api = module(code).loadsWithSuccess();
        api.containsEvaluable(myFunc);
        api.containsEvaluable(value);
      }

      @Test
      void mono_native_impure_function() {
        module(
                """
            @NativeImpure("Impl.met")
            String myFunc();
            """)
            .loadsWithSuccess()
            .containsEvaluable(sAnnotatedFunc(
                2,
                sNativeAnnotation(1, sString(1, "Impl.met"), false),
                sStringType(),
                "myFunc",
                nlist()));
      }

      @Test
      void poly_native_impure_function() {
        var code =
            """
            @NativeImpure("Impl.met")
            A myFunc<A>();
            """;
        var var = varA();
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(sAnnotatedFunc(
                2, sNativeAnnotation(1, sString(1, "Impl.met"), false), var, "myFunc", nlist()));
      }

      @Test
      void mono_native_pure_function() {
        module("""
            @Native("Impl.met")
            String myFunc();
            """)
            .loadsWithSuccess()
            .containsEvaluable(sAnnotatedFunc(
                2,
                sNativeAnnotation(1, sString(1, "Impl.met"), true),
                sStringType(),
                "myFunc",
                nlist()));
      }

      @Test
      void poly_native_pure_function() {
        var code = """
            @Native("Impl.met")
            A myFunc<A>();
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(sAnnotatedFunc(
                2, sNativeAnnotation(1, sString(1, "Impl.met"), true), varA(), "myFunc", nlist()));
      }

      @Test
      void mono_native_pure_function_with_param_with_default_value() {
        var code =
            """
            @Native("Impl.met")
            String myFunc(
              Blob p1 =
                0x07);
            """;
        var value = sValue(4, "myFunc~p1", sBlob(4, 7));
        var params = nlist(sItem(3, sBlobType(), "p1", "myFunc~p1"));
        var ann = sNativeAnnotation(1, sString(1, "Impl.met"), true);
        var myFunc = sAnnotatedFunc(2, ann, sStringType(), "myFunc", params);
        var api = module(code).loadsWithSuccess();
        api.containsEvaluable(myFunc);
        api.containsEvaluable(value);
      }

      @Test
      void poly_native_pure_function_with_param_with_default_value() {
        var code =
            """
            @Native("Impl.met")
            A myFunc<A>(
              Blob p1 =
                0x07);
            """;
        var value = sValue(4, "myFunc~p1", sBlob(4, 7));
        var params = nlist(sItem(3, sBlobType(), "p1", "myFunc~p1"));
        var ann = sNativeAnnotation(1, sString(1, "Impl.met"), true);
        var myFunc = sAnnotatedFunc(2, ann, varA(), "myFunc", params);
        var api = module(code).loadsWithSuccess();
        api.containsEvaluable(myFunc);
        api.containsEvaluable(value);
      }

      @Test
      void mono_bytecode_function() {
        module("""
            @Bytecode("impl")
            String myFunc();
            """)
            .loadsWithSuccess()
            .containsEvaluable(sBytecodeFunc(2, sStringType(), "myFunc", nlist()));
      }

      @Test
      void poly_bytecode_function() {
        var code = """
            @Bytecode("impl")
            A myFunc<A>();
            """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(sBytecodeFunc(2, varA(), "myFunc", nlist()));
      }

      @Test
      void mono_bytecode_function_with_param_with_default_value() {
        var code =
            """
            @Bytecode("Impl.met")
            String myFunc(
              Blob param1 =
                0x07);
            """;
        var value = sValue(4, "myFunc~param1", sBlob(4, 7));
        var params = nlist(sItem(3, sBlobType(), "param1", "myFunc~param1"));
        var myFunc = sBytecodeFunc(2, "Impl.met", sStringType(), "myFunc", params);
        var api = module(code).loadsWithSuccess();
        api.containsEvaluable(myFunc);
        api.containsEvaluable(value);
      }

      @Test
      void poly_bytecode_function_with_param_with_default_value() {
        var code =
            """
            @Bytecode("Impl.met")
            A myFunc<A>(
              Blob param1 =
                0x07);
            """;
        var value = sValue(4, "myFunc~param1", sBlob(4, 7));
        var params = nlist(sItem(3, sBlobType(), "param1", "myFunc~param1"));
        var myFunc = sBytecodeFunc(2, "Impl.met", varA(), "myFunc", params);
        var api = module(code).loadsWithSuccess();
        api.containsEvaluable(myFunc);
        api.containsEvaluable(value);
      }
    }

    @Nested
    class _struct {
      @Test
      void empty_struct_type() {
        module("""
            MyStruct{
            }
            """)
            .loadsWithSuccess()
            .containsType(sStructType("MyStruct", nlist()));
      }

      @Test
      void struct_type() {
        module("""
            MyStruct{
              String field
            }
            """)
            .loadsWithSuccess()
            .containsType(sStructType("MyStruct", nlist(sSig(sStringType(), "field"))));
      }
    }

    @Nested
    class _param_default_value {
      @Test
      void default_val_referencing_poly_value() {
        var code =
            """
            Int myFunc(
              [Int] param1 =
                empty)
                = 7;
            [A] empty<A> = [];
            """;
        var emptyReference = sPolyReference(3, sScheme(sArrayType(varA())), fqn("empty"));
        var defName = "myFunc~param1";
        var value = sValue(3, defName, sInstantiate(3, list(varA()), emptyReference));
        var params = nlist(sItem(2, sIntArrayT(), "param1", defName));
        var func = sFunc(1, sIntType(), "myFunc", params, sInt(4, 7));
        var api = module(code).loadsWithSuccess();
        api.containsEvaluable(func);
        api.containsEvaluable(value);
      }

      @Test
      void default_val_for_generic_param_referencing_poly_value() {
        var code =
            """
            Int myFunc<B>(
              [B] param1 =
                empty)
                = 7;
            [A] empty<A> = [];
            """;
        var emptyReference = sPolyReference(3, sScheme(sArrayType(varA())), fqn("empty"));
        var defaultBody = sInstantiate(3, list(varA()), emptyReference);
        var defaultValue = sValue(3, sScheme(sArrayType(varA())), "myFunc~param1", defaultBody);
        var params = nlist(sItem(2, sArrayType(varB()), "param1", some("myFunc~param1")));
        var func = sFunc(1, sIntType(), "myFunc", params, sInt(4, 7));
        var api = module(code).loadsWithSuccess();
        api.containsEvaluable(func);
        api.containsEvaluable(defaultValue);
      }

      @Test
      void default_value_using_literal() {
        var code =
            """
            Int myFunc(
              Int param1 =
                11)
                = 7;
            """;
        var value = sValue(3, "myFunc~param1", sInt(3, 11));
        var params = nlist(sItem(2, sIntType(), "param1", "myFunc~param1"));
        var func = sFunc(1, sIntType(), "myFunc", params, sInt(4, 7));
        var api = module(code).loadsWithSuccess();
        api.containsEvaluable(func);
        api.containsEvaluable(value);
      }
    }
  }

  @Nested
  class _parens {
    @Test
    void with_literal() {
      module("""
          result =
            (0x07);
          """)
          .loadsWithSuccess()
          .containsEvaluable(sValue(1, sBlobType(), "result", sBlob(2, 7)));
    }

    @Test
    void with_operator() {
      module(
              """
          result =
          ([
            0x07,
            0x08
          ]);
          """)
          .loadsWithSuccess()
          .containsEvaluable(
              sValue(1, sBlobArrayT(), "result", sOrder(2, sBlobType(), sBlob(3, 7), sBlob(4, 8))));
    }
  }
}
