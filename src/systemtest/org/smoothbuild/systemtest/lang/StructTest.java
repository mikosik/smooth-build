package org.smoothbuild.systemtest.lang;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class StructTest extends SystemTestCase {
  @Test
  public void read_struct_field() throws Exception {
    createUserModule("""
            MyStruct {
              String field,
            }
            String result = myStruct("abc").field;
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsString("result"))
        .isEqualTo("abc");
  }
}
