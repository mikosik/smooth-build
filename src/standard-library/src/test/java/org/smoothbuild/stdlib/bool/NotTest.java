package org.smoothbuild.stdlib.bool;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestCase;

public class NotTest extends StandardLibraryTestCase {

  @Test
  public void not_false_returns_true() throws Exception {
    var userModule = """
        result = not(false);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bBool(true));
  }

  @Test
  public void not_true_returns_false() throws Exception {
    var userModule = """
        result = not(true);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bBool(false));
  }
}
