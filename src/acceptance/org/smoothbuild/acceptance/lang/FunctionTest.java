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
    givenScript(
        "  function^ = 'abc';  ",
        "  result = 'abc';     ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(1, "token recognition error at: '^'");
  }

  @Test
  public void function_name_starting_with_large_letter_causes_error() throws Exception {
    givenScript(
        "  FunctionName = 'abc';  ",
        "  result = 'abc';        ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(1, "no viable alternative at input 'FunctionName='");
  }

  @Test
  public void function_name_with_one_large_letter_causes_error() throws Exception {
    givenScript(
        "  F = 'abc';       ",
        "  result = 'abc';  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(1, "no viable alternative at input 'F='");
  }

  @Test
  public void duplicate_function_causes_error() throws Exception {
    givenScript(
        "  function1 = 'abc';  ",
        "  function1 = 'def';  ");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    thenSysOutContainsParseError(2, "'function1' is already defined at build.smooth:1.\n");
  }

  @Test
  public void function_with_same_name_as_constructor_causes_error() throws Exception {
    givenScript(
        "  MyStruct {}        ",
        "  myStruct = 'def';  ");
    whenSmoothBuild("myStruct");
    thenFinishedWithError();
    thenSysOutContainsParseError(2, "'myStruct' is already defined at build.smooth:1.");
  }

  @Test
  public void overriding_core_function_causes_error() throws Exception {
    givenScript(
        "  aFile = 'abc';  ");
    whenSmoothBuild("aFile");
    thenFinishedWithError();
    thenSysOutContainsParseError(1, "'aFile' is already defined at");
  }

  @Test
  public void direct_function_recursion_causes_error() throws IOException {
    givenScript(
        "  function1 = function1;  ");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    thenSysOutContains("Function call graph contains cycle");
  }

  @Test
  public void indirect_function_recursion_with_two_steps_causes_error() throws IOException {
    givenScript(
        "  function1 = function2;  ",
        "  function2 = function1;  ");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    thenSysOutContains("Function call graph contains cycle");
  }

  @Test
  public void indirect_recursion_via_argument_causes_error() throws IOException {
    givenScript(
        "  String function1 = myIdentity(function1());  ",
        "  String myIdentity(String s) = s;             ");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    thenSysOutContains("Function call graph contains cycle");
  }

  @Test
  public void indirect_recursion_with_three_steps_causes_error() throws IOException {
    givenScript(
        "  function1 = function2;  ",
        "  function2 = function3;  ",
        "  function3 = function1;  ");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    thenSysOutContains("Function call graph contains cycle");
  }

  @Test
  public void call_to_undefined_function_causes_error() throws IOException {
    givenScript(
        "  function1 = undefinedFunction();  ");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    thenSysOutContainsParseError(1, "'undefinedFunction' is undefined.");
  }

  @Test
  public void call_to_undefined_function_with_argument_causes_error() throws IOException {
    givenScript(
        "  function1 = undefinedFunction(abc='a');  ");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    thenSysOutContainsParseError(1, "'undefinedFunction' is undefined.");
  }

  @Test
  public void argument_less_call_doesnt_need_parentheses() throws IOException {
    givenScript(
        "  function1 = 'abc';      ",
        "  result    = function1;  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("abc");
  }

  @Test
  public void argument_less_call_doesnt_need_parentheses_in_pipe() throws IOException {
    givenScript(
        "  stringIdentity(String string) = string;  ",
        "  result = 'abc' | stringIdentity;         ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("abc");
  }

  @Test
  public void call_to_builtin_function_shadowed_by_parameter_causes_error() throws IOException {
    givenScript(
        "  function1(String zip) = zip();  ",
        "  result = function1('abc');      ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(1, "Parameter 'zip' cannot be called as it is not a function.");
  }

  @Test
  public void call_to_function_shadowed_by_parameter_causes_error() throws IOException {
    givenScript(
        "  function1 = 'abc';                          ",
        "  function2(String function1) = function1();  ",
        "  result = function2('def');                  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(2,
        "Parameter 'function1' cannot be called as it is not a function.");
  }

  @Test
  public void function_expression_type_not_convertible_to_function_type_causes_error()
      throws IOException {
    givenScript(
        "  String result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(1, "Function 'result' has body which type is '[Nothing]' and it is " +
        "not convertible to function's declared result type 'String'.");
  }

  @Test
  public void function_with_declared_result_type() throws IOException {
    givenScript(
        "  String result = 'abc';  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("abc");
  }

  @Test
  public void function_with_result_which_type_is_undefined_causes_error() throws IOException {
    givenScript(
        "  Undefined result = 'abc';  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(1, "Undefined type 'Undefined'.\n");
  }

  @Test
  public void function_with_generic_result_type_when_some_param_has_such_type_is_allowed()
      throws Exception {
    givenScript(
        "  A testIdentity(A value) = value;  ");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void function_with_generic_result_type_when_some_param_has_such_core_type_is_allowed()
      throws Exception {
    givenNativeJar(GenericResult.class);
    givenScript(
        "  A genericResult([A] array);  ");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void function_with_generic_array_result_type_when_some_param_has_such_type_is_allowed()
      throws Exception {
    givenScript(
        "  [A] testArrayIdentity(A value) = [value];  ");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void function_with_generic_array_result_type_when_some_param_has_such_core_type_is_allowed()
      throws Exception {
    givenScript(
        "  [A] testArrayIdentity([A] value) = value;  ");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void function_with_generic_result_type_when_no_param_has_such_core_type_causes_error()
      throws Exception {
    givenNativeJar(GenericResult.class);
    givenScript(
        "  A genericResult([B] array);  ");
    whenSmoothList();
    thenFinishedWithError();
    thenSysOutContainsParseError(1, "Undefined generic type 'A'. "
        + "Only generic types used in declaration of function parameters can be used here.");
  }

  @Test
  public void function_with_generic_array_result_type_when_no_param_has_such_core_type_causes_error()
      throws IOException {
    givenScript(
        "  [A] result = [];  ");
    whenSmoothList();
    thenFinishedWithError();
    thenSysOutContainsParseError(1, "Undefined generic type 'A'. "
        + "Only generic types used in declaration of function parameters can be used here.");
  }

  @Test
  public void function_with_nothing_result_type_is_allowed()
      throws Exception {
    givenNativeJar(ReportError.class);
    givenScript(
        "  Nothing reportError(String message);  ");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void function_with_nothing_array_result_type_is_allowed() throws IOException {
    givenScript(
        "  [Nothing] result = [];  ");
    whenSmoothList();
    thenFinishedWithSuccess();
  }

  @Test
  public void function_with_result_type_which_is_supertype_of_function_expression()
      throws IOException {
    givenScript(
        "  Blob func = file(toBlob('abc'), 'file.txt');  ",
        "  result = 'abc';                               ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void function_result_cannot_be_assigned_to_non_convertible_type_even_when_function_expression_is_convertible()
      throws IOException {
    givenScript(
        "  Blob func = file(toBlob('abc'), 'file.txt');  ",
        "  File result = func;                           ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(2, "Function 'result' has body which type is 'Blob' and it is not " +
        "convertible to function's declared result type 'File'.");
  }
}
