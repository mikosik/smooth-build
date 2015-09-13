package org.smoothbuild.acceptance.builtin.string;

import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ToBlobTest extends AcceptanceTestCase {
  @Test
  public void to_blob_function() throws IOException {
    givenFile("file.txt", "abc");
    givenBuildScript(script("result: toBlob('abc');"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("abc"));
  }
}
