package org.smoothbuild.acceptance.slib.java;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class JarUnjarTest extends AcceptanceTestCase {
  @Test
  public void jar_unjar() throws IOException {
    createUserModule("""
            result = [ file(0x41, "dir/file1.txt"), file(0x42, "file2.txt") ]
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
    assertSysOutContains("Cannot read archive. Corrupted data?");
  }
}
