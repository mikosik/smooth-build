package org.smoothbuild.systemtest.stdlib.common;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;
import org.smoothbuild.testing.func.nativ.ThrowException;

public class IfTest extends SystemTestCase {
  @Test
  public void if_returns_first_value_when_condition_is_true() throws Exception {
    createUserModule(
        """
            result = if(true, "then clause", "else clause");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsString("result")).isEqualTo("then clause");
  }

  @Test
  public void if_returns_second_value_when_condition_is_false() throws Exception {
    createUserModule(
        """
            result = if(false, "then clause", "else clause");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsString("result")).isEqualTo("else clause");
  }

  @Test
  public void first_value_should_not_be_evaluated_when_condition_is_false() throws Exception {
    createNativeJar(ThrowException.class);
    createUserModule(format(
        """
            @Native("%s")
            A throwException();
            result = if(false, throwException(), "else clause");
            """,
        ThrowException.class.getCanonicalName()));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsString("result")).isEqualTo("else clause");
  }

  @Test
  public void second_value_should_not_be_evaluated_when_condition_is_true() throws Exception {
    createNativeJar(ThrowException.class);
    createUserModule(format(
        """
            @Native("%s")
            A throwException();
            result = if(true, "then clause", throwException());
            """,
        ThrowException.class.getCanonicalName()));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsString("result")).isEqualTo("then clause");
  }

  @Nested
  class in_if_nested_inside_other_if {
    @Test
    public void first_value_should_not_be_evaluated_when_condition_is_false() throws Exception {
      createNativeJar(ThrowException.class);
      createUserModule(format(
          """
            @Native("%s")
            A throwException();
            result = if(true, if(false, throwException(), "else clause"), "ignored");
            """,
          ThrowException.class.getCanonicalName()));
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactAsString("result")).isEqualTo("else clause");
    }

    @Test
    public void second_value_should_not_be_evaluated_when_condition_is_true() throws Exception {
      createNativeJar(ThrowException.class);
      createUserModule(format(
          """
            @Native("%s")
            A throwException();
            result = if(true, if(true, "then clause", throwException()), "ignored");
            """,
          ThrowException.class.getCanonicalName()));
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactAsString("result")).isEqualTo("then clause");
    }
  }
}
