package org.smoothbuild.stdlib.common;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.log.base.Log.error;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestContext;

public class ErrorTest extends StandardLibraryTestContext {
  @Test
  void error_log_is_reported() throws Exception {
    var code = """
        Int result = error("This is message.");
        """;
    createUserModule(code);
    evaluate("result");
    assertThat(logs()).containsExactly(error("This is message."));
  }
}
