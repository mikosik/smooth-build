package org.smoothbuild.virtualmachine.bytecode.helper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.containsErrorOrAbove;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class StoredLogStructTest extends VmTestContext {

  @Test
  void empty_list_contains_no_errors() throws Exception {
    var messages = bLogArrayEmpty();
    assertThat(containsErrorOrAbove(messages)).isFalse();
  }

  @Test
  void list_with_info_message_contains_no_errors() throws Exception {
    var messages = bArray(bInfoLog("info message"));
    assertThat(containsErrorOrAbove(messages)).isFalse();
  }

  @Test
  void list_with_warning_message_contains_no_errors() throws Exception {
    var messages = bArray(bWarningLog("warning message"));
    assertThat(containsErrorOrAbove(messages)).isFalse();
  }

  @Test
  void list_with_error_message_contains_error_or_above() throws Exception {
    var messages = bArray(bErrorLog("error message"));
    assertThat(containsErrorOrAbove(messages)).isTrue();
  }

  @Test
  void list_with_fatal_message_contains_error_or_above() throws Exception {
    var messages = bArray(bFatalLog("error message"));
    assertThat(containsErrorOrAbove(messages)).isTrue();
  }
}
