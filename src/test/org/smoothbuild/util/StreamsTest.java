package org.smoothbuild.util;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.testing.common.StreamTester.inputStreamContaining;
import static org.smoothbuild.util.Streams.copy;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.testory.common.Closure;

public class StreamsTest {
  byte[] bytes = new byte[] { 1, 2, 3 };
  String content = "content";
  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  InputStream inputStream;

  // inputStreamToString()

  @Test
  public void input_stream_to_string() throws Exception {
    given(inputStream = inputStreamContaining(content));
    when(Streams.inputStreamToString(inputStream));
    thenReturned(content);
  }

  @Test
  public void empty_input_stream_to_string() throws Exception {
    given(content = "");
    given(inputStream = inputStreamContaining(content));
    when(Streams.inputStreamToString(inputStream));
    thenReturned(content);
  }

  @Test
  public void input_stream_to_string_rethrows_io_exceptions() throws Exception {
    inputStream = mock(InputStream.class);
    BDDMockito.willThrow(IOException.class).given(inputStream).read((byte[]) Matchers.any());
    try {
      Streams.inputStreamToString(inputStream);
      fail("exception should be thrown");
    } catch (IOException e) {
      // expected
    }
  }

  // inputStreamToByteArray()

  @Test
  public void input_stream_to_byte_array() throws Exception {
    given(inputStream = new ByteArrayInputStream(bytes.clone()));
    when(Streams.inputStreamToByteArray(inputStream));
    thenReturned(bytes);
  }

  @Test
  public void empty_input_stream_to_byt_array() throws Exception {
    given(inputStream = new ByteArrayInputStream(new byte[] {}));
    when(Streams.inputStreamToByteArray(inputStream));
    thenReturned(new byte[] {});
  }

  @Test
  public void input_stream_to_byte_array_rethrows_io_exceptions() throws Exception {
    inputStream = mock(InputStream.class);
    BDDMockito.willThrow(IOException.class).given(inputStream).read((byte[]) Matchers.any());
    try {
      Streams.inputStreamToByteArray(inputStream);
      fail("exception should be thrown");
    } catch (IOException e) {
      // expected
    }
  }

  // copy()

  @Test
  public void bytes_are_copied_from_input_stream_to_output_stream() throws Exception {
    given(inputStream = new ByteArrayInputStream(bytes));
    when($copy(inputStream, outputStream));
    thenEqual(outputStream.toByteArray(), bytes);
  }

  @Test
  public void input_stream_is_closed_by_copy() throws IOException {
    inputStream = Mockito.mock(InputStream.class);
    Mockito.when(inputStream.read((byte[]) Matchers.any())).thenReturn(-1);
    Streams.copy(inputStream, outputStream);
    verify(inputStream).close();
  }

  @Test
  public void output_stream_is_closed_by_copy() throws IOException {
    outputStream = Mockito.mock(ByteArrayOutputStream.class);
    inputStream = new ByteArrayInputStream(bytes);
    copy(inputStream, outputStream);
    verify(outputStream).close();
  }

  private static Closure $copy(final InputStream inputStream,
      final ByteArrayOutputStream outputStream) {
    return new Closure() {
      @Override
      public Void invoke() throws Throwable {
        Streams.copy(inputStream, outputStream);
        return null;
      }
    };
  }
}
