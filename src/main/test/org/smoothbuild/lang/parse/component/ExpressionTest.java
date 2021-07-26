package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.TestModuleLoader.module;
import static org.smoothbuild.lang.TestingLang.array;
import static org.smoothbuild.lang.TestingLang.blob;
import static org.smoothbuild.lang.TestingLang.call;
import static org.smoothbuild.lang.TestingLang.constr;
import static org.smoothbuild.lang.TestingLang.fieldRead;
import static org.smoothbuild.lang.TestingLang.function;
import static org.smoothbuild.lang.TestingLang.nativ;
import static org.smoothbuild.lang.TestingLang.parameter;
import static org.smoothbuild.lang.TestingLang.parameterRef;
import static org.smoothbuild.lang.TestingLang.reference;
import static org.smoothbuild.lang.TestingLang.string;
import static org.smoothbuild.lang.TestingLang.struct;
import static org.smoothbuild.lang.TestingLang.value;
import static org.smoothbuild.lang.base.type.TestingItemSignature.itemSignature;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.a;
import static org.smoothbuild.lang.base.type.TestingTypes.f;
import static org.smoothbuild.lang.base.type.TestingTypes.item;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.define.Constructor;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.StructType;

public class ExpressionTest {
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
        .containsReferencable(
            value(1, a(BLOB), "result", array(2, BLOB, blob(3, 7), blob(4, 8))));
  }

  @Test
  public void blob_literal_expression() {
    module("""
          result =
            0x07;
          """)
        .loadsSuccessfully()
        .containsReferencable(value(1, BLOB, "result", blob(2, 7)));
  }

  @Nested
  class _call_expression {
    @Test
    public void with_function_reference() {
      module("""
          @Native("Impl.met")
          String myFunction();
          result = myFunction();
          """)
          .loadsSuccessfully()
          .containsReferencable(
              value(3, STRING, "result", call(3, STRING, reference(3, f(STRING), "myFunction"))));
    }

    @Test
    public void with_function_reference_and_argument() {
      module("""
          @Native("Impl.met")
          String myFunction(Blob param1);
          result = myFunction(
            0x07);
          """)
          .loadsSuccessfully()
          .containsReferencable(value(3, STRING, "result", call(3, STRING,
              reference(3, f(STRING, item(BLOB, "param1")), "myFunction"), blob(4, 7))));
    }

    @Test
    public void with_function_reference_and_named_argument() {
      module("""
          @Native("Impl.met")
          String myFunction(Blob param1);
          result = myFunction(param1=
            0x07);
          """)
          .loadsSuccessfully()
          .containsReferencable(value(3, STRING, "result", call(3, STRING,
              reference(3, f(STRING, item(BLOB, "param1")), "myFunction"), blob(4, 7))));
    }

    @Test
    public void with_constructor_reference() {
      StructType struct = struct("MyStruct", itemSignature(STRING, "field"));
      Constructor constr = constr(1, struct, "myStruct", parameter(STRING, "field"));
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
      StructType struct = struct("MyStruct", itemSignature(STRING, "field"));
      module("""
          MyStruct {
            String field
          }
          result = myStruct(
            "aaa");
          """)
          .loadsSuccessfully()
          .containsReferencable(value(4, struct, "result", call(4, struct,
              reference(4, f(struct, item(STRING, "field")), "myStruct"), string(5, "aaa"))));
    }
  }

  @Test
  public void field_read_expression() {
    ItemSignature field = itemSignature(STRING, "field");
    StructType myStruct = struct("MyStruct", field);
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
            value(5, STRING, "result", fieldRead(6, field, reference(5, myStruct, "struct"))));
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
          .containsReferencable(value(2, STRING, "result", nativ(1, "Impl.met", true)));
    }

    @Test
    public void that_is_impure_annotating_value() {
      module("""
          @Native("Impl.met", IMPURE)
          String result;
          """)
          .loadsSuccessfully()
          .containsReferencable(value(2, STRING, "result", nativ(1, "Impl.met", false)));
    }

    @Test
    public void that_is_explicitly_pure_annotating_value() {
      module("""
          @Native("Impl.met", PURE)
          String result;
          """)
          .loadsSuccessfully()
          .containsReferencable(value(2, STRING, "result", nativ(1, "Impl.met", true)));
    }

    @Test
    public void annotating_function() {
      module("""
          @Native("Impl.met")
          String myFunction();
          """)
          .loadsSuccessfully()
          .containsReferencable(function(2, STRING, "myFunction", nativ(1, "Impl.met", true)));
    }

    @Test
    public void that_is_impure_annotating_function() {
      module("""
          @Native("Impl.met", IMPURE)
          String myFunction();
          """)
          .loadsSuccessfully()
          .containsReferencable(function(2, STRING, "myFunction", nativ(1, "Impl.met", false)));
    }

    @Test
    public void that_is_explicitly_pure_annotating_function() {
      module("""
          @Native("Impl.met", PURE)
          String myFunction();
          """)
          .loadsSuccessfully()
          .containsReferencable(function(2, STRING, "myFunction", nativ(1, "Impl.met", true)));
    }
  }

  @Test
  public void parameter_reference_expression() {
    module("""
          Blob myFunction(Blob param1)
            = param1;
          """)
        .loadsSuccessfully()
        .containsReferencable(function(
            1, BLOB, "myFunction", parameterRef(2, BLOB, "param1"), parameter(BLOB, "param1")));
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
          .containsReferencable(value(2, STRING, "result", reference(3, STRING, "myValue")));
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
              reference(3, f(STRING), "myFunction")));
    }
  }

  @Test
  public void string_literal_expression() {
    module("""
          result =
            "abc";
          """)
        .loadsSuccessfully()
        .containsReferencable(value(1, STRING, "result", string(2, "abc")));
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
          .containsReferencable(value(1, BLOB, "myValue", blob(2, 7)));
    }

    @Test
    public void non_native_value_without_body_fails() {
      module("""
          String result;
          """)
          .loadsWithError(1, "Non native value cannot have empty body.");
    }

    @Test
    public void native_value_with_body_fails() {
      module("""
          @Native("Impl.met")
          String myValue = "abc";
          """)
          .loadsWithError(2, "Native value cannot have body.");
    }

    @Test
    public void native_value_without_declared_result_type_causes_error() {
      module("""
        @Native("Impl.met")
        myFunction;""")
          .loadsWithError(2, "`myFunction` is native so it should have declared result type.");
    }

    @Test
    public void defined_function() {
      module("""
          Blob myFunction() =
            0x07;
          """)
          .loadsSuccessfully()
          .containsReferencable(function(1, BLOB, "myFunction", blob(2, 7)));
    }

    @Test
    public void defined_function_with_parameter() {
      module("""
          String myFunction(
            Blob param1)
            = "abc";
          """)
          .loadsSuccessfully()
          .containsReferencable(function(1, STRING, "myFunction",
              string(3, "abc"), parameter(BLOB, "param1")));
    }

    @Test
    public void function_with_parameter_with_default_value() {
      module("""
          String myFunction(
            Blob param1 =
              0x07)
              = "abc";
          """)
          .loadsSuccessfully()
          .containsReferencable(function(1, STRING, "myFunction",
              string(4, "abc"), parameter(BLOB, "param1", blob(3, 7))));
    }

    @Test
    public void non_native_function_without_body() {
      module("""
          String myFunction();
          """)
          .loadsWithError(1, "Non native function cannot have empty body.");
    }

    @Test
    public void native_function_with_body() {
      module("""
          @Native("Impl.met")
          String myFunction() = "abc";
          """)
          .loadsWithError(2, "Native function cannot have body.");
    }

    @Test
    public void native_function_without_declared_result_type_fails() {
      module("""
        @Native("Impl.met")
        myFunction();
        """)
          .loadsWithError(2, "`myFunction` is native so it should have declared result type.");
    }

    @Test
    public void struct_type() {
      module("""
          MyStruct {
            String field
          }
          """)
          .loadsSuccessfully()
          .containsType(struct("MyStruct", itemSignature(STRING, "field")));
    }
  }
}
