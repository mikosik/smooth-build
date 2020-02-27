package org.smoothbuild.acceptance.builtin.java;

import static org.testory.Testory.thenEqual;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class JavaPropertyTest extends AcceptanceTestCase {
  @Test
  public void java_property_returns_property_value() throws Exception {
    givenScript(
        "  result = javaProperty('java.vm.info');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactContent("result"), "mixed mode");
  }

  @Test
  public void java_property_throws_error_for_unknown_property() throws Exception {
    givenScript(
        "  result = javaProperty('uknown.property');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Unknown property 'uknown.property'.");
  }
}
