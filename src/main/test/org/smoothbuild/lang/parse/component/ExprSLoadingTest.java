package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.base.type.TestingTypesS.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypesS.INT;
import static org.smoothbuild.lang.base.type.TestingTypesS.STRING;
import static org.smoothbuild.lang.base.type.TestingTypesS.a;
import static org.smoothbuild.lang.base.type.TestingTypesS.f;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Named.named;
import static org.smoothbuild.util.collect.NamedList.namedList;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.define.Constructor;
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
        .containsReferencable(value(1, a(BLOB), "result",
            orderS(2, BLOB, blobS(3, 7), blobS(4, 8))));
  }

  @Test
  public void blob_literal_expression() {
    module("""
          result =
            0x07;
          """)
        .loadsSuccessfully()
        .containsReferencable(value(1, BLOB, "result", blobS(2, 7)));
  }

  @Test
  public void int_literal_expression() {
    module("""
          result =
            123;
          """)
        .loadsSuccessfully()
        .containsReferencable(value(1, INT, "result", intS(2, 123)));
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
          .containsReferencable(value(2, STRING, "result",
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
          .containsReferencable(value(2, STRING, "result",
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
          .containsReferencable(value(2, STRING, "result",
              callS(2, STRING, refS(2, f(STRING, BLOB), "myFunction"), blobS(3, 7))));
    }

    @Test
    public void with_value_reference() {
      module("""
          @Native("Impl.met")
          String() myValue;
          result = myValue();
          """)
          .loadsSuccessfully()
          .containsReferencable(value(3, STRING, "result",
              callS(3, STRING, refS(3, f(STRING), "myValue"))));
    }

    @Test
    public void with_value_reference_and_argument() {
      module("""
          @Native("Impl.met")
          String(Blob) myValue;
          result = myValue(
            0x07);
          """)
          .loadsSuccessfully()
          .containsReferencable(
              value(3, STRING, "result", callS(3, STRING,
              refS(3, f(STRING, BLOB), "myValue"), blobS(4, 7))));
    }

    @Test
    public void with_constructor_reference() {
      var struct = structST("MyStruct", namedList(list(named("field", STRING))));
      Constructor constr = constructor(1, struct, "myStruct", parameter(2, STRING, "field"));
      module("""
          MyStruct {
            String field
          }
          """)
          .loadsSuccessfully()
          .containsReferencable(constr);
    }

    @Test
    public void with_constructor_reference_and_argument() {
      var struct = structST("MyStruct", namedList(list(named("field", STRING))));
      module("""
          MyStruct {
            String field
          }
          result = myStruct(
            "aaa");
          """)
          .loadsSuccessfully()
          .containsReferencable(value(4, struct, "result",
              callS(4, struct, refS(4, f(struct, STRING), "myStruct"), stringS(5, "aaa"))));
    }

    @Test
    public void with_parameter_reference() {
      module("""
          result(String() f) = f();
          """)
          .loadsSuccessfully()
          .containsReferencable(functionS(1, STRING, "result",
              callS(1, STRING, paramRefS(f(STRING), "f")), parameter(1, f(STRING), "f")));
    }

    @Test
    public void with_parameter_reference_and_argument() {
      module("""
          result(String(Blob) f) = f(0x09);
          """)
          .loadsSuccessfully()
          .containsReferencable(functionS(1, STRING, "result",
              callS(1, STRING, paramRefS(f(STRING, BLOB), "f"), blobS(1, 9)),
              parameter(1, f(STRING, BLOB), "f")));
    }
  }

  @Test
  public void select_expression() {
    var myStruct = structST("MyStruct", namedList(list(named("field", STRING))));
    module("""
          MyStruct {
            String field,
          }
          MyStruct struct = myStruct("abc");
          result = struct
            .field;
          """)
        .loadsSuccessfully()
        .containsReferencable(
            value(5, STRING, "result", selectS(6, STRING, 0, refS(5, myStruct, "struct"))));
  }

  @Nested
  class _native_expression {
    @Test
    public void annotating_value() {
      module("""
          @Native("Impl.met")
          String result;
          """)
          .loadsSuccessfully()
          .containsReferencable(
              value(2, STRING, "result", annotation(1, stringS(1, "Impl.met"), true)));
    }

    @Test
    public void that_is_impure_annotating_value() {
      module("""
          @Native("Impl.met", IMPURE)
          String result;
          """)
          .loadsSuccessfully()
          .containsReferencable(
              value(2, STRING, "result", annotation(1, stringS(1, "Impl.met"), false)));
    }

    @Test
    public void that_is_explicitly_pure_annotating_value() {
      module("""
          @Native("Impl.met", PURE)
          String result;
          """)
          .loadsSuccessfully()
          .containsReferencable(
              value(2, STRING, "result", annotation(1, stringS(1, "Impl.met"), true)));
    }

    @Test
    public void annotating_function() {
      module("""
          @Native("Impl.met")
          String myFunction();
          """)
          .loadsSuccessfully()
          .containsReferencable(
              functionS(2, STRING, "myFunction", annotation(1, stringS(1, "Impl.met"), true)));
    }

    @Test
    public void that_is_impure_annotating_function() {
      module("""
          @Native("Impl.met", IMPURE)
          String myFunction();
          """)
          .loadsSuccessfully()
          .containsReferencable(
              functionS(2, STRING, "myFunction", annotation(1, stringS(1, "Impl.met"), false)));
    }

    @Test
    public void that_is_explicitly_pure_annotating_function() {
      module("""
          @Native("Impl.met", PURE)
          String myFunction();
          """)
          .loadsSuccessfully()
          .containsReferencable(
              functionS(2, STRING, "myFunction", annotation(1, stringS(1, "Impl.met"), true)));
    }
  }

  @Test
  public void parameter_reference_expression() {
    module("""
          Blob myFunction(Blob param1)
            = param1;
          """)
        .loadsSuccessfully()
        .containsReferencable(functionS(
            1, BLOB, "myFunction", paramRefS(2, BLOB, "param1"), parameter(1, BLOB, "param1")));
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
          .containsReferencable(
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
          .containsReferencable(value(2, f(STRING), "result",
              refS(3, f(STRING), "myFunction")));
    }

    @Test
    public void to_constructor() {
      var structType = structST("MyStruct", namedList(list()));
      module("""
          MyStruct {}
          MyStruct() result =
            myStruct;
          """)
          .loadsSuccessfully()
          .containsReferencable(value(2, f(structType), "result",
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
        .containsReferencable(value(1, STRING, "result", stringS(2, "abc")));
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
          .containsReferencable(value(1, BLOB, "myValue", blobS(2, 7)));
    }

    @Test
    public void defined_function() {
      module("""
          Blob myFunction() =
            0x07;
          """)
          .loadsSuccessfully()
          .containsReferencable(functionS(1, BLOB, "myFunction", blobS(2, 7)));
    }

    @Test
    public void defined_function_with_parameter() {
      module("""
          String myFunction(
            Blob param1)
            = "abc";
          """)
          .loadsSuccessfully()
          .containsReferencable(functionS(1, STRING, "myFunction",
              stringS(3, "abc"), parameter(2, BLOB, "param1")));
    }

    @Test
    public void defined_function_with_parameter_with_default_argument() {
      module("""
          String myFunction(
            Blob param1 =
              0x07)
              = "abc";
          """)
          .loadsSuccessfully()
          .containsReferencable(functionS(1, STRING, "myFunction",
              stringS(4, "abc"), parameter(2, BLOB, "param1", blobS(3, 7))));
    }

    @Test
    public void struct_type() {
      module("""
          MyStruct {
            String field
          }
          """)
          .loadsSuccessfully()
          .containsType(structST("MyStruct", namedList(list(named("field", STRING)))));
    }
  }
}
