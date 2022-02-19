package org.smoothbuild.parse.component;

import static java.util.Optional.empty;
import static org.smoothbuild.testing.type.TestingTS.BLOB;
import static org.smoothbuild.testing.type.TestingTS.INT;
import static org.smoothbuild.testing.type.TestingTS.STRING;
import static org.smoothbuild.testing.type.TestingTS.a;
import static org.smoothbuild.testing.type.TestingTS.f;
import static org.smoothbuild.util.collect.NList.nList;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.define.ItemS;
import org.smoothbuild.testing.TestingContext;

public class ExprSLoadingTest extends TestingContext {
  @Test
  public void blob_literal_expression() {
    module("""
          result =
            0x07;
          """)
        .loadsWithSuccess()
        .containsEval(defValS(1, BLOB, "result", blobS(2, 7)));
  }

  @Test
  public void int_literal_expression() {
    module("""
          result =
            123;
          """)
        .loadsWithSuccess()
        .containsEval(defValS(1, INT, "result", intS(2, 123)));
  }

  @Test
  public void order_expression() {
    module("""
          result =
          [
            0x07,
            0x08
          ];
          """)
        .loadsWithSuccess()
        .containsEval(defValS(1, a(BLOB), "result",
            orderS(2, BLOB, blobS(3, 7), blobS(4, 8))));
  }

  @Nested
  class _call_expression {
    @Test
    public void with_func_reference() {
      module("""
          String myFunc() = "abc";
          result = myFunc();
          """)
          .loadsWithSuccess()
          .containsEval(defValS(2, STRING, "result",
              callS(2, STRING, topRefS(2, f(STRING), "myFunc"))));
    }

    @Test
    public void with_func_reference_and_arg() {
      module("""
          String myFunc(Blob b) = "abc";
          result = myFunc(
            0x07);
          """)
          .loadsWithSuccess()
          .containsEval(defValS(2, STRING, "result",
              callS(2, STRING, topRefS(2, f(STRING, BLOB), "myFunc"), blobS(3, 7))));
    }

    @Test
    public void with_func_reference_and_named_arg() {
      module("""
          String myFunc(Blob b) = "abc";
          result = myFunc(b=
            0x07);
          """)
          .loadsWithSuccess()
          .containsEval(defValS(2, STRING, "result",
              callS(2, STRING, topRefS(2, f(STRING, BLOB), "myFunc"), blobS(3, 7))));
    }

    @Test
    public void with_value_reference() {
      module("""
          @Native("Impl.met")
          String myFunc();
          String() myValue = myFunc;
          result = myValue();
          """)
          .loadsWithSuccess()
          .containsEval(
              defValS(4, STRING, "result",
                  callS(4, STRING, topRefS(4, f(STRING), "myValue"))));
    }

    @Test
    public void with_value_reference_and_arg() {
      module("""
          @Native("Impl.met")
          String myFunc(Blob blob);
          String(Blob) myValue = myFunc;
          result = myValue(
            0x07);
          """)
          .loadsWithSuccess()
          .containsEval(
              defValS(4, STRING, "result",
                  callS(4, STRING, topRefS(4, f(STRING, BLOB), "myValue"), blobS(5, 7))));
    }

    @Test
    public void with_ctor_reference() {
      var struct = structTS("MyStruct", nList(sigS(STRING, "field")));
      var params = struct.fields()
          .map(i -> new ItemS(i.type(), modPath(), i.nameSane(), empty(), loc(2)));
      var ctor = syntCtorS(1, funcTS(struct, params.list()), modPath(), "myStruct", params);
      module("""
          MyStruct {
            String field
          }
          """)
          .loadsWithSuccess()
          .containsEval(ctor);
    }

    @Test
    public void with_ctor_reference_and_arg() {
      var struct = structTS("MyStruct", nList(sigS(STRING, "field")));
      module("""
          MyStruct {
            String field
          }
          result = myStruct(
            "aaa");
          """)
          .loadsWithSuccess()
          .containsEval(defValS(4, struct, "result",
              callS(4, struct, topRefS(4, f(struct, STRING), "myStruct"), stringS(5, "aaa"))));
    }

    @Test
    public void with_param_reference() {
      module("""
          result(String() f) = f();
          """)
          .loadsWithSuccess()
          .containsEval(defFuncS(1, STRING, "result",
              callS(1, STRING, paramRefS(f(STRING), "f")), nList(itemS(1, f(STRING), "f"))));
    }

    @Test
    public void with_param_reference_and_arg() {
      module("""
          result(String(Blob) f) = f(0x09);
          """)
          .loadsWithSuccess()
          .containsEval(defFuncS(1, STRING, "result",
              callS(1, STRING, paramRefS(f(STRING, BLOB), "f"), blobS(1, 9)),
              nList(itemS(1, f(STRING, BLOB), "f"))));
    }
  }

