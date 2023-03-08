package org.smoothbuild.systemtest.slib.file;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class FileConstructorTest extends SystemTestCase {
  @Test
  public void file_constructor() throws Exception {
    createUserModule("""
            result = File(0x41, "name.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("name.txt", "A");
  }
}
