package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkState;

import java.util.Objects;

import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.value.Value;

import com.google.common.collect.ImmutableList;

public class Output {
  private final Value result;
  private final ImmutableList<Message> messages;

  public Output(Value result) {
    this(result, ImmutableList.<Message> of());
  }

  public Output(Iterable<? extends Message> messages) {
    this(null, messages);
  }

  public Output(Value result, Iterable<? extends Message> messages) {
    this.result = result;
    this.messages = ImmutableList.copyOf(messages);
  }

  public boolean hasResult() {
    return result != null;
  }

  public Value result() {
    checkState(hasResult(), "Output does not contain result.");
    return result;
  }

  public ImmutableList<Message> messages() {
    return messages;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Output && equals((Output) object);
  }

  public boolean equals(Output output) {
    return Objects.equals(result, output.result)
        && Objects.equals(messages, output.messages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(result, messages);
  }

  @Override
  public String toString() {
    return "TaskOutput(" + result + ", " + messages + ")";
  }
}
