package org.smoothbuild.lang.parse.component;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.TestModuleLoader.err;
import static org.smoothbuild.lang.TestModuleLoader.module;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;

public class LiteralTest {
  @Nested
  class blob_literal {
    @ParameterizedTest
    @ValueSource(strings = {
        "0x",
        "0x12",
        "0x1234",
        "0x12345678",
        "0xabcdef",
        "0xABCDEF",
        "0xabcdefABCDEF"})
    public void is_legal(String literal) {
      module("result = " + literal + ";")
          .loadsSuccessfully();
    }

    @Nested
    class causes_error_when {
      @Test
      public void has_only_one_digit() {
        module("result = 0x1;")
            .loadsWithError(1, "Illegal Blob literal. Expected even number of digits.");
      }

      @Test
      public void has_odd_number_of_digits() {
        module("result = 0x123;")
            .loadsWithError(1, "Illegal Blob literal. Expected even number of digits.");
      }

      @Test
      public void has_non_digit_character() {
        module("result = 0xGG;")
            .loadsWithError(1, """
              extraneous input 'GG' expecting ';'
              result = 0xGG;
                         ^^""");
      }
    }
  }

  @Nested
  class string_literal {
    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "abc",
        "abcdefghijklmnopqrstuvwxyz",
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
        "0123456789",  // digits
        "abc←",        // unicode character
        "#",           // smooth language comment opening character
        "'",           // single quote
        "\\\\",        // escaped backslash
        "\\t",         // escaped tab
        "\\b",         // escaped backspace
        "\\n",         // escaped new line
        "\\r",         // escaped carriage return
        "\\f",         // escaped form feed
        "\\\""         // escaped double quotes
    })
    public void is_legal(String literal) {
      module("result = \"" + literal + "\";")
          .loadsSuccessfully();
    }

    @Nested
    class causes_error_when {
      @Test
      public void has_no_closing_quote() {
        module("""
             result = "abc;
             """)
            .loadsWithProblems();
      }

      @Test
      public void spans_to_next_line() {
        module("""
             result = "ab
             cd";
             """)
            .loadsWithProblems();
      }

      @Test
      public void has_illegal_escape_sequence() {
        module("""
             result = "\\A";
             """)
            .loadsWithError(1, "Illegal escape sequence at char index = 1. "
                + "Legal sequences are: \\t \\b \\n \\r \\f \\\" \\\\.");
      }

      @Test
      public void has_escape_sequence_without_code() {
        module("""
             result = "\\";
             """)
            .loadsWithError(1, "Missing escape code after backslash \\ at char index = 0.");
      }
    }
  }

  @Nested
  class array_literal {
    @ParameterizedTest
    @ArgumentsSource(ArrayElements.class)
    public void with_one_element(String literal) {
      module("result = [" + literal + "];")
          .loadsSuccessfully();
    }

    @ParameterizedTest
    @ArgumentsSource(ArrayElements.class)
    public void with_two_elements(String literal) {
      module("result = [" + literal + ", " + literal + "];")
          .loadsSuccessfully();
    }

    @ParameterizedTest
    @ArgumentsSource(ArrayElements.class)
    public void with_array_containing_one_element(String literal) {
      module("result = [[" + literal + "]];")
          .loadsSuccessfully();
    }

    @ParameterizedTest
    @ArgumentsSource(ArrayElements.class)
    public void with_array_and_empty_array_elements(String literal) {
      module("result = [[" + literal + "], []];")
          .loadsSuccessfully();
    }

    @ParameterizedTest
    @ArgumentsSource(ArrayElements.class)
    public void with_array_containing_two_elements(String literal) {
      module("result = [[" + literal + ", " + literal + "]];")
          .loadsSuccessfully();
    }

    private static class ArrayElements implements ArgumentsProvider {
      @Override
      public Stream<Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
            arguments("[]"),
            arguments("0x01"),
            arguments("\"abc\"")
        );
      }
    }

    @Test
    public void error_in_first_element_doesnt_suppress_error_in_second_element() {
      module("""
            myFunction() = "abc";
            result = [
              myFunction(unknown1=""),
              myFunction(unknown2="")
            ];
            """)
          .loadsWithErrors(List.of(
              err(3, "In call to `myFunction`: Unknown parameter `unknown1`."),
              err(4, "In call to `myFunction`: Unknown parameter `unknown2`.")
          ));
    }
  }
}
