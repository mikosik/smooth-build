package org.smoothbuild.accept;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.accept.AcceptanceTestCase;

public class StructTest extends AcceptanceTestCase {
  @Test
  public void read_struct_field() throws Exception {
    createUserModule("""
            MyStruct {
              String field,
            }
            String result = myStruct("abc").field;
            """);
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(stringB("abc"));
  }
}
