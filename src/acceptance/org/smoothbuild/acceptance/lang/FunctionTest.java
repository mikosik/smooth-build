package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.containsString;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class FunctionTest extends AcceptanceTestCase {

  @Test
  public void illegal_function_name_is_forbidden() throws Exception {
    givenScript("function^name: 'abc';");
    whenSmoothBuild("function^name");
    thenFinishedWithError();
    then(output(), containsString("Illegal function name 'function^name'"));
  }

  @Test
  public void duplicate_function_is_forbidden() throws Exception {
    givenScript("function1: 'abc'; function1: 'def';");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    thenEqual(output(), "build.smooth:1: error: Function 'function1' is already defined.\n");
  }

  @Test
  public void overriding_core_function_is_forbidden() throws Exception {
    givenScript("file: 'abc';");
    whenSmoothBuild("file");
    thenFinishedWithError();
    thenEqual(output(),
        "build.smooth:1: error: Function 'file' cannot override builtin function with the same name.\n");
  }

  @Test
  public void direct_function_recursion_is_forbidden() throws IOException {
    givenScript("function1: function1;");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    then(output(), containsString("Function call graph contains cycle"));
  }

  @Test
  public void indirect_function_recursion_with_two_steps_is_forbidden() throws IOException {
    givenScript("function1: function2; function2: function1;");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    then(output(), containsString("Function call graph contains cycle"));
  }

  @Test
  public void indirect_recursion_with_three_steps_is_forbidden() throws IOException {
    givenScript("function1: function2; function2: function3; function3: function1;");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    then(output(), containsString("Function call graph contains cycle"));
  }

  @Test
  public void call_to_unknown_function_causes_error() throws IOException {
    givenScript("function1: unknownFunction;");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    thenEqual(output(), "build.smooth:1: error: Call to unknown function 'unknownFunction'.\n");
  }
}
