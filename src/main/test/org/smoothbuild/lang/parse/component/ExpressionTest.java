package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.TestingLang.array;
import static org.smoothbuild.lang.TestingLang.blob;
import static org.smoothbuild.lang.TestingLang.call;
import static org.smoothbuild.lang.TestingLang.constr;
import static org.smoothbuild.lang.TestingLang.field;
import static org.smoothbuild.lang.TestingLang.fieldRead;
import static org.smoothbuild.lang.TestingLang.function;
import static org.smoothbuild.lang.TestingLang.parameter;
import static org.smoothbuild.lang.TestingLang.parameterRef;
import static org.smoothbuild.lang.TestingLang.string;
import static org.smoothbuild.lang.TestingLang.struct;
import static org.smoothbuild.lang.TestingLang.value;
import static org.smoothbuild.lang.TestingLang.valueRef;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.a;
import static org.smoothbuild.lang.parse.component.TestModuleLoader.module;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.base.Item;
import org.smoothbuild.lang.base.type.StructType;

public class ExpressionTest {
  @Test
  public void value_with_empty_body() {
    module("""
          String result;
          """)
        .loadsSuccessfully()
        .containsEvaluable(value(1, STRING, "result"));
  }

  @Test
  public void value_reference() {
    module("""
          String myValue;
          String result =
            myValue;
          """)
        .loadsSuccessfully()
        .containsEvaluable(value(2, STRING, "result", valueRef(3, STRING, "myValue")));
  }

  @Test
  public void function_with_empty_body() {
    module("""
          String myFunction();
          """)
        .loadsSuccessfully()
        .containsEvaluable(function(1, STRING, "myFunction"));
  }

  @Test
  public void function_with_body() {
    module("""
          Blob myFunction() =
            0x07;
          """)
        .loadsSuccessfully()
        .containsEvaluable(function(1, BLOB, "myFunction", blob(2, 7)));
  }

  @Test
  public void function_with_parameter() {
    module("""
          String myFunction(
            Blob param1);
          """)
        .loadsSuccessfully()
        .containsEvaluable(function(1, STRING, "myFunction", parameter(2, BLOB, "param1")));
  }

  @Test
  public void function_with_parameter_reference() {
    module("""
          Blob myFunction(Blob param1)
            = param1;
          """)
        .loadsSuccessfully()
        .containsEvaluable(function(
            1, BLOB, "myFunction", parameterRef(BLOB, "param1", 2), parameter(1, BLOB, "param1")));
  }

  @Test
  public void function_with_parameter_with_default_value() {
    module("""
          String myFunction(
            Blob param1 =
              0x07);
          """)
        .loadsSuccessfully()
        .containsEvaluable(
            function(1, STRING, "myFunction", parameter(2, BLOB, "param1", blob(3, 7))));
  }

  @Test
  public void function_call() {
    module("""
          String myFunction();
          result = myFunction();
          """)
        .loadsSuccessfully()
        .containsEvaluable(
            value(2, STRING, "result", call(2, STRING, function(1, STRING, "myFunction"))));
  }

  @Test
  public void function_call_with_argument() {
    Function function = function(1, STRING, "myFunction", parameter(1, BLOB, "param1"));
    module("""
          String myFunction(Blob param1);
          result = myFunction(
            0x07);
          """)
        .loadsSuccessfully()
        .containsEvaluable(
            value(2, STRING, "result", call(2, STRING, function, blob(3, 7))));
  }

  @Test
  public void function_call_with_named_argument() {
    Function function = function(1, STRING, "myFunction", parameter(1, BLOB, "param1"));
    module("""
          String myFunction(Blob param1);
          result = myFunction(param1=
            0x07);
          """)
        .loadsSuccessfully()
        .containsEvaluable(
            value(2, STRING, "result", call(2, STRING, function, blob(3, 7))));
  }

  @Test
  public void blob_literal() {
    module("""
          result =
            0x07;
          """)
        .loadsSuccessfully()
        .containsEvaluable(value(1, BLOB, "result", blob(2, 7)));
  }

  @Test
  public void string_literal() {
    module("""
          result =
            "abc";
          """)
        .loadsSuccessfully()
        .containsEvaluable(value(1, STRING, "result", string(2, "abc")));
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
        .containsEvaluable(
            value(1, a(BLOB), "result", array(2, BLOB, blob(3, 7), blob(4, 8))));
  }

  @Test
  public void field_read() {
    Item field = field(2, STRING, "field");
    StructType myStruct = struct(1, "MyStruct", field);
    module("""
          MyStruct {
            String field,
          }
          MyStruct struct;
          result = struct
            .field;
          """)
        .loadsSuccessfully()
        .containsEvaluable(
            value(5, STRING, "result", fieldRead(6, field, valueRef(5, myStruct, "struct"))));
  }

  @Test
  public void array_type() {
    module("""
          [String] result;
          """)
        .loadsSuccessfully()
        .containsEvaluable(value(1, a(STRING), "result"));
  }

  @Test
  public void struct_type() {
    module("""
          MyStruct {
            String field
          }
          """)
        .loadsSuccessfully()
        .containsType(struct(1, "MyStruct", field(2, STRING, "field")));
  }

  @Test
  public void constructor() {
    StructType struct = struct(1, "MyStruct", field(2, STRING, "field"));
    Constructor constr = constr(1, struct, "myStruct", parameter(2, STRING, "field"));
    module("""
          MyStruct {
            String field
          }
          """)
        .loadsSuccessfully()
        .containsEvaluable(constr);
  }

  @Test
  public void constructor_call_with_argument() {
    StructType struct = struct(1, "MyStruct", field(2, STRING, "field"));
    Constructor constr = constr(1, struct, "myStruct", parameter(2, STRING, "field"));
    module("""
          MyStruct {
            String field
          }
          result = myStruct(
            "aaa");
          """)
        .loadsSuccessfully()
        .containsEvaluable(value(4, struct, "result", call(4, struct, constr, string(5, "aaa"))));
  }
}
