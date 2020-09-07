package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.parse.component.TestModuleLoader.module;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TrailingCommaTest {
  @Nested
  class array_literal {
    @Test
    public void can_have_trailing_comma() {
      module(arrayLiteral("0x01,"))
          .loadsSuccessfully();
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
  class parameter_list {
    @Test
    public void can_have_trailing_comma() {
      module(functionDeclaration("String string,"))
          .loadsSuccessfully();
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
        myFunction(PLACEHOLDER) = "abc";
        """.replace("PLACEHOLDER", string);
    }
  }

  @Nested
  class field_list {
    @Test
    public void can_have_trailing_comma() {
      module(structDeclaration("String field,"))
          .loadsSuccessfully();
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
      module(functionCall("0x01,"))
          .loadsSuccessfully();
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
        myFunction(Blob blob) = blob;
        result = myFunction(PLACEHOLDER);
        """.replace("PLACEHOLDER", string);
    }
  }
}
