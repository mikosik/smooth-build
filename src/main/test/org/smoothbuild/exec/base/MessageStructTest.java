package org.smoothbuild.exec.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.exec.base.MessageStruct.containsErrors;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.testing.TestingContext;

public class MessageStructTest extends TestingContext {
  private Array messages;

  @Test
  public void empty_list_contains_no_errors() {
    messages = messageArrayEmtpy();
    assertThat(containsErrors(messages))
        .isFalse();
  }

  @Test
  public void list_with_info_message_contains_no_errors() {
    messages = array(infoMessage("info message"));
    assertThat(containsErrors(messages))
        .isFalse();
  }

  @Test
  public void list_with_warning_messsage_contains_no_errors() {
    messages = array(warningMessage("warning message"));
    assertThat(containsErrors(messages))
        .isFalse();
  }

  @Test
  public void list_with_error_messsage_contains_errors() {
    messages = array(errorMessage("error message"));
    assertThat(containsErrors(messages))
        .isTrue();
  }
}
