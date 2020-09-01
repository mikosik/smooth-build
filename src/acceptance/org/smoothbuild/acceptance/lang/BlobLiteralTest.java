package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

import okio.ByteString;

public class BlobLiteralTest extends AcceptanceTestCase {
  @Test
  public void zero_digits_is_acceptable() throws Exception {
    createUserModule("""
            result = 0x;
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContent("result"))
        .isEqualTo(ByteString.of());
  }

  @Test
  public void one_digit_causes_error() throws Exception {
    createUserModule("""
            result = 0x1;
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(
        1, "Illegal Blob literal. Expected even number of digits.");
  }

  @Test
  public void two_digits_literal() throws IOException {
    createUserModule("""
            result = 0x0102;
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContent("result"))
        .isEqualTo(ByteString.of((byte) 1, (byte) 2));
  }

  @Test
  public void odd_number_of_digits_causes_error() throws Exception {
    createUserModule("""
            result = 0x123;
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(
        1, "Illegal Blob literal. Expected even number of digits.");
  }

  @Test
  public void many_digits_literal() throws IOException {
    createUserModule("""
            result = 0x010203;
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContent("result"))
        .isEqualTo(ByteString.of((byte) 1, (byte) 2, (byte) 3));
  }

  @Test
  public void non_digit_char_causes_error() throws Exception {
    createUserModule("""
            result = 0xGG;
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(
        1, "extraneous input 'GG' expecting ';'");
  }

  @Test
  public void large_letters_are_decoded() throws IOException {
    createUserModule("""
            result = 0xABCDEF;
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContent("result"))
        .isEqualTo(ByteString.of((byte) 0xAB, (byte) 0xCD, (byte) 0xEF));
  }

  @Test
  public void small_letters_are_decoded() throws IOException {
    createUserModule("""
            result = 0xabcdef;
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContent("result"))
        .isEqualTo(ByteString.of((byte) 0xAB, (byte) 0xCD, (byte) 0xEF));
  }
}
