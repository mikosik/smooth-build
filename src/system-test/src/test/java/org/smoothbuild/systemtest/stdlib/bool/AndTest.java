package org.smoothbuild.systemtest.stdlib.bool;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;
import org.smoothbuild.testing.func.nativ.ThrowException;

public class AndTest extends SystemTestCase {
  @Test
  public void false_and_false_returns_false() throws IOException {
    createUserModule("""
            result = and(false, false);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result")).isEqualTo(false);
  }

  @Test
  public void false_and_true_returns_false() throws IOException {
    createUserModule("""
            result = and(false, true);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result")).isEqualTo(false);
  }

  @Test
  public void true_and_false_returns_false() throws IOException {
    createUserModule("""
            result = and(true, false);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result")).isEqualTo(false);
  }

  @Test
  public void true_and_true_returns_true() throws IOException {
    createUserModule("""
            result = and(true, true);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result")).isEqualTo(true);
  }

  @Test
  public void second_value_should_not_be_evaluated_when_first_is_false() throws Exception {
    createNativeJar(ThrowException.class);
    createUserModule(format(
        """
            @Native("%s")
            A throwException();
            result = and(false, throwException());
            """,
        ThrowException.class.getCanonicalName()));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result")).isEqualTo(false);
  }
}
