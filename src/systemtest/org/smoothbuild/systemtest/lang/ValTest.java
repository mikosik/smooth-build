package org.smoothbuild.systemtest.lang;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class ValTest extends SystemTestCase {
  @Test
  public void value_can_be_reference_to_polymorphic_function() throws Exception {
    createUserModule("""
          A myId(A a) = a;
          myId2 = myId;
          result = myId2("abc");
          """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsString("result"))
        .isEqualTo("abc");
  }
}
