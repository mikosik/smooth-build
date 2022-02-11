package org.smoothbuild.systemtest.slib.java;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class JarUnjarTest extends SystemTestCase {
  @Test
  public void jar_unjar() throws IOException {
    createUserModule("""
            result = [file("dir/file1.txt", 0x41), file("file2.txt", 0x42)]
              | jar() | unjar();
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file1.txt", "A", "file2.txt", "B");
  }

  @Test
  public void corrupted_archive_causes_error() throws IOException {
    createUserModule("""
            randomJunk = 0x123456;
            result =  unjar(randomJunk);
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(
        "Cannot read archive. Corrupted data? Internal message: Could not fill buffer");
  }
}
