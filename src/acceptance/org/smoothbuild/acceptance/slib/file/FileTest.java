package org.smoothbuild.acceptance.slib.file;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class FileTest extends AcceptanceTestCase {
  @Test
  public void file_ctor() throws Exception {
    createUserModule("""
            result = file(0x41, "name.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("name.txt", "A");
  }
}
