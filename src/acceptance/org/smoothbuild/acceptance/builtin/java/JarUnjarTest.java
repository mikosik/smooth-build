package org.smoothbuild.acceptance.builtin.java;

import static org.hamcrest.Matchers.containsString;
import static org.smoothbuild.acceptance.FileArrayMatcher.isFileArrayWith;
import static org.testory.Testory.then;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class JarUnjarTest extends AcceptanceTestCase {
  @Test
  public void jar_unjar() throws IOException {
    givenFile("dir/file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenScript("result: [file('//dir/file1.txt'), file('//file2.txt')] | jar | unjar;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isFileArrayWith("dir/file1.txt", "abc", "file2.txt", "def"));
  }

  @Test
  public void corrupted_archive_causes_error() throws IOException {
    givenScript("result: toBlob('random junk') | unjar;");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Cannot read archive. Corrupted data?"));
  }
}
