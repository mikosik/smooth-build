package org.smoothbuild.acceptance.builtin.compress;

import static org.smoothbuild.acceptance.FileArrayMatcher.isFileArrayWith;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ZipUnzipTest extends AcceptanceTestCase {
  @Test
  public void zip_unzip() throws IOException {
    givenFile("dir/file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenBuildScript(script("result: [file('dir/file1.txt'), file('file2.txt')] | zip | unzip;"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isFileArrayWith("dir/file1.txt", "abc", "file2.txt", "def"));
  }
}
