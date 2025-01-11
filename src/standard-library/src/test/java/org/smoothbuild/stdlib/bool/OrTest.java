package org.smoothbuild.stdlib.bool;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestContext;
import org.smoothbuild.virtualmachine.testing.func.nativ.ThrowException;

public class OrTest extends StandardLibraryTestContext {
  @Test
  void false_or_false_returns_false() throws Exception {
    var userModule = """
        result = or(false, false);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bBool(false));
  }

  @Test
  void false_or_true_returns_true() throws Exception {
    var userModule = """
        result = or(false, true);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bBool(true));
  }

  @Test
  void true_or_false_returns_true() throws Exception {
    var userModule = """
        result = or(true, false);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bBool(true));
  }

  @Test
  void true_or_true_returns_true() throws Exception {
    var userModule = """
        result = or(true, true);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bBool(true));
  }

  @Test
  void second_value_should_not_be_evaluated_when_first_is_true() throws Exception {
    var userModule = format(
        """
            @Native("%s")
            A throwException<A>();
            result = or(true, throwException());
            """,
        ThrowException.class.getCanonicalName());
    createUserModule(userModule, ThrowException.class);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bBool(true));
  }
}
