package org.smoothbuild.acceptance.argument;

import static org.hamcrest.Matchers.containsString;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class RequiredTest extends AcceptanceTestCase {
  @Test
  public void fails_when_required_parameter_is_missing() throws Exception {
    givenScript("result: oneRequired();");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "Not all parameters required by 'oneRequired' function has been specified."));
  }

  @Test
  public void fails_when_only_one_out_of_two_required_parameters_is_present() throws Exception {
    givenScript("result: twoRequired(stringA='abc');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "Not all parameters required by 'twoRequired' function has been specified."));
  }
}
