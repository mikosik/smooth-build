package org.smoothbuild.acceptance.lang;

import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.lang.nativ.ReturnValue;

public class FunctionTest extends AcceptanceTestCase {
  @Test
  public void illegal_function_name_causes_error() throws Exception {
    givenScript("function^name = 'abc';"
        + "      result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithError();
  }

  @Test
  public void duplicate_function_causes_error() throws Exception {
    givenScript("function1 = 'abc';\n"
        + "      function1 = 'def';\n");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    thenOutputContainsError(2, "'function1' is already defined at build.smooth:1.\n");
  }

  @Test
  public void function_with_same_name_as_struct_causes_error() throws Exception {
    givenScript("MyStruct {}         \n"
        + "      MyStruct = 'def';   \n");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    thenOutputContainsError(2, "'MyStruct' is already defined at build.smooth:1.\n");
  }

  @Test
  public void function_with_same_name_as_basic_type_causes_error() throws Exception {
    givenScript("String = 'def';\n");
    whenSmoothList();
    thenFinishedWithError();
    thenOutputContainsError(1, "'String' is already defined.\n");
  }

  @Test
  public void overriding_core_function_causes_error() throws Exception {
    givenScript("file = 'abc';");
    whenSmoothBuild("file");
    thenFinishedWithError();
    thenOutputContainsError(1, "'file' is already defined at");
  }

  @Test
  public void direct_function_recursion_causes_error() throws IOException {
    givenScript("function1 = function1;");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    thenOutputContains("Function call graph contains cycle");
  }

  @Test
  public void indirect_function_recursion_with_two_steps_causes_error() throws IOException {
    givenScript("function1 = function2; function2 = function1;");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    thenOutputContains("Function call graph contains cycle");
  }

  @Test
  public void indirect_recursion_via_argument_causes_error() throws IOException {
    givenScript("String function1 = myIdentity(function1());"
        + "      String myIdentity(String s) = s;");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    thenOutputContains("Function call graph contains cycle");
  }

  @Test
  public void indirect_recursion_with_three_steps_causes_error() throws IOException {
    givenScript("function1 = function2; function2 = function3; function3 = function1;");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    thenOutputContains("Function call graph contains cycle");
  }

  @Test
  public void call_to_unknown_function_causes_error() throws IOException {
    givenScript("function1 = unknownFunction();");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    thenOutputContains("build.smooth:1: error: 'unknownFunction' is undefined.\n");
  }

  @Test
  public void call_to_unknown_function_with_argument_causes_error() throws IOException {
    givenScript("function1 = unknownFunction(abc='a');");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    thenOutputContains("build.smooth:1: error: 'unknownFunction' is undefined.\n");
  }

  @Test
  public void argument_less_call_doesnt_need_parentheses() throws IOException {
    givenScript("function1 = 'abc';"
        + "      result    = function1;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void argument_less_call_doesnt_need_parentheses_in_pipe() throws IOException {
    givenScript("stringIdentity(String string) = string;"
        + "      result = 'abc' | stringIdentity;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void call_to_builtin_function_shadowed_by_parameter_causes_error() throws IOException {
    givenScript("function1(String zip) = zip();"
        + "      result = function1('abc');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains(
        "build.smooth:1: error: Parameter 'zip' cannot be called as it is not a function.\n");
  }

  @Test
  public void call_to_function_shadowed_by_parameter_causes_error() throws IOException {
    givenScript("function1 = 'abc';"
        + "      function2(String function1) = function1();"
        + "      result = function2('def');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains(
        "build.smooth:1: error: Parameter 'function1' cannot be called as it is not a function.\n");
  }

  @Test
  public void function_expression_type_not_convertable_to_function_type_causes_error()
      throws IOException {
    givenScript("String result = [];");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(1, "Type of function's 'result' expression"
        + " is [Nothing] which is not convertable to function's declared result type String.\n");
  }

  @Test
  public void function_with_declared_result_type() throws IOException {
    givenScript("String result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void function_with_unknown_result_type_causes_error() throws IOException {
    givenScript("Unknown result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(1, "Unknown type 'Unknown'.\n");
  }

  @Test
  public void function_with_nothing_as_result_type_causes_error() throws IOException {
    givenNativeJar(ReturnValue.class);
    givenScript("Nothing returnValue();           \n"
        + "      result = returnValue();          \n");
    whenSmoothList();
    thenFinishedWithError();
    thenOutputContainsError(1, "Nothing type cannot be used as function result type.");
  }

  @Test
  public void function_with_array_of_nothing_as_result_type() throws IOException {
    givenScript("[Nothing] result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void function_with_result_type_which_is_supertype_of_function_expression()
      throws IOException {
    givenFile("file.txt", "abc");
    givenScript("Blob func = file('//file.txt');"
        + "      result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void function_result_cannot_be_assigned_to_non_convertable_type_even_when_function_expression_is_convertible()
      throws IOException {
    givenFile("file.txt", "abc");
    givenScript("Blob func = file('//file.txt');"
        + "      File result = func;");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(1, "Type of function's 'result' expression "
        + "is Blob which is not convertable to function's declared result type File.\n");
  }
}
