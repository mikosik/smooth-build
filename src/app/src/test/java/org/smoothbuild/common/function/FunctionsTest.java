package org.smoothbuild.common.function;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.function.Functions.invokeWithTunneling;
import static org.smoothbuild.common.function.Functions.sneakyFunction;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.io.IOException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class FunctionsTest {
  @Nested
  class _sneakyFunction {
    @Test
    void propagates_checked_exception_that_is_not_present_in_its_throws_clause() {
      var sneakyFunction = sneakyFunction((String string) -> {
        throw new IOException();
      });
      assertCall(() -> sneakyFunction.apply("abc")).throwsException(IOException.class);
    }

    @Test
    void forwards_call_to_wrapped_function1() {
      var sneakyFunction = sneakyFunction((String string) -> string);
      assertThat(sneakyFunction.apply("abc")).isEqualTo("abc");
    }
  }

  @Nested
  class _invokeWithTunelling {
    @Test
    void forwards_call_to_function1() {
      assertThat((String) invokeWithTunneling(f -> f.apply(7), Object::toString))
          .isEqualTo("7");
    }

    @Test
    void propagates_checked_exception_from_function1() {
      Function1<Integer, String, IOException> function1 = (i) -> {
        throw new IOException();
      };
      assertCall(() -> invokeWithTunneling(f -> f.apply(7), function1))
          .throwsException(IOException.class);
    }

    @Test
    void exception_declared_in_function1_is_declared_by_invokeWithTunneling() {
      Function1<Integer, String, IOException> function1 = Object::toString;
      try {
        invokeWithTunneling(f -> f.apply(7), function1);
      } catch (IOException e) {
        // catch block intentionally left here to make sure that invokeWithTunneling is has
        // throws containing the same exception as declared by Function1 generic argument.
      }
    }
  }
}
