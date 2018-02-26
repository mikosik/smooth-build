package org.smoothbuild.acceptance.builtin.java;

import static java.util.regex.Pattern.matches;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class JavaVersionTest extends AcceptanceTestCase {
  @Test
  public void error_is_logged_when_compilation_error_occurs() throws Exception {
    givenScript("result = javaVersion();");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(matches("[0-9]+\\.[0-9]+\\.[0-9]+_[0-9]+", artifactContent("result")));
  }
}
