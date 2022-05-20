package org.smoothbuild.parse.component;

import static java.util.Optional.empty;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.define.ItemS;
import org.smoothbuild.testing.TestingContext;

public class ObjSLoadingTest extends TestingContext {
  @Test
  public void blob_literal_expression() {
    module("""
          result =
            0x07;
          """)
        .loadsWithSuccess()
        .containsTopRefable(defValS(1, blobTS(), "result", blobS(2, 7)));
  }

  @Test
  public void int_literal_expression() {
    module("""
          result =
            123;
          """)
        .loadsWithSuccess()
        .containsTopRefable(defValS(1, intTS(), "result", intS(2, 123)));
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
        .containsTopRefable(defValS(1, arrayTS(blobTS()), "result",
            orderS(2, blobTS(), blobS(3, 7), blobS(4, 8))));
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
          .containsTopRefable(defValS(2, stringTS(), "result",
              callS(2, stringTS(), refS(2, funcTS(stringTS()), "myFunc"))));
    }

    @Test
    public void with_func_reference_and_arg() {
      module("""
          String myFunc(Blob b) = "abc";
          result = myFunc(
            0x07);
          """)
          .loadsWithSuccess()
          .containsTopRefable(defValS(2, stringTS(), "result", callS(2, stringTS(),
              refS(2, funcTS(stringTS(), list(blobTS())), "myFunc"), blobS(3, 7))));
    }

    @Test
    public void with_func_reference_and_named_arg() {
      module("""
          String myFunc(Blob b) = "abc";
          result = myFunc(b=
            0x07);
          """)
          .loadsWithSuccess()
          .containsTopRefable(defValS(2, stringTS(), "result", callS(2, stringTS(),
              refS(2, funcTS(stringTS(), list(blobTS())), "myFunc"), blobS(3, 7))));
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
          .containsTopRefable(
              defValS(4, stringTS(), "result",
                  callS(4, stringTS(), refS(4, funcTS(stringTS()), "myValue"))));
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
          .containsTopRefable(
              defValS(4, stringTS(), "result",
                  callS(4, stringTS(), refS(4, funcTS(stringTS(), list(blobTS())), "myValue"),
                      blobS(5, 7))));
    }

    @Test
    public void with_ctor_reference() {
      var struct = structTS("MyStruct", nList(sigS(stringTS(), "field")));
      var params = struct.fields()
          .map(i -> new ItemS(i.type(), i.nameSane(), empty(), loc(2)));
      var ctor = syntCtorS(1, funcTS(struct, params.list()), modPath(), "myStruct", params);
      module("""
          MyStruct {
            String field
          }
          """)
          .loadsWithSuccess()
          .containsTopRefable(ctor);
    }

    @Test
    public void with_ctor_reference_and_arg() {
      var struct = structTS("MyStruct", nList(sigS(stringTS(), "field")));
      module("""
          MyStruct {
            String field
          }
          result = myStruct(
            "aaa");
          """)
          .loadsWithSuccess()
          .containsTopRefable(defValS(4, struct, "result", callS(4, struct, refS(4,
              funcTS(struct, list(stringTS())), "myStruct"), stringS(5, "aaa"))));
    }

    @Test
    public void with_param_reference() {
      module("""
          result(String() f) = f();
          """)
          .loadsWithSuccess()
          .containsTopRefable(defFuncS(1, stringTS(), "result",
              callS(1, stringTS(), paramRefS(funcTS(stringTS()), "f")), nList(itemS(1, funcTS(stringTS()), "f"))));
    }

