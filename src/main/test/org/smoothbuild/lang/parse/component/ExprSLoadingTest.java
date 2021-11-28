package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.base.type.TestingTypesS.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypesS.INT;
import static org.smoothbuild.lang.base.type.TestingTypesS.STRING;
import static org.smoothbuild.lang.base.type.TestingTypesS.a;
import static org.smoothbuild.lang.base.type.TestingTypesS.f;
import static org.smoothbuild.util.collect.NList.nList;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.define.ConstructorS;
import org.smoothbuild.testing.TestingContext;

public class ExprSLoadingTest extends TestingContext {
  @Test
  public void array_literal_expression() {
    module("""
          result =
          [
            0x07,
            0x08
          ];
          """)
        .loadsSuccessfully()
        .containsEvaluable(value(1, a(BLOB), "result",
            orderS(2, BLOB, blobS(3, 7), blobS(4, 8))));
  }

  @Test
  public void blob_literal_expression() {
    module("""
          result =
            0x07;
          """)
        .loadsSuccessfully()
        .containsEvaluable(value(1, BLOB, "result", blobS(2, 7)));
  }

  @Test
  public void int_literal_expression() {
    module("""
          result =
            123;
          """)
        .loadsSuccessfully()
        .containsEvaluable(value(1, INT, "result", intS(2, 123)));
  }

  @Nested
  class _call_expression {
    @Test
    public void with_function_reference() {
      module("""
          String myFunction() = "abc";
          result = myFunction();
          """)
          .loadsSuccessfully()
          .containsEvaluable(value(2, STRING, "result",
              callS(2, STRING, refS(2, f(STRING), "myFunction"))));
    }

    @Test
    public void with_function_reference_and_argument() {
      module("""
          String myFunction(Blob b) = "abc";
          result = myFunction(
            0x07);
          """)
          .loadsSuccessfully()
          .containsEvaluable(value(2, STRING, "result",
              callS(2, STRING, refS(2, f(STRING, BLOB), "myFunction"), blobS(3, 7))));
    }

    @Test
    public void with_function_reference_and_named_argument() {
      module("""
          String myFunction(Blob b) = "abc";
          result = myFunction(b=
            0x07);
          """)
          .loadsSuccessfully()
          .containsEvaluable(value(2, STRING, "result",
              callS(2, STRING, refS(2, f(STRING, BLOB), "myFunction"), blobS(3, 7))));
    }

    @Test
    public void with_value_reference() {
      module("""
          @Native("Impl.met")
          String myFunc();
          String() myValue = myFunc;
          result = myValue();
          """)
          .loadsSuccessfully()
          .containsEvaluable(
              value(4, STRING, "result",
                  callS(4, STRING, refS(4, f(STRING), "myValue"))));
    }

    @Test
    public void with_value_reference_and_argument() {
      module("""
          @Native("Impl.met")
          String myFunc(Blob blob);
          String(Blob) myValue = myFunc;
          result = myValue(
            0x07);
          """)
          .loadsSuccessfully()
          .containsEvaluable(
              value(4, STRING, "result",
                  callS(4, STRING, refS(4, f(STRING, BLOB), "myValue"), blobS(5, 7))));
    }

    @Test
    public void with_constructor_reference() {
      var struct = structST("MyStruct", nList(isig("field", STRING)));
      ConstructorS constr = constructorS(1, struct, "myStruct", param(2, STRING, "field"));
      module("""
          MyStruct {
            String field
          }
          """)
          .loadsSuccessfully()
          .containsEvaluable(constr);
    }

    @Test
    public void with_constructor_reference_and_argument() {
      var struct = structST("MyStruct", nList(isig("field", STRING)));
      module("""
          MyStruct {
            String field
          }
          result = myStruct(
            "aaa");
          """)
          .loadsSuccessfully()
          .containsEvaluable(value(4, struct, "result",
              callS(4, struct, refS(4, f(struct, STRING), "myStruct"), stringS(5, "aaa"))));
    }

    @Test
    public void with_param_reference() {
      module("""
          result(String() f) = f();
          """)
          .loadsSuccessfully()
          .containsEvaluable(functionS(1, STRING, "result",
              callS(1, STRING, paramRefS(f(STRING), "f")), param(1, f(STRING), "f")));
    }

