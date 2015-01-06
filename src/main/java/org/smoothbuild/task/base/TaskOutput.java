package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.Message;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

public class TaskOutput {
  private final Value returnValue;
  private final ImmutableList<Message> messages;

  public TaskOutput(Value returnValue) {
    this(returnValue, ImmutableList.<Message> of());
  }

  public TaskOutput(Iterable<? extends Message> messages) {
    this(null, messages);
  }

  public TaskOutput(Value returnValue, Iterable<? extends Message> messages) {
    this.returnValue = returnValue;
    this.messages = ImmutableList.copyOf(messages);
  }

  public boolean hasReturnValue() {
    return returnValue != null;
  }

  public Value returnValue() {
    checkState(hasReturnValue(), "TaskOutput does not contain any value.");
    return returnValue;
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
    return Objects.equal(this.returnValue, that.returnValue) && this.messages.equals(that.messages);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(returnValue, messages);
  }

  @Override
  public String toString() {
    return "TaskOutput(" + returnValue + ", " + messages + ")";
  }
}
