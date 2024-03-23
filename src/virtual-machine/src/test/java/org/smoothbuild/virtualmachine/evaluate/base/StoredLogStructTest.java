package org.smoothbuild.virtualmachine.evaluate.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.containsErrorOrAbove;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArray;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class StoredLogStructTest extends TestingVirtualMachine {
  private BArray messages;

  @Test
  public void empty_list_contains_no_errors() throws Exception {
    messages = logArrayEmpty();
    assertThat(containsErrorOrAbove(messages)).isFalse();
  }

  @Test
  public void list_with_info_message_contains_no_errors() throws Exception {
    messages = arrayB(infoLog("info message"));
    assertThat(containsErrorOrAbove(messages)).isFalse();
  }

  @Test
  public void list_with_warning_message_contains_no_errors() throws Exception {
    messages = arrayB(warningLog("warning message"));
    assertThat(containsErrorOrAbove(messages)).isFalse();
  }

  @Test
  public void list_with_error_message_contains_error_or_above() throws Exception {
    messages = arrayB(errorLog("error message"));
    assertThat(containsErrorOrAbove(messages)).isTrue();
  }

  @Test
  public void list_with_fatal_message_contains_error_or_above() throws Exception {
    messages = arrayB(fatalLog("error message"));
    assertThat(containsErrorOrAbove(messages)).isTrue();
  }
}
