package org.smoothbuild.systemtest.cli;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class ScriptParsingTest extends SystemTestCase {
  @Test
  public void syntax_error_is_reported() throws Exception {
    createUserModule("""
            result =
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(2, "mismatched input '<EOF>' expecting ");
  }

  @Test
  public void syntax_error_contains_code_with_problematic_part_marked() throws Exception {
    createUserModule("""
            result(String a, String BadName) = "abc";
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(1,
        "mismatched input 'BadName' expecting {'(', NAME}",
        "result(String a, String BadName) = \"abc\";",
        "                        ^^^^^^^");
  }

  @Test
  public void lexer_error_contains_code_with_problematic_part_marked() throws Exception {
    createUserModule("""
            func* = "abc";
            result = "abc";
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(1,
        "token recognition error at: '*'",
        "func* = \"abc\";",
        "    ^");
  }
}
