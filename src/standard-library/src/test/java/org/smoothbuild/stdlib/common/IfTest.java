package org.smoothbuild.stdlib.common;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestContext;
import org.smoothbuild.virtualmachine.testing.func.nativ.ThrowException;

public class IfTest extends StandardLibraryTestContext {
  @Test
  void if_returns_first_value_when_condition_is_true() throws Exception {
    var userModule = """
        result = if(true, "then clause", "else clause");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bString("then clause"));
  }

  @Test
  void if_returns_second_value_when_condition_is_false() throws Exception {
    createUserModule(
        """
            result = if(false, "then clause", "else clause");
            """);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bString("else clause"));
  }

  @Test
  void first_value_should_not_be_evaluated_when_condition_is_false() throws Exception {
    var userModule = format(
        """
            @Native("%s")
            A throwException();
            result = if(false, throwException(), "else clause");
            """,
        ThrowException.class.getCanonicalName());
    createUserModule(userModule, ThrowException.class);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bString("else clause"));
  }

  @Test
  void second_value_should_not_be_evaluated_when_condition_is_true() throws Exception {
    var userModule = format(
        """
            @Native("%s")
            A throwException();
            result = if(true, "then clause", throwException());
            """,
        ThrowException.class.getCanonicalName());
    createUserModule(userModule, ThrowException.class);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bString("then clause"));
  }

  @Nested
  class in_if_nested_inside_other_if {
    @Test
    void first_value_should_not_be_evaluated_when_condition_is_false() throws Exception {
      var userModule = format(
          """
              @Native("%s")
              A throwException();
              result = if(true, if(false, throwException(), "else clause"), "ignored");
              """,
          ThrowException.class.getCanonicalName());
      createUserModule(userModule, ThrowException.class);
      evaluate("result");
      assertThat(artifact()).isEqualTo(bString("else clause"));
    }

    @Test
    void second_value_should_not_be_evaluated_when_condition_is_true() throws Exception {
      var userModule = format(
          """
              @Native("%s")
              A throwException();
              result = if(true, if(true, "then clause", throwException()), "ignored");
              """,
          ThrowException.class.getCanonicalName());
      createUserModule(userModule, ThrowException.class);
      evaluate("result");
      assertThat(artifact()).isEqualTo(bString("then clause"));
    }
  }
}
