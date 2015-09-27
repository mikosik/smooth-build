package org.smoothbuild.acceptance.builtin.compress;

import static org.smoothbuild.acceptance.FileArrayMatcher.isFileArrayWith;
import static org.testory.Testory.then;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ZipUnzipTest extends AcceptanceTestCase {
  @Test
  public void zip_unzip() throws IOException {
    givenFile("dir/file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenScript("result: [file('dir/file1.txt'), file('file2.txt')] | zip | unzip;");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), isFileArrayWith("dir/file1.txt", "abc", "file2.txt", "def"));
  }
}
