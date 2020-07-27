package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class StringLiteralTest extends AcceptanceTestCase {
  @Test
  public void string_literal() throws IOException {
    createUserModule(
        "  result = 'abc';  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void missing_closing_quote() throws Exception {
    createUserModule(
        "  result = 'abc;  ");
    runSmoothBuild("result");
    assertFinishedWithError();
  }

  @Test
  public void spanning_to_next_line() throws Exception {
    createUserModule(
        "  result = 'ab\nc';  ");
    runSmoothBuild("result");
    assertFinishedWithError();
  }

  @Test
  public void empty_string() throws IOException {
    createUserModule(
        "  result = '';  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("");
  }

  @Test
  public void with_letters() throws IOException {
    createUserModule(
        "  result = 'abcdefghijklmnopqrstuvwxyz';  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abcdefghijklmnopqrstuvwxyz");
  }

  @Test
  public void with_capital_letters() throws IOException {
    createUserModule(
        "  result = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
  }

  @Test
  public void with_digits() throws IOException {
    createUserModule(
        "  result = '0123456789';  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("0123456789");
  }

  @Test
  public void with_unicode_in_utf8() throws IOException {
    createUserModule(
        "  result = 'abc←';  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc←");
  }

  @Test
  public void with_smooth_lang_comment_character() throws IOException {
    createUserModule(
        "  result = '#';  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("#");
  }

  @Test
  public void with_single_quotes() throws IOException {
    createUserModuleRaw("result = \"'\";");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("'");
  }

  @Test
  public void with_escaped_backslash() throws IOException {
    createUserModule(
        "  result = '\\\\';  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("\\");
  }

  @Test
  public void with_escaped_tab() throws IOException {
    createUserModule(
        "  result = '\\t';  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("\t");
  }

  @Test
  public void with_escaped_backspace() throws IOException {
    createUserModule(
        "  result = '\\b';  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("\b");
  }

  @Test
  public void with_escaped_new_line() throws IOException {
    createUserModule(
        "  result = '\\n';  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("\n");
  }

  @Test
  public void with_escaped_carriage_return() throws IOException {
    createUserModule(
        "  result = '\\r';  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("\r");
  }

  @Test
  public void with_escaped_form_feed() throws IOException {
    createUserModule(
        "  result = '\\r';  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("\r");
  }

  @Test
  public void with_escaped_double_quotes() throws IOException {
    createUserModule(
        "  result = '\\\"';  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("\"");
  }

  @Test
  public void with_illegal_escape_sequence() throws IOException {
    createUserModule(
        "  result = '\\A';  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(1,
        "Illegal escape sequence. Legal sequences are: \\t \\b \\n \\r \\f \\\" \\\\.\n");
  }
}
