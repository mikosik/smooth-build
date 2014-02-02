package org.smoothbuild.util;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;

public class ForwardingOutputStreamTest {
  OutputStream outputStream = mock(OutputStream.class);
  ForwardingOutputStream forwardingOutputStream = new ForwardingOutputStream(outputStream);

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
    int oneByte = 33;
    forwardingOutputStream.write(oneByte);
    verify(outputStream).write(oneByte);
    verifyNoMoreInteractions(outputStream);
  }

  @Test
  public void byte_call_with_byte_array_is_forwarded() throws IOException {
    byte[] bytes = new byte[] { 1, 2, 2 };
    forwardingOutputStream.write(bytes);
    verify(outputStream).write(bytes);
    verifyNoMoreInteractions(outputStream);
  }

  @Test
  public void byte_call_with_byte_array_and_offset_is_forwarded() throws IOException {
    byte[] bytes = new byte[] { 1, 2, 2 };
    int offset = 13;
    int length = 17;
    forwardingOutputStream.write(bytes, offset, length);
    verify(outputStream).write(bytes, offset, length);
    verifyNoMoreInteractions(outputStream);
  }

  @Test
  public void flush_call_is_forwarded() throws IOException {
    forwardingOutputStream.flush();
    verify(outputStream).flush();
    verifyNoMoreInteractions(outputStream);
  }

  @Test
  public void close_call_is_forwarded() throws IOException {
    forwardingOutputStream.close();
    verify(outputStream).close();
    verifyNoMoreInteractions(outputStream);
  }
}
