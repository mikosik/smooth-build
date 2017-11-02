package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.containsString;
import static org.testory.Testory.then;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class PipeTest extends AcceptanceTestCase {

  @Test
  public void regression_test_error_in_expression_of_argument_of_not_first_element_of_pipe()
      throws IOException {
    givenScript("function1 = 'abc';"
        + "      result = 'abc' | twoStrings(stringA=function1(unknown='')) ;");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Function 'function1' has no parameter 'unknown'."));
  }
}
