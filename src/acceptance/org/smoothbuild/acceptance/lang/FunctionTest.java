package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.containsString;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class FunctionTest extends AcceptanceTestCase {

  @Test
  public void illegal_function_name_is_forbidden() throws Exception {
    givenBuildScript(script("function^name: 'abc';"));
    whenRunSmoothBuild("function^name");
    thenReturnedCode(1);
    thenPrinted(containsString("Illegal function name 'function^name'"));
  }

  @Test
  public void duplicate_function_is_forbidden() throws Exception {
    givenBuildScript(script("function1: 'abc'; function1: 'def';"));
    whenRunSmoothBuild("function1");
    thenReturnedCode(1);
    thenPrinted(containsString("Duplicate function 'function1'"));
  }

  @Test
  public void overriding_core_function_is_forbidden() throws Exception {
    givenBuildScript(script("file: 'abc';"));
    whenRunSmoothBuild("file");
    thenReturnedCode(1);
    thenPrinted(containsString(
        "Function 'file' cannot override builtin function with the same name."));
  }

  @Test
  public void direct_function_recursion_is_forbidden() throws IOException {
    givenBuildScript(script("function1: function1;"));
    whenRunSmoothBuild("function1");
    thenReturnedCode(1);
    thenPrinted(containsString("Function call graph contains cycle"));
  }

  @Test
  public void indirect_function_recursion_with_two_steps_is_forbidden() throws IOException {
    givenBuildScript(script("function1: function2; function2: function1;"));
    whenRunSmoothBuild("function1");
    thenReturnedCode(1);
    thenPrinted(containsString("Function call graph contains cycle"));
  }

  @Test
  public void indirect_recursion_with_three_steps_is_forbidden() throws IOException {
    givenBuildScript(script("function1: function2; function2: function3; function3: function1;"));
    whenRunSmoothBuild("function1");
    thenReturnedCode(1);
    thenPrinted(containsString("Function call graph contains cycle"));
  }

}
