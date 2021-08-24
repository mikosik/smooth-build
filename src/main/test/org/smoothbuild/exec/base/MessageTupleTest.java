package org.smoothbuild.exec.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.exec.base.MessageTuple.containsErrors;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.testing.TestingContext;

public class MessageTupleTest extends TestingContext {
  private Array messages;

  @Test
  public void empty_list_contains_no_errors() {
    messages = emptyMessageArray();
    assertThat(containsErrors(messages))
        .isFalse();
  }

  @Test
  public void list_with_info_message_contains_no_errors() {
    messages = arrayV(infoMessageV("info message"));
    assertThat(containsErrors(messages))
        .isFalse();
  }

  @Test
  public void list_with_warning_messsage_contains_no_errors() {
    messages = arrayV(warningMessageV("warning message"));
    assertThat(containsErrors(messages))
        .isFalse();
  }

  @Test
  public void list_with_error_messsage_contains_errors() {
    messages = arrayV(errorMessageV("error message"));
    assertThat(containsErrors(messages))
        .isTrue();
  }
}
