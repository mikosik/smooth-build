package org.smoothbuild.vm.evaluate.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.run.eval.MessageStruct.containsErrorOrAbove;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;

public class MessageStructTest extends TestContext {
  private ArrayB messages;

  @Test
  public void empty_list_contains_no_errors() {
    messages = messageArrayEmpty();
    assertThat(containsErrorOrAbove(messages))
        .isFalse();
  }

  @Test
  public void list_with_info_message_contains_no_errors() {
    messages = arrayB(infoMessage("info message"));
    assertThat(containsErrorOrAbove(messages))
        .isFalse();
  }

  @Test
  public void list_with_warning_message_contains_no_errors() {
    messages = arrayB(warningMessage("warning message"));
    assertThat(containsErrorOrAbove(messages))
        .isFalse();
  }

  @Test
  public void list_with_error_message_contains_error_or_above() {
    messages = arrayB(errorMessage("error message"));
    assertThat(containsErrorOrAbove(messages))
        .isTrue();
  }

  @Test
  public void list_with_fatal_message_contains_error_or_above() {
    messages = arrayB(fatalMessage("error message"));
    assertThat(containsErrorOrAbove(messages))
        .isTrue();
  }
}
