package org.smoothbuild.systemtest.stdlib.bool;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class FalseTest extends SystemTestCase {
  @Test
  public void false_value() throws IOException {
    createUserModule("""
            result = false;
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result")).isEqualTo(false);
  }
}
