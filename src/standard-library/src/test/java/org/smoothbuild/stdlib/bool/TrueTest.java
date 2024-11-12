package org.smoothbuild.stdlib.bool;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestContext;

public class TrueTest extends StandardLibraryTestContext {
  @Test
  void true_value() throws Exception {
    var userModule = """
        result = true;
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bBool(true));
  }
}
