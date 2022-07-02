package org.smoothbuild.vm.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.run.eval.MessageStruct.containsErrors;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.cnst.ArrayB;
import org.smoothbuild.testing.TestContext;

public class MessageStructTest extends TestContext {
  private ArrayB messages;

  @Test
  public void empty_list_contains_no_errors() {
    messages = messageArrayEmtpy();
    assertThat(containsErrors(messages))
        .isFalse();
  }

  @Test
  public void list_with_info_message_contains_no_errors() {
    messages = arrayB(infoMessage("info message"));
    assertThat(containsErrors(messages))
        .isFalse();
  }

  @Test
  public void list_with_warning_messsage_contains_no_errors() {
    messages = arrayB(warningMessage("warning message"));
    assertThat(containsErrors(messages))
        .isFalse();
  }

  @Test
  public void list_with_error_messsage_contains_errors() {
    messages = arrayB(errorMessage("error message"));
    assertThat(containsErrors(messages))
        .isTrue();
  }
}
