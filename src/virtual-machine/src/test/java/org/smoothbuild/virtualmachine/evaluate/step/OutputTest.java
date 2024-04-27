package org.smoothbuild.virtualmachine.evaluate.step;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class OutputTest extends TestingVirtualMachine {
  @Test
  public void null_messages_are_forbidden() {
    assertCall(() -> new Output(aString(), null)).throwsException(NullPointerException.class);
  }

  @Test
  public void value_returns_value() throws Exception {
    assertThat(new Output(aString(), messages()).value()).isEqualTo(aString());
  }

  @Test
  public void messages_returns_messages() throws Exception {
    Output output = new Output(aString(), messages());
    assertThat(output.storedLogs()).isEqualTo(messages());
  }

  @Test
  public void output_created_without_messages_has_no_messages() throws Exception {
    Output output = new Output(aString(), bLogArrayEmpty());
    assertThat(output.storedLogs()).isEqualTo(bLogArrayEmpty());
  }

  @Test
  public void outputs_with_same_value_and_messages_are_equal() throws Exception {
    Output output = new Output(aString(), messages());
    assertThat(output).isEqualTo(new Output(aString(), messages()));
  }

  @Test
  public void outputs_with_same_value_and_no_messages_are_equal() throws Exception {
    Output output = new Output(aString(), bLogArrayEmpty());
    assertThat(output).isEqualTo(new Output(aString(), bLogArrayEmpty()));
  }

  @Test
  public void outputs_with_same_message_and_no_value_are_equal() throws Exception {
    Output output = new Output(null, messages());
    assertThat(output).isEqualTo(new Output(null, messages()));
  }

  @Test
  public void outputs_with_same_value_but_different_messages_are_not_equal() throws Exception {
    Output output = new Output(aString(), messages());
    assertThat(output).isNotEqualTo(new Output(aString(), bLogArrayEmpty()));
  }

  @Test
  public void outputs_with_different_value_and_same_messages_are_not_equal() throws Exception {
    Output output = new Output(aString(), messages());
    assertThat(output).isNotEqualTo(new Output(bString("def"), messages()));
  }

  @Test
  public void output_without_value_is_not_equal_to_output_with_value() throws Exception {
    Output output = new Output(aString(), messages());
    assertThat(output).isNotEqualTo(new Output(null, messages()));
  }

  @Test
  public void identical_outputs_have_same_hash_code() throws Exception {
    Output output = new Output(aString(), messages());
    assertThat(output).isEqualTo(new Output(aString(), messages()));
  }

  private BArray messages() throws BytecodeException {
    return bLogArrayWithOneError();
  }

  private BString aString() throws BytecodeException {
    return bString("abc");
  }
}