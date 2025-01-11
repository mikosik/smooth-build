package org.smoothbuild.cli.accept;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.evaluator.testing.EvaluatorTestContext;

public class ParseErrorsTest extends EvaluatorTestContext {
  @Test
  void syntax_error_is_reported() throws Exception {
    createUserModule("result =");
    evaluate("result");
    assertThat(logs())
        .containsExactly(
            userError(
                1,
                """
                mismatched input '<EOF>' expecting {'<', '(', '[', NAME, INT, BLOB, STRING}
                result =
                        ^"""));
  }

  @Test
  void syntax_error_contains_code_with_problematic_part_marked() throws Exception {
    createUserModule("result =");
    evaluate("result");
    assertThat(logs())
        .containsExactly(
            userError(
                1,
                """
                mismatched input '<EOF>' expecting {'<', '(', '[', NAME, INT, BLOB, STRING}
                result =
                        ^"""));
  }

  @Test
  void lexer_error_contains_code_with_problematic_part_marked() throws Exception {
    createUserModule("""
            func* = "abc";
            result = "abc";
            """);
    evaluate("result");
    assertThat(logs())
        .containsExactly(
            userError(
                1,
                """
            token recognition error at: '*'
            func* = "abc";
                ^"""));
  }
}
