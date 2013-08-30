package org.smoothbuild.testing;

import static org.smoothbuild.testing.TestingStream.assertContent;
import static org.smoothbuild.testing.TestingStream.writeAndClose;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.fs.mem.MemoryFile;

public class TestingStreamTest {

  @Test
  public void testWriteAndClose() throws IOException {
    String content = "content to test.";
    MemoryFile file = new MemoryFile("file");

    writeAndClose(file.createOutputStream(), content);

    assertContent(file.createInputStream(), content);
    try {
      assertContent(file.createInputStream(), "different content");
      Assert.fail("exception should be thrown");
    } catch (AssertionError e) {
      // expected
    }
  }

  @Test
  public void testWriteAndCloseWithEmptyContent() throws IOException {
    String content = "";
    MemoryFile file = new MemoryFile("file");

    writeAndClose(file.createOutputStream(), content);

    assertContent(file.createInputStream(), content);
    try {
      assertContent(file.createInputStream(), "different content");
      Assert.fail("exception should be thrown");
    } catch (AssertionError e) {
      // expected
    }
  }
}
