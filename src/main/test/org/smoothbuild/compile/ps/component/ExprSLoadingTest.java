package org.smoothbuild.compile.ps.component;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compile.lang.type.TypeFS.INT;
import static org.smoothbuild.compile.lang.type.VarSetS.varSetS;
import static org.smoothbuild.util.collect.NList.nlist;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compile.lang.define.CallS;
import org.smoothbuild.compile.lang.define.EvaluableS;
import org.smoothbuild.compile.lang.define.ExprS;
import org.smoothbuild.compile.lang.define.NamedExprValueS;
import org.smoothbuild.testing.TestContext;

public class ExprSLoadingTest extends TestContext {
  @Nested
  class _literal {
    @Test
    public void blob_literal() {
      module("""
          result =
            0x07;
          """)
          .loadsWithSuccess()
          .containsEvaluable(valueS(1, blobTS(), "result", blobS(2, 7)));
    }

    @Test
    public void int_literal() {
      module("""
          result =
            123;
          """)
          .loadsWithSuccess()
          .containsEvaluable(valueS(1, intTS(), "result", intS(2, 123)));
    }

    @Test
    public void string_literal() {
      module("""
          result =
            "abc";
          """)
          .loadsWithSuccess()
          .containsEvaluable(valueS(1, stringTS(), "result", stringS(2, "abc")));
    }
  }

  @Nested
  class _expr {
    @Nested
    class _anonymous_function {
      @Test
      public void mono_anonymous_function() {
        var code = """
            result =
              (Int int)
                -> int;
            """;
        var anonymousFunc = anonymousFuncS(
            2, nlist(itemS(2, INT, "int")), paramRefS(3, INT, "int"));
        var monoized = monoizeS(2, varMap(), anonymousFunc);
        var result = valueS(1, "result", monoized);
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(result);
      }

      @Test
      public void mono_anonymous_func_using_generic_type_of_enclosing_function() {
        var code = """
            (A)->A myFunc(A outerA) =
              (A a)
                -> a;
            """;
        var body = paramRefS(3, varA(), "a");
        var anonymousFunc = anonymousFuncS(2, varSetS(), nlist(itemS(2, varA(), "a")), body);
        var monoized = monoizeS(2, varMap(), anonymousFunc);
        var myFunc = funcS(1, "myFunc", nlist(itemS(1, varA(), "outerA")), monoized);
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(myFunc);
      }

      @Test
      public void mono_anonymous_function_using_generic_type_of_enclosing_func_two_level_deep() {
        var code = """
            () -> (A)->A myFunc(A outerA) =
              ()
                -> (A a)
                  -> a;
            """;
        var deeperBody = paramRefS(4, varA(), "a");
        var deeperAnonymousFunc = anonymousFuncS(
            3, varSetS(), nlist(itemS(3, varA(), "a")), deeperBody);
        var monoDeeperAnonymousFunc = monoizeS(3, varMap(), deeperAnonymousFunc);
        var anonFunc = anonymousFuncS(2, varSetS(), nlist(), monoDeeperAnonymousFunc);
        var monoAnonymousFunc = monoizeS(2, varMap(), anonFunc);
        var myFunc = funcS(1, "myFunc", nlist(itemS(1, varA(), "outerA")), monoAnonymousFunc);
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(myFunc);
      }

      @Test
      public void poly_anonymous_function() {
        var code = """
            result =
              (A a)
                -> a;
            """;
        var anonymousFunc = anonymousFuncS(
            2, nlist(itemS(2, varA(), "a")), paramRefS(3, varA(), "a"));
        var monoized = monoizeS(2, varMap(varA(), varB()), anonymousFunc);
        var result = valueS(1, "result", monoized);
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(result);
      }
    }

    @Nested
    class _call {
      @Nested
      class _with_default_arg {
        @Test
        public void with_reference_to_poly_val() {
          var polyVal = bytecodeValueS(4, varA(), "polyVal");
          var monoized = monoizeS(1, varMap(varA(), varA()), polyVal);
          var arg = monoizeS(2, varMap(varA(), intTS()), valueS(1, "myFunc:b", monoized));
          test_default_arg("polyVal", arg);
        }

