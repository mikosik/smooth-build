package org.smoothbuild.exec.algorithm;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.testing.TestingContext;

public class OutputTest extends TestingContext {
  @Test
  public void null_messages_are_forbidden() {
    assertCall(() -> new Output(aString(), null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void value_returns_value() {
    assertThat(new Output(aString(), messages()).value())
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
    Output output = new Output(aString(), emptyMessageArray());
    assertThat(output.messages())
        .isEqualTo(emptyMessageArray());
  }

  @Test
  public void value_throws_exception_when_no_value_is_present() {
    Output output = new Output(null, messages());
    assertCall(output::value)
        .throwsException(IllegalStateException.class);
  }

  @Test
  public void has_value_returns_true_when_value_is_present() {
    Output output = new Output(aString(), messages());
    assertThat(output.hasValue())
        .isTrue();
  }

  @Test
  public void has_value_returns_false_when_no_value_is_present() {
    Output output = new Output(null, messages());
    assertThat(output.hasValue())
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
    Output output = new Output(aString(), emptyMessageArray());
    assertThat(output)
        .isEqualTo(new Output(aString(), emptyMessageArray()));
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
        .isNotEqualTo(new Output(aString(), emptyMessageArray()));
  }

  @Test
  public void outputs_with_different_value_and_same_messages_are_not_equal() {
    Output output = new Output(aString(), messages());
    assertThat(output)
        .isNotEqualTo(new Output(string("def"), messages()));
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

  private Array messages() {
    return messageArrayWithOneError();
  }

  private RString aString() {
    return string("abc");
  }
}
