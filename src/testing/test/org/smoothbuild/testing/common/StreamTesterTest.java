package org.smoothbuild.testing.common;

import static org.junit.Assert.fail;
import static org.smoothbuild.testing.common.StreamTester.assertContent;
import static org.smoothbuild.testing.common.StreamTester.inputStreamContaining;
import static org.smoothbuild.testing.common.StreamTester.inputStreamToBytes;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.testory.Closure;

public class StreamTesterTest {
  private String content;
  private InputStream inputStream;
  private ByteArrayOutputStream outputStream;

  @Test
  public void input_stream_containing_string() throws Exception {
    given(content = "content");
    given(inputStream = inputStreamContaining(content));
    when($inputStreamToString(inputStream));
    thenReturned(content);
  }

  @Test
  public void input_stream_containing_empty_string() throws Exception {
    given(content = "");
    given(inputStream = inputStreamContaining(content));
    when($inputStreamToString(inputStream));
    thenReturned(content);
  }

  @Test
  public void write_and_close() throws IOException {
    given(content = "content");
    given(outputStream = new ByteArrayOutputStream());
    when($writeAndClose(outputStream, content));
    thenEqual(outputStream.toString(), content);
  }

  @Test
  public void write_and_close_empty() throws IOException {
    given(content = "");
    given(outputStream = new ByteArrayOutputStream());
    when($writeAndClose(outputStream, content));
    thenEqual(outputStream.toString(), content);
  }

  @Test
  public void testAssertContentSucceedsWhenContentIsEqual() throws Exception {
    String content = "content";
    InputStream inputStream = inputStreamContaining(content);

    assertContent(inputStream, content);
  }

  @Test
  public void testAssertContentFailsWhenContentIsNotEqual() throws Exception {
    String content = "content";
    InputStream inputStream = inputStreamContaining(content + "suffix");

    try {
      assertContent(inputStream, content);
    } catch (AssertionError e) {
      // expected
      return;
    }
    fail("exception should be thrown");
  }

  // inputStreamToBytes()

  @Test
  public void input_stream_to_bytes() throws Exception {
    given(content = "content");
    given(inputStream = inputStreamContaining(content));
    when(inputStreamToBytes(inputStream));
    thenReturned(content.getBytes());
  }

  @Test
  public void empty_input_stream_to_bytes() throws Exception {
    given(content = "");
    given(inputStream = inputStreamContaining(content));
    when(inputStreamToBytes(inputStream));
    thenReturned(content.getBytes());
  }

  @Test
  public void inputStreamToBytesClosesStream() throws Exception {
    InputStream inputStream = mock(InputStream.class);
    given(willReturn(-1), inputStream).read(any(byte[].class));

    inputStreamToBytes(inputStream);
    thenCalled(inputStream).close();
  }

  private static ByteArrayInputStream inputStream(ByteArrayOutputStream outputStream) {
    return new ByteArrayInputStream(outputStream.toByteArray());
  }

  private static Closure $inputStreamToString(final InputStream inputStream) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return inputStreamToString(inputStream);
      }
    };

  }

  private static Closure $writeAndClose(final ByteArrayOutputStream outputStream,
      final String content) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        writeAndClose(outputStream, content);
        return null;
      }
    };
  }
}
