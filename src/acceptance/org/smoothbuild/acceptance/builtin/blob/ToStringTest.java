package org.smoothbuild.acceptance.builtin.blob;

import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ToStringTest extends AcceptanceTestCase {
  @Test
  public void to_string_function() throws IOException {
    givenFile("file1.txt", "abc");
    givenBuildScript(script("result: file('file1.txt') | content | toString;"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("abc"));
  }
}