  @Test
  public void param_reference_expression() {
    module("""
          Blob myFunc(Blob param1)
            = param1;
          """)
        .loadsWithSuccess()
        .containsEval(defFuncS(
            1, BLOB, "myFunc", paramRefS(2, BLOB, "param1"), nList(itemS(1, BLOB, "param1"))));
  }

  @Test
  public void select_expression() {
    var myStruct = structTS("MyStruct", nList(sigS(STRING, "field")));
    module("""
          MyStruct {
            String field,
          }
          MyStruct struct = myStruct("abc");
          result = struct
            .field;
          """)
        .loadsWithSuccess()
        .containsEval(defValS(5, STRING, "result",
            selectS(6, STRING, topRefS(5, myStruct, "struct"), "field")));
  }

  @Nested
  class _reference_expression {
    @Test
    public void to_value() {
      module("""
          String myValue = "abc";
          String result =
            myValue;
          """)
          .loadsWithSuccess()
          .containsEval(
              defValS(2, STRING, "result", topRefS(3, STRING, "myValue")));
    }

    @Test
    public void to_func() {
      module("""
          String myFunc() = "abc";
          String() result =
            myFunc;
          """)
          .loadsWithSuccess()
          .containsEval(defValS(2, f(STRING), "result", topRefS(3, f(STRING), "myFunc")));
    }

    @Test
    public void to_ctor() {
      var structT = structTS("MyStruct", nList());
      module("""
          MyStruct {}
          MyStruct() result =
            myStruct;
          """)
          .loadsWithSuccess()
          .containsEval(defValS(2, f(structT), "result", topRefS(3, f(structT), "myStruct")));
    }
  }

  @Test
  public void string_literal_expression() {
    module("""
          result =
            "abc";
          """)
        .loadsWithSuccess()
        .containsEval(defValS(1, STRING, "result", stringS(2, "abc")));
  }

  @Nested
  class _definition_of {
    @Test
    public void def_value() {
      var code = """
          Blob myValue =
            0x07;
          """;
      module(code)
          .loadsWithSuccess()
          .containsEval(defValS(1, BLOB, "myValue", blobS(2, 7)));
    }

    @Test
    public void bytecode_value() {
      var code = """
          @Bytecode("implementation")
          Blob myValue;
          """;
      module(code)
          .loadsWithSuccess()
          .containsEval(annValS(2, bytecodeS(1, "implementation"), BLOB, "myValue"));
    }

    @Test
    public void def_func() {
      module("""
          Blob myFunc() =
            0x07;
          """)
          .loadsWithSuccess()
          .containsEval(defFuncS(1, BLOB, "myFunc", blobS(2, 7), nList()));
    }

    @Test
    public void def_func_with_param() {
      module("""
          String myFunc(
            Blob param1)
            = "abc";
          """)
          .loadsWithSuccess()
          .containsEval(defFuncS(1, STRING, "myFunc", stringS(3, "abc"),
              nList(itemS(2, BLOB, "param1"))));
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
          .containsEval(defFuncS(1, STRING, "myFunc", stringS(4, "abc"),
              nList(itemS(2, BLOB, "param1", blobS(3, 7)))));
    }

    @Test
    public void native_impure_func() {
      module("""
          @NativeImpure("Impl.met")
          String myFunc();
          """)
          .loadsWithSuccess()
          .containsEval(
              natFuncS(2, STRING, "myFunc", nList(), nativeS(1, stringS(1, "Impl.met"), false)));
    }

    @Test
    public void native_pure_func() {
      module("""
          @Native("Impl.met")
          String myFunc();
          """)
          .loadsWithSuccess()
          .containsEval(
              natFuncS(2, STRING, "myFunc", nList(), nativeS(1, stringS(1, "Impl.met"), true)));
    }

    @Test
    public void native_pure_func_with_default_argument() {
      module("""
          @Native("Impl.met")
          String myFunc(
            Blob param1 =
              0x07);
          """)
          .loadsWithSuccess()
          .containsEval(
              natFuncS(2, STRING, "myFunc", nList(itemS(3, BLOB, "param1", blobS(4, 7))),
                  nativeS(1, stringS(1, "Impl.met"), true)));
    }

    @Test
    public void bytecode_func() {
      module("""
          @Bytecode("Impl.met")
          String myFunc();
          """)
          .loadsWithSuccess()
          .containsEval(
              byteFuncS(2, bytecodeS(stringS(1, "Impl.met"), loc(1)), STRING, "myFunc", nList()));
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
          .containsEval(
              byteFuncS(2, bytecodeS(1, stringS(1, "Impl.met")), STRING, "myFunc",
                  nList(itemS(3, BLOB, "param1", blobS(4, 7)))));
    }

    @Test
    public void struct_type() {
      module("""
          MyStruct {
            String field
          }
          """)
          .loadsWithSuccess()
          .containsType(structTS("MyStruct", nList(sigS(STRING, "field"))));
    }
  }
}