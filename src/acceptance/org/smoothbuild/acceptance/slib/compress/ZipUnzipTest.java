package org.smoothbuild.acceptance.slib.compress;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ZipUnzipTest extends AcceptanceTestCase {
  @Test
  public void zip_unzip() throws IOException {
    createFile("dir/file1.txt", "abc");
    createFile("file2.txt", "def");
    createUserModule(
        "  result = [ aFile('dir/file1.txt'), aFile('file2.txt') ] | zip | unzip;  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file1.txt", "abc", "file2.txt", "def");
  }

  @Test
  public void corrupted_archive_causes_error() throws IOException {
    createUserModule(
        "  result = toBlob('random junk') | unzip;  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Cannot read archive. Corrupted data?");
  }
}
