package org.smoothbuild.acceptance.builtin.compress;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ZipUnzipTest extends AcceptanceTestCase {
  @Test
  public void zip_unzip() throws IOException {
    givenFile("dir/file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenScript(
        "  result = [ aFile('dir/file1.txt'), aFile('file2.txt') ] | zip | unzip;  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactDir("result"))
        .containsExactly("dir/file1.txt", "abc", "file2.txt", "def");
  }

  @Test
  public void corrupted_archive_causes_error() throws IOException {
    givenScript(
        "  result = toBlob('random junk') | unzip;  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContains("Cannot read archive. Corrupted data?");
  }
}
