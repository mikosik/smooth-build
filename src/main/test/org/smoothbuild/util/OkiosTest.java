package org.smoothbuild.util;

import static org.smoothbuild.util.Okios.copyAllAndClose;
import static org.smoothbuild.util.Okios.readAndClose;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Sink;

public class OkiosTest {
  private final ByteString bytes = ByteString.encodeUtf8("test string");
  private BufferedSource source;
  private Sink sink;
  private final Buffer buffer = new Buffer();

  // copyAllAndClose()

  @Test
  public void copy_all_and_close_copies_bytes() throws Exception {
    when(() -> copyAllAndClose(bufferWith(bytes), buffer));
    thenEqual(buffer.readByteString(), bytes);
  }

  @Test
  public void copy_all_and_close_works_for_empty_source() throws Exception {
    when(() -> copyAllAndClose(bufferWith(ByteString.of()), buffer));
    thenEqual(buffer.readByteString(), ByteString.of());
  }

  @Test
  public void copy_all_and_close_closes_source() throws Exception {
    given(source = mock(BufferedSource.class));
    when(() -> copyAllAndClose(source, buffer));
    thenCalled(source).close();
  }

  @Test
  public void copy_all_and_close_closes_sink() throws Exception {
    given(sink = mock(Sink.class));
    when(() -> copyAllAndClose(bufferWith(ByteString.of()), sink));
    thenCalled(sink).close();
  }

  // readAndClose()

  @Test
  public void read_and_close_copies_bytes() throws Exception {
    when(() -> readAndClose(bufferWith(bytes), source -> source.readByteString()));
    thenReturned(bytes);
  }

  @Test
  public void read_and_close_works_for_empty_source() throws Exception {
    when(() -> readAndClose(bufferWith(ByteString.of()), source -> source.readByteString()));
    thenReturned(ByteString.of());
  }

  @Test
  public void read_and_close_closes_source() throws Exception {
    given(source = mock(BufferedSource.class));
    when(() -> readAndClose(source, source -> ""));
    thenCalled(source).close();
  }

  private static Buffer bufferWith(ByteString bytes) {
    Buffer buffer = new Buffer();
    buffer.write(bytes);
    return buffer;
  }
}
