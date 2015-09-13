package org.smoothbuild.acceptance.builtin.file;

import static org.smoothbuild.acceptance.FileArrayMatcher.isFileArrayWith;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ConcatenateTest extends AcceptanceTestCase {
  @Test
  public void concatenate_files_function() throws Exception {
    givenFile("file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenBuildScript(script(
        "result: concatenateFiles(files=[file('file1.txt')], with=[file('file2.txt')]);"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isFileArrayWith("file1.txt", "abc", "file2.txt", "def"));
  }
}
