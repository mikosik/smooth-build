package org.smoothbuild.systemtest.slib.file;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class FileTest extends SystemTestCase {
  @Test
  public void file_ctor() throws Exception {
    createUserModule("""
            result = file("name.txt", 0x41);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("name.txt", "A");
  }
}