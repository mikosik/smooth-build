package org.smoothbuild.acceptance.slib.common;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.ThrowException;

public class IfTest extends AcceptanceTestCase {
  @Test
  public void if_returns_first_value_when_condition_is_true() throws Exception {
    createUserModule(
        "  result = if(true, 'then clause', 'else clause');  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("then clause");
  }

  @Test
  public void if_returns_second_value_when_condition_is_false() throws Exception {
    createUserModule(
        "  result = if(false, 'then clause', 'else clause');  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("else clause");
  }

  @Test
  public void first_value_should_not_be_evaluated_when_condition_is_false() throws Exception {
    createNativeJar(ThrowException.class);
    createUserModule(
        "  Nothing throwException();                               ",
        "  result = if(false, throwException(), 'else clause');  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("else clause");
  }

  @Test
  public void second_value_should_not_be_evaluated_when_condition_is_true() throws Exception {
    createNativeJar(ThrowException.class);
    createUserModule(
        "  Nothing throwException();                              ",
        "  result = if(true, 'then clause', throwException());  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("then clause");
  }

  @Nested
  class in_if_nested_inside_other_if {
    @Test
    public void first_value_should_not_be_evaluated_when_condition_is_false() throws Exception {
      createNativeJar(ThrowException.class);
      createUserModule(
          "  Nothing throwException();                                                      ",
          "  result = if(true, if(false, throwException(), 'else clause'), 'ignored');  ");
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactFileContentAsString("result"))
          .isEqualTo("else clause");
    }

    @Test
    public void second_value_should_not_be_evaluated_when_condition_is_true()
        throws Exception {
      createNativeJar(ThrowException.class);
      createUserModule(
          "  Nothing throwException();                                                     ",
          "  result = if(true, if(true, 'then clause', throwException()), 'ignored');  ");
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactFileContentAsString("result"))
          .isEqualTo("then clause");
    }
  }
}
