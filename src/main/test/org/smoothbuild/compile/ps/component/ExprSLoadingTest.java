package org.smoothbuild.compile.ps.component;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.NList.nlist;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compile.lang.define.CallS;
import org.smoothbuild.compile.lang.define.DefValueS;
import org.smoothbuild.compile.lang.define.EvaluableS;
import org.smoothbuild.compile.lang.define.ExprS;
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
          .containsEvaluable(defValS(1, blobTS(), "result", blobS(2, 7)));
    }

    @Test
    public void int_literal() {
      module("""
          result =
            123;
          """)
          .loadsWithSuccess()
          .containsEvaluable(defValS(1, intTS(), "result", intS(2, 123)));
    }

    @Test
    public void string_literal() {
      module("""
          result =
            "abc";
          """)
          .loadsWithSuccess()
          .containsEvaluable(defValS(1, stringTS(), "result", stringS(2, "abc")));
    }
  }

  @Nested
  class _operator {
    @Nested
    class _call {
      @Nested
      class _with_default_arg {
        @Test
        public void with_reference_to_poly_val() {
          var polyVal = byteValS(4, varA(), "polyVal");
          var polyRef = monoizeS(1, varMap(varA(), varA()), polyVal);
          var arg = monoizeS(2, varMap(varA(), intTS()), defValS(1, "myFunc:b", polyRef));
          test_default_arg("polyVal", arg);
        }

        @Test
        public void with_reference_to_poly_func() {
          var polyFunc = byteFuncS(6, varA(), "polyFunc", nlist());
          var polyRef = monoizeS(1, varMap(varA(), varA()), polyFunc);
          var paramDefaultValue = defValS("myFunc:b", callS(1, polyRef));
          var expected = monoizeS(2, varMap(varA(), intTS()), paramDefaultValue);
          test_default_arg("polyFunc()", expected);
        }

        @Test
        public void with_reference_to_int() {
          var paramDefaultValue = defValS("myFunc:b", intS(1, 7));
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
          ExprS actualDefArg = ((CallS) ((DefValueS) result).body()).args().get(0);
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
        var result = defValS(2, intTS(), "result", callS(2, monoizeS(2, myReturnInt)));
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
        var result = defValS(2, intTS(), "result", callS(2, monoizeS(2, myIntId), intS(2, 3)));
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
        var result = defValS(2, intTS(), "result", callS(2, monoizeS(2, myIntId), intS(3, 7)));
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
        var result = defValS(2, intTS(), "result", callS(3, monoizeS(3, myIntId), intS(2, 7)));
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
            .containsEvaluable(defValS(2, intTS(), "result",
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
        var myValue = defValS(2, funcTS(intTS()), "myValue", monoizeS(2, myReturnInt));
        var result = defValS(3, intTS(), "result", callS(3, monoizeS(3, myValue)));
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
        var myValue = defValS(2, funcTS(intTS(), intTS()), "myValue", monoizeS(2, myIntId));
        var result = defValS(3, intTS(), "result", callS(3, monoizeS(3, myValue), intS(4, 7)));
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(result);
      }

      @Test
      public void with_ctor_reference() {
        var struct = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
        var ctor = syntCtorS(1, struct, "myStruct");
        module("""
          MyStruct {
            String field
          }
          """)
            .loadsWithSuccess()
            .containsEvaluable(ctor);
      }

      @Test
      public void with_ctor_reference_and_arg() {
        var code = """
            MyStruct {
              String field
            }
            result = myStruct(
              "aaa");
            """;
        var struct = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
        var ctor = syntCtorS(1, struct);
        var resultBody = callS(4, monoizeS(4, ctor), stringS(5, "aaa"));
        var result = defValS(4, struct, "result", resultBody);
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
            .containsEvaluable(defFuncS(
                1, stringTS(), "result", nlist(itemS(1, funcTS(stringTS()), "f")),
                callS(1, paramRefS(funcTS(stringTS()), "f"))));
      }

      @Test
      public void with_ref_and_arg() {
        module("""
          result((Blob)->String f) = f(0x09);
          """)
            .loadsWithSuccess()
            .containsEvaluable(defFuncS(
                1, stringTS(), "result", nlist(itemS(1, funcTS(stringTS(), blobTS()), "f")),
                callS(1, paramRefS(funcTS(stringTS(), blobTS()), "f"), blobS(1, 9))));
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
        var myValue = defValS(1, stringTS(), "myValue", stringS("abc"));
        var result = defValS(2, stringTS(), "result", monoizeS(3, myValue));
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
            .containsEvaluable(defValS(2, arrayTS(intTS()), "result",
                monoizeS(3, varMap(varA(), intTS()),
                    defValS(1, arrayTS(varA()), "myValue", orderS(varA())))));
      }

      @Test
      public void to_mono_func() {
        var code = """
            String myFunc() = "abc";
            ()->String result =
              myFunc;
            """;
        var myFunc = defFuncS(1, "myFunc", nlist(), stringS("abc"));
        var result = defValS(2, funcTS(stringTS()), "result", monoizeS(3, myFunc));
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
        var myId = defFuncS(1, "myId", nlist(itemS(varA(), "a")), paramRefS(1, varA(), "a"));
        var resultBody = monoizeS(3, varMap(varA(), intTS()), myId);
        var result = defValS(2, funcTS(intTS(), intTS()), "result", resultBody);
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(result);
      }

      @Test
      public void to_ctor() {
        var code = """
            MyStruct {}
            ()->MyStruct result =
              myStruct;
            """;
        var structT = structTS("MyStruct", nlist());
        var myStruct = syntCtorS(1, structT);
        var result = defValS(2, funcTS(structT), "result", monoizeS(3, myStruct));
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
            .containsEvaluable(defValS(
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
            .containsEvaluable(defValS(
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
        var returnInt = defFuncS(1, "returnInt", nlist(), intS(1, 7));
        var orderS = orderS(3, monoizeS(4, returnInt));
        var expected = defValS(2, "result", orderS);
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
      var myFunc = defFuncS(1, blobTS(), "myFunc", nlist(itemS(1, blobTS(), "param1")), body);
      module(code)
          .loadsWithSuccess()
          .containsEvaluable(myFunc);
    }

    @Test
    public void select() {
      var code = """
          @Native("impl")
          MyStruct getStruct();
          MyStruct {
            String field,
          }
          result = getStruct()
            .field;
          """;
      var myStruct = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
      var getStruct = annFuncS(2, natAnnS(), myStruct, "getStruct", nlist());
      var resultBody = selectS(7, callS(6, monoizeS(6, getStruct)), "field");
      var result = defValS(6, stringTS(), "result", resultBody);
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
      public void def_mono_value() {
        var code = """
          Blob myValue =
            0x07;
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(defValS(1, blobTS(), "myValue", blobS(2, 7)));
      }

      @Test
      public void def_poly_value() {
        var code = """
          [A] myValue =
            [];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(defValS(1, arrayTS(varA()), "myValue", orderS(2, varA())));
      }

      @Test
      public void bytecode_mono_value() {
        var code = """
          @Bytecode("impl")
          Blob myValue;
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(byteValS(2, blobTS(), "myValue"));
      }

      @Test
      public void bytecode_poly_value() {
        var code = """
          @Bytecode("impl")
          A myValue;
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(byteValS(2, varA(), "myValue"));
      }
    }

    @Nested
    class _func {
      @Test
      public void def_mono_func() {
        module("""
          Blob myFunc() =
            0x07;
          """)
            .loadsWithSuccess()
            .containsEvaluable(defFuncS(1, blobTS(), "myFunc", nlist(), blobS(2, 7)));
      }

      @Test
      public void def_poly_func() {
        module("""
          [A] myFunc() =
            [];
          """)
            .loadsWithSuccess()
            .containsEvaluable(defFuncS(1, arrayTS(varA()), "myFunc", nlist(),
                orderS(2, varA())));
      }

      @Test
      public void def_mono_func_with_param() {
        module("""
          String myFunc(
            Blob param1)
            = "abc";
          """)
            .loadsWithSuccess()
            .containsEvaluable(
                defFuncS(1, stringTS(), "myFunc", nlist(itemS(2, blobTS(), "param1")),
                    stringS(3, "abc")));
      }

      @Test
      public void def_poly_func_with_param() {
        module("""
          A myFunc(
            A a)
            = a;
          """)
            .loadsWithSuccess()
            .containsEvaluable(
                defFuncS(1, varA(), "myFunc", nlist(itemS(2, varA(), "a")),
                    paramRefS(3, varA(), "a")));
      }

      @Test
      public void def_mono_func_with_param_with_default_value() {
        var code = """
            String myFunc(
              Blob param1 =
                0x07)
                = "abc";
            """;
        var params = nlist(itemS(2, blobTS(), "param1", defValS(2, "myFunc:param1", blobS(3, 7))));
        var myFunc = defFuncS(1, stringTS(), "myFunc", params, stringS(4, "abc"));
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(myFunc);
      }

      @Test
      public void def_poly_func_with_param_with_default_value() {
        var code = """
            A myFunc(
              A a =
                7)
                = a;
            """;
        var params = nlist(itemS(2, varA(), "a", defValS(2, "myFunc:a", intS(3, 7))));
        var myFunc = defFuncS(1, varA(), "myFunc", params, paramRefS(4, varA(), "a"));
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(myFunc);
      }

      @Test
      public void native_impure_mono_func() {
        module("""
          @NativeImpure("Impl.met")
          String myFunc();
          """)
            .loadsWithSuccess()
            .containsEvaluable(annFuncS(
                2, natAnnS(1, stringS(1, "Impl.met"), false), stringTS(), "myFunc", nlist()));
      }

      @Test
      public void native_impure_poly_func() {
        module("""
          @NativeImpure("Impl.met")
          A myFunc();
          """)
            .loadsWithSuccess()
            .containsEvaluable(annFuncS(
                2, natAnnS(1, stringS(1, "Impl.met"), false), varA(), "myFunc", nlist()));
      }

      @Test
      public void native_pure_mono_func() {
        module("""
          @Native("Impl.met")
          String myFunc();
          """)
            .loadsWithSuccess()
            .containsEvaluable(annFuncS(
                2, natAnnS(1, stringS(1, "Impl.met"), true), stringTS(), "myFunc", nlist()));
      }

      @Test
      public void native_pure_poly_func() {
        module("""
          @Native("Impl.met")
          A myFunc();
          """)
            .loadsWithSuccess()
            .containsEvaluable(annFuncS(
                2, natAnnS(1, stringS(1, "Impl.met"), true), varA(), "myFunc", nlist()));
      }

      @Test
      public void native_pure_mono_func_with_param_with_default_value() {
        var code = """
            @Native("Impl.met")
            String myFunc(
              Blob p1 =
                0x07);
            """;
        var params = nlist(itemS(3, blobTS(), "p1", defValS(3, "myFunc:p1", blobS(4, 7))));
        var ann = natAnnS(1, stringS(1, "Impl.met"), true);
        var myFunc = annFuncS(2, ann, stringTS(), "myFunc", params);
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(myFunc);
      }

      @Test
      public void native_pure_poly_func_with_param_with_default_value() {
        var code = """
            @Native("Impl.met")
            A myFunc(
              Blob p1 =
                0x07);
            """;
        var params = nlist(itemS(3, blobTS(), "p1", defValS(3, "myFunc:p1", blobS(4, 7))));
        var ann = natAnnS(1, stringS(1, "Impl.met"), true);
        var myFunc = annFuncS(2, ann, varA(), "myFunc", params);
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(myFunc);
      }

      @Test
      public void bytecode_mono_func() {
        module("""
          @Bytecode("impl")
          String myFunc();
          """)
            .loadsWithSuccess()
            .containsEvaluable(byteFuncS(2, stringTS(), "myFunc", nlist()));
      }

      @Test
      public void bytecode_poly_func() {
        module("""
          @Bytecode("impl")
          A myFunc();
          """)
            .loadsWithSuccess()
            .containsEvaluable(byteFuncS(2, varA(), "myFunc", nlist()));
      }

      @Test
      public void bytecode_mono_func_with_param_with_default_value() {
        var code = """
            @Bytecode("Impl.met")
            String myFunc(
              Blob param1 =
                0x07);
            """;
        var params = nlist(itemS(3, blobTS(), "param1", defValS(3, "myFunc:param1", blobS(4, 7))));
        var myFunc = byteFuncS(2, "Impl.met", stringTS(), "myFunc", params);
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(myFunc);
      }

      @Test
      public void bytecode_poly_func_with_param_with_default_value() {
        var code = """
            @Bytecode("Impl.met")
            A myFunc(
              Blob param1 =
                0x07);
            """;
        var params = nlist(itemS(3, blobTS(), "param1", defValS(3, "myFunc:param1", blobS(4, 7))));
        var myFunc = byteFuncS(2, "Impl.met", varA(), "myFunc", params);
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
          MyStruct {
          }
          """)
            .loadsWithSuccess()
            .containsType(structTS("MyStruct", nlist()));
      }

      @Test
      public void struct_type() {
        module("""
          MyStruct {
            String field
          }
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
        var empty = defValS(5, "empty", orderS(5, varA()));
        var defaultValue = defValS(2, "myFunc:param1", monoizeS(3, varMap(varA(), varA()), empty));
        var params = nlist(itemS(2, arrayTS(intTS()), "param1", defaultValue));
        var func = defFuncS(1, intTS(), "myFunc", params, intS(4, 7));
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
        var empty = defValS(5, "empty", orderS(5, varA()));
        var defaultValue = defValS(2, "myFunc:param1", monoizeS(3, varMap(varA(), varA()), empty));
        var params = nlist(itemSPoly(2, arrayTS(varB()), "param1", Optional.of(defaultValue)));
        var func = defFuncS(1, intTS(), "myFunc", params, intS(4, 7));
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
        var defaultValue = defValS(2, "myFunc:param1", intS(3, 11));
        var params = nlist(itemS(2, intTS(), "param1", defaultValue));
        var func = defFuncS(1, intTS(), "myFunc", params, intS(4, 7));
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
          .containsEvaluable(defValS(1, blobTS(), "result", blobS(2, 7)));
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
          .containsEvaluable(defValS(
              1, arrayTS(blobTS()), "result", orderS(2, blobTS(), blobS(3, 7), blobS(4, 8))));
    }
  }
}
