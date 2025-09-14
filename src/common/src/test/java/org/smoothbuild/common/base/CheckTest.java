package org.smoothbuild.common.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.base.Check.checkInitializedToNotNull;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Test;

public class CheckTest {
  @Test
  void throws_exception_for_null_value() {
    assertCall(() -> checkInitializedToNotNull(null, "myvalue"))
        .throwsException(new NullPointerException("`myvalue` has not been initialized yet."));
  }

  @Test
  void returns_value_when_it_is_not_null() {
    assertThat(checkInitializedToNotNull(77, "myvalue")).isEqualTo(77);
  }
}
