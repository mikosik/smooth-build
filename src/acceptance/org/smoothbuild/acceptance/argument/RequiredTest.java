package org.smoothbuild.acceptance.argument;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class RequiredTest extends AcceptanceTestCase {
  @Test
  public void fails_when_required_parameter_is_missing() throws Exception {
    givenScript("func(String string) = string;"
        + "      result = func();");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Not all parameters required by 'func' function has been specified.");
  }

  @Test
  public void fails_when_only_one_out_of_two_required_parameters_is_present() throws Exception {
    givenScript("func(String stringA, String stringB) = stringA;"
        + "      result = func(stringA='abc');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Not all parameters required by 'func' function has been specified.");
  }
}
