package org.smoothbuild.stdlib.bool;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestContext;

public class FalseTest extends StandardLibraryTestContext {
  @Test
  void false_value() throws Exception {
    var userModule = """
        result = false;
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bBool(false));
  }
}
