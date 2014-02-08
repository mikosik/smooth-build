package org.smoothbuild.util;

import static org.junit.Assert.fail;
import static org.smoothbuild.testing.common.StreamTester.inputStreamContaining;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.Testory.willThrow;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.Invocations.invoke;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.testory.Closure;
import org.testory.On;
import org.testory.common.Nullable;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;

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
    given(inputStream = mock(InputStream.class));
    given(willThrow(new IOException()), inputStream).read(any(byte[].class));
    try {
      Streams.inputStreamToString(inputStream);
      fail("exception should be thrown");
    } catch (IOException e) {
      // expected
    }
  }

  @Test
  public void input_stream_to_string_closes_stream() throws Exception {
    given(inputStream = mock(InputStream.class));
    given(willForwardTo(inputStreamContaining(content)), onAnyMethod());
    when(Streams.inputStreamToString(inputStream));
    thenCalled(inputStream).close();
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
    given(willThrow(new IOException()), inputStream).read(any(byte[].class));
    try {
      Streams.inputStreamToByteArray(inputStream);
      fail("exception should be thrown");
    } catch (IOException e) {
      // expected
    }
  }

  @Test
  public void input_stream_to_byte_array_closes_stream() throws Exception {
    given(inputStream = mock(InputStream.class));
    given(willForwardTo(new ByteArrayInputStream(bytes)), onAnyMethod());
    when(Streams.inputStreamToByteArray(inputStream));
    thenCalled(inputStream).close();
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

  public static On onAnyMethod() {
    return new On() {
      @Override
      public boolean matches(Invocation invocation) {
        return true;
      }
    };
  }

  // TODO remove when spying implemented in Testory
  public static Handler willForwardTo(final Object instance) {
    return new Handler() {
      @Override
      @Nullable
      public Object handle(Invocation invocation) throws Throwable {
        return invoke(invocation(invocation.method, instance, invocation.arguments));
      }
    };
  }
}
