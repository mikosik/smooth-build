package org.smoothbuild.systemtest.lang;

import static com.google.common.truth.Truth.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.systemtest.SystemTestCase;

import okio.ByteString;

public class LiteralTest extends SystemTestCase {
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
    assertThat(artifactAsByteString("result"))
        .isEqualTo(ByteString.decodeHex(hexDigits));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "0",
      "1",
      "-1",
      "1234",
      "-123456",
      "123456789000000"})
  public void int_literal_value_is_decoded(String intLiteral) throws Exception {
    createUserModule("result = " + intLiteral + ";");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsByteString("result"))
        .isEqualTo(ByteString.of(new BigInteger(intLiteral, 10).toByteArray()));
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
    createUserModule("result = \"" + string + "\";");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsString("result"))
        .isEqualTo(string.translateEscapes());
  }
}
