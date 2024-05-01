package org.smoothbuild.virtualmachine.evaluate.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.containsErrorOrAbove;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.testing.TestingVm;

public class StoredLogStructTest extends TestingVm {
  private BArray messages;

  @Test
  void empty_list_contains_no_errors() throws Exception {
    messages = bLogArrayEmpty();
    assertThat(containsErrorOrAbove(messages)).isFalse();
  }

  @Test
  void list_with_info_message_contains_no_errors() throws Exception {
    messages = bArray(bInfoLog("info message"));
    assertThat(containsErrorOrAbove(messages)).isFalse();
  }

  @Test
  void list_with_warning_message_contains_no_errors() throws Exception {
    messages = bArray(bWarningLog("warning message"));
    assertThat(containsErrorOrAbove(messages)).isFalse();
  }

  @Test
  void list_with_error_message_contains_error_or_above() throws Exception {
    messages = bArray(bErrorLog("error message"));
    assertThat(containsErrorOrAbove(messages)).isTrue();
  }

  @Test
  void list_with_fatal_message_contains_error_or_above() throws Exception {
    messages = bArray(bFatalLog("error message"));
    assertThat(containsErrorOrAbove(messages)).isTrue();
  }
}
