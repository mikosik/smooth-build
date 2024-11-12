package org.smoothbuild.stdlib.blob;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestContext;

public class ToStringTest extends StandardLibraryTestContext {
  @Test
  void to_string_func() throws Exception {
    var userModule = """
        result = toString(0x41);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bString("A"));
  }
}
