package org.smoothbuild.acceptance.lang;

import static org.testory.Testory.thenEqual;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.lang.nativ.ThrowException;

public class ParameterTest extends AcceptanceTestCase {
  @Test
  public void no_parameters() throws Exception {
    givenScript("noParameters() = 'abc';");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_bool() throws Exception {
    givenScript("oneParameter(Bool bool) = 'abc';");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_string() throws Exception {
    givenScript("oneParameter(String string) = 'abc';");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_blob() throws Exception {
    givenScript("oneParameter(Blob blob) = 'abc';");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_file() throws Exception {
    givenScript("oneParameter(File file) = 'abc';");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_nothing() throws Exception {
    givenScript("oneParameter(Nothing nothing) = 'abc';");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void it_is_not_possible_to_declare_parameter_of_type_value() throws Exception {
    givenScript("oneParameter(Value value) = 'abc';");
    whenSmoothList();
    thenFinishedWithError();
    thenOutputContainsError(1, "Undefined type 'Value'.\n");
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_generic_type() throws Exception {
    givenScript("oneParameter(a param) = 'abc';");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void it_is_not_possible_to_declare_parameter_of_undefined_type() throws Exception {
    givenScript("oneParameter(Undefined undefined) = 'abc';");
    whenSmoothList();
    thenFinishedWithError();
    thenOutputContainsError(1, "Undefined type 'Undefined'.\n");
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_bool_array() throws Exception {
    givenScript("oneParameter([Bool] array) = 'abc';");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_string_array() throws Exception {
    givenScript("oneParameter([String] array) = 'abc';");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_blob_array() throws Exception {
    givenScript("oneParameter([Blob] array) = 'abc';");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_file_array() throws Exception {
    givenScript("oneParameter([File] array) = 'abc';");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_nothing_array() throws Exception {
    givenScript("oneParameter([Nothing] array) = 'abc';");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_generic_array() throws Exception {
    givenScript("oneParameter([a] array) = 'abc';");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void it_is_not_possible_to_declare_parameter_of_array_of_unknown_type() throws Exception {
    givenScript("oneParameter([Undefined] param) = 'abc';");
    whenSmoothList();
    thenFinishedWithError();
    thenOutputContainsError(1, "Undefined type 'Undefined'.\n");
  }

  @Test
  public void it_is_possible_to_declare_parameter_of_type_string_array2() throws Exception {
    givenScript("oneParameter([[String]] array) = 'abc';");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void it_is_possible_to_declare_parameter_with_trailing_comma() throws Exception {
    givenScript("myFunction(String string, ) = string;");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void two_parameters_with_same_name_causes_error() throws Exception {
    givenScript("twoParameters(    \n"
        + "          String name1, \n"
        + "          String name1  \n"
        + "      ) = 'abc';        \n");
    whenSmoothList();
    thenFinishedWithError();
    thenOutputContainsError(3, "'name1' is already defined at build.smooth:2.\n");
  }

  @Test
  public void default_parameter_before_non_default_causes_error() throws Exception {
    givenScript("defaultBeforeNonDefault(    \n"
        + "          String default = 'value',         \n"
        + "          String nonDefault                 \n"
        + "      ) = 'abc';                            \n");
    whenSmoothList();
    thenFinishedWithError();
    thenOutputContainsError(3, "parameter with default value must be placed after all parameters " +
        "which don't have default value.\n");
  }

  @Test
  public void calling_defined_function_with_one_parameter() throws Exception {
    givenScript("func(String string) = 'abc';"
        + "result = func('def');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactContent("result"), "abc");
  }

  @Test
  public void defined_function_that_returns_parameter() throws Exception {
    givenScript("func(String string) = string;"
        + "result = func('abc');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactContent("result"), "abc");
  }

  @Test
  public void argument_is_not_evaluated_when_assigned_to_not_used_parameter() throws Exception {
    givenNativeJar(ThrowException.class);
    givenScript("String throwException();"
        + "      func(String notUsedParameter) = 'abc';"
        + "      result = func(throwException());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactContent("result"), "abc");
  }

  @Test
  public void calling_parameter_as_function_causes_error() throws Exception {
    givenScript("func(String param) = param();"
        + "      result = func('abc');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(1, "Parameter 'param' cannot be called as it is not a function.\n");
  }

  @Test
  public void parameter_can_shadow_builtin_function() throws Exception {
    givenScript("func(String zip) = zip;"
        + "      result = func('abc');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactContent("result"), "abc");
  }

  @Test
  public void parameter_can_shadow_function() throws Exception {
    givenScript("func1 = 'abc';"
        + "      func2(String func) = func;"
        + "      result = func2('def');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactContent("result"), "def");
  }
}
