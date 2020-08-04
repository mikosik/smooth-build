package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.ThrowException;

public class DefaultObjectTest extends AcceptanceTestCase {
  @Test
  public void default_value_with_type_not_assignable_to_parameter_type_causes_error()
      throws Exception {
    createUserModule("""
            func([String] withDefault = "abc") = withDefault;
            result = func();
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(1, "Parameter 'withDefault' is of type '[String]' so"
        + " it cannot have default value of type 'String'.");
  }

  @Test
  public void default_value_can_have_type_convertible_to_parameter_type()
      throws Exception {
    createUserModule("""
            func(Blob param = file(toBlob("abc"), "file.txt")) = param;
            result = func();
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void generic_parameter_with_default_value_causes_error()
      throws Exception {
    createUserModule(
        "  A testIdentity(A value = 'aaa') = value;  ");
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(
        1, "Parameter 'value' is of type 'A' so it cannot have default value of type 'String'.");
  }

  @Test
  public void default_value_expression_can_be_a_call()
      throws Exception {
    createUserModule("""
            value = "abc";
            func(String withDefault = value()) = withDefault;
            result = func();
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void parameter_is_equal_to_its_default_value_when_not_assigned_in_call() throws Exception {
    createUserModule("""
            func(String withDefault = "abc") = withDefault;
            result = func();
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void default_value_is_ignored_when_parameter_is_assigned_in_a_call() throws Exception {
    createUserModule(
        "  func(String withDefault = 'abc') = withDefault;  ",
        "  result = func('def');                            ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("def");
  }

  @Test
  public void default_value_is_not_evaluated_when_not_needed() throws Exception {
    createNativeJar(ThrowException.class);
    createUserModule("""
            Nothing throwException();
            func(String withDefault = throwException()) = withDefault;
            result = func("def");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("def");
  }

  @Test
  public void default_value_of_one_parameter_cannot_reference_other_parameter() throws Exception {
    createUserModule(
        "  func(String param, String withDefault = param) = param;  ",
        "  result = 'abc';                                          ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(1, "'param' is undefined.");
  }
}
