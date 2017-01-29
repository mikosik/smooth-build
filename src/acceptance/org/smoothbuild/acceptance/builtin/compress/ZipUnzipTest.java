package org.smoothbuild.acceptance.builtin.compress;

import static org.hamcrest.Matchers.containsString;
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
    givenScript("result = [file('//dir/file1.txt'), file('//file2.txt')] | zip | unzip;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isFileArrayWith("dir/file1.txt", "abc", "file2.txt", "def"));
  }

  @Test
  public void corrupted_archive_causes_error() throws IOException {
    givenScript("result = toBlob('random junk') | unzip;");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Cannot read archive. Corrupted data?"));
  }
}