        @Test
        public void with_reference_to_poly_func() {
          var polyFunc = bytecodeFuncS(6, varA(), "polyFunc", nlist());
          var monoized = monoizeS(1, varMap(varA(), varA()), polyFunc);
          var paramDefaultValue = valueS("myFunc:b", callS(1, monoized));
          var expected = monoizeS(2, varMap(varA(), intTS()), paramDefaultValue);
          test_default_arg("polyFunc()", expected);
        }

        @Test
        public void with_reference_to_int() {
          var paramDefaultValue = valueS("myFunc:b", intS(1, 7));
          test_default_arg("7", monoizeS(2, paramDefaultValue));
        }

        private void test_default_arg(String bodyCode, ExprS expected) {
          var code = """
            B myFunc(B b = $$$) = b;
            Int result = myFunc();
            @Bytecode("impl")
            A polyVal;
            @Bytecode("impl")
            A polyFunc();
            
            """.replace("$$$", bodyCode);

          EvaluableS result = module(code)
              .loadsWithSuccess()
              .getModuleAsDefinitions().evaluables().get("result");
          ExprS actualDefArg = ((CallS) ((NamedExprValueS) result).body()).args().get(0);
          assertThat(actualDefArg)
              .isEqualTo(expected);
        }
      }

      @Test
      public void with_mono_func_reference() {
        var code = """
            Int myReturnInt() = 3;
            result = myReturnInt();
            """;
        var myReturnInt = returnIntFuncS();
        var result = valueS(2, intTS(), "result", callS(2, monoizeS(2, myReturnInt)));
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(result);
      }

      @Test
      public void with_mono_func_reference_and_arg() {
        var code = """
            Int myIntId(Int i) = i;
            result = myIntId(3);
            """;
        var myIntId = intIdFuncS();
        var result = valueS(2, intTS(), "result", callS(2, monoizeS(2, myIntId), intS(2, 3)));
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(result);
      }

      @Test
      public void with_mono_func_reference_and_named_arg() {
        var code = """
            Int myIntId(Int i) = i;
            result = myIntId(i=
              7);
            """;
        var myIntId = intIdFuncS();
        var result = valueS(2, intTS(), "result", callS(2, monoizeS(2, myIntId), intS(3, 7)));
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(result);
      }

      @Test
      public void with_mono_func_reference_and_piped_arg() {
        var code = """
            Int myIntId(Int i) = i;
            result = 7
              | myIntId();
            """;
        var myIntId = intIdFuncS();
        var result = valueS(2, intTS(), "result", callS(3, monoizeS(3, myIntId), intS(2, 7)));
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(result);
      }

      @Test
      public void with_poly_func_reference_and_arg() {
        module("""
          A myId(A a) = a;
          result = myId(7);
          """)
            .loadsWithSuccess()
            .containsEvaluable(valueS(2, intTS(), "result",
                callS(2,
                    monoizeS(2, varMap(varA(), intTS()), idFuncS()),
                    intS(2, 7))));
      }

      @Test
      public void with_value_reference() {
        var code = """
            Int myReturnInt() = 3;
            ()->Int myValue = myReturnInt;
            result = myValue();
            """;
        var myReturnInt = returnIntFuncS();
        var myValue = valueS(2, funcTS(intTS()), "myValue", monoizeS(2, myReturnInt));
        var result = valueS(3, intTS(), "result", callS(3, monoizeS(3, myValue)));
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(result);
      }

      @Test
      public void with_value_reference_and_arg() {
        var code = """
            Int myIntId(Int i) = i;
            (Int)->Int myValue = myIntId;
            result = myValue(
              7);
            """;
        var myIntId = intIdFuncS();
        var myValue = valueS(2, funcTS(intTS(), intTS()), "myValue", monoizeS(2, myIntId));
        var result = valueS(3, intTS(), "result", callS(3, monoizeS(3, myValue), intS(4, 7)));
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(result);
      }

      @Test
      public void with_ctor_reference() {
        var struct = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
        var constructor = constructorS(1, struct, "myStruct");
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
        var code = """
            MyStruct(
              String field
            )
            result = myStruct(
              "aaa");
            """;
        var struct = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
        var constructor = constructorS(1, struct);
        var resultBody = callS(4, monoizeS(4, constructor), stringS(5, "aaa"));
        var result = valueS(4, struct, "result", resultBody);
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(result);
      }

