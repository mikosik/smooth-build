package org.smoothbuild.util.io;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.util.io.Okios.copyAllAndClose;
import static org.smoothbuild.util.io.Okios.intToByteString;
import static org.smoothbuild.util.io.Okios.readAndClose;
import static org.smoothbuild.util.io.Okios.writeAndClose;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Sink;

public class OkiosTest {
  private static final ByteString bytes = ByteString.encodeUtf8("test string");

  @Nested
  class _copy_all_and_close {
    @Test
    public void copies_bytes() throws Exception {
      Buffer buffer = new Buffer();
      copyAllAndClose(bufferWith(bytes), buffer);
      assertThat(buffer.readByteString())
          .isEqualTo(bytes);
    }

    @Test
    public void works_for_empty_source() throws Exception {
      Buffer buffer = new Buffer();
      copyAllAndClose(bufferWith(ByteString.of()), buffer);
      assertThat(buffer.readByteString())
          .isEqualTo(ByteString.of());
    }

    @Test
    public void closes_source() throws Exception {
      Buffer buffer = new Buffer();
      BufferedSource source = mock(BufferedSource.class);
      copyAllAndClose(source, buffer);
      verify(source).close();
    }

    @Test
    public void closes_sink() throws Exception {
      Sink sink = mock(Sink.class);
      copyAllAndClose(bufferWith(ByteString.of()), sink);
      verify(sink).close();
    }
  }

  @Nested
  class _read_and_close {
    @Test
    public void copies_bytes() throws Exception {
      ByteString byteString = readAndClose(bufferWith(bytes), BufferedSource::readByteString);
      assertThat(byteString)
          .isEqualTo(bytes);
    }

    @Test
    public void works_for_empty_source() throws Exception {
      ByteString byteString =
          readAndClose(bufferWith(ByteString.of()), BufferedSource::readByteString);
      assertThat(byteString)
          .isEqualTo(ByteString.of());
    }

    @Test
    public void closes_source() throws Exception {
      BufferedSource source = mock(BufferedSource.class);
      readAndClose(source, s -> "");
      verify(source).close();
    }
  }

  @Nested
  class _write_and_close {
    @Test
    public void copies_bytes() throws Exception {
      Buffer buffer = new Buffer();
      writeAndClose(buffer, s -> s.write(bytes));
      assertThat(buffer.readByteString())
          .isEqualTo(bytes);
    }

    @Test
    public void works_for_empty_sink() throws Exception {
      Buffer buffer = new Buffer();
      writeAndClose(buffer, s -> s.write(ByteString.of()));
      assertThat(buffer.readByteString())
          .isEqualTo(ByteString.of());
    }

    @Test
    public void closes_sink() throws Exception {
      BufferedSink sink = mock(BufferedSink.class);
      writeAndClose(sink, s -> {});
      verify(sink).close();
    }
  }

  private static Buffer bufferWith(ByteString bytes) {
    Buffer buffer = new Buffer();
    buffer.write(bytes);
    return buffer;
  }

  @Nested
  class _int_to_byte_string {
    @ParameterizedTest
    @MethodSource("int_to_bytestring_cases")
    public void name(int anInt, ByteString byteString) {
      assertThat(intToByteString(anInt))
          .isEqualTo(byteString);
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
          arguments(0x12345678, ByteString.of((byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78))
      );
    }
  }
}
