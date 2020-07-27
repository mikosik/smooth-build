package org.smoothbuild.acceptance.slib.java;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class JarUnjarTest extends AcceptanceTestCase {
  @Test
  public void jar_unjar() throws IOException {
    createUserModule(
        "  result = [ file(toBlob('abc'), 'dir/file1.txt'), file(toBlob('def'), 'file2.txt') ]  ",
        "    | jar | unjar;                                                                     ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file1.txt", "abc", "file2.txt", "def");
  }

  @Test
  public void corrupted_archive_causes_error() throws IOException {
    createUserModule(
        "  result = toBlob('random junk') | unjar;  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Cannot read archive. Corrupted data?");
  }
}
