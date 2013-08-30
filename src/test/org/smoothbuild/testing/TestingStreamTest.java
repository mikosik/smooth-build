package org.smoothbuild.testing;

import static org.smoothbuild.testing.TestingStream.assertContent;
import static org.smoothbuild.testing.TestingStream.writeAndClose;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class TestingStreamTest {

  @Test
  public void testWriteAndClose() throws IOException {
    String content = "content to test.";
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    writeAndClose(outputStream, content);

    assertContent(inputStream(outputStream), content);

    try {
      assertContent(inputStream(outputStream), "different content");
      Assert.fail("exception should be thrown");
    } catch (AssertionError e) {
      // expected
    }
  }

  @Test
  public void testWriteAndCloseWithEmptyContent() throws IOException {
    String content = "";
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    writeAndClose(outputStream, content);

    assertContent(inputStream(outputStream), content);
    try {
      assertContent(inputStream(outputStream), "different content");
      Assert.fail("exception should be thrown");
    } catch (AssertionError e) {
      // expected
    }
  }

  private static ByteArrayInputStream inputStream(ByteArrayOutputStream outputStream) {
    return new ByteArrayInputStream(outputStream.toByteArray());
  }
}
