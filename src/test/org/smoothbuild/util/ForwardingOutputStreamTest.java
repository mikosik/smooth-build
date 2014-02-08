package org.smoothbuild.util;

import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.Testory.willThrow;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;
import org.testory.On;
import org.testory.TestoryAssertionError;
import org.testory.proxy.Invocation;

public class ForwardingOutputStreamTest {
  OutputStream outputStream = mock(OutputStream.class);
  ForwardingOutputStream forwardingOutputStream = new ForwardingOutputStream(outputStream);

  int oneByte = 33;
  byte[] bytes = new byte[] { 1, 2, 3 };
  int offset = 13;
  int length = 17;

  @Before
  public void before() {
    given(willThrow(new TestoryAssertionError()), onInstance(outputStream));
  }

  @Test
  public void null_output_stream_is_forbidden() throws Exception {
    try {
      new ForwardingOutputStream(null);
      fail("exception should be thrown");
    } catch (NullPointerException e) {
      // expected
    }
  }

  @Test
  public void byte_call_with_one_byte_is_forwarded() throws IOException {
    given(willReturn(null), outputStream).write(oneByte);
    when(forwardingOutputStream).write(oneByte);
    thenCalled(outputStream).write(oneByte);
  }

  @Test
  public void byte_call_with_byte_array_is_forwarded() throws IOException {
    given(willReturn(null), outputStream).write(bytes);
    when(forwardingOutputStream).write(bytes);
    thenCalled(outputStream).write(bytes);
  }

  @Test
  public void byte_call_with_byte_array_and_offset_is_forwarded() throws IOException {
    given(willReturn(null), outputStream).write(bytes, offset, length);
    when(forwardingOutputStream).write(bytes, offset, length);
    thenCalled(outputStream).write(bytes, offset, length);
  }

  @Test
  public void flush_call_is_forwarded() throws IOException {
    given(willReturn(null), outputStream).flush();
    when(forwardingOutputStream).flush();
    thenCalled(outputStream).flush();
  }

  @Test
  public void close_call_is_forwarded() throws IOException {
    given(willReturn(null), outputStream).close();
    when(forwardingOutputStream).close();
    thenCalled(outputStream).close();
  }

  private static On onInstance(final Object mock) {
    return new On() {
      @Override
      public boolean matches(Invocation invocation) {
        return mock == invocation.instance;
      }
    };
  }
}
