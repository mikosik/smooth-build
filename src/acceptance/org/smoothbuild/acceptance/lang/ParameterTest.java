package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.ThrowException;

public class ParameterTest extends AcceptanceTestCase {
  @Test
  public void no_parameters() throws Exception {
    createUserModule(
        "  noParameters() = 'abc';  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_bool() throws Exception {
    createUserModule(
        "  oneParameter(Bool bool) = 'abc';  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_string() throws Exception {
    createUserModule(
        "  oneParameter(String string) = 'abc';  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_blob() throws Exception {
    createUserModule(
        "  oneParameter(Blob blob) = 'abc';  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_file() throws Exception {
    createUserModule(
        "  oneParameter(File file) = 'abc';  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_nothing() throws Exception {
    createUserModule(
        "  oneParameter(Nothing nothing) = 'abc';  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void it_is_not_possible_to_declare_parameter_of_type_value() throws Exception {
    createUserModule(
        "  oneParameter(Value value) = 'abc';  ");
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(1, "Undefined type 'Value'.\n");
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_generic_type() throws Exception {
    createUserModule(
        "  oneParameter(A param) = 'abc';  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void it_is_not_possible_to_declare_parameter_of_undefined_type() throws Exception {
    createUserModule(
        "  oneParameter(Undefined undefined) = 'abc';  ");
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(1, "Undefined type 'Undefined'.\n");
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_bool_array() throws Exception {
    createUserModule(
        "  oneParameter([Bool] array) = 'abc';  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_string_array() throws Exception {
    createUserModule(
        "  oneParameter([String] array) = 'abc';  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_blob_array() throws Exception {
    createUserModule(
        "  oneParameter([Blob] array) = 'abc';  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_file_array() throws Exception {
    createUserModule(
        "  oneParameter([File] array) = 'abc';  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_nothing_array() throws Exception {
    createUserModule(
        "  oneParameter([Nothing] array) = 'abc';  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_generic_array() throws Exception {
    createUserModule(
        "  oneParameter([A] array) = 'abc';  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void it_is_not_possible_to_declare_parameter_of_array_of_unknown_type() throws Exception {
    createUserModule(
        "  oneParameter([Undefined] param) = 'abc';  ");
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(1, "Undefined type 'Undefined'.\n");
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_string_array2() throws Exception {
    createUserModule(
        "  oneParameter([[String]] array) = 'abc';  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_with_trailing_comma() throws Exception {
    createUserModule(
        "  myFunction(String string, ) = string;  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void two_parameters_with_same_name_causes_error() throws Exception {
    createUserModule(
        "  twoParameters(     ",
        "      String name1,  ",
        "      String name1   ",
        "  ) = 'abc';         ");
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(3, "'name1' is already defined at build.smooth:2.\n");
  }

  @Test
  public void default_parameter_before_non_default_causes_error() throws Exception {
    createUserModule(
        "  defaultBeforeNonDefault(       ",
        "      String default = 'value',  ",
        "      String nonDefault          ",
        "  ) = 'abc';                     ");
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(3, "parameter with default value must be placed after all parameters " +
        "which don't have default value.\n");
  }

  @Test
  public void calling_defined_function_with_one_parameter() throws Exception {
    createUserModule(
        "  func(String string) = 'abc';  ",
        "  result = func('def');         ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void defined_function_that_returns_parameter() throws Exception {
    createUserModule(
        "  func(String string) = string;  ",
        "  result = func('abc');          ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void argument_is_not_evaluated_when_assigned_to_not_used_parameter() throws Exception {
    createNativeJar(ThrowException.class);
    createUserModule(
        "  Nothing throwException();               ",
        "  func(String notUsedParameter) = 'abc';  ",
        "  result = func(throwException());        ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void calling_parameter_as_function_causes_error() throws Exception {
    createUserModule(
        "  func(String param) = param();  ",
        "  result = func('abc');          ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(1, "Parameter 'param' cannot be called as it is not a function.\n");
  }

  @Test
  public void parameter_can_shadow_builtin_function() throws Exception {
    createUserModule(
        "  func(String zip) = zip;  ",
        "  result = func('abc');    ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void parameter_can_shadow_function() throws Exception {
    createUserModule(
        "  func1 = 'abc';              ",
        "  func2(String func) = func;  ",
        "  result = func2('def');      ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("def");
  }
}
