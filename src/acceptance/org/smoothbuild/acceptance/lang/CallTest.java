package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class CallTest extends AcceptanceTestCase {
  @Test
  public void call_without_parentheses_inside_pipe_is_allowed() throws IOException {
    createUserModule("""
          myIdentity(A value) = value;
          result = "abc" | myIdentity;
          """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void call_to_parameter_causes_error() throws IOException {
    createUserModule("""
          myFunction(String value) = value();
          """);
    runSmoothList();
    assertFinishedWithError();
    assertSysOutContainsParseError(
        1, "Parameter `value` cannot be called as it is not a function.");
  }
}