      @Test
      public void with_ref() {
        module("""
          result(()->String f) = f();
          """)
            .loadsWithSuccess()
            .containsEvaluable(funcS(
                1, stringTS(), "result", nlist(itemS(1, funcTS(stringTS()), "f")),
                callS(1, paramRefS(funcTS(stringTS()), "f"))));
      }

      @Test
      public void with_ref_and_arg() {
        module("""
          result((Blob)->String f) = f(0x09);
          """)
            .loadsWithSuccess()
            .containsEvaluable(funcS(
                1, stringTS(), "result", nlist(itemS(1, funcTS(blobTS(), stringTS()), "f")),
                callS(1, paramRefS(funcTS(blobTS(), stringTS()), "f"), blobS(1, 9))));
      }
    }

    @Nested
    class _poly_ref {
      @Test
      public void to_mono_value() {
        var code = """
            String myValue = "abc";
            String result =
              myValue;
            """;
        var myValue = valueS(1, stringTS(), "myValue", stringS("abc"));
        var result = valueS(2, stringTS(), "result", monoizeS(3, myValue));
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(result);
      }

      @Test
      public void to_poly_value() {
        module("""
          [A] myValue = [];
          [Int] result =
            myValue;
          """)
            .loadsWithSuccess()
            .containsEvaluable(valueS(2, arrayTS(intTS()), "result",
                monoizeS(3, varMap(varA(), intTS()),
                    valueS(1, arrayTS(varA()), "myValue", orderS(varA())))));
      }

      @Test
      public void to_mono_func() {
        var code = """
            String myFunc() = "abc";
            ()->String result =
              myFunc;
            """;
        var myFunc = funcS(1, "myFunc", nlist(), stringS("abc"));
        var result = valueS(2, funcTS(stringTS()), "result", monoizeS(3, myFunc));
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(result);
      }

      @Test
      public void to_poly_func() {
        var code = """
            A myId(A a) = a;
            (Int)->Int result =
              myId;
            """;
        var myId = funcS(1, "myId", nlist(itemS(varA(), "a")), paramRefS(1, varA(), "a"));
        var resultBody = monoizeS(3, varMap(varA(), intTS()), myId);
        var result = valueS(2, funcTS(intTS(), intTS()), "result", resultBody);
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(result);
      }

      @Test
      public void to_ctor() {
        var code = """
            MyStruct()
            ()->MyStruct result =
              myStruct;
            """;
        var structT = structTS("MyStruct", nlist());
        var constructor = constructorS(1, structT);
        var result = valueS(2, funcTS(structT), "result", monoizeS(3, constructor));
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(result);
      }
    }

    @Nested
    class _order {
      @Test
      public void order() {
        module("""
          result =
          [
            0x07,
            0x08
          ];
          """)
            .loadsWithSuccess()
            .containsEvaluable(valueS(
                1, arrayTS(blobTS()), "result", orderS(2, blobTS(), blobS(3, 7), blobS(4, 8))));
      }

      @Test
      public void order_with_piped_value() {
        module("""
          result = 0x07 |
          [
            0x08
          ];
          """)
            .loadsWithSuccess()
            .containsEvaluable(valueS(
                1, arrayTS(blobTS()), "result", orderS(2, blobTS(), blobS(1, 7), blobS(3, 8))));
      }

      @Test
      public void order_of_funcs() {
        var code = """
          Int returnInt() = 7;
          result =
          [
            returnInt,
          ];
          """;
        var returnInt = funcS(1, "returnInt", nlist(), intS(1, 7));
        var orderS = orderS(3, monoizeS(4, returnInt));
        var expected = valueS(2, "result", orderS);
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(expected);
      }
    }

    @Test
    public void param_ref() {
      var code = """
          Blob myFunc(Blob param1)
            = param1;
          """;
      var body = paramRefS(2, blobTS(), "param1");
      var myFunc = funcS(1, blobTS(), "myFunc", nlist(itemS(1, blobTS(), "param1")), body);
      module(code)
          .loadsWithSuccess()
          .containsEvaluable(myFunc);
    }

    @Test
    public void select() {
      var code = """
          @Native("impl")
          MyStruct getStruct();
          MyStruct(
            String field,
          )
          result = getStruct()
            .field;
          """;
      var myStruct = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
      var getStruct = annotatedFuncS(2, nativeAnnotationS(), myStruct, "getStruct", nlist());
      var resultBody = selectS(7, callS(6, monoizeS(6, getStruct)), "field");
      var result = valueS(6, stringTS(), "result", resultBody);
      module(code)
          .loadsWithSuccess()
          .containsEvaluable(result);
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
            .containsEvaluable(valueS(1, blobTS(), "myValue", blobS(2, 7)));
      }

