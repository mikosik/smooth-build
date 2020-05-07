package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class PipeTest extends AcceptanceTestCase {
  @Test
  public void regression_test_error_in_expression_of_argument_of_not_first_element_of_pipe()
      throws IOException {
    givenScript(
        "  function1 = 'abc';                                   ",
        "  String myIdentity(String value) = value;             ",
        "  result = 'abc' | myIdentity(function1(unknown=''));  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContains("In call to `function1`: Unknown parameter 'unknown'.");
  }

  @Test
  public void regression_test_pipe_can_be_used_as_argument()
      throws IOException {
    givenScript(
        "  myIdentity(A value) = value;              ",
        "  result = myIdentity('abc' | myIdentity);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("abc");
  }
}
