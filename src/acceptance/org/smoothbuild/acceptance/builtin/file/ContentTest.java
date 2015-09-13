package org.smoothbuild.acceptance.builtin.file;

import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ContentTest extends AcceptanceTestCase {
  @Test
  public void content_function() throws IOException {
    givenFile("file1.txt", "abc");
    givenBuildScript(script("result: file('file1.txt') | content;"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("abc"));
  }
}
