package org.smoothbuild.acceptance.builtin.java;

import static org.smoothbuild.acceptance.FileArrayMatcher.isFileArrayWith;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class JarUnjarTest extends AcceptanceTestCase {
  @Test
  public void jar_unjar() throws IOException {
    givenFile("dir/file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenBuildScript(script("result: [file('dir/file1.txt'), file('file2.txt')] | jar | unjar;"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isFileArrayWith("dir/file1.txt", "abc", "file2.txt", "def"));
  }
}
