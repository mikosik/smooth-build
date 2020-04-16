package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class StringLiteralTest extends AcceptanceTestCase {
  @Test
  public void string_literal() throws IOException {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("abc");
  }

  @Test
  public void missing_closing_quote() throws Exception {
    givenScript(
        "  result = 'abc;  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
  }

  @Test
  public void spanning_to_next_line() throws Exception {
    givenScript(
        "  result = 'ab\nc';  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
  }

  @Test
  public void empty_string() throws IOException {
    givenScript(
        "  result = '';  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("");
  }

  @Test
  public void with_letters() throws IOException {
    givenScript(
        "  result = 'abcdefghijklmnopqrstuvwxyz';  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("abcdefghijklmnopqrstuvwxyz");
  }

  @Test
  public void with_capital_letters() throws IOException {
    givenScript(
        "  result = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
  }

  @Test
  public void with_digits() throws IOException {
    givenScript(
        "  result = '0123456789';  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("0123456789");
  }

  @Test
  public void with_smooth_lang_comment_character() throws IOException {
    givenScript(
        "  result = '#';  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("#");
  }

  @Test
  public void with_single_quotes() throws IOException {
    givenRawScript("result = \"'\";");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("'");
  }

  @Test
  public void with_escaped_backslash() throws IOException {
    givenScript(
        "  result = '\\\\';  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("\\");
  }

  @Test
  public void with_escaped_tab() throws IOException {
    givenScript(
        "  result = '\\t';  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("\t");
  }

  @Test
  public void with_escaped_backspace() throws IOException {
    givenScript(
        "  result = '\\b';  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("\b");
  }

  @Test
  public void with_escaped_new_line() throws IOException {
    givenScript(
        "  result = '\\n';  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("\n");
  }

  @Test
  public void with_escaped_carriage_return() throws IOException {
    givenScript(
        "  result = '\\r';  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("\r");
  }

  @Test
  public void with_escaped_form_feed() throws IOException {
    givenScript(
        "  result = '\\r';  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("\r");
  }

  @Test
  public void with_escaped_double_quotes() throws IOException {
    givenScript(
        "  result = '\\\"';  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("\"");
  }

  @Test
  public void with_illegal_escape_sequence() throws IOException {
    givenScript(
        "  result = '\\A';  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(1,
        "Illegal escape sequence. Legal sequences are: \\t \\b \\n \\r \\f \\\" \\\\.\n");
  }
}
