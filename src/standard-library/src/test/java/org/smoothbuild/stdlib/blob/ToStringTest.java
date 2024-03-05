package org.smoothbuild.stdlib.blob;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestCase;

public class ToStringTest extends StandardLibraryTestCase {
  @Test
  public void to_string_func() throws Exception {
    var userModule = """
        result = toString(0x41);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(stringB("A"));
  }
}
