package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.equalTo;
import static org.testory.Testory.then;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class PipeTest extends AcceptanceTestCase {
  @Test
  public void regression_test_error_in_expression_of_argument_of_not_first_element_of_pipe()
      throws IOException {
    givenScript("function1 = 'abc';                                    \n" +
        "        String myIdentity(String value) = value;              \n"
        + "      result = 'abc' | myIdentity(function1(unknown=''));   \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Function 'function1' has no parameter 'unknown'.");
  }

  @Test
  public void regression_test_pipe_can_be_used_as_argument()
      throws IOException {
    givenScript("myIdentity(A value) = value;               \n" +
        "        result = myIdentity('abc' | myIdentity);   \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactContent("result"), equalTo("abc"));
  }
}
