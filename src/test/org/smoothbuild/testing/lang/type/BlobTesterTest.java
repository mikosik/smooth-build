package org.smoothbuild.testing.lang.type;

import static org.junit.Assert.fail;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SBlob;

public class BlobTesterTest {
  String content = "some content";
  Path path = path("my/path");

  @Test
  public void asserting_content_succeeds_when_blob_content_equals_expected() throws IOException {
    SBlob blob = new FakeBlob(content);
    BlobTester.assertContains(blob, content);
  }

  @Test
  public void asserting_content_fails_when_blob_content_does_not_equal_expected()
      throws IOException {
    SBlob blob = new FakeBlob(content);
    try {
      BlobTester.assertContains(blob, content + "suffix");
    } catch (AssertionError e) {
      // expected
      return;
    }
    fail("exception should be thrown");
  }
}
