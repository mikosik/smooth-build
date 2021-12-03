package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.base.type.TestingTypesS.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypesS.INT;
import static org.smoothbuild.lang.base.type.TestingTypesS.STRING;
import static org.smoothbuild.lang.base.type.TestingTypesS.a;
import static org.smoothbuild.lang.base.type.TestingTypesS.f;
import static org.smoothbuild.util.collect.NList.nList;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class ExprSLoadingTest extends TestingContext {
  @Test
  public void array_literal_expression() {
    mod("""
          result =
          [
            0x07,
            0x08
          ];
          """)
        .loadsSuccessfully()
        .containsEval(defValS(1, a(BLOB), "result",
            orderS(2, BLOB, blobS(3, 7), blobS(4, 8))));
  }

  @Test
  public void blob_literal_expression() {
    mod("""
          result =
            0x07;
          """)
        .loadsSuccessfully()
        .containsEval(defValS(1, BLOB, "result", blobS(2, 7)));
  }

  @Test
  public void int_literal_expression() {
    mod("""
          result =
            123;
          """)
        .loadsSuccessfully()
        .containsEval(defValS(1, INT, "result", intS(2, 123)));
  }

  @Nested
  class _call_expression {
    @Test
    public void with_func_reference() {
      mod("""
          String myFunc() = "abc";
          result = myFunc();
          """)
          .loadsSuccessfully()
          .containsEval(defValS(2, STRING, "result",
              callS(2, STRING, refS(2, f(STRING), "myFunc"))));
    }

    @Test
    public void with_func_reference_and_arg() {
      mod("""
          String myFunc(Blob b) = "abc";
          result = myFunc(
            0x07);
          """)
          .loadsSuccessfully()
          .containsEval(defValS(2, STRING, "result",
              callS(2, STRING, refS(2, f(STRING, BLOB), "myFunc"), blobS(3, 7))));
    }

    @Test
    public void with_func_reference_and_named_arg() {
      mod("""
          String myFunc(Blob b) = "abc";
          result = myFunc(b=
            0x07);
          """)
          .loadsSuccessfully()
          .containsEval(defValS(2, STRING, "result",
              callS(2, STRING, refS(2, f(STRING, BLOB), "myFunc"), blobS(3, 7))));
    }

    @Test
    public void with_value_reference() {
      mod("""
          @Native("Impl.met")
          String myFunc();
          String() myValue = myFunc;
          result = myValue();
          """)
          .loadsSuccessfully()
          .containsEval(
              defValS(4, STRING, "result",
                  callS(4, STRING, refS(4, f(STRING), "myValue"))));
    }

    @Test
    public void with_value_reference_and_arg() {
      mod("""
          @Native("Impl.met")
          String myFunc(Blob blob);
          String(Blob) myValue = myFunc;
          result = myValue(
            0x07);
          """)
          .loadsSuccessfully()
          .containsEval(
              defValS(4, STRING, "result",
                  callS(4, STRING, refS(4, f(STRING, BLOB), "myValue"), blobS(5, 7))));
    }

    @Test
    public void with_ctor_reference() {
      var struct = structST("MyStruct", nList(sigS(STRING, "field")));
      var combine = combineS(1, struct, paramRefS(1, stringST(), "field"));
      var ctor = funcS(1, struct, "myStruct", combine, itemS(2, STRING, "field"));
      mod("""
          MyStruct {
            String field
          }
          """)
          .loadsSuccessfully()
          .containsEval(ctor);
    }

    @Test
    public void with_ctor_reference_and_arg() {
      var struct = structST("MyStruct", nList(sigS(STRING, "field")));
      mod("""
          MyStruct {
            String field
          }
          result = myStruct(
            "aaa");
          """)
          .loadsSuccessfully()
          .containsEval(defValS(4, struct, "result",
              callS(4, struct, refS(4, f(struct, STRING), "myStruct"), stringS(5, "aaa"))));
    }

    @Test
    public void with_param_reference() {
      mod("""
          result(String() f) = f();
          """)
          .loadsSuccessfully()
          .containsEval(funcS(1, STRING, "result",
              callS(1, STRING, paramRefS(f(STRING), "f")), itemS(1, f(STRING), "f")));
    }

    @Test
    public void with_param_reference_and_arg() {
      mod("""
          result(String(Blob) f) = f(0x09);
          """)
          .loadsSuccessfully()
          .containsEval(funcS(1, STRING, "result",
              callS(1, STRING, paramRefS(f(STRING, BLOB), "f"), blobS(1, 9)),
              itemS(1, f(STRING, BLOB), "f")));
    }
  }

  @Test
  public void select_expression() {
    var myStruct = structST("MyStruct", nList(sigS(STRING, "field")));
    mod("""
          MyStruct {
            String field,
          }
          MyStruct struct = myStruct("abc");
          result = struct
            .field;
          """)
        .loadsSuccessfully()
        .containsEval(
            defValS(5, STRING, "result", selectS(6, STRING, refS(5, myStruct, "struct"), "field")));
  }

  @Nested
  class _nat_func {
    @Test
    public void default_pureness() {
      mod("""
          @Native("Impl.met")
          String myFunc();
          """)
          .loadsSuccessfully()
          .containsEval(
              funcS(2, STRING, "myFunc", annS(1, stringS(1, "Impl.met"), true)));
    }

    @Test
    public void impure() {
      mod("""
          @Native("Impl.met", IMPURE)
          String myFunc();
          """)
          .loadsSuccessfully()
          .containsEval(
              funcS(2, STRING, "myFunc", annS(1, stringS(1, "Impl.met"), false)));
    }

    @Test
    public void pure() {
      mod("""
          @Native("Impl.met", PURE)
          String myFunc();
          """)
          .loadsSuccessfully()
          .containsEval(
              funcS(2, STRING, "myFunc", annS(1, stringS(1, "Impl.met"), true)));
    }
  }

  @Test
  public void param_reference_expression() {
    mod("""
          Blob myFunc(Blob param1)
            = param1;
          """)
        .loadsSuccessfully()
        .containsEval(funcS(
            1, BLOB, "myFunc", paramRefS(2, BLOB, "param1"), itemS(1, BLOB, "param1")));
  }

  @Nested
  class _reference_expression {
    @Test
    public void to_value() {
      mod("""
          String myValue = "abc";
          String result =
            myValue;
          """)
          .loadsSuccessfully()
          .containsEval(
              defValS(2, STRING, "result", refS(3, STRING, "myValue")));
    }

    @Test
    public void to_func() {
      mod("""
          String myFunc() = "abc";
          String() result =
            myFunc;
          """)
          .loadsSuccessfully()
          .containsEval(defValS(2, f(STRING), "result",
              refS(3, f(STRING), "myFunc")));
    }

    @Test
    public void to_ctor() {
      var structType = structST("MyStruct", nList());
      mod("""
          MyStruct {}
          MyStruct() result =
            myStruct;
          """)
          .loadsSuccessfully()
          .containsEval(defValS(2, f(structType), "result",
              refS(3, f(structType), "myStruct")));
    }
  }

  @Test
  public void string_literal_expression() {
    mod("""
          result =
            "abc";
          """)
        .loadsSuccessfully()
        .containsEval(defValS(1, STRING, "result", stringS(2, "abc")));
  }

  @Nested
  class _definition_of {
    @Test
    public void defined_value() {
      mod("""
          Blob myValue =
            0x07;
          """)
          .loadsSuccessfully()
          .containsEval(defValS(1, BLOB, "myValue", blobS(2, 7)));
    }

    @Test
    public void def_func() {
      mod("""
          Blob myFunc() =
            0x07;
          """)
          .loadsSuccessfully()
          .containsEval(funcS(1, BLOB, "myFunc", blobS(2, 7)));
    }

    @Test
    public void def_func_with_param() {
      mod("""
          String myFunc(
            Blob param1)
            = "abc";
          """)
          .loadsSuccessfully()
          .containsEval(funcS(1, STRING, "myFunc",
              stringS(3, "abc"), itemS(2, BLOB, "param1")));
    }

    @Test
    public void def_func_with_param_with_default_arg() {
      mod("""
          String myFunc(
            Blob param1 =
              0x07)
              = "abc";
          """)
          .loadsSuccessfully()
          .containsEval(funcS(1, STRING, "myFunc",
              stringS(4, "abc"), itemS(2, BLOB, "param1", blobS(3, 7))));
    }

    @Test
    public void struct_type() {
      mod("""
          MyStruct {
            String field
          }
          """)
          .loadsSuccessfully()
          .containsType(structST("MyStruct", nList(sigS(STRING, "field"))));
    }
  }
}