    @Test
    public void with_param_reference_and_arg() {
      module("""
          result(String(Blob) f) = f(0x09);
          """)
          .loadsWithSuccess()
          .containsTopRefable(defFuncS(1, stringTS(), "result",
              callS(1, stringTS(), paramRefS(funcTS(stringTS(), list(blobTS())), "f"), blobS(1, 9)),
              nList(itemS(1, funcTS(stringTS(), list(blobTS())), "f"))));
    }
  }

  @Test
  public void param_reference_expression() {
    module("""
          Blob myFunc(Blob param1)
            = param1;
          """)
        .loadsWithSuccess()
        .containsTopRefable(defFuncS(
            1, blobTS(), "myFunc", paramRefS(2, blobTS(), "param1"), nList(itemS(1, blobTS(), "param1"))));
  }

  @Test
  public void select_expression() {
    var myStruct = structTS("MyStruct", nList(sigS(stringTS(), "field")));
    module("""
          MyStruct {
            String field,
          }
          MyStruct struct = myStruct("abc");
          result = struct
            .field;
          """)
        .loadsWithSuccess()
        .containsTopRefable(defValS(5, stringTS(), "result",
            selectS(6, stringTS(), refS(5, myStruct, "struct"), "field")));
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
          .containsTopRefable(
              defValS(2, stringTS(), "result", refS(3, stringTS(), "myValue")));
    }

    @Test
    public void to_func() {
      module("""
          String myFunc() = "abc";
          String() result =
            myFunc;
          """)
          .loadsWithSuccess()
          .containsTopRefable(defValS(2, funcTS(stringTS()), "result",
              refS(3, funcTS(stringTS()), "myFunc")));
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
          .containsTopRefable(defValS(2, funcTS(structT), "result",
              refS(3, funcTS(structT), "myStruct")));
    }
  }

  @Test
  public void string_literal_expression() {
    module("""
          result =
            "abc";
          """)
        .loadsWithSuccess()
        .containsTopRefable(defValS(1, stringTS(), "result", stringS(2, "abc")));
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
          .containsTopRefable(defValS(1, blobTS(), "myValue", blobS(2, 7)));
    }

    @Test
    public void bytecode_value() {
      var code = """
          @Bytecode("implementation")
          Blob myValue;
          """;
      module(code)
          .loadsWithSuccess()
          .containsTopRefable(annValS(2, bytecodeS(1, "implementation"), blobTS(), "myValue"));
    }

    @Test
    public void def_func() {
      module("""
          Blob myFunc() =
            0x07;
          """)
          .loadsWithSuccess()
          .containsTopRefable(defFuncS(1, blobTS(), "myFunc", blobS(2, 7), nList()));
    }

    @Test
    public void def_func_with_param() {
      module("""
          String myFunc(
            Blob param1)
            = "abc";
          """)
          .loadsWithSuccess()
          .containsTopRefable(defFuncS(1, stringTS(), "myFunc", stringS(3, "abc"),
              nList(itemS(2, blobTS(), "param1"))));
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
          .containsTopRefable(defFuncS(1, stringTS(), "myFunc", stringS(4, "abc"),
              nList(itemS(2, blobTS(), "param1", blobS(3, 7)))));
    }

    @Test
    public void native_impure_func() {
      module("""
          @NativeImpure("Impl.met")
          String myFunc();
          """)
          .loadsWithSuccess()
          .containsTopRefable(
              natFuncS(2, stringTS(), "myFunc", nList(), nativeS(1, stringS(1, "Impl.met"), false)));
    }

    @Test
    public void native_pure_func() {
      module("""
          @Native("Impl.met")
          String myFunc();
          """)
          .loadsWithSuccess()
          .containsTopRefable(
              natFuncS(2, stringTS(), "myFunc", nList(), nativeS(1, stringS(1, "Impl.met"), true)));
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
          .containsTopRefable(
              natFuncS(2, stringTS(), "myFunc", nList(itemS(3, blobTS(), "param1", blobS(4, 7))),
                  nativeS(1, stringS(1, "Impl.met"), true)));
    }

    @Test
    public void bytecode_func() {
      module("""
          @Bytecode("Impl.met")
          String myFunc();
          """)
          .loadsWithSuccess()
          .containsTopRefable(
              byteFuncS(2, bytecodeS(stringS(1, "Impl.met"), loc(1)), stringTS(), "myFunc", nList()));
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
          .containsTopRefable(
              byteFuncS(2, bytecodeS(1, stringS(1, "Impl.met")), stringTS(), "myFunc",
                  nList(itemS(3, blobTS(), "param1", blobS(4, 7)))));
    }

    @Test
    public void struct_type() {
      module("""
          MyStruct {
            String field
          }
          """)
          .loadsWithSuccess()
          .containsType(structTS("MyStruct", nList(sigS(stringTS(), "field"))));
    }
  }
}
