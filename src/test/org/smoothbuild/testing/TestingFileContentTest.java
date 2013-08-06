package org.smoothbuild.testing;

import static org.smoothbuild.testing.TestingFileContent.assertFileContent;
import static org.smoothbuild.testing.TestingFileContent.writeAndClose;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.fs.mem.InMemoryFile;

public class TestingFileContentTest {

  @Test
  public void testWriteAndClose() throws IOException {
    String content = "content to test.";
    InMemoryFile file = new InMemoryFile("file");

    writeAndClose(file.createOutputStream(), content);

    assertFileContent(file.createInputStream(), content);
    try {
      assertFileContent(file.createInputStream(), "different content");
      Assert.fail("exception should be thrown");
    } catch (AssertionError e) {
      // expected
    }
  }
}
