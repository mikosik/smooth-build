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
import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.testing.TestingContext;

public class ExpressionLoadingTest extends TestingContext {
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
        .containsReferencable(valueExpression(1, a(BLOB), "result",
            arrayExpression(2, BLOB, blobExpression(3, 7), blobExpression(4, 8))));
  }

  @Test
  public void blob_literal_expression() {
    module("""
          result =
            0x07;
          """)
        .loadsSuccessfully()
        .containsReferencable(
            valueExpression(1, BLOB, "result", blobExpression(2, 7)));
  }

  @Test
  public void int_literal_expression() {
    module("""
          result =
            123;
          """)
        .loadsSuccessfully()
        .containsReferencable(
            valueExpression(1, INT, "result", intExpression(2, 123)));
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
          .containsReferencable(valueExpression(2, STRING, "result",
              callExpression(2, STRING, referenceExpression(2, f(STRING), "myFunction"))));
    }

    @Test
    public void with_function_reference_and_argument() {
      module("""
          String myFunction(Blob b) = "abc";
          result = myFunction(
            0x07);
          """)
          .loadsSuccessfully()
          .containsReferencable(valueExpression(2, STRING, "result",
              callExpression(2, STRING, referenceExpression(2, f(STRING, BLOB),
                  "myFunction"), blobExpression(3, 7))));
    }

    @Test
    public void with_function_reference_and_named_argument() {
      module("""
          String myFunction(Blob b) = "abc";
          result = myFunction(b=
            0x07);
          """)
          .loadsSuccessfully()
          .containsReferencable(valueExpression(2, STRING, "result",
              callExpression(2, STRING, referenceExpression(2, f(STRING, BLOB),
                  "myFunction"), blobExpression(3, 7))));
    }

    @Test
    public void with_value_reference() {
      module("""
          @Native("Impl.met")
          String() myValue;
          result = myValue();
          """)
          .loadsSuccessfully()
          .containsReferencable(valueExpression(3, STRING, "result",
              callExpression(3, STRING, referenceExpression(3, f(STRING), "myValue"))));
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
              valueExpression(3, STRING, "result", callExpression(3, STRING,
              referenceExpression(3, f(STRING, BLOB), "myValue"), blobExpression(4, 7))));
    }

    @Test
    public void with_constructor_reference() {
      StructType struct = structST("MyStruct", namedList(list(named("field", STRING))));
      Constructor constr = constrExpression(1, struct, "myStruct",
          parameterExpression(2, STRING, "field"));
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
      StructType struct = structST("MyStruct", namedList(list(named("field", STRING))));
      module("""
          MyStruct {
            String field
          }
          result = myStruct(
            "aaa");
          """)
          .loadsSuccessfully()
          .containsReferencable(valueExpression(4, struct, "result",
              callExpression(4, struct, referenceExpression(4, f(struct, STRING),
                  "myStruct"), stringExpression(5, "aaa"))));
    }

    @Test
    public void with_parameter_reference() {
      module("""
          result(String() f) = f();
          """)
          .loadsSuccessfully()
          .containsReferencable(functionExpression(1, STRING, "result",
              callExpression(1, STRING, parameterRefExpression(f(STRING), "f")),
              parameterExpression(1, f(STRING), "f")));
    }

    @Test
    public void with_parameter_reference_and_argument() {
      module("""
          result(String(Blob) f) = f(0x09);
          """)
          .loadsSuccessfully()
          .containsReferencable(functionExpression(1, STRING, "result",
              callExpression(1, STRING, parameterRefExpression(f(STRING, BLOB), "f"), blobExpression(1, 9)),
              parameterExpression(1, f(STRING, BLOB), "f")));
    }
  }

  @Test
  public void select_expression() {
    StructType myStruct = structST("MyStruct", namedList(list(named("field", STRING))));
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
            valueExpression(5, STRING, "result",
                selectExpression(6, STRING, 0, referenceExpression(5, myStruct, "struct"))));
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
              valueExpression(2, STRING, "result", annotation(1, stringExpression(1, "Impl.met"), true)));
    }

    @Test
    public void that_is_impure_annotating_value() {
      module("""
          @Native("Impl.met", IMPURE)
          String result;
          """)
          .loadsSuccessfully()
          .containsReferencable(
              valueExpression(2, STRING, "result", annotation(1, stringExpression(1, "Impl.met"), false)));
    }

    @Test
    public void that_is_explicitly_pure_annotating_value() {
      module("""
          @Native("Impl.met", PURE)
          String result;
          """)
          .loadsSuccessfully()
          .containsReferencable(
              valueExpression(2, STRING, "result", annotation(1, stringExpression(1, "Impl.met"), true)));
    }

    @Test
    public void annotating_function() {
      module("""
          @Native("Impl.met")
          String myFunction();
          """)
          .loadsSuccessfully()
          .containsReferencable(
              functionExpression(2, STRING, "myFunction", annotation(1, stringExpression(1, "Impl.met"), true)));
    }

    @Test
    public void that_is_impure_annotating_function() {
      module("""
          @Native("Impl.met", IMPURE)
          String myFunction();
          """)
          .loadsSuccessfully()
          .containsReferencable(
              functionExpression(2, STRING, "myFunction", annotation(1, stringExpression(1, "Impl.met"), false)));
    }

    @Test
    public void that_is_explicitly_pure_annotating_function() {
      module("""
          @Native("Impl.met", PURE)
          String myFunction();
          """)
          .loadsSuccessfully()
          .containsReferencable(
              functionExpression(2, STRING, "myFunction", annotation(1, stringExpression(1, "Impl.met"), true)));
    }
  }

  @Test
  public void parameter_reference_expression() {
    module("""
          Blob myFunction(Blob param1)
            = param1;
          """)
        .loadsSuccessfully()
        .containsReferencable(functionExpression(
            1, BLOB, "myFunction", parameterRefExpression(2, BLOB, "param1"), parameterExpression(1, BLOB, "param1")));
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
              valueExpression(2, STRING, "result", referenceExpression(3, STRING, "myValue")));
    }

    @Test
    public void to_function() {
      module("""
          String myFunction() = "abc";
          String() result =
            myFunction;
          """)
          .loadsSuccessfully()
          .containsReferencable(valueExpression(2, f(STRING), "result",
              referenceExpression(3, f(STRING), "myFunction")));
    }

    @Test
    public void to_constructor() {
      StructType structType = structST("MyStruct", namedList(list()));
      module("""
          MyStruct {}
          MyStruct() result =
            myStruct;
          """)
          .loadsSuccessfully()
          .containsReferencable(valueExpression(2, f(structType), "result",
              referenceExpression(3, f(structType), "myStruct")));
    }
  }

  @Test
  public void string_literal_expression() {
    module("""
          result =
            "abc";
          """)
        .loadsSuccessfully()
        .containsReferencable(
            valueExpression(1, STRING, "result", stringExpression(2, "abc")));
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
          .containsReferencable(
              valueExpression(1, BLOB, "myValue", blobExpression(2, 7)));
    }

    @Test
    public void defined_function() {
      module("""
          Blob myFunction() =
            0x07;
          """)
          .loadsSuccessfully()
          .containsReferencable(
              functionExpression(1, BLOB, "myFunction", blobExpression(2, 7)));
    }

    @Test
    public void defined_function_with_parameter() {
      module("""
          String myFunction(
            Blob param1)
            = "abc";
          """)
          .loadsSuccessfully()
          .containsReferencable(functionExpression(1, STRING, "myFunction",
              stringExpression(3, "abc"), parameterExpression(2, BLOB, "param1")));
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
          .containsReferencable(functionExpression(1, STRING, "myFunction",
              stringExpression(4, "abc"), parameterExpression(2, BLOB, "param1", blobExpression(3, 7))));
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
