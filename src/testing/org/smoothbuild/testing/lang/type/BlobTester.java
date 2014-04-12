package org.smoothbuild.testing.lang.type;

import static org.smoothbuild.testing.common.StreamTester.assertContent;

import java.io.IOException;

import org.smoothbuild.lang.base.SBlob;

public class BlobTester {
  public static void assertContains(SBlob blob, String content) throws IOException {
    assertContent(blob.openInputStream(), content);
  }
}
