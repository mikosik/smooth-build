package org.smoothbuild.parse.component;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nlist;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.define.CallS;
import org.smoothbuild.lang.define.DefValS;
import org.smoothbuild.lang.define.ExprS;
import org.smoothbuild.lang.define.MonoRefableS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.util.collect.Lists;

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
          .containsRefable(polyDefValS(1, blobTS(), "result", blobS(2, 7)));
    }

    @Test
    public void int_literal() {
      module("""
          result =
            123;
          """)
          .loadsWithSuccess()
          .containsRefable(polyDefValS(1, intTS(), "result", intS(2, 123)));
    }

    @Test
    public void string_literal() {
      module("""
          result =
            "abc";
          """)
          .loadsWithSuccess()
          .containsRefable(polyDefValS(1, stringTS(), "result", stringS(2, "abc")));
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
          var polyVal = polyByteValS(4, varA(), "polyVal");
          var defArgBodyMapped = monoizeS(1, varMap(varA(), intTS()), polyVal);
          test_default_arg("polyVal", defArgBodyMapped);
        }

        @Test
        public void with_reference_to_poly_func() {
          var polyVal = polyByteFuncS(6, varA(), "polyFunc", nlist());
          var defArgBodyMapped = callS(1, intTS(), monoizeS(1, varMap(varA(), intTS()), polyVal));
          test_default_arg("polyFunc()", defArgBodyMapped);
        }

        @Test
        public void with_reference_to_int() {
          var defArgBodyMapped = intS(1, 7);
          test_default_arg("7", defArgBodyMapped);
        }

        private void test_default_arg(String bodyCode, ExprS defArg) {
          var code = """
            B myFunc(B b = $$$) = b;
            Int result = myFunc();
            @Bytecode("impl")
            A polyVal;
            @Bytecode("impl")
            A polyFunc();
            
            """.replace("$$$", bodyCode);

          var polyVal = polyByteValS(4, varA(), "polyVal");
          var defArgBody = monoizeS(1, varMap(varA(), varB()), polyVal);
          var params = nlist(itemS(1, varB(), "b", defArgBody));
          var func = polyDefFuncS(1, "myFunc", params, paramRefS(1, varB(), "b"));
          var monoizedFunc = monoizeS(2, varMap(varB(), intTS()), func);
          MonoRefableS result = module(code)
              .loadsWithSuccess()
              .getModAsDefinitions().refables().get("result").mono();
          ExprS actualDefArg = ((CallS) ((DefValS) result).body()).args().get(0);
          assertThat(actualDefArg)
              .isEqualTo(defArg);
        }
      }

      @Test
      public void with_func_reference() {
        module("""
          Int myReturnInt() = 3;
          result = myReturnInt();
          """)
            .loadsWithSuccess()
            .containsRefable(polyDefValS(2, intTS(), "result",
                callS(2, intTS(), returnIntFuncS())));
      }

      @Test
      public void with_func_reference_and_arg() {
        module("""
          Int myIntId(Int i) = i;
          result = myIntId(3);
          """)
            .loadsWithSuccess()
            .containsRefable(polyDefValS(2, intTS(), "result",
                callS(2, intTS(), intIdFuncS(), intS(2, 3))));
      }

      @Test
      public void with_func_reference_and_named_arg() {
        module("""
          Int myIntId(Int i) = i;
          result = myIntId(i=
            7);
          """)
            .loadsWithSuccess()
            .containsRefable(polyDefValS(2, intTS(), "result",
                callS(2, intTS(), intIdFuncS(), intS(3, 7))));
      }

      @Test
      public void with_value_reference() {
        module("""
          Int myReturnInt() = 3;
          Int() myValue = myReturnInt;
          result = myValue();
          """)
            .loadsWithSuccess()
            .containsRefable(
                polyDefValS(3, intTS(), "result",
                    callS(3, intTS(),
                        defValS(2,
                            funcTS(intTS(), Lists.<TypeS>list()), "myValue", returnIntFuncS()))));
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
            .containsRefable(
                polyDefValS(3, intTS(), "result",
                    callS(3, intTS(),
                        defValS(2, funcTS(intTS(), list(intTS())), "myValue", intIdFuncS()),
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
            .containsRefable(ctor);
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
            .containsRefable(polyDefValS(4, struct, "result",
                callS(4, struct, syntCtorS(1, struct), stringS(5, "aaa"))));
      }

      @Test
      public void with_param_reference() {
        module("""
          result(String() f) = f();
          """)
            .loadsWithSuccess()
            .containsRefable(polyDefFuncS(1, stringTS(), "result",
                callS(1, stringTS(), paramRefS(funcTS(stringTS()), "f")), nlist(itemS(1,
                    funcTS(stringTS()), "f"))));
      }

      @Test
      public void with_param_reference_and_arg() {
        module("""
          result(String(Blob) f) = f(0x09);
          """)
            .loadsWithSuccess()
            .containsRefable(polyDefFuncS(1, stringTS(), "result",
                callS(1, stringTS(), paramRefS(funcTS(stringTS(), list(blobTS())), "f"), blobS(1, 9)),
                nlist(itemS(1, funcTS(stringTS(), list(blobTS())), "f"))));
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
            .containsRefable(polyDefValS(2, stringTS(), "result",
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
            .containsRefable(polyDefValS(2, arrayTS(intTS()), "result",
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
            .containsRefable(polyDefValS(2, funcTS(stringTS()), "result",
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
            .containsRefable(polyDefValS(
                2, funcTS(intTS(), list(intTS())),
                "result",
                monoizeS(3, varMap(varA(), intTS()),
                    polyDefFuncS(1, "myId", nlist(itemS(varA(), "a")), paramRefS(1, varA(), "a")))));
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
            .containsRefable(polyDefValS(2, funcTS(structT), "result", syntCtorS(1, structT)));
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
          .containsRefable(polyDefValS(
              1, arrayTS(blobTS()), "result", orderS(2, blobTS(), blobS(3, 7), blobS(4, 8))));
    }

    @Test
    public void param_ref() {
      module("""
          Blob myFunc(Blob param1)
            = param1;
          """)
          .loadsWithSuccess()
          .containsRefable(polyDefFuncS(1, blobTS(), "myFunc",
              paramRefS(2, blobTS(), "param1"), nlist(itemS(1, blobTS(), "param1"))));
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
          .containsRefable(polyDefValS(6, stringTS(), "result",
              selectS(7, stringTS(),
                  callS(6, myStruct, natFuncS(2, myStruct, "getStruct", nlist(), natAnnS())),
                  "field")));
    }
  }

  @Nested
  class _definition_of {
    @Nested
    class _value {
      @Test
      public void def_value() {
        var code = """
          Blob myValue =
            0x07;
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefable(polyDefValS(1, blobTS(), "myValue", blobS(2, 7)));
      }

      @Test
      public void bytecode_value() {
        var code = """
          @Bytecode("impl")
          Blob myValue;
          """;
        module(code)
            .loadsWithSuccess()
            .containsRefable(polyByteValS(2, blobTS(), "myValue"));
      }
    }

    @Nested
    class _func {
      @Test
      public void def_func() {
        module("""
          Blob myFunc() =
            0x07;
          """)
            .loadsWithSuccess()
            .containsRefable(polyDefFuncS(1, blobTS(), "myFunc", blobS(2, 7), nlist()));
      }

      @Test
      public void def_func_with_param() {
        module("""
          String myFunc(
            Blob param1)
            = "abc";
          """)
            .loadsWithSuccess()
            .containsRefable(polyDefFuncS(1, stringTS(), "myFunc", stringS(3, "abc"),
                nlist(itemS(2, blobTS(), "param1"))));
      }

      @Test
      public void def_func_with_param_with_default_arg() {
        module("""
          String myFunc(
            Blob param1 =
              0x07)
              = "abc";
          """)
            .loadsWithSuccess()
            .containsRefable(polyDefFuncS(1, stringTS(), "myFunc", stringS(4, "abc"),
                nlist(itemS(2, blobTS(), "param1", blobS(3, 7)))));
      }

      @Test
      public void native_impure_func() {
        module("""
          @NativeImpure("Impl.met")
          String myFunc();
          """)
            .loadsWithSuccess()
            .containsRefable(polyNatFuncS(
                2, stringTS(), "myFunc", nlist(), natAnnS(1, stringS(1, "Impl.met"), false)));
      }

      @Test
      public void native_pure_func() {
        module("""
          @Native("Impl.met")
          String myFunc();
          """)
            .loadsWithSuccess()
            .containsRefable(polyNatFuncS(
                2, stringTS(), "myFunc", nlist(), natAnnS(1, stringS(1, "Impl.met"), true)));
      }

      @Test
      public void native_pure_func_with_default_argument() {
        module("""
          @Native("Impl.met")
          String myFunc(
            Blob p1 =
              0x07);
          """)
            .loadsWithSuccess()
            .containsRefable(
                polyNatFuncS(2, stringTS(), "myFunc", nlist(itemS(3, blobTS(), "p1", blobS(4, 7))),
                    natAnnS(1, stringS(1, "Impl.met"), true)));
      }

      @Test
      public void bytecode_func() {
        module("""
          @Bytecode("impl")
          String myFunc();
          """)
            .loadsWithSuccess()
            .containsRefable(polyByteFuncS(2, stringTS(), "myFunc", nlist()));
      }

      @Test
      public void bytecode_func_with_default_argument() {
        module("""
          @Bytecode("Impl.met")
          String myFunc(
            Blob param1 =
              0x07);
          """)
            .loadsWithSuccess()
            .containsRefable(
                polyByteFuncS(2, "Impl.met", stringTS(), "myFunc",
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
    class _default_arg {
      @Test
      public void default_arg_referencing_poly_val() {
        var code = """
            Int myFunc(
              [Int] param1 =
                empty)
                = 7;
            [A] empty = [];
            """;
        var defaultArg = monoizeS(
            3, varMap(varA(), intTS()), polyDefValS(5, "empty", orderS(5, varA())));
        var params = nlist(itemS(2, arrayTS(intTS()), "param1", defaultArg));
        var func = polyDefFuncS(1, intTS(), "myFunc", intS(4, 7), params);
        module(code)
            .loadsWithSuccess()
            .containsRefable(func);
      }

      @Test
      public void default_arg_for_generic_param_referencing_poly_val() {
        var code = """
            Int myFunc(
              [B] param1 =
                empty)
                = 7;
            [A] empty = [];
            """;
        var defaultArg = monoizeS(
            3, varMap(varA(), varB()), polyDefValS(5, "empty", orderS(5, varA())));
        var params = nlist(itemS(2, arrayTS(varB()), "param1", defaultArg));
        var func = polyDefFuncS(1, intTS(), "myFunc", intS(4, 7), params);
        module(code)
            .loadsWithSuccess()
            .containsRefable(func);
      }

      @Test
      public void default_arg_using_literal() {
        var code = """
            Int myFunc(
              Int param1 =
                11)
                = 7;
            """;
        var defaultArg = intS(3, 11);
        var params = nlist(itemS(2, intTS(), "param1", defaultArg));
        var func = polyDefFuncS(1, intTS(), "myFunc", intS(4, 7), params);
        module(code)
            .loadsWithSuccess()
            .containsRefable(func);
      }
    }
  }
}
