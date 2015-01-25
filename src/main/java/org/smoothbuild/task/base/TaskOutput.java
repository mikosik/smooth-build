package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.Message;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

public class TaskOutput {
  private final Value result;
  private final ImmutableList<Message> messages;

  public TaskOutput(Value result) {
    this(result, ImmutableList.<Message> of());
  }

  public TaskOutput(Iterable<? extends Message> messages) {
    this(null, messages);
  }

  public TaskOutput(Value result, Iterable<? extends Message> messages) {
    this.result = result;
    this.messages = ImmutableList.copyOf(messages);
  }

  public boolean hasResult() {
    return result != null;
  }

  public Value result() {
    checkState(hasResult(), "TaskOutput does not contain result.");
    return result;
  }

  public ImmutableList<Message> messages() {
    return messages;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof TaskOutput)) {
      return false;
    }
    TaskOutput that = (TaskOutput) object;
    return Objects.equal(this.result, that.result) && this.messages.equals(that.messages);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(result, messages);
  }

  @Override
  public String toString() {
    return "TaskOutput(" + result + ", " + messages + ")";
  }
}
