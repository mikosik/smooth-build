package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.GenericResult;
import org.smoothbuild.acceptance.testing.ReportError;

public class FunctionTest extends AcceptanceTestCase {
  @Test
  public void illegal_function_name_causes_error() throws Exception {
    createUserModule(
        "  function^ = 'abc';  ",
        "  result = 'abc';     ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(1, "token recognition error at: '^'");
  }

  @Test
  public void function_name_starting_with_large_letter_causes_error() throws Exception {
    createUserModule(
        "  FunctionName = 'abc';  ",
        "  result = 'abc';        ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(1, "no viable alternative at input 'FunctionName='");
  }

  @Test
  public void function_name_with_one_large_letter_causes_error() throws Exception {
    createUserModule(
        "  F = 'abc';       ",
        "  result = 'abc';  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(1, "no viable alternative at input 'F='");
  }

  @Test
  public void direct_function_recursion_causes_error() throws IOException {
    createUserModule(
        "  function1 = function1;  ");
    runSmoothBuild("function1");
    assertFinishedWithError();
    assertSysOutContains("Function call graph contains cycle");
  }

  @Test
  public void indirect_function_recursion_with_two_steps_causes_error() throws IOException {
    createUserModule(
        "  function1 = function2;  ",
        "  function2 = function1;  ");
    runSmoothBuild("function1");
    assertFinishedWithError();
    assertSysOutContains("Function call graph contains cycle");
  }

  @Test
  public void indirect_recursion_via_argument_causes_error() throws IOException {
    createUserModule(
        "  String function1 = myIdentity(function1());  ",
        "  String myIdentity(String s) = s;             ");
    runSmoothBuild("function1");
    assertFinishedWithError();
    assertSysOutContains("Function call graph contains cycle");
  }

  @Test
  public void indirect_recursion_with_three_steps_causes_error() throws IOException {
    createUserModule(
        "  function1 = function2;  ",
        "  function2 = function3;  ",
        "  function3 = function1;  ");
    runSmoothBuild("function1");
    assertFinishedWithError();
    assertSysOutContains("Function call graph contains cycle");
  }

  @Test
  public void call_to_undefined_function_causes_error() throws IOException {
    createUserModule(
        "  function1 = undefinedFunction();  ");
    runSmoothBuild("function1");
    assertFinishedWithError();
    assertSysOutContainsParseError(1, "'undefinedFunction' is undefined.");
  }

  @Test
  public void call_to_undefined_function_with_argument_causes_error() throws IOException {
    createUserModule(
        "  function1 = undefinedFunction(abc='a');  ");
    runSmoothBuild("function1");
    assertFinishedWithError();
    assertSysOutContainsParseError(1, "'undefinedFunction' is undefined.");
  }

  @Test
  public void argument_less_call_doesnt_need_parentheses() throws IOException {
    createUserModule(
        "  function1 = 'abc';      ",
        "  result    = function1;  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void argument_less_call_doesnt_need_parentheses_in_pipe() throws IOException {
    createUserModule(
        "  stringIdentity(String string) = string;  ",
        "  result = 'abc' | stringIdentity;         ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void call_to_builtin_function_shadowed_by_parameter_causes_error() throws IOException {
    createUserModule(
        "  function1(String zip) = zip();  ",
        "  result = function1('abc');      ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(1, "Parameter 'zip' cannot be called as it is not a function.");
  }

  @Test
  public void call_to_function_shadowed_by_parameter_causes_error() throws IOException {
    createUserModule(
        "  function1 = 'abc';                          ",
        "  function2(String function1) = function1();  ",
        "  result = function2('def');                  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(2,
        "Parameter 'function1' cannot be called as it is not a function.");
  }

  @Test
  public void function_expression_type_not_convertible_to_function_type_causes_error()
      throws IOException {
    createUserModule(
        "  String result = [];  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(1, "Function 'result' has body which type is '[Nothing]' and it is " +
        "not convertible to function's declared result type 'String'.");
  }

  @Test
  public void function_with_declared_result_type() throws IOException {
    createUserModule(
        "  String result = 'abc';  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void function_with_result_which_type_is_undefined_causes_error() throws IOException {
    createUserModule(
        "  Undefined result = 'abc';  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(1, "Undefined type 'Undefined'.\n");
  }

  @Test
  public void function_with_generic_result_type_when_some_param_has_such_type_is_allowed()
      throws Exception {
    createUserModule(
        "  A testIdentity(A value) = value;  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void function_with_generic_result_type_when_some_param_has_such_core_type_is_allowed()
      throws Exception {
    createNativeJar(GenericResult.class);
    createUserModule(
        "  A genericResult([A] array);  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void function_with_generic_array_result_type_when_some_param_has_such_type_is_allowed()
      throws Exception {
    createUserModule(
        "  [A] testArrayIdentity(A value) = [value];  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void function_with_generic_array_result_type_when_some_param_has_such_core_type_is_allowed()
      throws Exception {
    createUserModule(
        "  [A] testArrayIdentity([A] value) = value;  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void function_with_generic_result_type_when_no_param_has_such_core_type_causes_error()
      throws Exception {
    createNativeJar(GenericResult.class);
    createUserModule(
        "  A genericResult([B] array);  ");
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(1, "Undefined generic type 'A'. "
        + "Only generic types used in declaration of function parameters can be used here.");
  }

  @Test
  public void function_with_generic_array_result_type_when_no_param_has_such_core_type_causes_error()
      throws IOException {
    createUserModule(
        "  [A] result = [];  ");
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(1, "Undefined generic type 'A'. "
        + "Only generic types used in declaration of function parameters can be used here.");
  }

  @Test
  public void function_with_nothing_result_type_is_allowed()
      throws Exception {
    createNativeJar(ReportError.class);
    createUserModule(
        "  Nothing reportError(String message);  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void function_with_nothing_array_result_type_is_allowed() throws IOException {
    createUserModule(
        "  [Nothing] result = [];  ");
    runSmoothList();
    assertFinishedWithSuccess();
  }

  @Test
  public void function_with_result_type_which_is_supertype_of_function_expression()
      throws IOException {
    createUserModule(
        "  Blob func = file(toBlob('abc'), 'file.txt');  ",
        "  result = 'abc';                               ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
  }

  @Test
  public void function_result_cannot_be_assigned_to_non_convertible_type_even_when_function_expression_is_convertible()
      throws IOException {
    createUserModule(
        "  Blob func = file(toBlob('abc'), 'file.txt');  ",
        "  File result = func;                           ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(2, "Function 'result' has body which type is 'Blob' and it is not " +
        "convertible to function's declared result type 'File'.");
  }
}