    @Test
    public void with_param_reference_and_argument() {
      module("""
          result(String(Blob) f) = f(0x09);
          """)
          .loadsSuccessfully()
          .containsEvaluable(functionS(1, STRING, "result",
              callS(1, STRING, paramRefS(f(STRING, BLOB), "f"), blobS(1, 9)),
              param(1, f(STRING, BLOB), "f")));
    }
  }

  @Test
  public void select_expression() {
    var myStruct = structST("MyStruct", nList(isig("field", STRING)));
    module("""
          MyStruct {
            String field,
          }
          MyStruct struct = myStruct("abc");
          result = struct
            .field;
          """)
        .loadsSuccessfully()
        .containsEvaluable(
            value(5, STRING, "result", selectS(6, STRING, 0, refS(5, myStruct, "struct"))));
  }

  @Nested
  class _native_function {
    @Test
    public void default_pureness() {
      module("""
          @Native("Impl.met")
          String myFunction();
          """)
          .loadsSuccessfully()
          .containsEvaluable(
              functionS(2, STRING, "myFunction", annotation(1, stringS(1, "Impl.met"), true)));
    }

    @Test
    public void impure() {
      module("""
          @Native("Impl.met", IMPURE)
          String myFunction();
          """)
          .loadsSuccessfully()
          .containsEvaluable(
              functionS(2, STRING, "myFunction", annotation(1, stringS(1, "Impl.met"), false)));
    }

    @Test
    public void pure() {
      module("""
          @Native("Impl.met", PURE)
          String myFunction();
          """)
          .loadsSuccessfully()
          .containsEvaluable(
              functionS(2, STRING, "myFunction", annotation(1, stringS(1, "Impl.met"), true)));
    }
  }

  @Test
  public void param_reference_expression() {
    module("""
          Blob myFunction(Blob param1)
            = param1;
          """)
        .loadsSuccessfully()
        .containsEvaluable(functionS(
            1, BLOB, "myFunction", paramRefS(2, BLOB, "param1"), param(1, BLOB, "param1")));
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
          .loadsSuccessfully()
          .containsEvaluable(
              value(2, STRING, "result", refS(3, STRING, "myValue")));
    }

    @Test
    public void to_function() {
      module("""
          String myFunction() = "abc";
          String() result =
            myFunction;
          """)
          .loadsSuccessfully()
          .containsEvaluable(value(2, f(STRING), "result",
              refS(3, f(STRING), "myFunction")));
    }

    @Test
    public void to_constructor() {
      var structType = structST("MyStruct", nList());
      module("""
          MyStruct {}
          MyStruct() result =
            myStruct;
          """)
          .loadsSuccessfully()
          .containsEvaluable(value(2, f(structType), "result",
              refS(3, f(structType), "myStruct")));
    }
  }

  @Test
  public void string_literal_expression() {
    module("""
          result =
            "abc";
          """)
        .loadsSuccessfully()
        .containsEvaluable(value(1, STRING, "result", stringS(2, "abc")));
  }

  @Nested
  class _definition_of {
    @Test
    public void defined_value() {
      module("""
          Blob myValue =
            0x07;
          """)
          .loadsSuccessfully()
          .containsEvaluable(value(1, BLOB, "myValue", blobS(2, 7)));
    }

    @Test
    public void defined_function() {
      module("""
          Blob myFunction() =
            0x07;
          """)
          .loadsSuccessfully()
          .containsEvaluable(functionS(1, BLOB, "myFunction", blobS(2, 7)));
    }

    @Test
    public void defined_function_with_param() {
      module("""
          String myFunction(
            Blob param1)
            = "abc";
          """)
          .loadsSuccessfully()
          .containsEvaluable(functionS(1, STRING, "myFunction",
              stringS(3, "abc"), param(2, BLOB, "param1")));
    }

    @Test
    public void defined_function_with_param_with_default_argument() {
      module("""
          String myFunction(
            Blob param1 =
              0x07)
              = "abc";
          """)
          .loadsSuccessfully()
          .containsEvaluable(functionS(1, STRING, "myFunction",
              stringS(4, "abc"), param(2, BLOB, "param1", blobS(3, 7))));
    }

    @Test
    public void struct_type() {
      module("""
          MyStruct {
            String field
          }
          """)
          .loadsSuccessfully()
          .containsType(structST("MyStruct", nList(isig("field", STRING))));
    }
  }
}
