package org.smoothbuild.acceptance.cmd;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ScriptParsingTest extends AcceptanceTestCase {
  @Test
  public void syntax_error_is_reported() throws Exception {
    givenScript(
        "  result =  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(1, "mismatched input '<EOF>' expecting ");
  }

  @Test
  public void syntax_error_contains_code_with_problematic_part_marked() throws Exception {
    givenScript(
        "  result(String a, String BadName) = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains(
        "build.smooth:1: error: mismatched input 'BadName' expecting IDENTIFIER",
        "  result(String a, String BadName) = \"abc\";",
        "                          ^^^^^^^",
        "");
  }

  @Test
  public void lexer_error_contains_code_with_problematic_part_marked() throws Exception {
    givenScript(
        "  function* = 'abc';  ",
        "  result = 'abc';     ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains(
        "build.smooth:1: error: token recognition error at: '*'",
        "  function* = \"abc\";  ",
        "          ^",
        "");
  }
}
