package org.smoothbuild.stdlib.bool;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestCase;

public class FalseTest extends StandardLibraryTestCase {
  @Test
  public void false_value() throws Exception {
    var userModule = """
        result = false;
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bBool(false));
  }
}
