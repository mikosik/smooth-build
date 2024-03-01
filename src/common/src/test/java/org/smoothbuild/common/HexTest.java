package org.smoothbuild.common;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Locale.ROOT;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.util.ArrayList;
import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class HexTest {
  @Test
  public void empty_string_is_decoded() throws DecodeHexException {
    assertThat(Hex.decode("")).isEqualTo(ByteString.of());
  }

  @ParameterizedTest
  @MethodSource("one_byte_values")
  public void one_byte_value_is_decoded(byte value, String encoded) throws DecodeHexException {
    assertThat(Hex.decode(encoded)).isEqualTo(ByteString.of(value));
  }

  private static Iterable<Arguments> one_byte_values() {
    ArrayList<Arguments> result = new ArrayList<>();
    String digits = "0123456789ABCDEF";
    for (int i = 0; i < 16; i++) {
      for (int j = 0; j < 16; j++) {
        String encoded = Character.toString(digits.charAt(i)) + digits.charAt(j);
        byte value = (byte) (16 * i + j);
        result.add(arguments(value, encoded));
        if (10 < i || 10 < j) {
          result.add(arguments(value, encoded.toLowerCase(ROOT)));
        }
      }
    }
    return result;
  }

  @Test
  public void two_bytes_value_is_decoded() throws DecodeHexException {
    assertThat(Hex.decode("0102")).isEqualTo(ByteString.of((byte) 1, (byte) 2));
  }

  @ParameterizedTest
  @ValueSource(strings = {"1", "123", "12345", "1234567"})
  public void odd_count_of_digits_causes_error(String string) {
    assertCall(() -> Hex.decode(string))
        .throwsException(DecodeHexException.expectedEvenNumberOfDigits());
  }

  @Test
  public void invalid_hex_digit_causes_error() {
    assertCall(() -> Hex.decode("1M")).throwsException(DecodeHexException.invalidHexDigits("M"));
  }
}
