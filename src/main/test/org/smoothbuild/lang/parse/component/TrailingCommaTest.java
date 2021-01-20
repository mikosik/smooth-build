package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.TestModuleLoader.module;
import static org.smoothbuild.lang.TestingLang.array;
import static org.smoothbuild.lang.TestingLang.blob;
import static org.smoothbuild.lang.TestingLang.call;
import static org.smoothbuild.lang.TestingLang.function;
import static org.smoothbuild.lang.TestingLang.parameter;
import static org.smoothbuild.lang.TestingLang.struct;
import static org.smoothbuild.lang.TestingLang.value;
import static org.smoothbuild.lang.base.type.TestingItemSignature.itemSignature;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.a;
import static org.smoothbuild.lang.base.type.TestingTypes.f;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.Function;

public class TrailingCommaTest {
  @Nested
  class array_literal {
    @Test
    public void can_have_trailing_comma() {
      module(arrayLiteral("0x07,"))
          .loadsSuccessfully()
          .containsDeclared(value(1, a(BLOB), "result", array(1, BLOB, blob(1, 7))));
    }

    @Test
    public void cannot_have_only_comma() {
      module(arrayLiteral(","))
          .loadsWithProblems();
    }

    @Test
    public void cannot_have_leading_comma() {
      module(arrayLiteral(",0x01"))
          .loadsWithProblems();
    }

    @Test
    public void cannot_have_two_trailing_commas() {
      module(arrayLiteral("0x01,,"))
          .loadsWithProblems();
    }

    private String arrayLiteral(CharSequence string) {
      return """
          result = [ PLACEHOLDER ];
          """.replace("PLACEHOLDER", string);
    }
  }

  @Nested
  class _function_parameter_list {
    @Test
    public void can_have_trailing_comma() {
      module(functionDeclaration("String param1,"))
          .loadsSuccessfully()
          .containsDeclared(function(1, STRING, "myFunction", parameter(1, STRING, "param1")));
    }

    @Test
    public void cannot_have_only_comma() {
      module(functionDeclaration(","))
          .loadsWithProblems();
    }

    @Test
    public void cannot_have_leading_comma() {
      module(functionDeclaration(",String string"))
          .loadsWithProblems();
    }

    @Test
    public void cannot_have_two_trailing_commas() {
      module(functionDeclaration("String string,,"))
          .loadsWithProblems();
    }

    private String functionDeclaration(CharSequence string) {
      return """
        String myFunction(PLACEHOLDER);
        """.replace("PLACEHOLDER", string);
    }
  }

  @Nested
  class _function_type_parameter_list {
    @Test
    public void can_have_trailing_comma() {
      module(functionTypeDeclaration("String,"))
          .loadsSuccessfully()
          .containsDeclared(value(1, f(BLOB, STRING), "myValue"));
    }

    @Test
    public void cannot_have_only_comma() {
      module(functionTypeDeclaration(","))
          .loadsWithProblems();
    }

    @Test
    public void cannot_have_leading_comma() {
      module(functionTypeDeclaration(",String"))
          .loadsWithProblems();
    }

    @Test
    public void cannot_have_two_trailing_commas() {
      module(functionTypeDeclaration("String,,"))
          .loadsWithProblems();
    }

    private String functionTypeDeclaration(CharSequence string) {
      return """
        Blob(PLACEHOLDER) myValue;
        """.replace("PLACEHOLDER", string);
    }
  }

  @Nested
  class field_list {
    @Test
    public void can_have_trailing_comma() {
      module(structDeclaration("String field,"))
          .loadsSuccessfully()
          .containsType(struct("MyStruct", itemSignature(STRING, "field")));
    }

    @Test
    public void cannot_have_only_comma() {
      module(structDeclaration(","))
          .loadsWithProblems();
    }

    @Test
    public void cannot_have_leading_comma() {
      module(structDeclaration(", String field"))
          .loadsWithProblems();
    }

    @Test
    public void cannot_have_two_trailing_commas() {
      module(structDeclaration("String field,,"))
          .loadsWithProblems();
    }

    private String structDeclaration(CharSequence string) {
      return """
        MyStruct { PLACEHOLDER }
        """.replace("PLACEHOLDER", string);
    }
  }

  @Nested
  class argument_list {
    @Test
    public void can_have_trailing_comma() {
      Function function = function(1, BLOB, "myFunction", parameter(1, BLOB, "blob"));
      module(functionCall("0x07,"))
          .loadsSuccessfully()
          .containsDeclared(value(2, BLOB, "result", call(2, BLOB, function, blob(2, 7))));
    }

    @Test
    public void cannot_have_only_comma() {
      module(functionCall(","))
          .loadsWithProblems();
    }

    @Test
    public void cannot_have_leading_comma() {
      module(functionCall(","))
          .loadsWithProblems();
    }

    @Test
    public void cannot_have_two_trailing_commas() {
      module(functionCall("0x01,,"))
          .loadsWithProblems();
    }

    private String functionCall(CharSequence string) {
      return """
        Blob myFunction(Blob blob);
        result = myFunction(PLACEHOLDER);
        """.replace("PLACEHOLDER", string);
    }
  }
}
