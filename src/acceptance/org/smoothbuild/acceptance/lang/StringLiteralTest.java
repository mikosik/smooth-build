package org.smoothbuild.acceptance.lang;

import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class StringLiteralTest extends AcceptanceTestCase {
  @Test
  public void string_literal() throws IOException {
    givenBuildScript(script("result : 'abc';"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("abc"));
  }

  @Test
  public void missing_closing_quote() throws Exception {
    givenBuildScript(script("result : 'abc;"));
    whenRunSmoothBuild("result");
    thenReturnedCode(1);
  }

  @Test
  public void empty_string() throws IOException {
    givenBuildScript(script("result : '';"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent(""));
  }

  @Test
  public void with_letters() throws IOException {
    givenBuildScript(script("result : 'abcdefghijklmnopqrstuvwxyz';"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("abcdefghijklmnopqrstuvwxyz"));
  }

  @Test
  public void with_capital_letters() throws IOException {
    givenBuildScript(script("result : 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
  }

  @Test
  public void with_digits() throws IOException {
    givenBuildScript(script("result : '0123456789';"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("0123456789"));
  }

  @Test
  public void with_smooth_lang_comment_character() throws IOException {
    givenBuildScript(script("result : '#';"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("#"));
  }

  @Test
  public void with_single_quotes() throws IOException {
    givenBuildScript("result : \"'\";");
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("'"));
  }

  @Test
  public void with_escaped_backslash() throws IOException {
    givenBuildScript(script("result : '\\\\';"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("\\"));
  }

  @Test
  public void with_escaped_tab() throws IOException {
    givenBuildScript(script("result : '\\t';"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("\t"));
  }

  @Test
  public void with_escaped_backspace() throws IOException {
    givenBuildScript(script("result : '\\b';"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("\b"));
  }

  @Test
  public void with_escaped_new_line() throws IOException {
    givenBuildScript(script("result : '\\n';"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("\n"));
  }

  @Test
  public void with_escaped_carriage_return() throws IOException {
    givenBuildScript(script("result : '\\r';"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("\r"));
  }

  @Test
  public void with_escaped_form_feed() throws IOException {
    givenBuildScript(script("result : '\\r';"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("\r"));
  }

  @Test
  public void with_escaped_double_quotes() throws IOException {
    givenBuildScript(script("result : '\\\"';"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("\""));
  }
}
