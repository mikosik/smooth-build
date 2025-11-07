package org.smoothbuild.virtualmachine.evaluate.step;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.virtualmachine.evaluate.step.BOutput.bOutput;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BOutputTest extends VmTestContext {
  @Test
  void creating_output_without_value_and_without_problem_fails() {
    assertCall(() -> bOutput(messages()))
        .throwsException(new IllegalArgumentException(
            "Cannot create BOutput without a value and with logs without problem."));
  }

  @Test
  void creating_output_with_value_and_with_problem_fails() {
    assertCall(() -> bOutput(bString(), errorMessages()))
        .throwsException(new IllegalArgumentException(
            "Cannot create BOutput with a value and with logs with a problem."));
  }

  @Test
  void null_messages_are_forbidden() {
    assertCall(() -> bOutput(aString(), null)).throwsException(NullPointerException.class);
  }

  @Test
  void null_messages2_are_forbidden() {
    assertCall(() -> bOutput(null)).throwsException(NullPointerException.class);
  }

  @Test
  void null_values_are_forbidden() {
    assertCall(() -> bOutput(null, messages())).throwsException(NullPointerException.class);
  }

  @Test
  void value_returns_value() throws Exception {
    assertThat(bOutput(aString(), messages()).value()).isEqualTo(some(aString()));
  }

  @Test
  void messages_returns_messages() throws Exception {
    var bOutput = bOutput(aString(), messages());
    assertThat(bOutput.storedLogs()).isEqualTo(messages());
  }

  @Test
  void output_created_without_messages_has_no_messages() throws Exception {
    var bOutput = bOutput(aString(), bLogArrayEmpty());
    assertThat(bOutput.storedLogs()).isEqualTo(bLogArrayEmpty());
  }

  @Test
  void outputs_with_same_value_and_messages_are_equal() throws Exception {
    var bOutput = bOutput(aString(), messages());
    assertThat(bOutput).isEqualTo(bOutput(aString(), messages()));
  }

  @Test
  void outputs_with_same_value_and_no_messages_are_equal() throws Exception {
    var bOutput = bOutput(aString(), bLogArrayEmpty());
    assertThat(bOutput).isEqualTo(bOutput(aString(), bLogArrayEmpty()));
  }

  @Test
  void outputs_with_same_message_and_no_value_are_equal() throws Exception {
    var bOutput = bOutput(errorMessages());
    assertThat(bOutput).isEqualTo(bOutput(errorMessages()));
  }

  @Test
  void outputs_with_same_value_but_different_messages_are_not_equal() throws Exception {
    var bOutput = bOutput(aString(), messages());
    assertThat(bOutput).isNotEqualTo(bOutput(aString(), bArray(bInfoLog())));
  }

  @Test
  void outputs_with_different_value_and_same_messages_are_not_equal() throws Exception {
    var bOutput = bOutput(aString(), messages());
    assertThat(bOutput).isNotEqualTo(bOutput(bString("def"), messages()));
  }

  @Test
  void output_without_value_is_not_equal_to_output_with_value() throws Exception {
    var bOutput = bOutput(aString(), messages());
    assertThat(bOutput).isNotEqualTo(bOutput(errorMessages()));
  }

  @Test
  void identical_outputs_have_same_hash_code() throws Exception {
    var bOutput = bOutput(aString(), messages());
    assertThat(bOutput).isEqualTo(bOutput(aString(), messages()));
  }

  private BArray messages() throws BytecodeException {
    return bLogArrayEmpty();
  }

  private BArray errorMessages() throws BytecodeException {
    return bLogArrayWithOneError();
  }

  private BString aString() throws BytecodeException {
    return bString("abc");
  }
}
