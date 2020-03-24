package org.smoothbuild.util;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.util.Okios.copyAllAndClose;
import static org.smoothbuild.util.Okios.readAndClose;

import org.junit.jupiter.api.Test;

import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Sink;

public class OkiosTest {
  private static final ByteString bytes = ByteString.encodeUtf8("test string");

  // copyAllAndClose()

  @Test
  public void copy_all_and_close_copies_bytes() throws Exception {
    Buffer buffer = new Buffer();
    copyAllAndClose(bufferWith(bytes), buffer);
    assertThat(buffer.readByteString())
        .isEqualTo(bytes);
  }

  @Test
  public void copy_all_and_close_works_for_empty_source() throws Exception {
    Buffer buffer = new Buffer();
    copyAllAndClose(bufferWith(ByteString.of()), buffer);
    assertThat(buffer.readByteString())
        .isEqualTo(ByteString.of());
  }

  @Test
  public void copy_all_and_close_closes_source() throws Exception {
    Buffer buffer = new Buffer();
    BufferedSource source = mock(BufferedSource.class);
    copyAllAndClose(source, buffer);
    verify(source).close();
  }

  @Test
  public void copy_all_and_close_closes_sink() throws Exception {
    Sink sink = mock(Sink.class);
    copyAllAndClose(bufferWith(ByteString.of()), sink);
    verify(sink).close();
  }

  // readAndClose()

  @Test
  public void read_and_close_copies_bytes() throws Exception {
    ByteString byteString = readAndClose(bufferWith(bytes), BufferedSource::readByteString);
    assertThat(byteString)
        .isEqualTo(bytes);
  }

  @Test
  public void read_and_close_works_for_empty_source() throws Exception {
    ByteString byteString =
        readAndClose(bufferWith(ByteString.of()), BufferedSource::readByteString);
    assertThat(byteString)
        .isEqualTo(ByteString.of());
  }

  @Test
  public void read_and_close_closes_source() throws Exception {
    BufferedSource source = mock(BufferedSource.class);
    readAndClose(source, s -> "");
    verify(source).close();
  }

  private static Buffer bufferWith(ByteString bytes) {
    Buffer buffer = new Buffer();
    buffer.write(bytes);
    return buffer;
  }
}
