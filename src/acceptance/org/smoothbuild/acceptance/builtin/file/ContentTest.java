package org.smoothbuild.acceptance.builtin.file;

import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ContentTest extends AcceptanceTestCase {
  @Test
  public void content_function() throws IOException {
    givenFile("file1.txt", "abc");
    givenScript("result: file('//file1.txt') | content;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }
}
