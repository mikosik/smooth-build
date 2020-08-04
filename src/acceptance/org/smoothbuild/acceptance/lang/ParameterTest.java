package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.ThrowException;

import okio.ByteString;

public class ParameterTest extends AcceptanceTestCase {
  @Nested
  class parameter_of_type {
    @Test
    public void bool() throws Exception {
      createUserModule(
          "  oneParameter(Bool bool) = 'abc';  ");
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void string() throws Exception {
      createUserModule(
          "  oneParameter(String string) = 'abc';  ");
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void blob() throws Exception {
      createUserModule(
          "  oneParameter(Blob blob) = 'abc';  ");
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void file() throws Exception {
      createUserModule(
          "  oneParameter(File file) = 'abc';  ");
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void nothing() throws Exception {
      createUserModule(
          "  oneParameter(Nothing nothing) = 'abc';  ");
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void value() throws Exception {
      createUserModule(
          "  oneParameter(Value value) = 'abc';  ");
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContainsParseError(1, "Undefined type 'Value'.\n");
    }

    @Test
    public void generic() throws Exception {
      createUserModule(
          "  oneParameter(A param) = 'abc';  ");
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void undefined() throws Exception {
      createUserModule(
          "  oneParameter(Undefined undefined) = 'abc';  ");
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContainsParseError(1, "Undefined type 'Undefined'.\n");
    }

    @Test
    public void bool_array() throws Exception {
      createUserModule(
          "  oneParameter([Bool] array) = 'abc';  ");
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void string_array() throws Exception {
      createUserModule(
          "  oneParameter([String] array) = 'abc';  ");
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void blob_array() throws Exception {
      createUserModule(
          "  oneParameter([Blob] array) = 'abc';  ");
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void file_array() throws Exception {
      createUserModule(
          "  oneParameter([File] array) = 'abc';  ");
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void nothing_array() throws Exception {
      createUserModule(
          "  oneParameter([Nothing] array) = 'abc';  ");
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void generic_array() throws Exception {
      createUserModule(
          "  oneParameter([A] array) = 'abc';  ");
      runSmoothList();
      assertFinishedWithSuccess();
    }

    @Test
    public void array_of_unknown_type() throws Exception {
      createUserModule(
          "  oneParameter([Undefined] param) = 'abc';  ");
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContainsParseError(1, "Undefined type 'Undefined'.\n");
    }

    @Test
    public void string_array2() throws Exception {
      createUserModule(
          "  oneParameter([[String]] array) = 'abc';  ");
      runSmoothList();
      assertFinishedWithSuccess();
    }
  }

  @Nested
  class default_value_from {
    @Test
    public void string_literal() throws Exception {
      createUserModule(
          "  oneParameter(String value = 'abc') = value;                      ",
          "  result = oneParameter();                                         ");
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactFileContentAsString("result"))
          .isEqualTo("abc");
    }

    @Test
    public void blob_literal() throws Exception {
      createUserModule(
          "  oneParameter(Blob value = 0xAB) = value;                         ",
          "  result = oneParameter();                                         ");
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactFileContent("result"))
          .isEqualTo(ByteString.of((byte) 0xAB));
    }

    @Test
    public void field_read() throws Exception {
      createUserModule(
          "  MyStruct { String field }                                        ",
          "  value = myStruct('abc');                                         ",
          "  oneParameter(String value = value.field) = value;                ",
          "  result = oneParameter();                                         ");
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactFileContentAsString("result"))
          .isEqualTo("abc");
    }

    @Test
    public void call() throws Exception {
      createUserModule(
          "  oneParameter(Bool value = true()) = value;                       ",
          "  result = oneParameter();                                         ");
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactFileContent("result"))
          .isEqualTo(ByteString.of((byte) 1));
    }

    @Test
    public void pipe() throws Exception {
      createUserModule(
          "  oneParameter(String value = true() | if('abc', 'def')) = value;  ",
          "  result = oneParameter();                                         ");
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactFileContentAsString("result"))
          .isEqualTo("abc");
    }
  }

  @Test
  public void no_parameters() throws Exception {
    createUserModule(
        "  noParameters() = 'abc';  ");
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
