package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkState;

import java.util.Objects;

import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.value.Value;

import com.google.common.collect.ImmutableList;

public class Output {
  private final Value result;
  private final ImmutableList<Message> messages;
  private final boolean cacheable;

  public Output(Value result) {
    this(result, ImmutableList.<Message> of());
  }

  public Output(Iterable<? extends Message> messages) {
    this(null, messages);
  }

  public Output(Value result, Iterable<? extends Message> messages) {
    this(result, messages, true);
  }

  public Output(Value result, Iterable<? extends Message> messages, boolean cacheable) {
    this.result = result;
    this.messages = ImmutableList.copyOf(messages);
    this.cacheable = cacheable;
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

  public boolean isCacheable() {
    return cacheable;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Output && equals((Output) object);
  }

  private boolean equals(Output output) {
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
