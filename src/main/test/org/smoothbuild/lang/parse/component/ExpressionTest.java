package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.TestModuleLoader.module;
import static org.smoothbuild.lang.TestingLang.array;
import static org.smoothbuild.lang.TestingLang.blob;
import static org.smoothbuild.lang.TestingLang.call;
import static org.smoothbuild.lang.TestingLang.constr;
import static org.smoothbuild.lang.TestingLang.fieldRead;
import static org.smoothbuild.lang.TestingLang.function;
import static org.smoothbuild.lang.TestingLang.parameter;
import static org.smoothbuild.lang.TestingLang.parameterRef;
import static org.smoothbuild.lang.TestingLang.reference;
import static org.smoothbuild.lang.TestingLang.string;
import static org.smoothbuild.lang.TestingLang.struct;
import static org.smoothbuild.lang.TestingLang.value;
import static org.smoothbuild.lang.base.define.TestingFilePath.nativeFilePath;
import static org.smoothbuild.lang.base.define.TestingLocation.loc;
import static org.smoothbuild.lang.base.type.TestingItemSignature.itemSignature;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.a;
import static org.smoothbuild.lang.base.type.TestingTypes.f;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.define.Constructor;
import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.expr.NativeExpression;

public class ExpressionTest {
  @Test
  public void native_value() {
    module("""
          @Native("Impl.met")
          String result;
          """)
        .loadsSuccessfully()
        .containsReferencable(value(2, STRING, "result", "Impl.met"));
  }

  @Test
  public void defined_value() {
    module("""
          @Native("Impl.met")
          String myValue;
          String result =
            myValue;
          """)
        .loadsSuccessfully()
        .containsReferencable(value(3, STRING, "result", reference(4, STRING, "myValue")));
  }

  @Test
  public void non_native_value_without_body() {
    module("""
          String result;
          """)
        .loadsWithError(1, "Non native value cannot have empty body.");
  }

  @Test
  public void native_value_with_body() {
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
  public void native_function() {
    module("""
          @Native("Impl.met")
          String myFunction();
          """)
        .loadsSuccessfully()
        .containsReferencable(function(2, STRING, "myFunction", "Impl.met"));
  }

  @Test
  public void native_impure_function() {
    module("""
          @Native("Impl.met", IMPURE)
          String myFunction();
          """)
        .loadsSuccessfully()
        .containsReferencable(function(2, STRING, "myFunction",
            new NativeExpression("Impl.met", false, loc(1), nativeFilePath())));
  }

  @Test
  public void native_explicitly_pure_function() {
    module("""
          @Native("Impl.met", PURE)
          String myFunction();
          """)
        .loadsSuccessfully()
        .containsReferencable(function(2, STRING, "myFunction",
            new NativeExpression("Impl.met", true, loc(1), nativeFilePath())));
  }

  @Test
  public void function_reference() {
    module("""
          @Native("Impl.met")
          String myFunction();
          String() result =
            myFunction;
          """)
        .loadsSuccessfully()
        .containsReferencable(value(3, f(STRING), "result", reference(4, f(STRING), "myFunction")));
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
  public void native_function_without_declared_result_type_causes_error() {
    module("""
        @Native("Impl.met")
        myFunction();
        """)
        .loadsWithError(2, "`myFunction` is native so it should have declared result type.");
  }

  @Test
  public void function_with_parameter() {
    module("""
          @Native("Impl.met")
          String myFunction(
            Blob param1);
          """)
        .loadsSuccessfully()
        .containsReferencable(function(2, STRING, "myFunction", "Impl.met",
            parameter(BLOB, "param1")));
  }

  @Test
  public void function_with_parameter_reference() {
    module("""
          Blob myFunction(Blob param1)
            = param1;
          """)
        .loadsSuccessfully()
        .containsReferencable(function(
            1, BLOB, "myFunction", parameterRef(2, BLOB, "param1"), parameter(BLOB, "param1")));
  }

  @Test
  public void function_with_parameter_with_default_value() {
    module("""
          @Native("Impl.met")
          String myFunction(
            Blob param1 =
              0x07);
          """)
        .loadsSuccessfully()
        .containsReferencable(
            function(2, STRING, "myFunction", "Impl.met", parameter(BLOB, "param1", blob(4, 7))));
  }

  @Test
  public void function_call() {
    module("""
          @Native("Impl.met")
          String myFunction();
          result = myFunction();
          """)
        .loadsSuccessfully()
        .containsReferencable(
            value(3, STRING, "result", call(3, STRING,
                function(2, STRING, "myFunction", "Impl.met"))));
  }

  @Test
  public void function_call_with_argument() {
    Function function = function(2, STRING, "myFunction", "Impl.met", parameter(BLOB, "param1"));
    module("""
          @Native("Impl.met")
          String myFunction(Blob param1);
          result = myFunction(
            0x07);
          """)
        .loadsSuccessfully()
        .containsReferencable(
            value(3, STRING, "result", call(3, STRING, function, blob(4, 7))));
  }

  @Test
  public void function_call_with_named_argument() {
    Function function = function(2, STRING, "myFunction", "Impl.met", parameter(BLOB, "param1"));
    module("""
          @Native("Impl.met")
          String myFunction(Blob param1);
          result = myFunction(param1=
            0x07);
          """)
        .loadsSuccessfully()
        .containsReferencable(
            value(3, STRING, "result", call(3, STRING, function, blob(4, 7))));
  }

  @Test
  public void blob_literal() {
    module("""
          result =
            0x07;
          """)
        .loadsSuccessfully()
        .containsReferencable(value(1, BLOB, "result", blob(2, 7)));
  }

  @Test
  public void string_literal() {
    module("""
          result =
            "abc";
          """)
        .loadsSuccessfully()
        .containsReferencable(value(1, STRING, "result", string(2, "abc")));
  }

  @Test
  public void array_literal() {
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
  public void field_read() {
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

  @Test
  public void array_type() {
    module("""
          @Native("Impl.met")
          [String] result;
          """)
        .loadsSuccessfully()
        .containsReferencable(value(2, a(STRING), "result", "Impl.met"));
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

  @Test
  public void constructor() {
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
  public void constructor_call_with_argument() {
    StructType struct = struct("MyStruct", itemSignature(STRING, "field"));
    Constructor constr = constr(1, struct, "myStruct", parameter(STRING, "field"));
    module("""
          MyStruct {
            String field
          }
          result = myStruct(
            "aaa");
          """)
        .loadsSuccessfully()
        .containsReferencable(value(4, struct, "result", call(4, struct, constr, string(5, "aaa"))));
  }
}
