package org.smoothbuild.util;

import static org.junit.Assert.fail;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.util.Streams.copy;
import static org.smoothbuild.util.Streams.inputStreamToByteArray;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.smoothbuild.util.Streams.writeAndClose;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.spy;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.Testory.willThrow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;
import org.testory.Closure;

public class StreamsTest {
  byte[] bytes = new byte[] { 1, 2, 3 };
  String content = "content";
  OutputStream outputStream = new ByteArrayOutputStream();
  InputStream inputStream;

  // inputStreamToString()

  @Test
  public void input_stream_to_string() throws Exception {
    given(inputStream = new ByteArrayInputStream(content.getBytes(CHARSET)));
    when(inputStreamToString(inputStream));
    thenReturned(content);
  }

  @Test
  public void empty_input_stream_to_string() throws Exception {
    given(content = "");
    given(inputStream = new ByteArrayInputStream(content.getBytes(CHARSET)));
    when(inputStreamToString(inputStream));
    thenReturned(content);
  }

  @Test
  public void input_stream_to_string_rethrows_io_exceptions() throws Exception {
    given(inputStream = mock(InputStream.class));
    given(willThrow(new IOException()), inputStream).read(any(byte[].class));
    try {
      inputStreamToString(inputStream);
      fail("exception should be thrown");
    } catch (IOException e) {
      // expected
    }
  }

  @Test
  public void input_stream_to_string_closes_stream() throws Exception {
    given(inputStream = spy(new ByteArrayInputStream(content.getBytes(CHARSET))));
    when(inputStreamToString(inputStream));
    thenCalled(inputStream).close();
  }

  // inputStreamToByteArray()

  @Test
  public void input_stream_to_byte_array() throws Exception {
    given(inputStream = new ByteArrayInputStream(bytes.clone()));
    when(inputStreamToByteArray(inputStream));
    thenReturned(bytes);
  }

  @Test
  public void empty_input_stream_to_byt_array() throws Exception {
    given(inputStream = new ByteArrayInputStream(new byte[] {}));
    when(inputStreamToByteArray(inputStream));
    thenReturned(new byte[] {});
  }

  @Test
  public void input_stream_to_byte_array_rethrows_io_exceptions() throws Exception {
    inputStream = mock(InputStream.class);
    given(willThrow(new IOException()), inputStream).read(any(byte[].class));
    try {
      inputStreamToByteArray(inputStream);
      fail("exception should be thrown");
    } catch (IOException e) {
      // expected
    }
  }

  @Test
  public void input_stream_to_byte_array_closes_stream() throws Exception {
    given(inputStream = spy(new ByteArrayInputStream(bytes)));
    when(inputStreamToByteArray(inputStream));
    thenCalled(inputStream).close();
  }

  // copy()

  @Test
  public void bytes_are_copied_from_input_stream_to_output_stream() throws Exception {
    given(inputStream = new ByteArrayInputStream(bytes));
    when($copy(inputStream, outputStream));
    thenEqual(((ByteArrayOutputStream) outputStream).toByteArray(), bytes);
  }

  @Test
  public void input_stream_is_closed_by_copy() throws IOException {
    given(inputStream = mock(InputStream.class));
    given(willReturn(-1), inputStream).read(any(byte[].class));
    when($copy(inputStream, outputStream));
    thenCalled(inputStream).close();
  }

  @Test
  public void output_stream_is_closed_by_copy() throws IOException {
    given(outputStream = mock(ByteArrayOutputStream.class));
    given(inputStream = new ByteArrayInputStream(bytes));
    when($copy(inputStream, outputStream));
    thenCalled(outputStream).close();
  }

  // writeAndClose

  @Test
  public void write_and_close() throws IOException {
    given(content = "content");
    given(outputStream = new ByteArrayOutputStream());
    when(writeAndClose(outputStream, content));
    thenEqual(outputStream.toString(), content);
  }

  @Test
  public void write_and_close_closes_stream() throws IOException {
    given(content = "content");
    given(outputStream = mock(ByteArrayOutputStream.class));
    when(writeAndClose(outputStream, content));
    thenCalled(outputStream).close();
  }

  @Test
  public void write_and_close_empty() throws IOException {
    given(content = "");
    given(outputStream = new ByteArrayOutputStream());
    when(writeAndClose(outputStream, content));
    thenEqual(outputStream.toString(), content);
  }

  private static Closure $copy(InputStream inputStream, OutputStream outputStream) {
    return () -> {
      copy(inputStream, outputStream);
      return null;
    };
  }
}
