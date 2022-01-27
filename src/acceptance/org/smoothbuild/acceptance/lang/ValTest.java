package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ValTest extends AcceptanceTestCase {
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