      @Test
      public void poly_expression_value() {
        var code = """
          [A] myValue =
            [];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(valueS(1, arrayTS(varA()), "myValue", orderS(2, varA())));
      }

      @Test
      public void mono_bytecode_value() {
        var code = """
          @Bytecode("impl")
          Blob myValue;
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(bytecodeValueS(2, blobTS(), "myValue"));
      }

      @Test
      public void poly_bytecode_value() {
        var code = """
          @Bytecode("impl")
          A myValue;
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(bytecodeValueS(2, varA(), "myValue"));
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
            .containsEvaluable(funcS(1, blobTS(), "myFunc", nlist(), blobS(2, 7)));
      }

      @Test
      public void poly_expression_function() {
        module("""
          [A] myFunc() =
            [];
          """)
            .loadsWithSuccess()
            .containsEvaluable(funcS(1, arrayTS(varA()), "myFunc", nlist(),
                orderS(2, varA())));
      }

      @Test
      public void mono_expression_function_with_param() {
        module("""
          String myFunc(
            Blob param1)
            = "abc";
          """)
            .loadsWithSuccess()
            .containsEvaluable(
                funcS(1, stringTS(), "myFunc", nlist(itemS(2, blobTS(), "param1")),
                    stringS(3, "abc")));
      }

      @Test
      public void poly_expression_function_with_param() {
        module("""
          A myFunc(
            A a)
            = a;
          """)
            .loadsWithSuccess()
            .containsEvaluable(
                funcS(1, varA(), "myFunc", nlist(itemS(2, varA(), "a")),
                    paramRefS(3, varA(), "a")));
      }

      @Test
      public void mono_expression_function_with_param_with_default_value() {
        var code = """
            String myFunc(
              Blob param1 =
                0x07)
                = "abc";
            """;
        var params = nlist(itemS(2, blobTS(), "param1", valueS(2, "myFunc:param1", blobS(3, 7))));
        var myFunc = funcS(1, stringTS(), "myFunc", params, stringS(4, "abc"));
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(myFunc);
      }

      @Test
      public void poly_expression_function_with_param_with_default_value() {
        var code = """
            A myFunc(
              A a =
                7)
                = a;
            """;
        var params = nlist(itemS(2, varA(), "a", valueS(2, "myFunc:a", intS(3, 7))));
        var myFunc = funcS(1, varA(), "myFunc", params, paramRefS(4, varA(), "a"));
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(myFunc);
      }

      @Test
      public void mono_native_impure_function() {
        module("""
          @NativeImpure("Impl.met")
          String myFunc();
          """)
            .loadsWithSuccess()
            .containsEvaluable(annotatedFuncS(
                2,
                nativeAnnotationS(1, stringS(1, "Impl.met"), false),
                stringTS(),
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
            .containsEvaluable(annotatedFuncS(
                2, nativeAnnotationS(1, stringS(1, "Impl.met"), false), varA(), "myFunc", nlist()));
      }

      @Test
      public void mono_native_pure_function() {
        module("""
          @Native("Impl.met")
          String myFunc();
          """)
            .loadsWithSuccess()
            .containsEvaluable(annotatedFuncS(2,
                nativeAnnotationS(1, stringS(1, "Impl.met"), true), stringTS(), "myFunc", nlist()));
      }

      @Test
      public void poly_native_pure_function() {
        module("""
          @Native("Impl.met")
          A myFunc();
          """)
            .loadsWithSuccess()
            .containsEvaluable(annotatedFuncS(
                2, nativeAnnotationS(1, stringS(1, "Impl.met"), true), varA(), "myFunc", nlist()));
      }

      @Test
      public void mono_native_pure_function_with_param_with_default_value() {
        var code = """
            @Native("Impl.met")
            String myFunc(
              Blob p1 =
                0x07);
            """;
        var params = nlist(itemS(3, blobTS(), "p1", valueS(3, "myFunc:p1", blobS(4, 7))));
        var ann = nativeAnnotationS(1, stringS(1, "Impl.met"), true);
        var myFunc = annotatedFuncS(2, ann, stringTS(), "myFunc", params);
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(myFunc);
      }

      @Test
      public void poly_native_pure_function_with_param_with_default_value() {
        var code = """
            @Native("Impl.met")
            A myFunc(
              Blob p1 =
                0x07);
            """;
        var params = nlist(itemS(3, blobTS(), "p1", valueS(3, "myFunc:p1", blobS(4, 7))));
        var ann = nativeAnnotationS(1, stringS(1, "Impl.met"), true);
        var myFunc = annotatedFuncS(2, ann, varA(), "myFunc", params);
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(myFunc);
      }

      @Test
      public void mono_bytecode_function() {
        module("""
          @Bytecode("impl")
          String myFunc();
          """)
            .loadsWithSuccess()
            .containsEvaluable(bytecodeFuncS(2, stringTS(), "myFunc", nlist()));
      }

      @Test
      public void poly_bytecode_function() {
        module("""
          @Bytecode("impl")
          A myFunc();
          """)
            .loadsWithSuccess()
            .containsEvaluable(bytecodeFuncS(2, varA(), "myFunc", nlist()));
      }

      @Test
      public void mono_bytecode_function_with_param_with_default_value() {
        var code = """
            @Bytecode("Impl.met")
            String myFunc(
              Blob param1 =
                0x07);
            """;
        var params = nlist(itemS(3, blobTS(), "param1", valueS(3, "myFunc:param1", blobS(4, 7))));
        var myFunc = bytecodeFuncS(2, "Impl.met", stringTS(), "myFunc", params);
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(myFunc);
      }

      @Test
      public void poly_bytecode_function_with_param_with_default_value() {
        var code = """
            @Bytecode("Impl.met")
            A myFunc(
              Blob param1 =
                0x07);
            """;
        var params = nlist(itemS(3, blobTS(), "param1", valueS(3, "myFunc:param1", blobS(4, 7))));
        var myFunc = bytecodeFuncS(2, "Impl.met", varA(), "myFunc", params);
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(myFunc);
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
            .containsType(structTS("MyStruct", nlist()));
      }

      @Test
      public void struct_type() {
        module("""
          MyStruct(
            String field
          )
          """)
            .loadsWithSuccess()
            .containsType(structTS("MyStruct", nlist(sigS(stringTS(), "field"))));
      }
    }

    @Nested
    class _param_default_value {
      @Test
      public void default_val_referencing_poly_value() {
        var code = """
            Int myFunc(
              [Int] param1 =
                empty)
                = 7;
            [A] empty = [];
            """;
        var empty = valueS(5, "empty", orderS(5, varA()));
        var defaultValue = valueS(2, "myFunc:param1", monoizeS(3, varMap(varA(), varA()), empty));
        var params = nlist(itemS(2, arrayTS(intTS()), "param1", defaultValue));
        var func = funcS(1, intTS(), "myFunc", params, intS(4, 7));
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(func);
      }

      @Test
      public void default_val_for_generic_param_referencing_poly_value() {
        var code = """
            Int myFunc(
              [B] param1 =
                empty)
                = 7;
            [A] empty = [];
            """;
        var empty = valueS(5, "empty", orderS(5, varA()));
        var defaultValue = valueS(2, "myFunc:param1", monoizeS(3, varMap(varA(), varA()), empty));
        var params = nlist(itemSPoly(2, arrayTS(varB()), "param1", Optional.of(defaultValue)));
        var func = funcS(1, intTS(), "myFunc", params, intS(4, 7));
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(func);
      }

      @Test
      public void default_value_using_literal() {
        var code = """
            Int myFunc(
              Int param1 =
                11)
                = 7;
            """;
        var defaultValue = valueS(2, "myFunc:param1", intS(3, 11));
        var params = nlist(itemS(2, intTS(), "param1", defaultValue));
        var func = funcS(1, intTS(), "myFunc", params, intS(4, 7));
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(func);
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
          .containsEvaluable(valueS(1, blobTS(), "result", blobS(2, 7)));
    }

    @Test
    public void with_operator() {
      module("""
          result =
          ([
            0x07,
            0x08
          ]);
          """)
          .loadsWithSuccess()
          .containsEvaluable(valueS(
              1, arrayTS(blobTS()), "result", orderS(2, blobTS(), blobS(3, 7), blobS(4, 8))));
    }
  }
}
