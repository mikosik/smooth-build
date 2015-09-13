package org.smoothbuild.acceptance.argument;

import static org.hamcrest.Matchers.containsString;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class RequiredTest extends AcceptanceTestCase {
  @Test
  public void fails_when_required_parameter_is_missing() throws Exception {
    givenBuildScript(script("result: oneRequired();"));
    whenRunSmoothBuild("result");
    thenReturnedCode(1);
    thenPrinted(containsString(
        "Not all parameters required by 'oneRequired' function has been specified."));
  }

  @Test
  public void fails_when_only_one_out_of_two_required_parameters_is_present() throws Exception {
    givenBuildScript(script("result: twoRequired(stringA='abc');"));
    whenRunSmoothBuild("result");
    thenReturnedCode(1);
    thenPrinted(containsString(
        "Not all parameters required by 'twoRequired' function has been specified."));
  }
}
