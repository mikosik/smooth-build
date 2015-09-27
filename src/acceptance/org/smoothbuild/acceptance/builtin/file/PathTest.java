package org.smoothbuild.acceptance.builtin.file;

import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class PathTest extends AcceptanceTestCase {
  @Test
  public void path_function() throws IOException {
    givenFile("file1.txt", "abc");
    givenScript("result: file('file1.txt') | path;");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), hasContent("file1.txt"));
  }
}
