package org.smoothbuild.virtualmachine.evaluate.step;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.testing.TestingVm;

public class BOutputTest extends TestingVm {
  @Test
  void null_messages_are_forbidden() {
    assertCall(() -> new BOutput(aString(), null)).throwsException(NullPointerException.class);
  }

  @Test
  void value_returns_value() throws Exception {
    assertThat(new BOutput(aString(), messages()).value()).isEqualTo(aString());
  }

  @Test
  void messages_returns_messages() throws Exception {
    var BOutput = new BOutput(aString(), messages());
    assertThat(BOutput.storedLogs()).isEqualTo(messages());
  }

  @Test
  void output_created_without_messages_has_no_messages() throws Exception {
    var BOutput = new BOutput(aString(), bLogArrayEmpty());
    assertThat(BOutput.storedLogs()).isEqualTo(bLogArrayEmpty());
  }

  @Test
  void outputs_with_same_value_and_messages_are_equal() throws Exception {
    var BOutput = new BOutput(aString(), messages());
    assertThat(BOutput).isEqualTo(new BOutput(aString(), messages()));
  }

  @Test
  void outputs_with_same_value_and_no_messages_are_equal() throws Exception {
    var BOutput = new BOutput(aString(), bLogArrayEmpty());
    assertThat(BOutput).isEqualTo(new BOutput(aString(), bLogArrayEmpty()));
  }

  @Test
  void outputs_with_same_message_and_no_value_are_equal() throws Exception {
    var BOutput = new BOutput(null, messages());
    assertThat(BOutput).isEqualTo(new BOutput(null, messages()));
  }

  @Test
  void outputs_with_same_value_but_different_messages_are_not_equal() throws Exception {
    var BOutput = new BOutput(aString(), messages());
    assertThat(BOutput).isNotEqualTo(new BOutput(aString(), bLogArrayEmpty()));
  }

  @Test
  void outputs_with_different_value_and_same_messages_are_not_equal() throws Exception {
    var BOutput = new BOutput(aString(), messages());
    assertThat(BOutput).isNotEqualTo(new BOutput(bString("def"), messages()));
  }

  @Test
  void output_without_value_is_not_equal_to_output_with_value() throws Exception {
    var BOutput = new BOutput(aString(), messages());
    assertThat(BOutput).isNotEqualTo(new BOutput(null, messages()));
  }

  @Test
  void identical_outputs_have_same_hash_code() throws Exception {
    var BOutput = new BOutput(aString(), messages());
    assertThat(BOutput).isEqualTo(new BOutput(aString(), messages()));
  }

  private BArray messages() throws BytecodeException {
    return bLogArrayWithOneError();
  }

  private BString aString() throws BytecodeException {
    return bString("abc");
  }
}
