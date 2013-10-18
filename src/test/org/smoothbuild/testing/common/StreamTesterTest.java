package org.smoothbuild.testing.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.testing.common.StreamTester.assertContent;
import static org.smoothbuild.testing.common.StreamTester.inputStreamToBytes;
import static org.smoothbuild.testing.common.StreamTester.inputStreamToString;
import static org.smoothbuild.testing.common.StreamTester.inputStreamWithContent;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class StreamTesterTest {

  @Test
  public void testInputStreamWithContent() throws Exception {
    String content = "content";
    InputStream inputStream = inputStreamWithContent(content);
    assertThat(inputStreamToString(inputStream)).isEqualTo(content);
  }

  @Test
  public void testInputStreamWithEmptyContent() throws Exception {
    String content = "";
    InputStream inputStream = inputStreamWithContent(content);
    assertThat(inputStreamToString(inputStream)).isEqualTo(content);
  }

  @Test
  public void testWriteAndClose() throws IOException {
    String content = "content to test.";
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    writeAndClose(outputStream, content);

    assertContent(inputStream(outputStream), content);
  }

  @Test
  public void testWriteAndCloseWithEmptyContent() throws IOException {
    String content = "";
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    writeAndClose(outputStream, content);

    assertContent(inputStream(outputStream), content);
  }

  @Test
  public void testAssertContentSucceedsWhenContentIsEqual() throws Exception {
    String content = "content";
    InputStream inputStream = inputStreamWithContent(content);

    assertContent(inputStream, content);
  }

  @Test
  public void testAssertContentFailsWhenContentIsNotEqual() throws Exception {
    String content = "content";
    InputStream inputStream = inputStreamWithContent(content + "suffix");

    try {
      assertContent(inputStream, content);
    } catch (AssertionError e) {
      // expected
      return;
    }
    fail("exception should be thrown");
  }

  // inputStreamToString

  @Test
  public void testInputStreamToString() throws Exception {
    String content = "content";
    InputStream inputStream = inputStreamWithContent(content);

    String actual = inputStreamToString(inputStream);

    assertThat(actual).isEqualTo(content);
  }

  @Test
  public void testEmptyInputStreamToString() throws Exception {
    String content = "";
    InputStream inputStream = inputStreamWithContent(content);

    String actual = inputStreamToString(inputStream);

    assertThat(actual).isEqualTo(content);
  }

  // inputStreamToBytes

  @Test
  public void testInputStreamToBytes() throws Exception {
    String content = "content";
    InputStream inputStream = inputStreamWithContent(content);

    byte[] actual = inputStreamToBytes(inputStream);

    assertThat(actual).isEqualTo(content.getBytes());
  }

  @Test
  public void testEmptyInputStreamToBytes() throws Exception {
    String content = "";
    InputStream inputStream = inputStreamWithContent(content);

    byte[] actual = inputStreamToBytes(inputStream);

    assertThat(actual).isEmpty();
  }

  @Test
  public void inputStreamToBytesClosesStream() throws Exception {
    InputStream inputStream = mock(InputStream.class);
    Mockito.when(inputStream.read((byte[]) Matchers.any())).thenReturn(-1);

    inputStreamToBytes(inputStream);
    verify(inputStream).close();
  }

  private static ByteArrayInputStream inputStream(ByteArrayOutputStream outputStream) {
    return new ByteArrayInputStream(outputStream.toByteArray());
  }
}
