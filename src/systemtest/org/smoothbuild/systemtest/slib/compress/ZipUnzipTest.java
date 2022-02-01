package org.smoothbuild.systemtest.slib.compress;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class ZipUnzipTest extends SystemTestCase {
  @Test
  public void zip_unzip() throws IOException {
    createFile("dir/file1.txt", "abc");
    createFile("file2.txt", "def");
    createUserModule("""
            result = [ projectFile("dir/file1.txt"), projectFile("file2.txt") ] | zip() | unzip();
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file1.txt", "abc", "file2.txt", "def");
  }

  @Test
  public void corrupted_archive_causes_error() throws IOException {
    createUserModule("""
            randomJunk = 0x123456;
            result = unzip(randomJunk);
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(
        "Cannot read archive. Corrupted data? Internal message: Could not fill buffer");
  }
}
