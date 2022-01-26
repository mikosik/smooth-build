package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class StructSTest extends AcceptanceTestCase {
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
