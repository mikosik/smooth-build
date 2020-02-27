package org.smoothbuild.acceptance.builtin.string;

import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ToBlobTest extends AcceptanceTestCase {
  @Test
  public void to_blob_function() throws IOException {
    givenFile("file.txt", "abc");
    givenScript(
        "  result = toBlob('abc');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }
}
