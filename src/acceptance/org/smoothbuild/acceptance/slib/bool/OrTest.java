package org.smoothbuild.acceptance.slib.bool;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.nativefunc.ThrowException;

public class OrTest extends AcceptanceTestCase {

  @Test
  public void false_or_false_returns_false() throws IOException {
    createUserModule("""
            result = or(false, false);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(false);
  }

  @Test
  public void false_or_true_returns_true() throws IOException {
    createUserModule("""
            result = or(false, true);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void true_or_false_returns_true() throws IOException {
    createUserModule("""
            result = or(true, false);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void true_or_true_returns_true() throws IOException {
    createUserModule("""
            result = or(true, true);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void second_value_should_not_be_evaluated_when_first_is_true() throws Exception {
    createNativeJar(ThrowException.class);
    createUserModule(format("""
            @Native("%s")
            Nothing throwException();
            result = or(true, throwException());
            """, ThrowException.class.getCanonicalName()));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }
}
