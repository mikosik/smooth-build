package org.smoothbuild.testing.lang.type;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

public class BlobTesterTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private final String content = "some content";

  @Test
  public void asserting_content_succeeds_when_blob_content_equals_expected() throws IOException {
    SBlob blob = objectsDb.blob(content);
    BlobTester.assertContains(blob, content);
  }

  @Test
  public void asserting_content_fails_when_blob_content_does_not_equal_expected()
      throws IOException {
    SBlob blob = objectsDb.blob(content);
    try {
      BlobTester.assertContains(blob, content + "suffix");
    } catch (AssertionError e) {
      // expected
      return;
    }
    fail("exception should be thrown");
  }
}
