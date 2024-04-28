package org.smoothbuild.stdlib.bool;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestCase;
import org.smoothbuild.virtualmachine.testing.func.nativ.ThrowException;

public class AndTest extends StandardLibraryTestCase {
  @Test
  void false_and_false_returns_false() throws Exception {
    var userModule = """
        result = and(false, false);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bBool(false));
  }

  @Test
  void false_and_true_returns_false() throws Exception {
    var userModule = """
        result = and(false, true);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bBool(false));
  }

  @Test
  void true_and_false_returns_false() throws Exception {
    var userModule = """
        result = and(true, false);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bBool(false));
  }

  @Test
  void true_and_true_returns_true() throws Exception {
    var userModule = """
        result = and(true, true);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bBool(true));
  }

  @Test
  void second_value_should_not_be_evaluated_when_first_is_false() throws Exception {
    var userModule = format(
        """
            @Native("%s")
            A throwException();
            result = and(false, throwException());
            """,
        ThrowException.class.getCanonicalName());
    createUserModule(userModule, ThrowException.class);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bBool(false));
  }
}
