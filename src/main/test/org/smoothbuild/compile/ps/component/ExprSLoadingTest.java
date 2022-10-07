package org.smoothbuild.compile.ps.component;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.NList.nlist;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compile.lang.define.CallS;
import org.smoothbuild.compile.lang.define.DefValS;
import org.smoothbuild.compile.lang.define.EvaluableS;
import org.smoothbuild.compile.lang.define.ExprS;
import org.smoothbuild.compile.lang.define.PolyEvaluableS;
import org.smoothbuild.compile.lang.define.UnnamedPolyValS;
import org.smoothbuild.compile.lang.define.UnnamedValS;
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
          .containsEvaluable(polyDefValS(1, blobTS(), "result", blobS(2, 7)));
    }

    @Test
    public void int_literal() {
      module("""
          result =
            123;
          """)
          .loadsWithSuccess()
          .containsEvaluable(polyDefValS(1, intTS(), "result", intS(2, 123)));
    }

    @Test
    public void string_literal() {
      module("""
          result =
            "abc";
          """)
          .loadsWithSuccess()
          .containsEvaluable(polyDefValS(1, stringTS(), "result", stringS(2, "abc")));
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
          var paramDefaultVal = polyByteValS(4, varA(), "polyVal");
          var monoizedParamBody = monoizeS(2, varMap(varA(), intTS()), paramDefaultVal);
          test_default_arg("polyVal", monoizedParamBody);
        }

        @Test
        public void with_reference_to_poly_func() {
          var polyFunc = polyByteFuncS(6, varA(), "polyFunc", nlist());
          var monoizedFunc = monoizeS(1, varMap(varA(), varA()), polyFunc);
          var paramDefaultVal = new UnnamedPolyValS(callS(1, varA(), monoizedFunc));
          var expected = monoizeS(2, varMap(varA(), intTS()), paramDefaultVal);
          test_default_arg("polyFunc()", expected);
        }

        @Test
        public void with_reference_to_int() {
          var paramDefaultVal = new UnnamedValS(intS(1, 7));
          test_default_arg("7", paramDefaultVal);
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
              .getModuleAsDefinitions().evaluables().get("result").mono();
          ExprS actualDefArg = ((CallS) ((DefValS) result).body()).args().get(0);
          assertThat(actualDefArg)
              .isEqualTo(expected);
        }
      }

      @Test
      public void with_mono_func_reference() {
        module("""
          Int myReturnInt() = 3;
          result = myReturnInt();
          """)
            .loadsWithSuccess()
            .containsEvaluable(polyDefValS(2, intTS(), "result",
                callS(2, intTS(), returnIntFuncS())));
      }

      @Test
      public void with_mono_func_reference_and_arg() {
        module("""
          Int myIntId(Int i) = i;
          result = myIntId(3);
          """)
            .loadsWithSuccess()
            .containsEvaluable(polyDefValS(2, intTS(), "result",
                callS(2, intTS(), intIdFuncS(), intS(2, 3))));
      }

      @Test
      public void with_mono_func_reference_and_named_arg() {
        module("""
          Int myIntId(Int i) = i;
          result = myIntId(i=
            7);
          """)
            .loadsWithSuccess()
            .containsEvaluable(polyDefValS(2, intTS(), "result",
                callS(2, intTS(), intIdFuncS(), intS(3, 7))));
      }

      @Test
      public void with_poly_func_reference_and_arg() {
        module("""
          A myId(A a) = a;
          result = myId(7);
          """)
            .loadsWithSuccess()
            .containsEvaluable(polyDefValS(2, intTS(), "result",
                callS(2, intTS(),
                    monoizeS(2, varMap(varA(), intTS()), idFuncS()),
                    intS(2, 7))));
      }

      @Test
      public void with_value_reference() {
        module("""
          Int myReturnInt() = 3;
          Int() myValue = myReturnInt;
          result = myValue();
          """)
            .loadsWithSuccess()
            .containsEvaluable(
                polyDefValS(3, intTS(), "result",
                    callS(3, intTS(),
                        defValS(2, funcTS(intTS()), "myValue", returnIntFuncS()))));
      }

      @Test
      public void with_value_reference_and_arg() {
        module("""
          Int myIntId(Int i) = i;
          Int(Int) myValue = myIntId;
          result = myValue(
            7);
          """)
            .loadsWithSuccess()
            .containsEvaluable(
                polyDefValS(3, intTS(), "result",
                    callS(3, intTS(),
                        defValS(2, funcTS(intTS(), intTS()), "myValue", intIdFuncS()),
                        intS(4, 7))));
      }

      @Test
      public void with_ctor_reference() {
        var struct = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
        var ctor = polySyntCtorS(1, struct, "myStruct");
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
        var struct = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
        module("""
          MyStruct {
            String field
          }
          result = myStruct(
            "aaa");
          """)
            .loadsWithSuccess()
            .containsEvaluable(polyDefValS(4, struct, "result",
                callS(4, struct, syntCtorS(1, struct), stringS(5, "aaa"))));
      }

      @Test
      public void with_ref() {
        module("""
          result(String() f) = f();
          """)
            .loadsWithSuccess()
            .containsEvaluable(polyDefFuncS(
                1, stringTS(), "result", nlist(itemS(1, funcTS(stringTS()), "f")),
                callS(1, stringTS(), refS(funcTS(stringTS()), "f"))));
      }

      @Test
      public void with_ref_and_arg() {
        module("""
          result(String(Blob) f) = f(0x09);
          """)
            .loadsWithSuccess()
            .containsEvaluable(polyDefFuncS(
                1, stringTS(), "result", nlist(itemS(1, funcTS(stringTS(), blobTS()), "f")),
                callS(1, stringTS(), refS(funcTS(stringTS(), blobTS()), "f"), blobS(1, 9))));
      }
    }
    @Nested
    class _reference {
      @Test
      public void to_mono_value() {
        module("""
          String myValue = "abc";
          String result =
            myValue;
          """)
            .loadsWithSuccess()
            .containsEvaluable(polyDefValS(2, stringTS(), "result",
                defValS(1, stringTS(), "myValue", stringS("abc"))));
      }

      @Test
      public void to_poly_value() {
        module("""
          [A] myValue = [];
          [Int] result =
            myValue;
          """)
            .loadsWithSuccess()
            .containsEvaluable(polyDefValS(2, arrayTS(intTS()), "result",
                monoizeS(3, varMap(varA(), intTS()),
                    polyDefValS(1, arrayTS(varA()), "myValue", orderS(varA())))));
      }

      @Test
      public void to_mono_func() {
        module("""
          String myFunc() = "abc";
          String() result =
            myFunc;
          """)
            .loadsWithSuccess()
            .containsEvaluable(polyDefValS(2, funcTS(stringTS()), "result",
                defFuncS(1, "myFunc", nlist(), stringS("abc"))));
      }

      @Test
      public void to_poly_func() {
        module("""
          A myId(A a) = a;
          Int(Int) result =
            myId;
          """)
            .loadsWithSuccess()
            .containsEvaluable(polyDefValS(2, funcTS(intTS(), intTS()), "result",
                monoizeS(3, varMap(varA(), intTS()),
                    polyDefFuncS(1, "myId", nlist(itemS(varA(), "a")), refS(1, varA(), "a")))));
      }

      @Test
      public void to_ctor() {
        var structT = structTS("MyStruct", nlist());
        module("""
          MyStruct {}
          MyStruct() result =
            myStruct;
          """)
            .loadsWithSuccess()
            .containsEvaluable(polyDefValS(2, funcTS(structT), "result", syntCtorS(1, structT)));
      }
    }

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
          .containsEvaluable(polyDefValS(
              1, arrayTS(blobTS()), "result", orderS(2, blobTS(), blobS(3, 7), blobS(4, 8))));
    }

    @Test
    public void ref() {
      module("""
          Blob myFunc(Blob param1)
            = param1;
          """)
          .loadsWithSuccess()
          .containsEvaluable(polyDefFuncS(1, blobTS(), "myFunc", nlist(itemS(1, blobTS(), "param1")),
              refS(2, blobTS(), "param1")));
    }

    @Test
    public void select() {
      var myStruct = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
      module("""
          @Native("impl")
          MyStruct getStruct();
          MyStruct {
            String field,
          }
          result = getStruct()
            .field;
          """)
          .loadsWithSuccess()
          .containsEvaluable(polyDefValS(6, stringTS(), "result",
              selectS(7,
                  callS(6, myStruct, natFuncS(2, myStruct, "getStruct", nlist(), natAnnS())),
                  "field")));
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
            .containsEvaluable(polyDefValS(1, blobTS(), "myValue", blobS(2, 7)));
      }

      @Test
      public void def_poly_value() {
        var code = """
          [A] myValue =
            [];
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(polyDefValS(1, arrayTS(varA()), "myValue", orderS(2, varA())));
      }

      @Test
      public void bytecode_mono_value() {
        var code = """
          @Bytecode("impl")
          Blob myValue;
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(polyByteValS(2, blobTS(), "myValue"));
      }

      @Test
      public void bytecode_poly_value() {
        var code = """
          @Bytecode("impl")
          A myValue;
          """;
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(polyByteValS(2, varA(), "myValue"));
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
            .containsEvaluable(polyDefFuncS(1, blobTS(), "myFunc", nlist(), blobS(2, 7)));
      }

      @Test
      public void def_poly_func() {
        module("""
          [A] myFunc() =
            [];
          """)
            .loadsWithSuccess()
            .containsEvaluable(polyDefFuncS(1, arrayTS(varA()), "myFunc", nlist(),
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
                polyDefFuncS(1, stringTS(), "myFunc", nlist(itemS(2, blobTS(), "param1")),
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
                polyDefFuncS(1, varA(), "myFunc", nlist(itemS(2, varA(), "a")),
                    refS(3, varA(), "a")));
      }

      @Test
      public void def_mono_func_with_param_with_default_val() {
        module("""
          String myFunc(
            Blob param1 =
              0x07)
              = "abc";
          """)
            .loadsWithSuccess()
            .containsEvaluable(polyDefFuncS(1, stringTS(), "myFunc",
                nlist(itemS(2, blobTS(), "param1", blobS(3, 7))), stringS(4, "abc")));
      }

      @Test
      public void def_poly_func_with_param_with_default_val() {
        module("""
          A myFunc(
            A a =
              7)
              = a;
          """)
            .loadsWithSuccess()
            .containsEvaluable(
                polyDefFuncS(1, varA(), "myFunc", nlist(itemS(2, varA(), "a", intS(3, 7))),
                    refS(4, varA(), "a")));
      }

      @Test
      public void native_impure_mono_func() {
        module("""
          @NativeImpure("Impl.met")
          String myFunc();
          """)
            .loadsWithSuccess()
            .containsEvaluable(polyNatFuncS(
                2, stringTS(), "myFunc", nlist(), natAnnS(1, stringS(1, "Impl.met"), false)));
      }

      @Test
      public void native_impure_poly_func() {
        module("""
          @NativeImpure("Impl.met")
          A myFunc();
          """)
            .loadsWithSuccess()
            .containsEvaluable(polyNatFuncS(
                2, varA(), "myFunc", nlist(), natAnnS(1, stringS(1, "Impl.met"), false)));
      }

      @Test
      public void native_pure_mono_func() {
        module("""
          @Native("Impl.met")
          String myFunc();
          """)
            .loadsWithSuccess()
            .containsEvaluable(polyNatFuncS(
                2, stringTS(), "myFunc", nlist(), natAnnS(1, stringS(1, "Impl.met"), true)));
      }

      @Test
      public void native_pure_poly_func() {
        module("""
          @Native("Impl.met")
          A myFunc();
          """)
            .loadsWithSuccess()
            .containsEvaluable(polyNatFuncS(
                2, varA(), "myFunc", nlist(), natAnnS(1, stringS(1, "Impl.met"), true)));
      }

      @Test
      public void native_pure_mono_func_with_param_with_default_val() {
        module("""
          @Native("Impl.met")
          String myFunc(
            Blob p1 =
              0x07);
          """)
            .loadsWithSuccess()
            .containsEvaluable(
                polyNatFuncS(2, stringTS(), "myFunc", nlist(itemS(3, blobTS(), "p1", blobS(4, 7))),
                    natAnnS(1, stringS(1, "Impl.met"), true)));
      }

      @Test
      public void native_pure_poly_func_with_param_with_default_val() {
        module("""
          @Native("Impl.met")
          A myFunc(
            Blob p1 =
              0x07);
          """)
            .loadsWithSuccess()
            .containsEvaluable(
                polyNatFuncS(2, varA(), "myFunc", nlist(itemS(3, blobTS(), "p1", blobS(4, 7))),
                    natAnnS(1, stringS(1, "Impl.met"), true)));
      }

      @Test
      public void bytecode_mono_func() {
        module("""
          @Bytecode("impl")
          String myFunc();
          """)
            .loadsWithSuccess()
            .containsEvaluable(polyByteFuncS(2, stringTS(), "myFunc", nlist()));
      }

      @Test
      public void bytecode_poly_func() {
        module("""
          @Bytecode("impl")
          A myFunc();
          """)
            .loadsWithSuccess()
            .containsEvaluable(polyByteFuncS(2, varA(), "myFunc", nlist()));
      }

      @Test
      public void bytecode_mono_func_with_param_with_default_val() {
        module("""
          @Bytecode("Impl.met")
          String myFunc(
            Blob param1 =
              0x07);
          """)
            .loadsWithSuccess()
            .containsEvaluable(
                polyByteFuncS(2, "Impl.met", stringTS(), "myFunc",
                    nlist(itemS(3, blobTS(), "param1", blobS(4, 7)))));
      }

      @Test
      public void bytecode_poly_func_with_param_with_default_val() {
        module("""
          @Bytecode("Impl.met")
          A myFunc(
            Blob param1 =
              0x07);
          """)
            .loadsWithSuccess()
            .containsEvaluable(
                polyByteFuncS(2, "Impl.met", varA(), "myFunc",
                    nlist(itemS(3, blobTS(), "param1", blobS(4, 7)))));
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
    class _param_default_val {
      @Test
      public void default_val_referencing_poly_val() {
        var code = """
            Int myFunc(
              [Int] param1 =
                empty)
                = 7;
            [A] empty = [];
            """;
        var defaultVal = Optional.<PolyEvaluableS>of(polyDefValS(5, "empty", orderS(5, varA())));
        var params = nlist(itemSPoly(2, arrayTS(intTS()), "param1", defaultVal));
        var func = polyDefFuncS(1, intTS(), "myFunc", params, intS(4, 7));
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(func);
      }

      @Test
      public void default_val_for_generic_param_referencing_poly_val() {
        var code = """
            Int myFunc(
              [B] param1 =
                empty)
                = 7;
            [A] empty = [];
            """;
        var defaultVal = Optional.<PolyEvaluableS>of(polyDefValS(5, "empty", orderS(5, varA())));
        var params = nlist(itemSPoly(2, arrayTS(varB()), "param1", defaultVal));
        var func = polyDefFuncS(1, intTS(), "myFunc", params, intS(4, 7));
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(func);
      }

      @Test
      public void default_val_using_literal() {
        var code = """
            Int myFunc(
              Int param1 =
                11)
                = 7;
            """;
        var defaultVal = intS(3, 11);
        var params = nlist(itemS(2, intTS(), "param1", defaultVal));
        var func = polyDefFuncS(1, intTS(), "myFunc", params, intS(4, 7));
        module(code)
            .loadsWithSuccess()
            .containsEvaluable(func);
      }
    }
  }
}
