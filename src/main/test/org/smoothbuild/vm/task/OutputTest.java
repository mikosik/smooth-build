package org.smoothbuild.vm.task;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.StringB;
import org.smoothbuild.testing.TestContext;

public class OutputTest extends TestContext {
  @Test
  public void null_messages_are_forbidden() {
    assertCall(() -> new Output(aString(), null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void value_returns_value() {
    assertThat(new Output(aString(), messages()).instB())
        .isEqualTo(aString());
  }

  @Test
  public void messages_returns_messages() {
    Output output = new Output(aString(), messages());
    assertThat(output.messages())
        .isEqualTo(messages());
  }

  @Test
  public void output_created_without_messages_has_no_messages() {
    Output output = new Output(aString(), messageArrayEmtpy());
    assertThat(output.messages())
        .isEqualTo(messageArrayEmtpy());
  }

  @Test
  public void value_throws_exception_when_no_value_is_present() {
    Output output = new Output(null, messages());
    assertCall(output::instB)
        .throwsException(IllegalStateException.class);
  }

  @Test
  public void has_value_returns_true_when_value_is_present() {
    Output output = new Output(aString(), messages());
    assertThat(output.hasVal())
        .isTrue();
  }

  @Test
  public void has_value_returns_false_when_no_value_is_present() {
    Output output = new Output(null, messages());
    assertThat(output.hasVal())
        .isFalse();
  }

  @Test
  public void outputs_with_same_value_and_messages_are_equal() {
    Output output = new Output(aString(), messages());
    assertThat(output)
        .isEqualTo(new Output(aString(), messages()));
  }

  @Test
  public void outputs_with_same_value_and_no_messages_are_equal() {
    Output output = new Output(aString(), messageArrayEmtpy());
    assertThat(output)
        .isEqualTo(new Output(aString(), messageArrayEmtpy()));
  }

  @Test
  public void outputs_with_same_message_and_no_value_are_equal() {
    Output output = new Output(null, messages());
    assertThat(output)
        .isEqualTo(new Output(null, messages()));
  }

  @Test
  public void outputs_with_same_value_but_different_messages_are_not_equal() {
    Output output = new Output(aString(), messages());
    assertThat(output)
        .isNotEqualTo(new Output(aString(), messageArrayEmtpy()));
  }

  @Test
  public void outputs_with_different_value_and_same_messages_are_not_equal() {
    Output output = new Output(aString(), messages());
    assertThat(output)
        .isNotEqualTo(new Output(stringB("def"), messages()));
  }

  @Test
  public void output_without_value_is_not_equal_to_output_with_value() {
    Output output = new Output(aString(), messages());
    assertThat(output)
        .isNotEqualTo(new Output(null, messages()));
  }

  @Test
  public void identical_outputs_have_same_hash_code() {
    Output output = new Output(aString(), messages());
    assertThat(output)
        .isEqualTo(new Output(aString(), messages()));
  }

  private ArrayB messages() {
    return messageArrayWithOneError();
  }

  private StringB aString() {
    return stringB("abc");
  }
}
