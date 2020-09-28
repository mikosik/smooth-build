package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.acceptance.AcceptanceTestCase;

import okio.ByteString;

public class LiteralTest extends AcceptanceTestCase {
  @ParameterizedTest
  @ValueSource(strings = {
      "",
      "12",
      "1234",
      "123456",
      "ABCDEF",
      "abcdef",
      "ABCDEFabcdef"})
  public void blob_literal_value_is_decoded(String hexDigits) throws Exception {
    createUserModule("result = 0x" + hexDigits + ";");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContent("result"))
        .isEqualTo(ByteString.decodeHex(hexDigits));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "",
      "abc",
      "abcdefghijklmnopqrstuvwxyz",
      "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
      "0123456789",  // digits
      "abc←",        // unicode character
      "#",           // smooth language comment opening character
      "#abc",        // smooth language comment opening character with additional characters
      "'",           // single quote
      "\\\\",        // escaped backslash
      "\\t",         // escaped tab
      "\\b",         // escaped backspace
      "\\n",         // escaped new line
      "\\r",         // escaped carriage return
      "\\f",         // escaped form feed
      "\\\""         // escaped double quotes
  })
  public void string_literal_value_is_decoded(String string) throws Exception {
    createUserModuleRaw("result = \"" + string + "\";");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo(string.translateEscapes());
  }
}