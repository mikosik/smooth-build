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
    givenScript("function1 = 'abc' | twoStrings(stringA=stringIdentity(unknown='')) ;");
    whenSmoothBuild("function1");
    thenFinishedWithError();
    then(output(), containsString("Function 'stringIdentity' has no parameter 'unknown'."));
  }
}
