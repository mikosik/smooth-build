package org.smoothbuild.common.io;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.io.Okios.intToByteString;

import java.util.List;
import okio.ByteString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class OkiosTest {
  @Nested
  class _int_to_byte_string {
    @ParameterizedTest
    @MethodSource("int_to_bytestring_cases")
    public void name(int anInt, ByteString byteString) {
      assertThat(intToByteString(anInt)).isEqualTo(byteString);
    }

    private static List<Arguments> int_to_bytestring_cases() {
      return List.of(
          arguments(-128, ByteString.of((byte) 0x80)),
          arguments(-1, ByteString.of((byte) 0xFF)),
          arguments(0, ByteString.of((byte) 0)),
          arguments(1, ByteString.of((byte) 1)),
          arguments(127, ByteString.of((byte) 127)),
          arguments(0x00000013, ByteString.of((byte) 0x13)),
          arguments(0xFFFFFF13, ByteString.of((byte) 0xFF, (byte) 0x13)),
          arguments(0xFFFFFFFF, ByteString.of((byte) 0xFF)),
          arguments(0x12345678, ByteString.of((byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78)));
    }
  }
}
